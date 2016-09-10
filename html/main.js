// websocket connection to server
var socket;

// a map from the name of a card in the supply to its UI elements
// 'card name' -> {pile:<div>, cost:<p>, pileSize:<p>, cross:<div>, plus:<div>, img:<img>}
var supplyCardElems = {};
// a map from the name of a card to the className of its span when displayed
// 'card name' -> 'action', or 'treasure', or 'victory', etc.
var supplyCardClassNames = {};

// a map from the name of a card to the div displaying its stack in the hand
// 'card name' -> <div>
var handCardElems = {};
// an array containing both the name of the card stack at that position in the hand and the size of that stack
// [{name:'card name', count:#}, {name:'card name', count:integer}, ...]
var handCounts = [];

// a map from card name to all of the information needed to display a popup describing that card.
// the type is a string like 'Action', 'Action-Attack', etc.
// cost is always the base cost (not affected by cards like Bridge)
// 'card name' -> {description:['line 1', 'line 2', ...], type:'Human-Readable-Type', cost:'$5'}
var cardDescriptions = {};

// an array representing the rows in the custom games table.
// rowDiv is the row element in the DOM.
// empty is true if the row is one of the empty rows automatically generated even if the table is empty.
// closed is true if the row is a game that has become closed and can be removed with the "remove closed" button
// [{rowDiv:<div>, empty:boolean, closed:boolean}, {rowDiv:<div>, empty:boolean, closed:boolean}, ...]
var gameListRows = [];
// a map from the name of a game to it's row in the table
// 'game name' -> integer
var gameRowIndex = {};

// an array of the different bots that can be added to custom games
// ['bot type 1', 'bot type 2', ...]
var availableBotNames = [];

// width of images in the hand, in em
var HAND_IMG_WIDTH = 10;
// the space between cards in a stack in the hand, in em
var HAND_IMG_SPACE_LEFT = 2;
var HAND_IMG_SPACE_TOP = 1;

// dimensions of the source images, in px
var SRC_IMG_HEIGHT = 72.0;
var SRC_IMG_WIDTH = 120.0;

// the number of empty rows initially in the "Custom Games" list
var INITIAL_GAME_LIST_ROWS = 5;

/*
Removes all child nodes from the given element.
*/
function removeAllChildNodes(elem) {
  while (elem.hasChildNodes()) {
    elem.removeChild(elem.lastChild);
  }
}

/*
Sets a popup to appear when the given element is hovered over with the mouse.
The popup will contain information for the card with the given name, which
should have an entry in cardDescriptions.
*/
function registerPopup(elem, cardName) {
  elem.onmouseover = function (e) {
    displayPopup(cardName);
  };
}

/*
Displays a popup with the information of the card with the given name, which
should have an entry in cardDescriptions.
*/
function displayPopup(cardName) {
  // get description info
  var description = cardDescriptions[cardName];
  // get the card name's background color
  var className = supplyCardClassNames[cardName];
  // set name
  var nameElem = document.getElementById('popupName');
  removeAllChildNodes(nameElem);
  var p = document.createElement('p');
  p.innerHTML = '<span class="' + className + '">' + cardName + '</span>';
  nameElem.appendChild(p);
  // set description lines
  var descriptionElem = document.getElementById('popupDescription');
  removeAllChildNodes(descriptionElem);
  for (var i = 0; i < description.description.length; i++) {
    p = document.createElement('p');
    p.innerHTML = description.description[i];
    descriptionElem.appendChild(p);
  }
  // set type
  var typeElem = document.getElementById('popupType');
  // the cost element (e.g. '<div id="popupCost"><p>$5</p></div>') is also a child of 'popupType', so just remove the type text (e.g. <p class="popupTypeText"><span class=".action">'Action'</span></p>)
  while (typeElem.getElementsByClassName('popupTypeText').length > 0) {
    typeElem.removeChild(typeElem.getElementsByClassName('popupTypeText')[0]);
  }
  p = document.createElement('p');
  p.className = 'popupTypeText';
  p.innerHTML = '<span class="' + className + '">' + description.type + '</span>';
  typeElem.appendChild(p);
  // set cost
  var costElem = document.getElementById('popupCost');
  removeAllChildNodes(costElem);
  p = document.createElement('p');
  p.innerHTML = description.cost;
  costElem.appendChild(p);
  // show
  var cardPopupContainer = document.getElementById('cardPopupContainer');
  cardPopupContainer.style.width = cardPopupContainer.parentElement.clientWidth + 'px';
  cardPopupContainer.style.display = 'block';
}

/*
Scroll to the bottom of the element with the given id.
*/
function scrollToBottom(id) {
  var div = document.getElementById(id);
  div.scrollTop = div.scrollHeight - div.clientHeight;
}

/*
Add the given line to the game log.
*/
function log(message) {
  var log = document.getElementById('log');
  var p = document.createElement('p');
  p.innerHTML = message;
  log.appendChild(p);
}

/*
Returns the location of the card art image for the given card.
e.g. 'Council Room' -> 'card_art/council_room.jpg'
*/
function cardArtSrc(cardName) {
  cardName = cardName.toLowerCase();
  cardName = cardName.replace(' ', '_');
  return 'card_art/' + cardName + '.jpg';
}

/*
Removes only the given class from the element's class name.
*/
function removeClass(elem, toRemove) {
  var newClassName = '';
  var classes = elem.className.split(' ');
  for(var i = 0; i < classes.length; i++) {
    if(classes[i] !== toRemove) {
      newClassName += classes[i] + ' ';
    }
  }
  elem.className = newClassName;
}

/*
Adds a stack of the given card to the hand, appending this new stack to the end.
*/
function addStackToHand(cardName, numCards) {
  if (numCards == 0) {
    return;
  }
  var imgWidth = HAND_IMG_WIDTH;
  var imgHeight = imgWidth * (SRC_IMG_HEIGHT / SRC_IMG_WIDTH);
  var imgMarginLeft = HAND_IMG_SPACE_LEFT;
  var imgMarginTop = HAND_IMG_SPACE_TOP;
  // div to hold stack images and name
  var handCard = document.createElement('div');
  handCardElems[cardName] = handCard;
  handCard.className = 'handCard';
  // div to hold stack of images
  var cardStack = document.createElement('div');
  cardStack.className = 'handCardStack';
  // manually calculate size of full image stack
  cardStack.style.width = (imgWidth + (numCards - 1) * imgMarginLeft).toString() + 'em';
  cardStack.style.height = (imgHeight + (numCards - 1) * imgMarginTop).toString() + 'em';
  // manually add and position images
  for (var i = 0; i < numCards; i++) {
    var img = document.createElement('img');
    img.src = cardArtSrc(cardName);
    img.style.marginLeft = (i * imgMarginLeft).toString() + 'em';
    img.style.marginTop = (i * imgMarginTop).toString() + 'em';
    cardStack.appendChild(img);
  }
  handCard.appendChild(cardStack);
  // div to hold card name
  var handCardName = document.createElement('div');
  handCardName.className = 'handCardName';
  var p = document.createElement('p');
  var className = supplyCardClassNames[cardName];
  p.innerHTML = '<span class="' + className + '">' + cardName + '</span>';
  handCardName.appendChild(p);
  handCard.appendChild(handCardName);
  // allow popups to appear when mousing over cards in the hand
  registerPopup(handCard, cardName);
  document.getElementById('hand').appendChild(handCard);
}

function addCardDescription(card) {
  var costString = '$' + card.cost.toString();
  cardDescriptions[card.name] = {'description':card.description, 'type':card.type, 'cost':costString};
}

function createPileCross() {
  return createDivContainingImage('cross', 'cross.png');
}

function createPilePlus() {
  return createDivContainingImage('plus', 'plus.png');
}

function createDivContainingImage(divClassName, imgSrc) {
  var div = document.createElement('div');
  div.className = divClassName;
  var img = document.createElement('img');
  img.src = imgSrc;
  div.appendChild(img);
  return div;
}

/*
Takes an array of objects containing the names, colors, descriptions, types, costs of the kigdom cards.
[{name:'card name', className:'action', description:['line 1', 'line 2', ...], type:'Human-Readable-Type', cost:integer}, ...]
Sets the kindom card UI elements in the supply pile.
*/
function setKingdomCards(cards) {
  // clear all data about supply cards
  supplyCardElems = {};
  supplyCardClassNames = {};
  cardDescriptions = {};
  // set kingdom card colors
  for (var i = 0; i < cards.length; i++) {
    supplyCardClassNames[cards[i].name] = cards[i].className;
  }
  // remove previous kingdom card UI elements
  var kingdom = document.getElementById('kingdom');
  removeAllChildNodes(kingdom);
  // for each kingdom card
  for (var i = 0; i < cards.length; i++) {
    // add popup description to cardDescriptions
    addCardDescription(cards[i]);
    // add a pile for this card
    var kingdomPile = document.createElement('div');
    kingdomPile.className = 'kingdomPile';
    registerPopup(kingdomPile, cards[i].name);
    // set name
    var nameDiv = document.createElement('div');
    nameDiv.className = 'name';
    var nameParagraph = document.createElement('p');
    nameParagraph.className = cards[i].className;
    nameParagraph.innerHTML = cards[i].name;
    nameDiv.appendChild(nameParagraph);
    kingdomPile.appendChild(nameDiv);
    // set status (cost & pile size)
    var status = document.createElement('div');
    status.className = 'status';
    // set cost
    var costDiv = document.createElement('div');
    costDiv.className = 'cost';
    var costParagraph = document.createElement('p');
    costParagraph.innerHTML = '$' + cards[i].cost.toString();
    costDiv.appendChild(costParagraph);
    status.appendChild(costDiv);
    // set pile size (initially unknown)
    var pileSizeDiv = document.createElement('div');
    pileSizeDiv.className = 'pileSize';
    var pileSizeParagraph = document.createElement('p');
    pileSizeParagraph.innerHTML = '(?)';
    pileSizeDiv.appendChild(pileSizeParagraph);
    status.appendChild(pileSizeDiv);
    kingdomPile.appendChild(status);
    // add visuals for this pile
    var cardArt = document.createElement('div');
    cardArt.className = 'cardArt';
    // add cross image to indicate when this pile is empty (initially hidden)
    var cross = createPileCross();
    cross.style.display = 'none';
    cardArt.appendChild(cross);
    // add plus image to indicate when this pile can be gained (initially hidden)
    var plus = createPilePlus();
    plus.style.display = 'none';
    cardArt.appendChild(plus);
    // add card art
    var img = document.createElement('img');
    img.src = cardArtSrc(cards[i].name);
    cardArt.appendChild(img);
    kingdomPile.appendChild(cardArt);
    kingdom.appendChild(kingdomPile);
    supplyCardElems[cards[i].name] = {'pile':kingdomPile, 'cost':costParagraph, 'pileSize':pileSizeParagraph, 'cross':cross, 'plus':plus, 'img':img};
  }
}

/*
Takes an array of objects containing the names, colors, descriptions, types, costs of the basic cards.
[{name:'card name', className:'action', description:['line 1', 'line 2', ...], type:'Human-Readable-Type', cost:integer}, ...]
Sets the basic card UI elements in the supply pile.
*/
function setBasicCards(cards) {
  // set basic card colors
  for (var i = 0; i < cards.length; i++) {
    supplyCardClassNames[cards[i].name] = cards[i].className;
  }
  // remove previous basic cards
  var basic = document.getElementById('basic');
  removeAllChildNodes(basic);
  // for each basic card
  for (var i = 0; i < cards.length; i++) {
    // add popup description to cardDescriptions
    addCardDescription(cards[i]);
    // add a pile for this card
    var basicPile = document.createElement('div');
    basicPile.className = 'basicPile';
    registerPopup(basicPile, cards[i].name);
    // add visuals for this pile
    var cardArt = document.createElement('div');
    cardArt.className = 'cardArt';
    // add cross image to indicate when this pile is empty (initially hidden)
    var cross = createPileCross();
    cross.style.display = 'none';
    cardArt.appendChild(cross);
    // add plus image to indicate when this pile can be gained (initially hidden)
    var plus = createPilePlus();
    plus.style.display = 'none';
    cardArt.appendChild(plus);
    // add card art
    var img = document.createElement('img');
    img.src = cardArtSrc(cards[i].name);
    cardArt.appendChild(img);
    basicPile.appendChild(cardArt);
    // add status (cost & pile size)
    var status = document.createElement('div');
    status.className = 'status';
    var p = document.createElement('p');
    // add cost
    var cost = document.createElement('span');
    cost.className = 'cost';
    cost.innerHTML = '$' + cards[i].cost.toString();
    p.appendChild(cost);
    p.appendChild(document.createTextNode(' '));
    // add pile size
    var pileSize = document.createElement('span');
    pileSize.className = 'pileSize';
    pileSize.innerHTML = '(?)';
    p.appendChild(pileSize);
    status.appendChild(p);
    basicPile.appendChild(status);
    basic.appendChild(basicPile);
    supplyCardElems[cards[i].name] = {'pile':basicPile, 'cross':cross, 'plus':plus, 'img':img, 'cost':cost, 'pileSize':pileSize};
  }
}

/*
Takes an map of card names to pile sizes.
'card name' -> integer
Changes the given pile sizes in the UI.
*/
function setPileSizes(piles) {
  // for each changed pile
  for (var cardName in piles) {
    if (piles.hasOwnProperty(cardName)) {
      // changed the displayed number
      var size = piles[cardName];
      supplyCardElems[cardName].pileSize.innerHTML = '(' + size.toString() + ')';
      // if the pile is empty
      if (size == 0) {
        // make the image partially transparent
        supplyCardElems[cardName].img.style.opacity = '0.3';
        // show the cross image
        supplyCardElems[cardName].cross.style.display = 'block';
      }
    }
  }
}

function setEmbargoTokens(card, numTokens) {
  var cardArtDiv = supplyCardElems[card].pile.getElementsByClassName('cardArt')[0];
  // remove previous embargo token UI element
  while (cardArtDiv.getElementsByClassName('embargoTokens').length != 0) {
    cardArtDiv.removeChild(cardArtDiv.getElementsByClassName('embargoTokens')[0]);
  }
  // add new embargo token UI element
  var tokensDiv = document.createElement('div');
  tokensDiv.className = 'embargoTokens';
  var tokensLabel = document.createElement('label');
  tokensLabel.innerHTML = numTokens.toString();
  tokensDiv.appendChild(tokensLabel);
  cardArtDiv.appendChild(tokensDiv);
}

function setActions(actions) {
  var numActions = document.getElementById('numActions');
  if (actions != '') {
    numActions.innerHTML = actions;
  } else {
    // insert a single space character
    numActions.innerHTML = String.fromCharCode(160);
  }
}

function setBuys(buys) {
  var numBuys = document.getElementById('numBuys');
  if (buys != '') {
    numBuys.innerHTML = buys;
  } else {
    // insert a single space character
    numBuys.innerHTML = String.fromCharCode(160);
  }
}

function setCoins(coins) {
  document.getElementById('numCoins').innerHTML = coins;
}

/*
Takes the size and id of either the 'draw' or 'discard'.
Changes the indicators to reflect that size.
*/
function setDeckSize(size, elemId) {
  var pile = document.getElementById(elemId);
  // remove previous card indicators
  removeAllChildNodes(pile);
  if (size <= 10) {
    // add multiple oneCard elements
    for (var i = 0; i < size; i++) {
      var div = document.createElement('div');
      div.className = 'oneCard';
      pile.appendChild(div);
    }
  } else {
    // add one manyCards element
    var div = document.createElement('div');
    div.className = 'manyCards';
    div.style.width = (8 * size).toString() + 'px';
    pile.appendChild(div);
  }
}

function setNativeVillageMat(contents) {
  var nativeVillageMat = document.getElementById('nativeVillageMat');
  removeAllChildNodes(nativeVillageMat);
  if (contents) {
    var nativeVillageParagraph = document.createElement('p');
    nativeVillageParagraph.innerHTML = 'Native Village: ' + contents;
    nativeVillageMat.appendChild(nativeVillageParagraph);
  }
}

function setDurations(contents) {
  var durations = document.getElementById('durations');
  removeAllChildNodes(durations);
  if (contents) {
    var durationsParagraph = document.createElement('p');
    durationsParagraph.innerHTML = 'Duration: ' + contents;
    durations.appendChild(durationsParagraph);
  }
}

/*
Takes an array of objects containing the stacks in the hand, in order.
[{name:'card name', count:integer}, ...]
*/
function setHand(hand) {
  handCardElems = {};
  handCounts = hand;
  // remove previous UI elements
  var handElem = document.getElementById('hand');
  removeAllChildNodes(handElem);
  // for each pile
  for (var i = 0; i < hand.length; i++) {
    addStackToHand(hand[i].name, hand[i].count);
  }
}

/*
Takes a map from card names to their new costs.
'card name' -> integer
*/
function setCardCosts(costs) {
  for (var cardName in costs) {
    if (costs.hasOwnProperty(cardName)) {
      supplyCardElems[cardName].cost.innerHTML = '$' + costs[cardName].toString();
    }
  }
}

/*
Takes the name of the player that the game is waiting on, or Undefined if it is
this player's turn.
*/
function setWaitingOn(player) {
  // hide "hurry up" button
  document.getElementById('hurryUpButton').style.display = 'none';
  // show "waiting on" indicator
  var waitingOn = document.getElementById('waitingOn');
  var waitingOnText = document.getElementById('waitingOnText');
  if (player) {
    waitingOn.style.display = 'block';
    waitingOnText.innerHTML = '- Waiting on ' + player + ' -';
  } else {
    waitingOn.style.display = 'none';
  }
}

/*
Shows the "hurry up" button.
*/
function allowHurryUp() {
  document.getElementById('hurryUpButton').style.display = 'inline-block';
}

/*
Sends the server a "hurry up" message for the player being waited on.
Hides the "hurry up" button.
*/
function hurryUp() {
  document.getElementById('hurryUpButton').style.display = 'none';
  socket.send(JSON.stringify({'type':'hurryUp'}));
}

function setPromptMessage(message, promptType) {
  var prompt = document.getElementById('prompt');
  prompt.className = promptType;
  removeAllChildNodes(prompt);
  var p = document.createElement('p');
  p.innerHTML = message;
  prompt.appendChild(p);
}

function addPromptButton(buttonText) {
  var button = document.createElement('button');
  button.innerHTML = buttonText;
  document.getElementById('prompt').appendChild(button);
  return button;
}

function displayPrompt() {
  document.getElementById('prompt').style.display = 'block';
}

function sendResponseOnMouseDown(elem, response) {
  elem.onmousedown = function () {
    endPrompt();
    sendResponse(response);
  };
}

/*
Removes the current prompt (if any), removes all onmousedown handlers from cards
in the supply and hand, and turns off visual flair like plus icons in the
supply.
*/
function endPrompt() {
  // hide prompt
  var prompt = document.getElementById('prompt');
  prompt.style.display = 'none';
  removeAllChildNodes(prompt);
  // disable choosing from supply
  for (var cardName in supplyCardElems) {
    if (supplyCardElems.hasOwnProperty(cardName)) {
      // hide the plus
      supplyCardElems[cardName].plus.style.display = 'none';
      // remove mousedown handlers
      var pile = supplyCardElems[cardName].pile;
      removeClass(pile, 'clickable');
      pile.onmousedown = null;
    }
  }
  // disable choosing from hand
  for (var cardName in handCardElems) {
    if (handCardElems.hasOwnProperty(cardName)) {
      var stack = handCardElems[cardName];
      removeClass(stack, 'clickable');
      stack.onmousedown = null;
    }
  }
}

function promptChooseFromSupply(choices, message, promptType, isMandatory, noneMessage) {
  // set message
  setPromptMessage(message, promptType);
  // if not mandatory, add "none" button
  if (!isMandatory) {
    var noneButton = addPromptButton(noneMessage);
    sendResponseOnMouseDown(noneButton, 'none');
  }
  // enable choosing the supply piles
  for (var i = 0; i < choices.length; i++) {
    // show plus icon
    supplyCardElems[choices[i]].plus.style.display = 'block';
    // add visual flair when hovering over this pile
    var pile = supplyCardElems[choices[i]].pile;
    pile.className += ' clickable';
    // choose on mouse down
    sendResponseOnMouseDown(pile, choices[i]);
  }
  displayPrompt();
}

function promptChooseFromHand(choices, message, promptType, isMandatory, noneMessage) {
  // set message
  setPromptMessage(message, promptType);
  // if not mandatory, add "none" button
  if (!isMandatory) {
    var noneButton = addPromptButton(noneMessage);
    sendResponseOnMouseDown(noneButton, 'none');
  }
  // click handlers
  for (var i = 0; i < choices.length; i++) {
    // add visual flair when hovering over stack
    var stack = handCardElems[choices[i]];
    stack.className += ' clickable';
    sendResponseOnMouseDown(stack, choices[i]);
  }
  displayPrompt();
}

function promptMultipleChoice(message, promptType, choices, disabled) {
  // set message
  setPromptMessage(message, promptType);
  // add buttons
  var buttons = [];
  for (var i = 0; i < choices.length; i++) {
    var button = addPromptButton(choices[i]);
    sendResponseOnMouseDown(button, i.toString());
    buttons.push(button);
  }
  // if there are any disabled buttons, remove their handlers
  if (disabled) {
    for (var i = 0; i < disabled.length; i++) {
      buttons[disabled[i]].disabled = true;
      buttons[disabled[i]].onmousedown = null;
    }
  }
  displayPrompt();
}

// the total number of cards to discard
var discardNumber = 0;
// a list of the cards discarded so far
var toDiscard = [];
function promptDiscardNumber(number, isMandatory, cause, promptType, destination) {
  discardNumber = number;
  toDiscard = [];
  // show prompt with number remaining
  setPromptMessage(promptDiscardNumberText(isMandatory, cause, destination), promptType);
  // if not mandatory, add "done" button
  if (!isMandatory) {
    var doneButton = addPromptButton('done');
    doneButton.onmousedown = function () {
      endPrompt();
      sendResponse(toDiscard);
    };
  }
  // discard handlers on click
  for (var cardName in handCardElems) {
    if (handCardElems.hasOwnProperty(cardName)) {
      var stack = handCardElems[cardName];
      stack.className += ' clickable';
      onMouseDownDiscard(stack, cardName, isMandatory, cause, destination);
    }
  }
  displayPrompt();
}

function onMouseDownDiscard(elem, cardName, isMandatory, cause, destination) {
  elem.onmousedown = function () {
    promptDiscardNumberClicked(cardName, isMandatory, cause, destination);
  };
}

function promptDiscardNumberClicked(discardedName, isMandatory, cause, destination) {
  // decrement clicked stack
  toDiscard.push(discardedName);
  for (var i = 0; i < handCounts.length; i++) {
    if (handCounts[i].name == discardedName) {
      handCounts[i].count -= 1;
      break;
    }
  }
  // update prompt text to reflect new number
  var promptText = document.getElementById('prompt').getElementsByTagName('p')[0];
  promptText.innerHTML = promptDiscardNumberText(isMandatory, cause, destination);
  // update ui to show new hand
  setHand(handCounts);
  // if discard number met
  if (toDiscard.length == discardNumber) {
    endPrompt();
    sendResponse(toDiscard);
  } else {
    // otherwise, add new handlers
    for (var cardName in handCardElems) {
      if (handCardElems.hasOwnProperty(cardName)) {
        var stack = handCardElems[cardName];
        stack.className += ' clickable';
        onMouseDownDiscard(stack, cardName, isMandatory, cause, destination);
      }
    }
  }
}

function promptDiscardNumberText(isMandatory, cause, destination) {
  if (destination === 'trash') {
    return cause + ': Trash ' + (isMandatory ? '' : 'up to ') + (discardNumber - toDiscard.length) + ' card(s)';
  } else if (destination === 'discard') {
    return cause + ': Discard ' + (isMandatory ? '' : 'up to ') + (discardNumber - toDiscard.length) + ' card(s)';
  } else { /* destination === 'draw' */
    return cause + ': Put ' + (isMandatory ? '' : 'up to ') + (discardNumber - toDiscard.length) + ' card(s) on top of your deck (the first card you choose will be on top of your deck)';
  }
}

function message(str) {
  log(str);
}

/*
Takes this player's username, the current game listings, and the available bot names.
gameListings is an array of objects containing all of the information to display
in each row.
[{name:string, numOpenings:integer, numPlayers:integer, sets:string, requiredCards:string, forbiddenCards:string}, ...]
*/
function enterLobby(username, gameListings, availableBots) {
  availableBotNames = availableBots;
  // hide everything except the lobby
  document.getElementById('loginContainer').style.display = 'none';
  document.getElementById('cardPopupContainer').style.display = 'none';
  document.getElementById('gameLobby').style.display = 'none';
  document.getElementById('game').style.display = 'none';
  // show the lobby
  document.getElementById('lobby').style.display = 'flex';
  // set username
  document.getElementById('lobbyUsernameText').innerHTML = username;
  // clear game list
  initializeGameList();
  // add current games
  for (var i = 0; i < gameListings.length; i++) {
    addGameListing(gameListings[i]);
  }
  // enable automatch
  var automatchCheckbox = document.getElementById('automatchCheckbox');
  automatchCheckbox.checked = false;
  automatchCheckbox.onclick = automatchSet;
  document.getElementById('automatch2Checkbox').onclick = automatchSettingsChanged;
  document.getElementById('automatch3Checkbox').onclick = automatchSettingsChanged;
  document.getElementById('automatch4Checkbox').onclick = automatchSettingsChanged;
  // enable "add bot" button
  document.getElementById('addBotButton').onmousedown = addBot;
  // enable "create custom game" button
  document.getElementById('createCustomGameButton').onmousedown = createCustomGame;
  // enable "remove closed button"
  document.getElementById('removeClosedButton').onmousedown = removeClosedGameListings;
}

/*
Send the current automatch settings to the server.
*/
function automatchSet() {
  var isOn = document.getElementById('automatchCheckbox').checked;
  var toSend = {'type':'automatch', 'isOn':isOn};
  if (isOn) {
    toSend['2'] = document.getElementById('automatch2Checkbox').checked;
    toSend['3'] = document.getElementById('automatch3Checkbox').checked;
    toSend['4'] = document.getElementById('automatch4Checkbox').checked;
  }
  socket.send(JSON.stringify(toSend));
}

/*
If automatch is on, send the new automatch settings to the server.
*/
function automatchSettingsChanged() {
  // only send the new settings if automatch is on
  if (document.getElementById('automatchCheckbox').checked) {
    automatchSet();
  }
}

/*
Returns an array conaining the names of the bots that the playet has added
to the "Bots" section.
*/
function getBots() {
  var selects = document.getElementById('botList').getElementsByTagName('select');
  var bots = [];
  // for each drop down menu, get the selected bot
  for (var i = 0; i < selects.length; i++) {
    bots.push(selects[i].options[selects[i].selectedIndex].value);
  }
  return bots;
}

/*
Adds a new drop down menu under the "Bots" section.
*/
function addBot() {
  // never allow more than three bots because games have a maximum of 4 players
  // and always at least one human player
  if (getBots().length >= 3) {
    return;
  }
  var botList = document.getElementById('botList');
  var div = document.createElement('div');
  var select = document.createElement('select');
  // add each available bot name as an <option> in the <select> drop down menu
  for (var i = 0; i < availableBotNames.length; i++) {
    var option = document.createElement('option');
    option.value = availableBotNames[i];
    option.innerHTML = availableBotNames[i];
    // default to the first available bot
    if (i == 0) {
      option.selected = 'selected';
    }
    select.appendChild(option);
  }
  // add a button to remove this bot
  var button = document.createElement('button');
  button.innerHTML = 'Remove';
  button.onmousedown = function () {
    removeBot(div);
  }
  div.appendChild(select);
  div.appendChild(button);
  botList.appendChild(div);
}

/*
Remove a bot from the "Create Custom Game" section by the <div> containing its
UI elements.
*/
function removeBot(div) {
  document.getElementById('botList').removeChild(div);
}

/*
Empty the "Custom Games" list.
*/
function initializeGameList() {
  gameListRows = [];
  gameRowIndex = {};
  var gameList = document.getElementById('gameList');
  removeAllChildNodes(gameList);
  // add a row of column labels
  addGameListRow({'name':'Name', 'players':'Players', 'sets':'Sets', 'cards':'Cards'}, false);
  for (var i = 0; i < INITIAL_GAME_LIST_ROWS; i++) {
    var rowDiv = document.createElement('div');
    rowDiv.className = 'rowDiv';
    gameList.appendChild(rowDiv);
    gameListRows.push({'rowDiv':rowDiv, 'empty':true, 'closed':false});
  }
}

function addGameListing(gameListing) {
  var name = gameListing.name;
  var row = {};
  row.name = name;
  row.players = gameListingPlayersString(false, gameListing.numOpenings, gameListing.numPlayers);
  row.sets = gameListing.sets;
  row.requiredCards = gameListing.requiredCards;
  row.forbiddenCards = gameListing.forbiddenCards;
  // if this game is already in the list and has re-opened
  if (gameRowIndex.hasOwnProperty(name)) {
    var index = gameRowIndex[name];
    var rowDiv = gameListRows[index].rowDiv;
    // re-open the game
    gameListRows[index].closed = false;
    // update any information that may have changed
    rowDiv.getElementsByClassName('playersDiv')[0].getElementsByTagName('p')[0].innerHTML = row.players;
    rowDiv.getElementsByClassName('setsDiv')[0].getElementsByTagName('p')[0].innerHTML = row.sets;
    setGameListingCards(rowDiv.getElementsByClassName('cardsDiv')[0], row.requiredCards, row.forbiddenCards);
    rowDiv.onmousedown = function () {
      joinGame(name);
    };
    rowDiv.className = 'rowDiv rowDivJoinable';
  } else {
    // otherwise, add a new row to the list
    addGameListRow(row, true);
  }
}

/*
Returns a <span> representing the "Players" status of a game in the
"Custom Games" list.
*/
function gameListingPlayersString(isClosed, numOpenings, numPlayers) {
  var className;
  if (isClosed || (numOpenings == numPlayers)) {
    className = 'gameListingClosed';
  } else {
    className = 'gameListingOpen';
  }
  var string;
  if (isClosed) {
    string = '(Closed)';
  } else {
    string = '(' + numPlayers.toString() + '/' + numOpenings.toString() + ')';
  }
  return '<span class="' + className + '">' + string + '</span>';
}

/*
Sets the "Cards" status of a game in the "Custom Games" list.
*/
function setGameListingCards(cardsDiv, requiredCards, forbiddenCards) {
  removeAllChildNodes(cardsDiv);
  var p;
  if (requiredCards) {
    p = document.createElement('p');
    p.innerHTML = requiredCards;
    cardsDiv.appendChild(p);
  }
  if (forbiddenCards) {
    p = document.createElement('p');
    p.innerHTML = 'Not: ' + forbiddenCards;
    cardsDiv.appendChild(p);
  }
}

/*
Set the status of a game in the "Custom Games" list.
Full and closed games can no longer be joined, otherwise they can.
*/
function updateGameListing(name, isClosed, numOpenings, numPlayers) {
  // ignore games that have never been sent to this client
  if (gameRowIndex.hasOwnProperty(name)) {
    var index = gameRowIndex[name];
    var rowDiv = gameListRows[index].rowDiv;
    // update "Players" status in the games list
    var players = gameListingPlayersString(isClosed, numOpenings, numPlayers);
    rowDiv.getElementsByClassName('playersDiv')[0].getElementsByTagName('p')[0].innerHTML = players;
    // update joinable status
    if (isClosed || (numPlayers == numOpenings)) {
      // game is full
      rowDiv.onmousedown = null;
      rowDiv.className = 'rowDiv';
    } else {
      // game is open
      rowDiv.onmousedown = function () {
        joinGame(name);
      };
      rowDiv.className = 'rowDiv rowDivJoinable';
    }
    // allow closed games to be removed with the "Remove Closed" button
    if (isClosed) {
      gameListRows[index].closed = true;
    }
  }
}

/*
Adds a row to the "Custom Games" list.
Can be used to add a text-only row by passing isGame=false and having a string
in row.cards instead of having row.requiredCards and row.forbiddenCards
*/
function addGameListRow(row, isGame) {
  var gameList = document.getElementById('gameList');
  var rowDiv = document.createElement('div');
  rowDiv.className = 'rowDiv';
  // set name
  var nameDiv = document.createElement('div');
  nameDiv.className = 'nameDiv';
  var p = document.createElement('p');
  p.innerHTML = row.name;
  nameDiv.appendChild(p);
  rowDiv.appendChild(nameDiv);
  // set players status
  var playersDiv = document.createElement('div');
  playersDiv.className = 'playersDiv';
  p = document.createElement('p');
  p.innerHTML = row.players;
  playersDiv.appendChild(p);
  rowDiv.appendChild(playersDiv);
  // set card sets
  var setsDiv = document.createElement('div');
  setsDiv.className = 'setsDiv';
  p = document.createElement('p');
  p.innerHTML = row.sets;
  setsDiv.appendChild(p);
  rowDiv.appendChild(setsDiv);
  // set required and forbidden cards
  var cardsDiv = document.createElement('div');
  cardsDiv.className = 'cardsDiv';
  if (row.cards) {
    // use an explicit string in the cards row
    p = document.createElement('p');
    p.innerHTML = row.cards;
    cardsDiv.appendChild(p);
  } else {
    setGameListingCards(cardsDiv, row.requiredCards, row.forbiddenCards);
  }
  rowDiv.appendChild(cardsDiv);
  // find the index to insert the new row
  var index = 0;
  for (;;) {
    if (index == gameListRows.length) {
      // stop at the end of the list
      break;
    } else if (gameListRows[index].empty) {
      // stop on an empty row
      break;
    }
    index++;
  }
  var rowEntry = {'row':row, 'rowDiv':rowDiv, 'empty':false, 'closed':(!isGame)};
  // insert the row in one of these three locations
  if (index < gameListRows.length - 1) {
    // somewhere before the last row
    gameList.removeChild(gameListRows[index].rowDiv);
    gameList.insertBefore(rowDiv, gameListRows[index + 1].rowDiv);
    gameListRows[index] = rowEntry;
  } else if(index == gameListRows.length - 1) {
    // replacing the last row
    gameList.removeChild(gameListRows[index].rowDiv);
    gameList.appendChild(rowDiv);
    gameListRows[index] = rowEntry;
  } else {
    // as a new row at the end
    gameList.appendChild(rowDiv);
    gameListRows.push(rowEntry);
  }
  if (isGame) {
    // on click, join game
    rowDiv.onmousedown = function () {
      joinGame(row.name);
    };
    rowDiv.className = 'rowDiv rowDivJoinable';
    gameRowIndex[row.name] = index;
  }
}

/*
Removes all of the rows from the "Cusom Games" list that contain closed games.
*/
function removeClosedGameListings() {
  // save the old rows
  var oldRows = gameListRows;
  // empty the list
  initializeGameList();
  // add back all of the old rows that had open games
  for (var i = 0; i < oldRows.length; i++) {
    if (!oldRows[i].empty && !oldRows[i].closed) {
      addGameListRow(oldRows[i].row, true);
    }
  }
}

/*
Sends a request to the server to join the game with the given name.
*/
function joinGame(name) {
  socket.send(JSON.stringify({'type':'joinGame', 'name':name}));
}

/*
Sends a request to the server to create a new custom game with the settings
in the "Create Custom Game" section.
*/
function createCustomGame() {
  // remove previous error messages
  var customGameSection = document.getElementById('customGameSection');
  var errorMessages = customGameSection.getElementsByClassName('customGameError');
  for (var i = 0; i < errorMessages.length; i++) {
    customGameSection.removeChild(errorMessages[i]);
  }
  // send new custom game request
  var name = document.getElementById('customGameName').value;
  var numPlayersSelect = document.getElementById('numPlayersSelect');
  var numPlayers = numPlayersSelect.options[numPlayersSelect.selectedIndex].value;
  // create a list of the checked card sets
  var sets = [];
  if (document.getElementById('baseCheckbox').checked) {
    sets.push('Base');
  }
  if (document.getElementById('intrigueCheckbox').checked) {
    sets.push('Intrigue');
  }
  if (document.getElementById('seasideCheckbox').checked) {
    sets.push('Seaside');
  }
  var cards = document.getElementById('customGameCards').value;
  var bots = getBots();
  socket.send(JSON.stringify({'type':'createCustomGame', 'name':name, 'numPlayers':numPlayers, 'sets':sets, 'cards':cards, 'bots':bots}));
}

/*
Display an error message under the "Create Custom Game" section.
*/
function customGameError(message) {
  var p = document.createElement('p');
  p.className = 'customGameError';
  p.innerHTML = message;
  document.getElementById('customGameSection').appendChild(p);
}

// boolean indicating whether this player is ready for the game to start
var playerIsReady;

function enterGameLobby(name, sets, requiredCards, forbiddenCards) {
  // hide general lobby
  document.getElementById('lobby').style.display = 'none';
  // show this game's lobby
  document.getElementById('gameLobby').style.display = 'block';
  // upon joining a game lobby, the player is not ready to start
  playerIsReady = false;
  // show game's name
  document.getElementById('gameLobbyName').innerHTML = name;
  // show game's card sets
  var gameLobbySets = document.getElementById('gameLobbySets');
  removeAllChildNodes(gameLobbySets);
  if (sets) {
    var p = document.createElement('p');
    p.innerHTML = 'Sets: ' + sets;
    gameLobbySets.appendChild(p);
  }
  // show game's required and forbidden cards
  var gameLobbyCards = document.getElementById('gameLobbyCards');
  removeAllChildNodes(gameLobbyCards);
  if (requiredCards) {
    var p = document.createElement('p');
    p.innerHTML = 'Cards: ' + requiredCards;
    gameLobbySets.appendChild(p);
  }
  if (forbiddenCards) {
    var p = document.createElement('p');
    p.innerHTML = 'Not: ' + forbiddenCards;
    gameLobbySets.appendChild(p);
  }
  // initialize ready button
  var readyButton = document.getElementById('gameLobbyReadyButton');
  readyButton.innerHTML = 'Ready';
  readyButton.onmousedown = togglePlayerIsReady;
  // initialize leave button
  document.getElementById('gameLobbyLeaveButton').onmousedown = gameLobbyLeave;
}

/*
Update the status of openings in the current game lobby.
players is an array of objects containing each opening's status
[{type:string, username:string, isReady:boolean}, ...]
*/
function updateGameLobby(players) {
  var playerList = document.getElementById('gameLobbyPlayerList');
  removeAllChildNodes(playerList);
  for (var i = 0; i < players.length; i++) {
    var player = players[i];
    var p = document.createElement('p');
    if (player.type === 'bot') {
      p.innerHTML = player.username;
    } else if (player.type === 'open') {
      p.innerHTML = '- Open -';
    } else {
      var readiness = player.isReady ? '<span class="ready">(Ready)</span>' : '<span class="notReady">(Not Ready)</span>';
      p.innerHTML = player.username + readiness;
    }
    playerList.appendChild(p);
  }
}

/*
Toggles the player's readiness and sends it to the server.
*/
function togglePlayerIsReady() {
  playerIsReady = !playerIsReady;
  document.getElementById('gameLobbyReadyButton').innerHTML = playerIsReady ? 'Not Ready' : 'Ready';
  socket.send(JSON.stringify({'type':'gameLobbyReady', 'isReady':playerIsReady}));
}

/*
Sends a request to leave the current game lobby to the server.
*/
function gameLobbyLeave() {
  socket.send(JSON.stringify({'type':'gameLobbyLeave'}));
}

// boolean indicating whether the "More" UI elements are displayed in the game
var showingMore;

function enterGame() {
  // clear log
  var logElem = document.getElementById('log');
  removeAllChildNodes(logElem);
  // clear chat
  var chatDisplay = document.getElementById('chatDisplay');
  removeAllChildNodes(chatDisplay);
  // enable chat sending
  var chatInput = document.getElementById('chatInput');
  chatInput.onkeydown = chatInputKeydown;
  var chatSendButton = document.getElementById('chatSendButton');
  chatSendButton.onmousedown = chatSend;
  // clear prompt
  endPrompt();
  // clear seaside UI
  setNativeVillageMat();
  setDurations();
  // hide lobby
  document.getElementById('lobby').style.display = 'none';
  document.getElementById('gameLobby').style.display = 'none';
  // hide "more"
  showingMore = true;
  toggleMore();
  document.getElementById('moreButton').onmousedown = toggleMore;
  document.getElementById('forfeitButton').onmousedown = forfeit;
  // show game
  document.getElementById('game').style.display = 'flex';
}

/*
Send the contents of the chat input if the return key was pressed.
*/
function chatInputKeydown(e) {
  if (e.which == 13 || e.keyCode == 13) {
    chatSend();
  }
}

/*
Sends the contents of the chat input to the server if the chat input is not
empty. Then clears the chat input.
*/
function chatSend() {
  var chatInput = document.getElementById('chatInput');
  var message = chatInput.value;
  if (message !== '') {
    socket.send(JSON.stringify({'type':'chat', 'message':message}));
    chatInput.value = '';
  }
}

/*
Appends a received chat message to chatDisplay.
*/
function receiveChat(username, message) {
  var p = document.createElement('p');
  p.innerHTML = '<span class="chatUsername">' + username + ':</span> ' + message;
  document.getElementById('chatDisplay').appendChild(p);
}

/*
Shows or hides the "More" UI elements.
*/
function toggleMore() {
  showingMore = !showingMore;
  if (showingMore) {
    document.getElementById('moreButton').innerHTML = 'Less';
    document.getElementById('forfeitButton').style.display = 'inline-block';
  } else {
    document.getElementById('moreButton').innerHTML = 'More';
    document.getElementById('forfeitButton').style.display = 'none';
  }
}

/*
Sends a request to forfeit the current game to the server.
*/
function forfeit() {
  socket.send(JSON.stringify({'type':'forfeit'}));
}

/*
Adds a "Return to Lobby" button to the game log.
*/
function endGame() {
  var button = document.createElement('button');
  button.innerHTML = 'Return to Lobby'
  button.onmousedown = function () {
    button.style.display = 'none';
    button.onmousedown = null;
    socket.send(JSON.stringify({'type':'returnToLobby'}));
  };
  document.getElementById('log').appendChild(button);
}

function executeCommand(command) {
  switch (command.command) {
    case 'loginError':
      loginError(command.message);
      break;
    case 'customGameError':
      customGameError(command.message);
      break;
    case 'enterLobby':
      enterLobby(command.username, command.gameListings, command.availableBots);
      break;
    case 'addGameListing':
      addGameListing(command.gameListing);
      break;
    case 'updateGameListing':
      updateGameListing(command.name, command.isClosed, command.numOpenings, command.numPlayers);
      break;
    case 'enterGameLobby':
      enterGameLobby(command.name, command.sets, command.requiredCards, command.forbiddenCards);
      break;
    case 'updateGameLobby':
      updateGameLobby(command.players);
      break;
    case 'enterGame':
      enterGame();
      break;
    case 'endGame':
      setWaitingOn();
      endGame();
      break;
    case 'setKingdomCards':
      setKingdomCards(command.cards);
      break;
    case 'setBasicCards':
      setBasicCards(command.cards);
      break;
    case 'setPileSizes':
      setPileSizes(command.piles);
      break;
    case 'setEmbargoTokens':
      setEmbargoTokens(command.card, command.numTokens);
      break;
    case 'setActions':
      setActions(command.actions);
      break;
    case 'setBuys':
      setBuys(command.buys);
      break;
    case 'setCoins':
      setCoins(command.coins);
      break;
    case 'setDrawSize':
      setDeckSize(command.size, 'drawStatus');
      break;
    case 'setDiscardSize':
      setDeckSize(command.size, 'discardStatus');
      break;
    case 'setNativeVillageMat':
      setNativeVillageMat(command.contents);
      break;
    case 'setDurations':
      setDurations(command.contents);
      break;
    case 'setHand':
      setHand(command.hand);
      break;
    case 'setCardCosts':
      setCardCosts(command.costs);
      break;
    case 'setWaitingOn':
      setWaitingOn(command.player);
      break;
    case 'allowHurryUp':
      allowHurryUp();
      break;
    case 'promptChooseFromSupply':
      setWaitingOn();
      promptChooseFromSupply(command.choices, command.message, command.promptType, command.isMandatory, command.noneMessage);
      break;
    case 'promptChooseFromHand':
      setWaitingOn();
      promptChooseFromHand(command.choices, command.message, command.promptType, command.isMandatory, command.noneMessage);
      break;
    case 'promptDiscardNumber':
      setWaitingOn();
      promptDiscardNumber(command.number, command.isMandatory, command.cause, command.promptType, command.destination);
      break;
    case 'promptMultipleChoice':
      setWaitingOn();
      promptMultipleChoice(command.message, command.promptType, command.choices, command.disabled);
      break;
    case 'message':
      message(command.message);
      break;
    case 'chat':
      receiveChat(command.username, command.message);
      break;
    default:
      // should never receive an uniplemented command
  }
}

/*
Receives a string from the server, executing it as a list of commands.
*/
function receiveServerCommands(text) {
  // print all commands for debugging
  console.log(text);
  commands = JSON.parse(text);
  // remember if the user was scrolled to the bottom of the game screen
  var lockToBottomOfGame = false;
  var gameDiv = document.getElementById('game');
  if (gameDiv.style.display == 'flex' && gameDiv.clientHeight - window.innerHeight == window.pageYOffset) {
    lockToBottomOfGame = true;
  }
  // execute commands
  for (var i = 0; i < commands.length; i++) {
    executeCommand(commands[i]);
  }
  // if the user was scrolled to the bottom of the game screen, scroll there again
  if (lockToBottomOfGame && gameDiv.style.display == 'flex') {
    window.scrollTo(0, document.getElementById('game').clientHeight);
  }
}

/*
Send a response to a prompt back to the server.
*/
function sendResponse(response) {
  socket.send(JSON.stringify({'type':'response', 'response':response}));
}

// boolean indicating whether a login request has been submitted and not
// accepted or rejected yet
var loginPending = false;
// boolean indicating whether the players is trying to create a new login
var creatingNewLogin = false;

/*
Send new login request to the server unless a login request is already pending.
*/
function sendLogin() {
  if (!loginPending) {
    // remove previous error messages
    var loginDiv = document.getElementById('login');
    var errorMessages = loginDiv.getElementsByClassName('loginError');
    for (var i = 0; i < errorMessages.length; i++) {
      loginDiv.removeChild(errorMessages[i]);
    }
    // if sending a new password, verify that password fields match
    if (creatingNewLogin) {
      if (document.getElementById('password').value !== document.getElementById('confirmPassword').value) {
        loginError('Confirm Password field does not match.');
        return;
      } else if (document.getElementById('password').value === '') {
        loginError('Password field is empty.');
        return;
      }
    }
    // disable new login requests until this one is processed
    loginPending = true;
    // send the username and hashed password
    var loginRequest = {};
    loginRequest.type = 'login';
    loginRequest.username = document.getElementById('username').value;
    var passwordRaw = document.getElementById('password').value;
    if (passwordRaw != '') {
      // hash with a static salt (TODO in the future, this might change to a server-supplied salt)
      var hash = dcodeIO.bcrypt.hashSync(passwordRaw, '$2a$10$OFkk3vKhcZqaYEHnNu0Wc.');
      loginRequest.password = hash;
    }
    if (creatingNewLogin) {
      loginRequest.newLogin = true;
    }
    socket.send(JSON.stringify(loginRequest));
  }
}

/*
Submit a login request if the return key was pressed in a text field on the
login page.
*/
function loginKeyDown(e) {
  if (e.which == 13 || e.keyCode == 13) {
    sendLogin();
  }
}

/*
Toggles the login mode for creating a new login.
Adds or removes "Confirm Password" field to the login page.
*/
function toggleCreatingNewLogin() {
  creatingNewLogin = !creatingNewLogin;
  var login  = document.getElementById('login');
  var newLogin = document.getElementById('newLogin');
  if (creatingNewLogin) {
    var label = document.createElement('label');
    label.id = 'confirmPasswordLabel';
    label.innerHTML = 'Confirm Password';
    login.insertBefore(label, document.getElementById('loginButtons'));
    var input = document.createElement('input');
    input.id = 'confirmPassword';
    input.type = 'password';
    input.name = 'confirmPassword';
    input.onkeydown = loginKeyDown;
    login.insertBefore(input, document.getElementById('loginButtons'));
    newLogin.innerHTML = 'Cancel';
  } else {
    login.removeChild(document.getElementById('confirmPasswordLabel'));
    login.removeChild(document.getElementById('confirmPassword'));
    newLogin.innerHTML = 'New user';
  }
}

/*
Display an error message on the login page.
*/
function loginError(message) {
  var loginDiv = document.getElementById('login');
  // append error message
  var p = document.createElement('p');
  p.className = 'loginError';
  p.innerHTML = message;
  loginDiv.appendChild(p);
  loginPending = false;
}

function init() {

  // hide unused elements
  document.getElementById('lobby').style.display = 'none';
  document.getElementById('gameLobby').style.display = 'none';
  document.getElementById('game').style.display = 'none';
  document.getElementById('waitingOn').style.display = 'none';
  document.getElementById('prompt').style.display = 'none';

  /* connect to server websocket */
  var host = location.host;
  if (host === '') {
    host = 'localhost';
  }
  host = host.split(':')[0];
  socket = new WebSocket('ws://' + host + ':8081');
  socket.onopen = function() {
    // connected
  };
  socket.onmessage = function(e) {
    receiveServerCommands(e.data);
  }
  socket.onclose = function() {
    // TODO announce that the player has been disconnected from the server
  };

  // setup unique controls
  document.getElementById('hurryUpButton').onmousedown = hurryUp;

  // initialize standard lobby options
  document.getElementById('automatch2Checkbox').checked = true;
  document.getElementById('automatch3Checkbox').checked = true;
  document.getElementById('automatch4Checkbox').checked = true;
  document.getElementById('baseCheckbox').checked = true;

  document.getElementById('newLogin').onmousedown = toggleCreatingNewLogin;
  // send login info on button press, or on pressing return in a text field
  document.getElementById('loginButton').onmousedown = sendLogin;
  document.getElementById('username').onkeydown = loginKeyDown;
  document.getElementById('password').onkeydown = loginKeyDown;

  // autofocus on username field
  document.getElementById('username').focus();
}

window.onload = function() {
  init();
};

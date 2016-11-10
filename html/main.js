// websocket connection to server
var socket;

// a map from card name to all of the information needed to display a popup describing that card.
// the type is a string like 'Action', 'Action-Attack', etc.
// cost is always the base cost (not affected by cards like Bridge)
// 'card name' -> {className:'action-victory-etc.', description:['line 1', 'line 2', ...], type:'Human-Readable-Type', cost:'$5'}
var cardDescriptions = {};

// a map from the name of a pile to its UI elements
// 'pile name' -> {pile:<div>, cost:<p>, pileSize:<p>, cross:<div>, plus:<div>, img:<img>}
var supplyPiles = {};

// a map from the name of a card to the div displaying its stack in the hand
// 'card name' -> <div>
var handCardElems = {};
// an array containing both the name of the card stack at that position in the hand and the size of that stack
// [{name:'card name', count:#}, {name:'card name', count:integer}, ...]
var handCounts = [];

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
  // remember if the user was scrolled to the bottom of the game screen
  var lockToBottomOfGame = isOnBottomOfGame();
  // get description info
  var description = cardDescriptions[cardName];
  // get the card name's background color
  var className = description.className;
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
  // if the user was scrolled to the bottom of the game screen, scroll there again
  if (lockToBottomOfGame && document.getElementById('game').style.display == 'flex') {
    scrollToBottomOfGame();
  }
}

var logIndents = [];

/*
Add the given text to the game log at the appropriate intentation level.
*/
function message(text, indent) {
  var p = document.createElement('p');
  p.innerHTML = text;
  if (indent == 0) {
    document.getElementById('log').appendChild(p);
    return;
  }
  while (logIndents.length - 1 < indent) {
    var indentDiv = document.createElement('div');
    indentDiv.className = 'logIndent';
    logIndents[logIndents.length - 1].appendChild(indentDiv);
    logIndents.push(indentDiv);
  }
  while (logIndents.length - 1 > indent) {
    logIndents.pop();
  }
  logIndents[indent].appendChild(p);
}

function newTurnMessage(text) {
  var indentDiv = document.createElement('div');
  indentDiv.className = 'turnIndent';
  var p = document.createElement('p');
  p.innerHTML = text;
  indentDiv.appendChild(p);
  document.getElementById('log').appendChild(indentDiv);
  logIndents = [indentDiv];
}

/*
Returns the location of the card art image for the given card.
e.g. 'Council Room' -> 'card_art/council_room.jpg'
*/
function cardArtSrc(cardName) {
  // remove " (1st ed.)" because first edition cards have the same card art
  // as their second edition counterparts
  var suffix = " (1st ed.)";
  if (cardName.endsWith(suffix)) {
    cardName = cardName.substring(0, cardName.length - suffix.length);
  }
  cardName = cardName.toLowerCase();
  cardName = cardName.split(' ').join('_');
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
  var className = cardDescriptions[cardName].className;
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

function createNonSupplyPileCross() {
  return createDivContainingImage('cross', 'cross_gray.png');
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

function setSupply(supply) {
  setCardDescriptions(supply.cardDescriptions);
  supplyPiles = {};
  setKingdomPiles(supply.kingdomPiles);
  setBasicPiles(supply.basicPiles);
  setNonSupplyPiles(supply.nonSupplyPiles);
  setPrizeCards(supply.prizeCards);
}

function setCardDescriptions(cardDescriptionsArray) {
  cardDescriptions = {};
  for (var i = 0; i < cardDescriptionsArray.length; i++) {
    var descriptionEntry = cardDescriptionsArray[i];
    cardDescriptions[descriptionEntry.name] = {
      className: descriptionEntry.className,
      type: descriptionEntry.type,
      cost: descriptionEntry.cost,
      description: descriptionEntry.description
    }
  }
}

/*
Takes an array of objects containing the names and top cards of the kingdom
piles.
[{id:'pile name', topCard:'card name'}, ...]
Initializes the UI elements for each pile.
*/
function setKingdomPiles(piles) {
  var kingdom = document.getElementById('kingdom');
  // remove previous kingdom card UI elements
  removeAllChildNodes(kingdom);
  // for each kingdom pile
  for (var i = 0; i < piles.length; i++) {
    var cardName = piles[i].topCard;
    var card = cardDescriptions[cardName];
    // add a pile for this card
    var kingdomPile = document.createElement('div');
    kingdomPile.className = 'kingdomPile';
    registerPopup(kingdomPile, cardName);
    // set name
    var nameDiv = document.createElement('div');
    nameDiv.className = 'name';
    var nameParagraph = document.createElement('p');
    nameParagraph.className = card.className;
    nameParagraph.innerHTML = cardName;
    nameDiv.appendChild(nameParagraph);
    if (piles[i].isBane || piles[i].mixedPileName) {
      var subtitleParagraph = document.createElement('p');
      subtitleParagraph.className = 'subtitle';
      if (piles[i].isBane) {
        subtitleParagraph.innerHTML = '(Bane)';
      } else {
        subtitleParagraph.innerHTML = '(' + piles[i].mixedPileName + ')';
      }
      nameDiv.appendChild(subtitleParagraph);
    }
    kingdomPile.appendChild(nameDiv);
    // set status (cost & pile size)
    var status = document.createElement('div');
    status.className = 'status';
    // set cost
    var costDiv = document.createElement('div');
    costDiv.className = 'cost';
    var costParagraph = document.createElement('p');
    costParagraph.innerHTML = card.cost;
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
    img.src = cardArtSrc(cardName);
    cardArt.appendChild(img);
    kingdomPile.appendChild(cardArt);
    kingdom.appendChild(kingdomPile);
    supplyPiles[piles[i].id] = {'pile':kingdomPile, 'name':nameParagraph, 'cost':costParagraph, 'pileSize':pileSizeParagraph, 'cross':cross, 'plus':plus, 'img':img};
  }
}

function setNonSupplyPiles(piles) {
  var nonSupply = document.getElementById('nonSupply');
  // remove previous non-supply UI elements
  removeAllChildNodes(nonSupply);
  // for each non-supply pile
  for (var i = 0; i < piles.length; i++) {
    var cardName = piles[i].topCard;
    var card = cardDescriptions[cardName];
    // add a pile for this card
    var pile = document.createElement('div');
    pile.className = 'kingdomPile';
    registerPopup(pile, cardName);
    // set name
    var nameDiv = document.createElement('div');
    nameDiv.className = 'name';
    var nameParagraph = document.createElement('p');
    nameParagraph.innerHTML = '<span class="' + card.className + '">' + cardName + '</span>';
    nameDiv.appendChild(nameParagraph);
    var subtitleParagraph = document.createElement('p');
    subtitleParagraph.className = 'subtitle';
    subtitleParagraph.innerHTML = '(Non-Supply)';
    nameDiv.appendChild(subtitleParagraph);
    pile.appendChild(nameDiv);
    // set status (pile size)
    var status = document.createElement('div');
    status.className = 'status';
    // set pile size (initially unknown)
    var pileSizeDiv = document.createElement('div');
    pileSizeDiv.className = 'pileSize';
    var pileSizeParagraph = document.createElement('p');
    pileSizeParagraph.innerHTML = '(?)';
    pileSizeDiv.appendChild(pileSizeParagraph);
    status.appendChild(pileSizeDiv);
    pile.appendChild(status);
    // add visuals for this pile
    var cardArt = document.createElement('div');
    cardArt.className = 'cardArt';
    // add cross image to indicate when this pile is empty (initially hidden)
    var cross = createNonSupplyPileCross();
    cross.style.display = 'none';
    cardArt.appendChild(cross);
    // add plus image to indicate when this pile can be gained (initially hidden)
    var plus = createPilePlus();
    plus.style.display = 'none';
    cardArt.appendChild(plus);
    // add card art
    var img = document.createElement('img');
    img.src = cardArtSrc(cardName);
    cardArt.appendChild(img);
    pile.appendChild(cardArt);
    nonSupply.appendChild(pile);
    supplyPiles[piles[i].id] = {'pile':pile, 'name':nameParagraph, 'pileSize':pileSizeParagraph, 'cross':cross, 'plus':plus, 'img':img};
  }
}

function setPrizeCards(cards) {
  var prizes = document.getElementById('prizes');
  // remove previous prize card UI elements
  removeAllChildNodes(prizes);
  // if cards is undefined, there are no prize cards
  if (!cards) {
    return;
  }
  // for each prize card
  for (var i = 0; i < cards.length; i++) {
    var cardName = cards[i];
    var card = cardDescriptions[cardName];
    // add a pile for this card
    var pile = document.createElement('div');
    pile.className = 'kingdomPile';
    registerPopup(pile, cardName);
    // set name
    var nameDiv = document.createElement('div');
    nameDiv.className = 'name';
    var nameParagraph = document.createElement('p');
    nameParagraph.className = card.className;
    nameParagraph.innerHTML = cardName;
    nameDiv.appendChild(nameParagraph);
    var subtitleParagraph = document.createElement('p');
    subtitleParagraph.className = 'subtitle';
    subtitleParagraph.innerHTML = '(Prize)';
    nameDiv.appendChild(subtitleParagraph);
    pile.appendChild(nameDiv);
    // add visuals for this pile
    var cardArt = document.createElement('div');
    cardArt.className = 'cardArt';
    // add cross image to indicate when this pile is empty (initially hidden)
    var cross = createNonSupplyPileCross();
    cross.style.display = 'none';
    cardArt.appendChild(cross);
    // add plus image to indicate when this pile can be gained (initially hidden)
    var plus = createPilePlus();
    plus.style.display = 'none';
    cardArt.appendChild(plus);
    // add card art
    var img = document.createElement('img');
    img.src = cardArtSrc(cardName);
    cardArt.appendChild(img);
    pile.appendChild(cardArt);
    prizes.appendChild(pile);
    supplyPiles[cardName] = {'pile':pile, 'cross':cross, 'plus':plus, 'img':img};
  }
}

function setPrizeCardRemoved(cardName) {
  // make the image partially transparent
  supplyPiles[cardName].img.style.opacity = '0.3';
  // show the cross image
  supplyPiles[cardName].cross.style.display = 'block';
}

/*
Takes an array of objects containing the names and top cards of the basic
piles.
[{id:'pile name', topCard:'card name'}, ...]
Initializes the UI elements for each pile.
*/
function setBasicPiles(piles) {
  var basic = document.getElementById('basic');
  // remove previous basic card UI elements
  removeAllChildNodes(basic);
  // for each basic card
  for (var i = 0; i < piles.length; i++) {
    var cardName = piles[i].topCard;
    var card = cardDescriptions[cardName];
    // add a pile for this card
    var basicPile = document.createElement('div');
    basicPile.className = 'basicPile';
    registerPopup(basicPile, cardName);
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
    img.src = cardArtSrc(cardName);
    cardArt.appendChild(img);
    basicPile.appendChild(cardArt);
    // add status (cost & pile size)
    var status = document.createElement('div');
    status.className = 'status';
    var p = document.createElement('p');
    // add cost
    var cost = document.createElement('span');
    cost.className = 'cost';
    cost.innerHTML = card.cost;
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
    supplyPiles[piles[i].id] = {'pile':basicPile, 'cross':cross, 'plus':plus, 'img':img, 'cost':cost, 'pileSize':pileSize};
  }
}

/*
Takes an map of pile names to sizes.
'pile name' -> integer
Changes the given pile sizes in the UI.
*/
function setPileSizes(sizes) {
  // for each changed pile
  for (var id in sizes) {
    if (sizes.hasOwnProperty(id)) {
      // changed the displayed number
      var size = sizes[id];
      supplyPiles[id].pileSize.innerHTML = '(' + size.toString() + ')';
      // if the pile is empty
      if (size == 0) {
        // make the image partially transparent
        supplyPiles[id].img.style.opacity = '0.3';
        // show the cross image
        supplyPiles[id].cross.style.display = 'block';
      }
    }
  }
}

function setTopCard(id, topCard) {
  pile = supplyPiles[id];
  if (topCard) {
    pile.name.innerHTML = topCard;
    pile.name.className = cardDescriptions[topCard].className;
    pile.img.src = cardArtSrc(topCard);
    registerPopup(pile.pile, topCard);
  } else {
    pile.name.innerHTML = '';
  }
}

function setEmbargoTokens(pileId, numTokens) {
  var pileTokensDiv = getPileTokensDiv(pileId);
  // remove previous embargo tokens
  while (pileTokensDiv.getElementsByClassName('embargoTokens').length != 0) {
    pileTokensDiv.removeChild(pileTokensDiv.getElementsByClassName('embargoTokens')[0]);
  }
  // add new embargo tokens
  var embargoTokensLabel = document.createElement('label');
  embargoTokensLabel.className = 'embargoTokens';
  embargoTokensLabel.innerHTML = numTokens.toString();
  pileTokensDiv.appendChild(embargoTokensLabel);
}

function setTradeRouteToken(card, hasToken) {
  var pileTokensDiv = getPileTokensDiv(card);
  // remove previous trade route token
  while (pileTokensDiv.getElementsByClassName('tradeRouteToken').length != 0) {
    pileTokensDiv.removeChild(pileTokensDiv.getElementsByClassName('tradeRouteToken')[0]);
  }
  if (hasToken) {
    // add new trade route token
    var tradeRouteTokensLabel = document.createElement('label');
    tradeRouteTokensLabel.className = 'tradeRouteToken';
    tradeRouteTokensLabel.innerHTML = '1';
    pileTokensDiv.appendChild(tradeRouteTokensLabel);
  }
}

function getPileTokensDiv(id) {
  var pile = supplyPiles[id].pile;
  var cardArtDiv = pile.getElementsByClassName('cardArt')[0];
  var pileTokensDiv;
  if (cardArtDiv.getElementsByClassName('pileTokens').length != 0) {
    // return the existing pile tokens div
    pileTokensDiv = cardArtDiv.getElementsByClassName('pileTokens')[0];
  } else {
    // create a pile tokens div if none exists
    pileTokensDiv = document.createElement('div');
    pileTokensDiv.className = 'pileTokens';
    cardArtDiv.appendChild(pileTokensDiv);
  }
  return pileTokensDiv;
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

function setCoins(coins, notAutoplayingTreasures) {
  if (notAutoplayingTreasures) {
    coins = '<span class="notAutoplayingTreasures">' + coins + '</span>';
  }
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
  // add multiple oneCard elements
  for (var i = 0; i < size; i++) {
    var div = document.createElement('div');
    div.className = 'oneCard';
    pile.appendChild(div);
  }
}

function setArea(id, name, contents) {
  var area = document.getElementById(id);
  removeAllChildNodes(area);
  if (contents) {
    var p = document.createElement('p');
    p.innerHTML = name + ': ' + contents;
    area.appendChild(p);
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
      supplyPiles[cardName].cost.innerHTML = costs[cardName];
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
  for (var cardName in supplyPiles) {
    if (supplyPiles.hasOwnProperty(cardName)) {
      // hide the plus
      supplyPiles[cardName].plus.style.display = 'none';
      // remove mousedown handlers
      var pile = supplyPiles[cardName].pile;
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

function promptBuyPhase(canBuy, hasUnplayedTreasure, canPlay, coinTokens) {
  setPromptMessage('Buy Phase: Buy cards from the supply, or play treasures from your hand.', 'buyPrompt');
  // "play all treasures" button
  if (hasUnplayedTreasure) {
    var playAllTreasuresButton = addPromptButton('Play all treasures');
    sendResponseOnMouseDown(playAllTreasuresButton, JSON.stringify({'responseType':'playAllTreasures'}));
  }
  // "spend coin tokens" button
  if (coinTokens) {
    var spendCoinTokensButton = addPromptButton('Spend coin tokens');
    spendCoinTokensButton.onmousedown = function () {
      switchSpendCoinTokens(hasUnplayedTreasure, coinTokens);
    };
  }
  // "end turn" button
  var endTurnButton = addPromptButton('End turn');
  sendResponseOnMouseDown(endTurnButton, JSON.stringify({'responseType':'endTurn'}));
  // enable buying cards
  for (var i = 0; i < canBuy.length; i++) {
    // show plus icon
    supplyPiles[canBuy[i]].plus.style.display = 'block';
    // add visual flair when hovering over this pile
    var pile = supplyPiles[canBuy[i]].pile;
    pile.className += ' clickable';
    // choose on mouse down
    sendResponseOnMouseDown(pile, JSON.stringify({'responseType':'buy', 'toBuy':canBuy[i]}));
  }
  // enable playing treasures
  for (var i = 0; i < canPlay.length; i++) {
    // add visual flair when hovering over stack
    var stack = handCardElems[canPlay[i]];
    stack.className += ' clickable';
    sendResponseOnMouseDown(stack, JSON.stringify({'responseType':'play', 'toPlay':canPlay[i]}));
  }
  displayPrompt();
}

function switchSpendCoinTokens(hasUnplayedTreasure, coinTokens) {
  setPromptMessage('Buy Phase: Spend how many coin tokens?', 'buyPrompt');
  for (var i = 1; i <= coinTokens; i++) {
    var button = addPromptButton(i.toString());
    sendResponseOnMouseDown(button, JSON.stringify({'responseType':'spendCoinTokens', 'toSpend':i}));
  }
  var cancelButton = addPromptButton('Cancel');
  cancelButton.onmousedown = function () {
    switchCancelCoinTokens(hasUnplayedTreasure, coinTokens);
  }
}

function switchCancelCoinTokens(hasUnplayedTreasure, coinTokens) {
  setPromptMessage('Buy Phase: Buy cards from the supply, or play treasures from your hand.', 'buyPrompt');
  // "play all treasures" button
  if (hasUnplayedTreasure) {
    var playAllTreasuresButton = addPromptButton('Play all treasures');
    sendResponseOnMouseDown(playAllTreasuresButton, JSON.stringify({'responseType':'playAllTreasures'}));
  }
  // "spend coin tokens" button
  if (coinTokens) {
    var spendCoinTokensButton = addPromptButton('Spend coin tokens');
    spendCoinTokensButton.onmousedown = function () {
      switchSpendCoinTokens(hasUnplayedTreasure, coinTokens);
    };
  }
  // "end turn" button
  var endTurnButton = addPromptButton('End turn');
  sendResponseOnMouseDown(endTurnButton, JSON.stringify({'responseType':'endTurn'}));
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
    supplyPiles[choices[i]].plus.style.display = 'block';
    // add visual flair when hovering over this pile
    var pile = supplyPiles[choices[i]].pile;
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

function promptChooseFromHandOrMultipleChoice(handChoices, message, promptType, multipleChoices) {
  setPromptMessage(message, promptType);
  // for multiple choice responses, send the number
  for (var i = 0; i < multipleChoices.length; i++) {
    sendResponseOnMouseDown(addPromptButton(multipleChoices[i]), i.toString());
  }
  // for hand responses, send the card name
  for (var i = 0; i < handChoices.length; i++) {
    var stack = handCardElems[handChoices[i]];
    stack.className += ' clickable';
    sendResponseOnMouseDown(stack, handChoices[i]);
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

/*
Takes this player's username, the current game listings, and the available bot names.
gameListings is an array of objects containing all of the information to display
in each row.
[{name:string, numOpenings:integer, numPlayers:integer, sets:string, requiredCards:string, forbiddenCards:string}, ...]
*/
function enterLobby(username, gameListings, availableBots) {
  availableBotNames = availableBots;
  // enable "Quick Game" option
  var quickGameBotSelect = document.getElementById('quickGameBotSelect');
  removeAllChildNodes(quickGameBotSelect);
  addAvailableBotOptions(quickGameBotSelect);
  document.getElementById('quickGameButton').onmousedown = quickGame;
  // show lobby
  showPage('lobby');
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

function quickGame() {
  var quickGameBotSelect = document.getElementById('quickGameBotSelect');
  var bot = quickGameBotSelect.options[quickGameBotSelect.selectedIndex].value;
  socket.send(JSON.stringify({'type':'quickGame', 'bot':bot}));
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
  // create a new drop down meny with all of the available bots as options
  var select = document.createElement('select');
  addAvailableBotOptions(select);
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

function addAvailableBotOptions(select) {
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
  if (document.getElementById('baseFirstEditionCheckbox').checked) {
    sets.push('Base (1st Ed.)');
  }
  if (document.getElementById('intrigueCheckbox').checked) {
    sets.push('Intrigue');
  }
  if (document.getElementById('intrigueFirstEditionCheckbox').checked) {
    sets.push('Intrigue (1st Ed.)');
  }
  if (document.getElementById('seasideCheckbox').checked) {
    sets.push('Seaside');
  }
  if (document.getElementById('prosperityCheckbox').checked) {
    sets.push('Prosperity');
  }
  if (document.getElementById('cornucopiaCheckbox').checked) {
    sets.push('Cornucopia');
  }
  if (document.getElementById('hinterlandsCheckbox').checked) {
    sets.push('Hinterlands');
  }
  if (document.getElementById('darkAgesCheckbox').checked) {
    sets.push('Dark Ages');
  }
  if (document.getElementById('guildsCheckbox').checked) {
    sets.push('Guilds');
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
  // show this game's lobby
  showPage('gameLobby');
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
  // clear trash
  setTrash();
  // clear seaside UI
  setArea('nativeVillageMat');
  setArea('islandMat');
  setArea('pirateShipMat');
  setArea('durations');
  setArea('inPlay');
  // clear other UI
  setArea('coinTokens');
  setArea('victoryTokenMat');
  setArea('tradeRouteMat');
  // clear prizes
  setPrizeCards([]);
  // hide popup
  document.getElementById('cardPopupContainer').style.display = 'none';
  // hide "more"
  showingMore = true;
  toggleMore();
  document.getElementById('moreButton').onmousedown = toggleMore;
  document.getElementById('forfeitButton').onmousedown = forfeit;
  // show game
  showPage('game');
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
    document.getElementById('moreContents').style.display = 'block';
  } else {
    document.getElementById('moreButton').innerHTML = 'More';
    document.getElementById('moreContents').style.display = 'none';
  }
}

function setTrash(contents) {
  var trash = document.getElementById('trash');
  if (contents && contents != '') {
    trash.innerHTML = 'Trash: ' + contents;
  } else {
    trash.innerHTML = 'Trash: (empty)';
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
    case 'loginAccepted':
      loginAccepted(command.username);
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
    case 'setSupply':
      setSupply(command.supply);
      break;
    case 'setPrizeCardRemoved':
      setPrizeCardRemoved(command.cardName);
      break;
    case 'addCardDescriptions':
      addCardDescriptions(command.cards);
      break;
    case 'setBasicCards':
      setBasicCards(command.cards);
      break;
    case 'setPileSizes':
      setPileSizes(command.pileSizes);
      break;
    case 'setTopCard':
      setTopCard(command.id, command.topCard);
      break;
    case 'setEmbargoTokens':
      setEmbargoTokens(command.pileId, command.numTokens);
      break;
    case 'setTradeRouteToken':
      setTradeRouteToken(command.card, command.hasToken);
      break;
    case 'setActions':
      setActions(command.actions);
      break;
    case 'setBuys':
      setBuys(command.buys);
      break;
    case 'setCoins':
      setCoins(command.coins, command.notAutoplayingTreasures);
      break;
    case 'setDrawSize':
      setDeckSize(command.size, 'drawStatus');
      break;
    case 'setDiscardSize':
      setDeckSize(command.size, 'discardStatus');
      break;
    case 'setCoinTokens':
      setArea('coinTokens', 'Coin Tokens', command.contents);
      break;
    case 'setVictoryTokenMat':
      setArea('victoryTokenMat', 'Victory Tokens', command.contents);
      break;
    case 'setTradeRouteMat':
      setArea('tradeRouteMat', 'Trade Route', command.contents);
      break;
    case 'setNativeVillageMat':
      setArea('nativeVillageMat', 'Native Village', command.contents);
      break;
    case 'setIslandMat':
      setArea('islandMat', 'Island', command.contents);
      break;
    case 'setPirateShipMat':
      setArea('pirateShipMat', 'Pirate Ship', command.contents);
      break;
    case 'setDurations':
      setArea('durations', 'Duration', command.contents);
      break;
    case 'setInPlay':
      setArea('inPlay', 'In Play', command.contents);
      break;
    case 'setTrash':
      setTrash(command.contents);
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
    case 'promptBuyPhase':
      setWaitingOn();
      promptBuyPhase(command.canBuy, command.hasUnplayedTreasure, command.canPlay, command.coinTokens);
      break;
    case 'promptChooseFromSupply':
      setWaitingOn();
      promptChooseFromSupply(command.choices, command.message, command.promptType, command.isMandatory, command.noneMessage);
      break;
    case 'promptChooseFromHand':
      setWaitingOn();
      promptChooseFromHand(command.choices, command.message, command.promptType, command.isMandatory, command.noneMessage);
      break;
    case 'promptChooseFromHandOrMultipleChoice':
      setWaitingOn();
      promptChooseFromHandOrMultipleChoice(command.handChoices, command.message, command.promptType, command.multipleChoices);
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
      message(command.text, command.indent);
      break;
    case 'newTurnMessage':
      newTurnMessage(command.text);
      break;
    case 'chat':
      receiveChat(command.username, command.message);
      break;
    default:
      // should never receive an uniplemented command
  }
}

function isOnBottomOfGame() {
  var gameDiv = document.getElementById('game');
  // if the player is scrolled to the exact bottom, or if the game is to short to have a scroll bar
  return (gameDiv.style.display == 'flex' && (gameDiv.clientHeight - window.innerHeight == window.pageYOffset || document.getElementById('gameColumns').clientHeight < document.getElementById('game').clientHeight));
}

function scrollToBottomOfGame() {
  // over-scroll just to be safe
  window.scrollTo(0, document.getElementById('game').clientHeight);
}

/*
Receives a string from the server, executing it as a list of commands.
*/
function receiveServerCommands(text) {
  commands = JSON.parse(text);
  // print all commands for debugging
  console.log(JSON.stringify(commands, null, 2));
  // remember if the user was scrolled to the bottom of the game screen
  var lockToBottomOfGame = isOnBottomOfGame();
  // execute commands
  for (var i = 0; i < commands.length; i++) {
    executeCommand(commands[i]);
  }
  // if the user was scrolled to the bottom of the game screen, scroll there again
  if (lockToBottomOfGame && document.getElementById('game').style.display == 'flex') {
    scrollToBottomOfGame();
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

function changeLogin() {
  // if the user left automatch on, turn it off
  var automatchCheckbox = document.getElementById('automatchCheckbox');
  if (automatchCheckbox.checked) {
    automatchCheckbox.checked = false;
    automatchSet();
  }

  // go from lobby to login
  showPage('loginContainer');

  document.getElementById('newLogin').onmousedown = toggleCreatingNewLogin;
  // send login info on button press, or on pressing return in a text field
  document.getElementById('loginButton').onmousedown = sendLogin;
  document.getElementById('username').onkeydown = loginKeyDown;
  document.getElementById('password').onkeydown = loginKeyDown;

  // autofocus on username field
  document.getElementById('username').focus();
}

function loginAccepted(username) {
  loginPending = false;
  // update username
  document.getElementById('lobbyUsernameText').innerHTML = username;
  // return to lobby from login
  showPage('lobby');
}

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

var pageDisplays = {'loginContainer':'flex', 'lobby':'block', 'gameLobby':'block', 'game':'flex', 'lostConnection':'flex'};
function showPage(toShow) {
  for (var pageId in pageDisplays) {
    if (pageDisplays.hasOwnProperty(pageId)) {
      var page = document.getElementById(pageId);
      if (pageId === toShow) {
        page.style.display = pageDisplays[pageId];
      } else {
        page.style.display = 'none';
      }
    }
  }
}

function init() {

  // show lobby
  showPage('lobby');
  // hide unused elements
  document.getElementById('cardPopupContainer').style.display = 'none';
  document.getElementById('waitingOn').style.display = 'none';
  document.getElementById('prompt').style.display = 'none';

  /* connect to server websocket */
  socket = new WebSocket('ws://' + location.host);
  socket.onopen = function() {
    // connected
  };
  socket.onmessage = function(e) {
    receiveServerCommands(e.data);
  }
  socket.onclose = function() {
    showPage('lostConnection');
  };

  // setup unique controls
  document.getElementById('hurryUpButton').onmousedown = hurryUp;

  // initialize standard lobby options
  document.getElementById('automatch2Checkbox').checked = true;
  document.getElementById('automatch3Checkbox').checked = true;
  document.getElementById('automatch4Checkbox').checked = true;
  document.getElementById('baseCheckbox').checked = true;
  document.getElementById('intrigueCheckbox').checked = true;
  document.getElementById('seasideCheckbox').checked = true;
  document.getElementById('prosperityCheckbox').checked = true;
  document.getElementById('cornucopiaCheckbox').checked = true;
  document.getElementById('hinterlandsCheckbox').checked = true;
  document.getElementById('darkAgesCheckbox').checked = true;
  document.getElementById('guildsCheckbox').checked = true;

  // set up lobby log in button
  document.getElementById('changeLoginButton').onclick = changeLogin;

}

window.onload = function() {
  init();
};

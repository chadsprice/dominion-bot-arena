let socket;

// {'Militia': {highlightType:'action', type: 'Action-Attack', description:['+$2', 'Each other player discards...'], cost:'$4'}}
let cardInfo = {};

let piles = {};

let logIndents = [];

let draw;
let discard;
let hand;
let inPlay;

// [{card: 'Copper', count: 3}, {card: 'Estate', count: 2}]
let handCounts;

let coins;
let isAutoplayingTreasures;

// the timout to show a popup next to the card the user is currently hovering over (null if none)
let popupTimeout = null;
const POPUP_DELAY_MILIS = 1000;

// an array representing the rows in the custom games table.
// rowDiv is the row element in the DOM.
// empty is true if the row is one of the empty rows automatically generated even if the table is empty.
// closed is true if the row is a game that has become closed and can be removed with the "remove closed" button
// [{rowDiv:<div>, empty:boolean, closed:boolean}, {rowDiv:<div>, empty:boolean, closed:boolean}, ...]
let gameListRows = [];
// {'Game 1': 0, 'Game 3': 2}
let gameRowIndex = {};

// ['BigMoney', 'BankWharf', ...]
let availableBotNames = [];

// width of images in the hand, in em
const HAND_IMG_WIDTH = 10;
// the space between cards in a stack in the hand, in em
const HAND_IMG_SPACE_LEFT = 2;
const HAND_IMG_SPACE_TOP = 1;

// dimensions of the source images, in px
const SRC_IMG_HEIGHT = 72.0;
const SRC_IMG_WIDTH = 120.0;
const SRC_IMG_ASPECT_RATIO =  SRC_IMG_HEIGHT / SRC_IMG_WIDTH;

// the number of empty rows initially in the "Custom Games" list
const INITIAL_GAME_LIST_ROWS = 5;

// 'Council Room' -> 'card_art/council_room.jpg'
function cardArtSrc(cardName) {
  // remove " (1st ed.)" because first edition cards have the same card art
  // as their second edition counterparts
  const suffix = " (1st ed.)";
  if (cardName.endsWith(suffix)) {
    cardName = cardName.substring(0, cardName.length - suffix.length);
  }
  cardName = cardName.toLowerCase();
  cardName = cardName.split(' ').join('_');
  return 'card_art/' + cardName + '.jpg';
}

function addPile(pile, card, subtitle, parentElem) {
  // the top card is the same as the pile unless specified otherwise
  if (!card) {
    card = pile;
  }

  const parent = $(parentElem);

  const info = cardInfo[card];
  const pileElem = $('<div>', {'class': 'kingdomPile'});
  registerPopup(pileElem, card);
  pileElem.append(
    // name
    $('<div>', {'class': 'name'}).append(
      $('<p>', {'class': info.highlightType, html: card}),
      // subtitle
      subtitle ?
        $('<p>', {'class': 'subtitle', html: subtitle})
      : []
    ),
    // status
    (parent.prop('id') !== 'prizes') ?
      $('<div>', {'class': 'status', }).append(
        // cost
        (parent.id !== 'nonSupply') ?
          $('<div>', {'class': 'cost'}).append(
            $('<p>', {html: info.cost})
          )
        : [],
        // pile size
        $('<div>', {'class': 'size'}).append(
          $('<p>', {html: '(?)'})
        )
      )
    : [],
    // art
    $('<div>', {'class': 'cardArt'}).append(
      // cross
      $('<div>', {'class': 'cross'}).append(
        $('<img>', {src: (parent.prop('id') === 'kingdom' || parent.prop('id') === 'basic') ? 'cross.png' : 'cross_gray.png'})
      ).hide(),
      $('<img>', {'class': 'boxBorder-'+info.highlightType, src: cardArtSrc(card)})
    )
  );
  parent.append(pileElem);

  piles[pile] = pileElem;
}

function setArea(id, name, contents) {
  const area = $('#'+id);
  area.empty();
  if (contents) {
    area.append(
      $('<p>', {html: name + ': ' + contents})
    );
  }
}

function setWaitingOn(player) {
  $('#hurryUpButton').hide();
  if (player) {
    $('#waitingOn').show();
    $('#waitingOnText').html('- Waiting on ' + player + ' -');
  } else {
    $('#waitingOn').hide();
  }
}

function allowHurryUp() {
  $('#hurryUpButton').show();
}

function hurryUp() {
  $('#hurryUpButton').hide();
  socket.send(JSON.stringify({'type': 'hurryUp'}));
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
  const quickGameBotSelect = $('#quickGameBotSelect');
  quickGameBotSelect.empty();
  addAvailableBotOptions(quickGameBotSelect);
  $('#quickGameButton').mousedown(quickGame);
  // show lobby
  showScreen('lobby');
  // set username
  $('#lobbyUsernameText').text(username);
  // clear game list
  initializeGameList();
  // add current games
  for (let i = 0; i < gameListings.length; i++) {
    addGameListing(gameListings[i]);
  }
  // enable automatch
  const automatchCheckbox = $('#automatchCheckbox');
  automatchCheckbox.prop('checked', false);
  automatchCheckbox.click(automatchSet);
  $('#automatch2Checkbox').click(automatchSettingsChanged);
  $('#automatch3Checkbox').click(automatchSettingsChanged);
  $('#automatch4Checkbox').click(automatchSettingsChanged);
  // enable "add bot" button
  $('#addBotButton').mousedown(addBot);
  // enable "create custom game" button
  $('#createCustomGameButton').mousedown(createCustomGame);
  // enable "remove closed button"
  $('#removeClosedButton').mousedown(removeClosedGameListings);
}

function quickGame() {
  const bot = $('#quickGameBotSelect option:selected').text();
  socket.send(JSON.stringify({'type': 'quickGame', 'bot': bot}));
}

function automatchSet() {
  const isOn = $('#automatchCheckbox').is(':checked');
  const toSend = {'type': 'automatch', 'isOn': isOn};
  if (isOn) {
    toSend['2'] = $('#automatch2Checkbox').is(':checked');
    toSend['3'] = $('#automatch3Checkbox').is(':checked');
    toSend['4'] = $('#automatch4Checkbox').is(':checked');
  }
  socket.send(JSON.stringify(toSend));
}

function automatchSettingsChanged() {
  // only send the new settings if automatch is on
  if ($('#automatchCheckbox').is(':checked')) {
    automatchSet();
  }
}

function getBots() {
  const bots = [];
  $('#botList select').each((__, select) => {
    bots.push($(select).find('option:selected').text());
  });
  return bots;
}

function addBot() {
  // never allow more than three bots because games have a maximum of 4 players
  // and always at least one human player
  if (getBots().length >= 3) {
    return;
  }
  const select = $('<select>');
  addAvailableBotOptions(select);
  const div = $('<div>');
  const removeButton = $('<button>', {html: 'Remove'});
  removeButton.mousedown(() => {
    removeBot(div);
  });
  div.append(
    select,
    removeButton
  );
  $('#botList').append(div);
}

function addAvailableBotOptions(select) {
  // add each available bot name as an <option> in the <select> drop down menu
  _.each(availableBotNames, (botName, i) => {
    const option = $('<option>', {value: botName, html: botName});
    if (i === 0) {
      option.prop('selected', true);
    }
    select.append(option);
  });
}

function removeBot(div) {
  div.remove();
}

function initializeGameList() {
  gameListRows = [];
  gameRowIndex = {};
  const gameList = $('#gameList');
  gameList.empty();
  // add a row of column labels
  addGameListRow({'name':'Name', 'players':'Players', 'sets':'Sets', 'cards':'Cards'}, false);
  _.times(INITIAL_GAME_LIST_ROWS, () => {
    const rowDiv = $('<div>', {'class': 'rowDiv'});
    gameList.append(rowDiv);
    gameListRows.push({'rowDiv': rowDiv, 'empty': true, 'closed': false});
  });
}

function addGameListing(gameListing) {
  const name = gameListing.name;
  const row = {};
  row.name = name;
  row.players = gameListingPlayersString(false, gameListing.numOpenings, gameListing.numPlayers);
  row.sets = gameListing.sets;
  row.requiredCards = gameListing.requiredCards;
  row.forbiddenCards = gameListing.forbiddenCards;
  // if this game is already in the list and has re-opened
  if (_.has(gameRowIndex, name)) {
    const index = gameRowIndex[name];
    const rowDiv = gameListRows[index].rowDiv;
    // re-open the game
    gameListRows[index].closed = false;
    // update any information that may have changed
    rowDiv.find('playersDiv p').html(row.players);
    rowDiv.find('setsDiv p').html(row.sets);
    setGameListingCards(rowDiv.find('cardsDiv'), row.requiredCards, row.forbiddenCards);
    rowDiv.mousedown(() => {
      joinGame(name);
    });
    rowDiv.attr('class', 'rowDiv rowDivJoinable');
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
  let className;
  if (isClosed || (numOpenings == numPlayers)) {
    className = 'gameListingClosed';
  } else {
    className = 'gameListingOpen';
  }
  let string;
  if (isClosed) {
    string = '(Closed)';
  } else {
    string = '(' + numPlayers.toString() + '/' + numOpenings.toString() + ')';
  }
  return '<span class="' + className + '">' + string + '</span>';
}

function setGameListingCards(cardsDiv, requiredCards, forbiddenCards) {
  cardsDiv.empty();
  if (requiredCards) {
    cardsDiv.append(
      $('<p>', {html: requiredCards})
    );
  }
  if (forbiddenCards) {
    cardsDiv.append(
      $('<p>', {html: 'Not: ' + forbiddenCards})
    );
  }
}

function updateGameListing(name, isClosed, numOpenings, numPlayers) {
  // ignore games that have never been sent to this client
  if (!_.has(gameRowIndex, name)) {
    return;
  }

  const index = gameRowIndex[name];
  const rowDiv = gameListRows[index].rowDiv;
  // update "Players" status in the games list
  const players = gameListingPlayersString(isClosed, numOpenings, numPlayers);
  rowDiv.find('playersDiv p').html(players);
  // update joinable status
  if (isClosed || (numPlayers == numOpenings)) {
    // game is full
    rowDiv.off('mousedown');
    rowDiv.attr('class', 'rowDiv');
  } else {
    // game is open
    rowDiv.mousedown(() => {
      joinGame(name);
    });
    rowDiv.attr('class', 'rowDiv rowDivJoinable');
  }
  // allow closed games to be removed with the "Remove Closed" button
  if (isClosed) {
    gameListRows[index].closed = true;
  }
}

/*
Adds a row to the "Custom Games" list.
Can be used to add a text-only row by passing isGame=false and having a string
in row.cards instead of having row.requiredCards and row.forbiddenCards
*/
function addGameListRow(row, isGame) {
  const gameList = $('#gameList');
  const rowDiv = $('<div>', {'class': 'rowDiv'}).append(
    // set name
    $('<div>', {'class': 'nameDiv'}).append(
      $('<p>', {html: row.name})
    ),
    // set players status
    $('<div>', {'class': 'playersDiv'}).append(
      $('<p>', {html: row.players})
    ),
    // set card sets
    $('<div>', {'class': 'setsDiv'}).append(
      $('<p>', {html: row.sets})
    )
  );
  // set required and forbidden cards
  const cardsDiv = $('<div>', {'class': 'cardsDiv'});
  if (row.cards) {
    // use an explicit string in the cards row
    cardsDiv.append(
      $('<p>', {html: row.cards})
    );
  } else {
    setGameListingCards(cardsDiv, row.requiredCards, row.forbiddenCards);
  }
  rowDiv.append(cardsDiv);
  // find the index to insert the new row
  let index = 0;
  for (;;) {
    if (index === gameListRows.length) {
      // stop at the end of the list
      break;
    } else if (gameListRows[index].empty) {
      // stop on an empty row
      break;
    }
    index++;
  }
  const rowEntry = {'row': row, 'rowDiv': rowDiv, 'empty': false, 'closed': (!isGame)};
  // insert the row in one of these three locations
  if (index < gameListRows.length - 1) {
    // somewhere before the last row
    gameListRows[index].rowDiv.remove();
    rowDiv.insertBefore(gameListRows[index + 1].rowDiv);
    gameListRows[index] = rowEntry;
  } else if(index == gameListRows.length - 1) {
    // replacing the last row
    gameListRows[index].rowDiv.remove();
    gameList.append(rowDiv);
    gameListRows[index] = rowEntry;
  } else {
    // as a new row at the end
    gameList.append(rowDiv);
    gameListRows.push(rowEntry);
  }
  if (isGame) {
    // on click, join game
    rowDiv.mousedown(() => {
      joinGame(row.name);
    });
    rowDiv.attr('class', 'rowDiv rowDivJoinable');
    gameRowIndex[row.name] = index;
  }
}

function removeClosedGameListings() {
  // save the old rows
  const oldRows = gameListRows;
  // empty the list
  initializeGameList();
  // add back all of the old rows that had open games
  _.chain(oldRows)
    .filter(oldRow => !oldRow.empty && !oldRow.closed)
    .each(oldRow => {
      addGameListRow(oldRow.row, true);
    });
}

function joinGame(name) {
  socket.send(JSON.stringify({'type': 'joinGame', 'name': name}));
}

function createCustomGame() {
  // remove previous error messages
  $('#customGameSection customGameError').remove();
  // send new custom game request
  const name = $('#customGameName').val();
  const numPlayers = $('#numPlayersSelect option:checked').val();
  // create a list of the checked card sets
  const sets = [];
  _.each({
    'baseCheckbox': 'Base',
    'baseFirstEditionCheckbox': 'Base (1st Ed.)',
    'intrigueCheckbox': 'Intrigue',
    'intrigueFirstEditionCheckbox': 'Intrigue (1st Ed.)',
    'seasideCheckbox': 'Seaside',
    'prosperityCheckbox': 'Prosperity',
    'cornucopiaCheckbox': 'Cornucopia',
    'hinterlandsCheckbox': 'Hinterlands',
    'darkAgesCheckbox': 'Dark Ages',
    'guildsCheckbox': 'Guilds',
  }, (set, id) => {
    if ($('#'+id).is(':checked')) {
      sets.push(set);
    }
  });
  const cards = $('#customGameCards').val();
  const bots = getBots();
  socket.send(JSON.stringify({'type': 'createCustomGame', 'name': name, 'numPlayers': numPlayers, 'sets': sets, 'cards': cards, 'bots': bots}));
}

function customGameError(message) {
  $('#customGameSection').append(
    $('<p>', {'class': 'customGameError', html: message})
  );
}

// boolean indicating whether this player is ready for the game to start
let playerIsReady;

function enterGameLobby(name, sets, requiredCards, forbiddenCards) {
  // show this game's lobby
  showScreen('gameLobby');
  // upon joining a game lobby, the player is not ready to start
  playerIsReady = false;
  // show game's name
  $('#gameLobbyName').text(name);
  // show game's card sets
  const gameLobbySets = $('#gameLobbySets');
  gameLobbySets.empty();
  if (sets) {
    gameLobbySets.append(
      $('<p>', {html: 'Sets: ' + sets})
    );
  }
  // show game's required and forbidden cards
  const gameLobbyCards = $('#gameLobbyCards');
  gameLobbyCards.empty();
  if (requiredCards) {
    gameLobbyCards.append(
      $('<p>', {html: 'Cards: ' + requiredCards})
    );
  }
  if (forbiddenCards) {
    gameLobbyCards.append(
      $('<p>', {html: 'Not: ' + forbiddenCards})
    );
  }
  // initialize ready button
  $('#gameLobbyReadyButton')
    .html('Ready')
    .mousedown(togglePlayerIsReady);
  // initialize leave button
  $('#gameLobbyLeaveButton').mousedown(gameLobbyLeave);
}

// [{type: 'bot', username: 'BigMoney[Bot]', isReady: true}, ...]
function updateGameLobby(players) {
  const playerList = $('#gameLobbyPlayerList');
  playerList.empty();
  _.each(players, player => {
    const p = $('<p>');
    if (player.type === 'bot') {
      p.html(player.username);
    } else if (player.type === 'open') {
      p.html('- Open -');
    } else {
      const readiness = player.isReady ? '<span class="ready">(Ready)</span>' : '<span class="notReady">(Not Ready)</span>';
      p.html(player.username + readiness);
    }
    playerList.append(p);
  });
}

function togglePlayerIsReady() {
  playerIsReady = !playerIsReady;
  $('#gameLobbyReadyButton').html(playerIsReady ? 'Not Ready' : 'Ready');
  socket.send(JSON.stringify({'type': 'gameLobbyReady', 'isReady': playerIsReady}));
}

function gameLobbyLeave() {
  socket.send(JSON.stringify({'type': 'gameLobbyLeave'}));
}

function chatInputKeydown(e) {
  // send the contents of the chat input if the return key was pressed
  if (e.which == 13 || e.keyCode == 13) {
    chatSend();
  }
}

function chatSend() {
  const chatInput = $('#chatInput');
  const message = chatInput.val();
  if (message !== '') {
    socket.send(JSON.stringify({'type': 'chat', 'message': message}));
    chatInput.val('');
  }
}

function receiveChat(username, message) {
  $('#chatDisplay').append(
    $('<p>', {html: '<span class="chatUsername">' + username + ':</span> ' + message})
  );
}

function forfeit() {
  socket.send(JSON.stringify({'type': 'forfeit'}));
}

function endGame() {
  const button = $('<button>', {html: 'Return to lobby'});
  button.mousedown(() => {
    button.hide();
    socket.send(JSON.stringify({'type': 'returnToLobby'}));
  });
  $('#log').append(button);
}

function enterGame(command) {
  cardInfo = command.cardInfo;
  piles = {};
  setKingdomPiles(command.kingdomPiles);
  setBasicPiles(command.basicPiles);
  setNonSupplyPiles(command.nonSupplyPiles);
  setPrizeCards(command.prizeCards);
  setOpponents(command.opponents);

  // clear log
  $('#log').empty();
  // hide waiting on
  setWaitingOn();
  // hurry up button
  $('#hurryUpButton').mousedown(hurryUp);
  // clear chat
  $('#chatDisplay').empty();
  // enable chat sending
  $('#chatInput').keydown(chatInputKeydown);
  $('#chatSendButton').mousedown(chatSend);
  // clear prompt
  $('#prompt').empty();
  // close popup
  closePopup();
  // enable forfeit button
  $('#forfeitButton').mousedown(forfeit);
  // show game
  showScreen('game');
}

function setKingdomPiles(piles) {
  const kingdom = $('#kingdom');
  kingdom.empty();
  _.each(piles, pile => {
    addPile(pile.id, pile.topCard, pile.subtitle, kingdom);
  });
}

function setBasicPiles(piles) {
  const basic = $('#basic');
  basic.empty();
  _.each(piles, pile => {
    addPile(pile.id, pile.topCard, undefined, basic);
  });
}

function setNonSupplyPiles(piles) {
  const nonSupply = $('#nonSupply');
  nonSupply.empty();
  _.each(piles, pile => {
    addPile(pile.id, pile.topCard, '(Non-Supply)', nonSupply);
  });
}

function setPrizeCards(cards) {
  const prizes = $('#prizes');
  prizes.empty();
  if (!cards) {
    return;
  }
  _.each(cards, card => {
    addPile(card, undefined, '(Prize)', prizes);
  });
}

function setOpponents(numOpponents) {
  /*const opponents = $('#opponents');
  opponents.empty();
  _.times(numOpponents, i => {
    // TODO
  });*/
}

function updateGameView(updates) {
  _.each(updates, (update, view) => {
    updateFunctions[view](update);
  });
}

function updatePiles(pileUpdates) {
  _.each(pileUpdates, (updates, pileId) => {
    const pile = piles[pileId];
    if (updates.hasOwnProperty('topCard')) {
      let nameClass;
      let cardArtImgSrc;
      let cardArtImgClass;
      if (updates.topCard !== '') {
        nameClass = cardInfo[updates.topCard].highlightType;
        cardArtImgSrc = cardArtSrc(updates.topCard);
        cardArtImgClass = 'boxBorder-'+cardInfo[updates.topCard].highlightType;
      } else {
        nameClass = '';
        cardArtImgSrc = cardArtSrc('none');
        cardArtImgClass = 'boxBorder-action';
      }
      pile.find('.name p:not(.subtitle)')
        .text(updates.topCard)
        .attr('class', nameClass);
      pile.find('.cardArt > img')
        .attr('src', cardArtImgSrc)
        .attr('class', cardArtImgClass);
      // TODO popup handler
    }
    if (updates.hasOwnProperty('cost')) {
      pile.find('.cost p')
        .text('$'+updates.cost.toString());
    }
    if (updates.hasOwnProperty('size')) {
      pile.find('.size')
        .text('(' + updates.size.toString() + ')');
      const img = pile.find('.cardArt > img');
      const cross = pile.find('.cross');
      if (updates.size === 0) {
        img.css('opacity', '0.3');
        cross.show();
      } else {
        img.css('opacity', '');
        cross.hide();
      }
    }
    if (updates.hasOwnProperty('embargoTokens')) {
      const embargoTokens = getPileTokens(pile, 'embargoTokens');
      if (updates.embargoTokens === 0) {
        embargoTokens.remove();
      } else {
        embargoTokens.text(updates.embargoTokens.toString());
      }
    }
    if (updates.hasOwnProperty('hasTradeRouteToken')) {
      const tradeRouteTokens = getPileTokens(pile, 'tradeRouteToken');
      if (updates.hasTradeRouteToken) {
        tradeRouteTokens.text('1');
      } else {
        tradeRouteTokens.remove();
      }
    }
  });
}

function getPileTokens(pile, tokenType) {
  const tokenContainer = getPileTokenContainer(pile);
  let tokens = tokenContainer.find('.'+tokenType);
  if (tokens.length === 0) {
    tokens = $('<label>', {'class': tokenType});
    tokenContainer.append(tokens);
  }
  return tokens;
}

function getPileTokenContainer(pile) {
  const cardArt = pile.find('.cardArt');
  let tokenContainer = cardArt.find('.pileTokens');
  if (tokenContainer.length === 0) {
    tokenContainer = $('<div>', {'class': 'pileTokens'});
    cardArt.append(tokenContainer);
  }
  return tokenContainer;
}

function updatePrizeCards(prizeCardUpdates) {
  _.each(prizeCardUpdates, (isPresent, prizeCard) => {
    const pile = piles[prizeCard];
    const img = pile.find('.cardArt > img');
    const cross = pile.find('.cross');
    if (isPresent) {
      img.css('opacity', '');
      cross.hide();
    } else {
      img.css('opacity', '0.3');
      cross.show();
    }
  });
}

function updateTrash(trashUpdate) {
  // TODO: card zone display
  //trash.set(trashUpdate);
  const trash = $('#trash');
  trash.html('Trash: ' + trashUpdate);
  /*if (_.isEmpty(trashUpdate)) {
    trash.html('Trash: (empty)');
  } else {
    trash.html('Trash: ' + JSON.stringify(trashUpdate));
  }*/
}

function updateTradeRoute(tradeRouteUpdate) {
  setArea('tradeRouteMat', 'Trade Route', tradeRouteUpdate === 0 ? undefined : '$'+tradeRouteUpdate);
}

function updateOpponents(opponentUpdates) {
  _.each(opponentUpdates, (updates, opponentIndex) => {
    const opponent = parseInt(opponentIndex);
    if (updates.hasOwnProperty('username')) {

    }
    if (updates.hasOwnProperty('handSize')) {

    }
    if (updates.hasOwnProperty('drawSize')) {

    }
    if (updates.hasOwnProperty('discardSize')) {

    }
    if (updates.hasOwnProperty('victoryPoints')) {

    }
    if (updates.hasOwnProperty('durations')) {

    }
    if (updates.hasOwnProperty('inPlay')) {

    }
  });
}

function updateDrawSize(drawSizeUpdate) {
  // TODO
  draw.set(drawSizeUpdate);
}

function updateDiscardSize(discardSizeUpdate) {
  // TODO
  discard.set(discardSizeUpdate);
}

function updateActions(actionsUpdate) {
  const numActions = $('#numActions');
  if (actionsUpdate) {
    numActions.text(actionsUpdate);
  } else {
    // insert a single space character
    numActions.text(String.fromCharCode(160));
  }
}

function updateBuys(buysUpdate) {
  const numBuys = $('#numBuys');
  if (buysUpdate) {
    numBuys.text(buysUpdate);
  } else {
    // insert a single space character
    numBuys.text(String.fromCharCode(160));
  }
}

function updateCoins(coinsUpdate) {
  coins = coinsUpdate;
  updateCoinsView();
}

function updateIsAutoplayingTreasures(isAutoplayingTreasuresUpdate) {
  isAutoplayingTreasures = isAutoplayingTreasuresUpdate;
  updateCoinsView();
}

function updateCoinsView() {
  const numCoins = $('#numCoins');
  if (isAutoplayingTreasures) {
    numCoins.text(coins.toString());
  } else {
    numCoins.html('<span class="notAutoplayingTreasures">' + coins.toString() + '</span>');
  }
}

function updateCoinTokens(coinTokensUpdate) {
  // TODO: coin icon
  // TODO: consistency
  setArea('coinTokens', 'Coin Tokens', coinTokensUpdate === 0 ? undefined : '$'+coinTokensUpdate);
}

function updatePirateShip(pirateShipUpdate) {
  // TODO: coin icon
  // TODO: consistency
  setArea('pirateShipMat', 'Pirate Ship', pirateShipUpdate === 0 ? undefined : '$'+pirateShipUpdate);
}

function updateVictoryTokens(victoryTokensUpdate) {
  // TODO: victory token icon
  // TODO: consistency
  setArea('victoryTokenMat', 'Victory Tokens', victoryTokensUpdate === 0 ? undefined : victoryTokensUpdate+' VP');
}

function updateVictoryPoints(victoryPointsUpdate) {

}

function updateNativeVillage(nativeVillageUpdate) {
  // TODO: card zone display
  //nativeVillage.set(nativeVillageUpdate);
  setArea('nativeVillageMat', 'Native Village', _.isEmpty(nativeVillageUpdate) ? undefined : JSON.stringify(nativeVillageUpdate));
}

function updateIsland(islandUpdate) {
  // TODO: card zone display
  //islands.set(islandUpdate);
  setArea('islandMat', 'Island', _.isEmpty(islandUpdate) ? undefined : JSON.stringify(islandUpdate));
}

function updateDurations(durationsUpdate) {
  // TODO: card zone display
  //durations.set(durationsUpdate);
  setArea('durations', 'Duration', _.isEmpty(durationsUpdate) ? undefined : JSON.stringify(durationsUpdate));
}

function updateInPlay(inPlayUpdate) {
  inPlay.set(inPlayUpdate);
}

function updateHand(handUpdate) {
  handCounts = handUpdate;
  hand.set(handUpdate);
}

const updateFunctions = {
  "piles": updatePiles,
  "prizeCards": updatePrizeCards,
  "trash": updateTrash,
  "tradeRoute": updateTradeRoute,
  "opponents": updateOpponents,
  "drawSize": updateDrawSize,
  "discardSize": updateDiscardSize,
  "actions": updateActions,
  "buys": updateBuys,
  "coins": updateCoins,
  "isAutoplayingTreasures": updateIsAutoplayingTreasures,
  "coinTokens": updateCoinTokens,
  "pirateShip": updatePirateShip,
  "victoryTokens": updateVictoryTokens,
  "victoryPoints": updateVictoryPoints,
  "nativeVillage": updateNativeVillage,
  "island": updateIsland,
  "durations": updateDurations,
  "inPlay": updateInPlay,
  "hand": updateHand,
};

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
      enterGame(command);
      break;
    case 'endGame':
      endGame();
      break;
    case 'updateGameView':
      updateGameView(command.updates);
      break;
    case 'setWaitingOn':
      setWaitingOn(command.player);
      break;
    case 'allowHurryUp':
      allowHurryUp();
      break;
    case 'prompt':
      setWaitingOn();
      prompt(command);
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

function receiveServerCommands(text) {
  const commands = JSON.parse(text);
  // print all commands for debugging
  _.each(commands, command => {
    /*if (command.command !== 'updateGameView') {
      return;
    }*/
    console.log('('+command.command+')');
    console.log(JSON.stringify(command, null, 2));
  });
  //console.log(JSON.stringify(commands, null, 2));
  // remember if the user was scrolled to the bottom of the game screen
  const lockToBottomOfGame = isOnBottomOfGame();
  // execute commands
  for (let i = 0; i < commands.length; i++) {
    executeCommand(commands[i]);
  }
  // if the user was scrolled to the bottom of the game screen, scroll there again
  //if (lockToBottomOfGame) { // TODO: this is a quick fix, for some reason the user isn't staying locked to the bottom during the opponent's turn
    scrollToBottomOfGame();
  //}
}

function isOnBottomOfGame() {
  const game = $('#game');
  if (!game.is(':visible')) {
    return false;
  }
  const scroll = $(document).scrollTop();
  const maxScroll = $(document).height() - $(window).height();
  return scroll === maxScroll;
}

function scrollToBottomOfGame() {
  $(document).scrollTop($(document).height() - $(window).height());
}

function sendResponse(response) {
  socket.send(JSON.stringify({'type': 'response', 'response': response}));
}

// boolean indicating whether a login request has been submitted and not
// accepted or rejected yet
let loginPending = false;
// boolean indicating whether the players is trying to create a new login
let creatingNewLogin = false;

function changeLogin() {
  // if the user left automatch on, turn it off
  const automatchCheckbox = $('#automatchCheckbox');
  if (automatchCheckbox.prop('checked')) {
    automatchCheckbox.prop('checked', false);
    automatchSet();
  }

  // go from lobby to login
  showScreen('loginContainer');

  $('#newLogin').mousedown(toggleCreatingNewLogin);
  // send login info on button press, or on pressing return in a text field
  $('#loginButton').mousedown(sendLogin);
  $('#username').keydown(loginKeyDown);
  $('#password').keydown(loginKeyDown);

  // autofocus on username field
  window.setTimeout(() => {
    $('#username').focus();
  }, 0);
}

function loginAccepted(username) {
  loginPending = false;
  // update username
  $('#lobbyUsernameText').text(username);
  // return to lobby from login
  showScreen('lobby');
}

/*
Send new login request to the server unless a login request is already pending.
*/
function sendLogin() {
  if (loginPending) {
    return;
  }
  $('.loginError').remove();
  // get username
  const username = $('#username').val();
  if (username === '') {
    loginError('Username cannot be empty.');
    return;
  }
  // get password
  const passwordPlaintext = $('#password').val();
  if (creatingNewLogin) {
    const confirmPasswordPlaintext = $('#confirmPassword').val();
    if (passwordPlaintext === '') {
      loginError('Password cannot be empty.');
      return;
    } else if (passwordPlaintext !== confirmPasswordPlaintext) {
      loginError('Confirm Password does not match.');
      return;
    }
  }
  // send login request
  loginPending = true;
  const loginRequest = {
    type: 'login',
    username: username
  };
  // hash password (TODO in the future, this might change to a server-supplied salt)
  if (passwordPlaintext !== '') {
    const passwordHash = dcodeIO.bcrypt.hashSync(passwordPlaintext, '$2a$10$OFkk3vKhcZqaYEHnNu0Wc.');
    loginRequest.password = passwordHash;
  }
  if (creatingNewLogin) {
    loginRequest.newLogin = true;
  }
  socket.send(JSON.stringify(loginRequest));
}

function loginKeyDown(e) {
  if (e.which == 13 || e.keyCode == 13) {
    sendLogin();
  }
}

function toggleCreatingNewLogin() {
  creatingNewLogin = !creatingNewLogin;
  const newLogin = $('#newLogin');
  if (creatingNewLogin) {
    $('#password').after(
      $('<label>', {id: 'confirmPasswordLabel', html: 'Confirm Password'}),
      $('<input>', {id: 'confirmPassword', type: 'password', name: 'confirmPassword'}).keydown(loginKeyDown)
    );
    newLogin.text('Cancel');
  } else {
    $('#confirmPasswordLabel').remove();
    $('#confirmPassword').remove();
    newLogin.text('New user');
  }
}

function loginError(message) {
  $('#login').append($('<p>', {'class': 'loginError', html: message}));
  loginPending = false;
}

const screenIds = ['lobby', 'loginContainer', 'gameLobby', 'game', 'lostConnection'];
function showScreen(toShowId) {
  _.each(screenIds, screenId => {
    const screen = $('#'+screenId);
    if (screenId === toShowId) {
      screen.show();
    } else {
      screen.hide();
    }
  });
}

function initUI() {
  draw = new CardCountDisplay('draw');
  discard = new CardCountDisplay('discard');
  hand = new CardZoneDisplay('hand');
  inPlay = new CardZoneDisplay('inPlay');

  // go to lobby
  showScreen('lobby');

  // default automatch to any game size
  $('#automatch2Checkbox').prop('checked', true);
  $('#automatch3Checkbox').prop('checked', true);
  $('#automatch4Checkbox').prop('checked', true);
  // default card sets to all except first editions
  $('#baseCheckbox').prop('checked', true);
  $('#intrigueCheckbox').prop('checked', true);
  $('#seasideCheckbox').prop('checked', true);
  $('#prosperityCheckbox').prop('checked', true);
  $('#cornucopiaCheckbox').prop('checked', true);
  $('#hinterlandsCheckbox').prop('checked', true);
  $('#darkAgesCheckbox').prop('checked', true);
  $('#guildsCheckbox').prop('checked', true);

  // log in button
  $('#changeLoginButton').mousedown(changeLogin);
}

$(document).ready(() => {
  initUI();

  // open websocket to server
  socket = new WebSocket('ws://' + location.host);
  socket.onopen = () => {
    // connected
  };
  socket.onmessage = e => {
    receiveServerCommands(e.data);
  };
  socket.onclose = () => {
    showScreen('lostConnection');
  };
});

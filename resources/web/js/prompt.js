let handCountsBeforeDiscard;

let discardNumberFromHand;
let discardDestination;
let discardAmount;

const promptTypes = {
  'normal': 'normalPrompt',
  'danger': 'dangerPrompt',
  'play': 'playPrompt',
  'buy': 'buyPrompt',
  'reaction': 'reactionPrompt',
};

function prompt(command) {
  const prompt = $('#prompt');
  prompt.empty();
  // type
  prompt.attr('class', 'prompt ' + promptTypes[command.type]);
  // message
  if (command.message) {
    if (command.message.split(':'.length == 2)) {
      prompt.append(
        $('<p>', {'class': 'title', html: command.message.split(':')[0]})
      );
      command.message = command.message.split(':')[1];
    }
    prompt.append(
      $('<p>', {'class': 'message', html: command.message})
    );
  }
  // supply choices
  if (command.supplyChoices) {
    _.each(command.supplyChoices, supplyChoice => {
      const pile = piles[supplyChoice];
      pile.addClass('clickable');
      pile.mousedown(() => {
        sendPromptResponse({'supplyChoice': supplyChoice});
      });
    });
  }
  // mixed pile choices
  if (command.mixedPileChoices) {
    _.each(command.mixedPileChoices, mixedPileChoice => {
      const pile = piles[mixedPileChoice];
      pile.addClass('clickable');
      pile.mousedown(() => {
        sendPromptResponse({'mixedPileChoice': mixedPileChoice});
      });
    });
  }
  // hand choices
  if (command.handChoices) {
    _.each(command.handChoices, handChoice => {
      const fan = hand.fans[handChoice].fan;
      fan.addClass('clickable');
      fan.mousedown(() => {
        sendPromptResponse({'handChoice': handChoice});
      });
    });
  }
  // multiple choices
  if (command.multipleChoices) {
    let disabled;
    if (command.disabledMultipleChoiceIndexes) {
      disabled = command.disabledMultipleChoiceIndexes;
    } else {
      disabled = [];
    }
    _.each(command.multipleChoices, (multipleChoice, i) => {
      const button = $('<button>', {html: multipleChoice});
      prompt.append(button);
      if (!_.contains(disabled, i)) {
        button.mousedown(() => {
          sendPromptResponse({'multipleChoiceIndex': i});
        });
      } else {
        button.prop('disabled', true);
      }
    });
  }
  // numberFromHand, destination, amount
  if (command.numberFromHand) {
    handCountsBeforeDiscard = _.map(handCounts, handCount => ({card: handCount.card, count: handCount.count}));
    discardNumberFromHand = command.numberFromHand;
    discardDestination = command.destination;
    discardAmount = command.amount;
    toDiscard = [];
    prompt.append(
      $('<p>', {'class': 'message', html: discardPromptMessage()})
    );
    _.each(handCounts, (handCount, i) => {
      const card = handCount.card;
      const fan = hand.fans[card].fan;
      fan.addClass('clickable');
      fan.mousedown(() => {
        handCounts[i].count -= 1;
        toDiscard.push(card);
        hand.showAfterDiscard(handCounts);
        prompt.find('p.message').text(discardPromptMessage);
        if (toDiscard.length === discardNumberFromHand) {
          sendPromptResponse({'handChoices': toDiscard});
        }
      });
    });
    if (discardAmount === 'up_to') {
      const doneButton = $('<button>', {html: 'Done'});
      prompt.append(doneButton);
      doneButton.mousedown(() => {
        sendPromptResponse({'handChoices': toDiscard});
      });
    } else if (discardAmount === 'exact_or_none') {
      const noneButton = $('<button>', {html: 'None'});
      prompt.append(noneButton);
      noneButton.mousedown(() => {
        hand.set(handCountsBeforeDiscard);
        sendPromptResponse({'handChoices': []});
      });
    }
  }
  // none choice
  if (command.noneText) {
    const button = $('<button>', {html: command.noneText});
    prompt.append(button);
    button.mousedown(() => {
      sendPromptResponse({});
    });
  }
  // display prompt
  prompt.show();
}

function discardPromptMessage() {
  const numLeftToDiscard = discardNumberFromHand - toDiscard.length;
  const numLeftText = ((discardAmount === 'up_to') ? 'up to ' : '') + numLeftToDiscard + ' card' + (numLeftToDiscard !== 1 ? 's' : '');
  let message;
  if (discardDestination === 'trash') {
    message = 'Trash ' + numLeftText;
  } else if (discardDestination === 'discard') {
    message = 'Discard ' + numLeftText;
  } else { /* destination === 'deck' */
    message = 'Put ' + numLeftText + ' on top of your deck (you will draw them in the order chosen)';
  }
  if (discardAmount === 'exact_or_none') {
    message = 'You may ' + message.substring(0, 1).toLowerCase() + message.substring(1);
  }
  return message;
}

function sendPromptResponse(response) {
  socket.send(JSON.stringify({'type': 'response', 'response': response}));

  // remove click handers from supply piles
  _.each(piles, pile => {
    pile.removeClass('clickable');
    pile.off('mousedown');
  });
  // remove click handlers from the hand
  _.chain(hand.fans)
    .pluck('fan')
    .each(fan => {
      fan.removeClass('clickable');
      fan.off('mousedown');
    });
  // hide the prompt
  $('#prompt').hide();
}

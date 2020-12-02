function formattedDescriptionMarkup(description) {
  const formattedDescription = _.map(description, line => formattedMarkup(line));

  const domElements = [];
  let unorderedListElements = null;
  const closeUnorderedListElements = () => {
    if (unorderedListElements !== null) {
      domElements.push(
        $('<ul>').append(unorderedListElements)
      );
      unorderedListElements = null;
    }
  };
  _.each(formattedDescription, line => {
    if (line.startsWith('*')) {
      if (unorderedListElements === null) {
        unorderedListElements = [];
      }
      unorderedListElements.push(
        $('<li>', {html: line.substring(1).trim()})
      );
    } else {
      closeUnorderedListElements();
      domElements.push(
        $('<p>', {html: line})
      );
    }
  });
  closeUnorderedListElements();
  return domElements;
}

function formattedMarkup(string) {
  return string.replace( // angle brackets -> bold
    /<([^<>]+)>/g,
    (match, group) => '<span class="bold">' + group + '</span>'
  ).replace( // square brackets -> card highlight
    /\[([^\[\]]+)\]/g,
    (match, group) => '<span class="' + highlightTypeOf(group) + '">' + group + '</span>'
  ).replace( // underscore -> non-breaking space
    /_/g,
    '&nbsp;'
  );
}

function highlightTypeOf(cardString) {
  while (cardString.length != '') {
    let highlightType = null;
    _.each(cardInfo, (info, card) => {
      if (card.startsWith(cardString)) {
        highlightType = info.highlightType;
      }
    });
    if (highlightType !== null) {
      return highlightType;
    }
    cardString = cardString.substring(0, cardString.length - 1);
  }
  return '';
}

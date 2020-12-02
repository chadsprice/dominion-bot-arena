function registerPopup(elem, card) {
  // when the mouse enters the element, set a timeout to show the popup
  elem.mouseenter(() => {
    popupTimeout = setTimeout(
      () => {
        popupTimeout = null;
        displayPopup(elem, card);
      },
      POPUP_DELAY_MILIS
    );
  });
  // when the mouse leaves the element, cancel the timeout and hide the popup
  elem.mouseleave(() => {
    if (popupTimeout !== null) {
      clearTimeout(popupTimeout);
    }
    closePopup();
  });
}

function displayPopup(elem, card) {
  closePopup();
  const info = cardInfo[card];
  const popupContainer = $('<div>', {'class': 'popupContainer'});
  const popup = $('<div>', {'class': 'popup box boxBorder-'+info.highlightType + ' boxBackground-'+info.highlightType});
  popupContainer.append(popup);
  popup.append(
    $('<div>', {'class': 'name'}).append(
      $('<p>').append(
        $('<span>', {html: card, 'class': info.highlightType})
      )
    )
  );
  popup.append(
    $('<div>', {'class': 'description'}).append(
      formattedDescriptionMarkup(info.description)
    )
  );
  popup.append(
    $('<div>', {'class': 'type'}).append(
      $('<div>', {'class': 'cost'}).append(
        $('<p>', {html: info.cost})
      ),
      $('<p>').append(
        $('<span>', {html: info.type, 'class': info.highlightType})
      )
    )
  );
  $('#game').append(popupContainer);
  // if this popup is for a card in a CardZoneDisplay
  if (elem.parent().hasClass('cardZoneDisplay')) {
    // put the popup above the element
    popupContainer.css({
      left: (elem.offset().left - (popupContainer.width() - elem.outerWidth()) / 2) + 'px',
      top: (elem.offset().top - popupContainer.height() - 5) + 'px'
    });
  } else {
    // otherwise, this popup is for a pile in the supply, so put the popup to the right of the element
    popupContainer.css({left: (elem.offset().left + elem.outerWidth() + 5) + 'px'});
    // if the popup would go off the screen
    if (elem.offset().top - (popupContainer.height() - elem.outerHeight()) / 2 + popupContainer.height() > $('#game').height()) {
      // put it at the bottom of the screen
      popupContainer.css({top: ($('#game').height() - popupContainer.height()) + 'px'});
    } else {
      popupContainer.css({top: (elem.offset().top - (popupContainer.height() - elem.outerHeight()) / 2) + 'px'});
    }
  }
}

function closePopup() {
  $('.popupContainer').remove();
}

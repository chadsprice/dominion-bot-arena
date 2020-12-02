class CardZoneDisplay {
  constructor(id) {
    this.display = $('#' + id);
    this.display.addClass('cardZoneDisplay');
    if (id === 'hand') {
      this.imageWidth = 10;
      this.imageHeight = this.imageWidth * SRC_IMG_ASPECT_RATIO;
      this.imageMarginLeft = 2;
      this.imageMarginTop = 1;
    } else {
      this.imageWidth = 5;
      this.imageHeight = this.imageWidth * SRC_IMG_ASPECT_RATIO;
      this.imageMarginLeft = 1;
      this.imageMarginTop = 0.5;
    }
  }

  set(counts) {
    this.fans = {};
    this.display.empty();
    if (_.every(counts, count => count.count === 0)) {
      this.display.hide();
    } else {
      this.display.show();
    }
    _.each(counts, count => {
      if (count.count > 0) {
        this.addFan(count.card, count.count);
      }
    });
  }

  showAfterDiscard(counts) {
    // call detach in order to preserve event handlers (click handlers would be removed with .empty())
    this.display.children().detach();
    if (_.every(counts, count => count.count === 0)) {
      this.display.hide();
    } else {
      this.display.show();
    }
    _.each(counts, count => {
      if (count.count > 0) {
        const fan = this.fans[count.card];
        _.each(fan.images, (image, i) => {
          if (i >= fan.images.length - count.count) {
            image.show();
          } else {
            image.hide();
          }
        });
        this.display.append(fan.fan);
      }
    });
  }

  addFan(card, count) {
    const fan = $('<div>', {'class': 'fan'});
    const imagesContainer = $('<div>', {'class': 'fanImages'})
      .width((this.imageWidth + (count-1)*this.imageMarginLeft).toString() + 'em')
      .height((this.imageHeight + (count-1)*this.imageMarginTop).toString() + 'em');
    const images = _.map(_.range(count), i =>
      $('<img>', {
        'class': 'boxBorder-'+cardInfo[card].highlightType,
        src: cardArtSrc(card)
      })
        .css('width', this.imageWidth.toString() + 'em')
        .css('margin-left', (i*this.imageMarginLeft).toString() + 'em')
        .css('margin-top', (i*this.imageMarginTop).toString() + 'em')
    );
    const label = $('<div>', {'class': 'fanLabel'}).append(
      $('<p>').append(
        $('<span>', {html: card, 'class': cardInfo[card].highlightType})
      )
    );
    this.display.append(
      fan.append(
        imagesContainer.append(
          images
        ),
        label
      )
    );
    this.fans[card] = {
      'fan': fan,
      'images': images
    };
    // TODO re-examine popup system
    registerPopup(fan, card);
  }
}

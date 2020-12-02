class CardCountDisplay {
  constructor(id) {
    if (id === 'draw' || id === 'discard') {
      this.imageWidth = 5;
      this.imageHeight = this.imageWidth * SRC_IMG_ASPECT_RATIO;
      this.imageMarginLeft = 0.5;
      this.imageMarginTop = 0.5;
    } else {
      this.imageWidth = 5;
      this.imageHeight = this.imageWidth * SRC_IMG_ASPECT_RATIO;
      this.imageMarginLeft = 0.25;
      this.imageMarginTop = 0.25;
    }

    const display = $('#' + id)
      .width((this.imageWidth + (3-1)*this.imageMarginLeft).toString() + 'em')
      .height((this.imageHeight + (3-1)*this.imageMarginTop).toString() + 'em');
    display.addClass('cardCountDisplay');

    this.cards = [];
    _.times(3, i => {
      this.cards[i] = $('<div>', {
        'class': 'cardCountDisplayCard'
      })
        .css('width', this.imageWidth.toString() + 'em')
        .css('height', this.imageHeight.toString() + 'em')
        .css('margin-left', (i*this.imageMarginLeft).toString() + 'em')
        .css('margin-top', (i*this.imageMarginTop).toString() + 'em');
      display.append(this.cards[i]);
    });
    this.count = $('<label>', {html: '?'});
    this.cards[2].append(this.count);
  }

  set(count) {
    _.times(2, i => {
      if (count >= 3-i) {
        this.cards[i].show();
      } else {
        this.cards[i].hide();
      }
    });
    if (count === 0) {
      this.cards[2].css('opacity', '0.3');
    } else {
      this.cards[2].css('opacity', '');
    }
    this.count.text(count.toString());
  }
}

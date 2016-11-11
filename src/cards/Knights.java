package cards;

import server.Card;

/**
 * Knights is just a placeholder card for the mixed Knight pile during kingdom card selection.
 */
public class Knights extends Card {

    public Knights() {
        // set to Action so that it has a UI color
        isAction = true;
    }

    @Override
    public int cost() {
        // even though this card will never be in play, say that it has a "cost" of $5 so that it won't be chosen as
        // the bane card
        return 5;
    }

    @Override
    public String toString() {
        return "Knights";
    }

}

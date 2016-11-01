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
    public String toString() {
        return "Knights";
    }

}

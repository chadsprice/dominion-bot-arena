package cards;

import server.Card;

import java.util.Set;

/**
 * Knights is just a placeholder card for the mixed Knight pile during kingdom card selection.
 */
public class Knights extends Card {

    @Override
    public String name() {
        return "Knights";
    }

    @Override
    public Set<Type> types() {
        // set to Action so that it has a UI color
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        // even though this card will never be in play, say that it has a "cost" of $5 so that it won't be chosen as
        // the bane card
        return 5;
    }

    @Override
    public String[] description() {
        throw new UnsupportedOperationException();
    }

}

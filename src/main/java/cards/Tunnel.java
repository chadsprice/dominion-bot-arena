package cards;

import server.Card;

import java.util.Set;

public class Tunnel extends Card {

    @Override
    public String name() {
        return "Tunnel";
    }

    @Override
    public Set<Type> types() {
        return types(Type.VICTORY);
    }

    @Override
    public String htmlType() {
        return "Victory-Reaction";
    }

    @Override
    public String htmlHighlightType() {
        return "victory-reaction";
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public String[] description() {
        return new String[] {
                "2_VP",
                "When you discard this other_than during a_Clean-up phase, you_may reveal_it. If_you_do, gain a_[Gold]."
        };
    }

    @Override
    public int victoryValue() {
        return 2;
    }

}

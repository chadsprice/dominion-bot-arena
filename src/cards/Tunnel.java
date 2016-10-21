package cards;

import server.Card;

public class Tunnel extends Card {

    public Tunnel() {
        isVictory = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public int victoryValue() {
        return 2;
    }

    @Override
    public String htmlClass() {
        return "victory-reaction";
    }

    @Override
    public String htmlType() {
        return "Victory-Reaction";
    }

    @Override
    public String[] description() {
        return new String[] {"2 VP", "When you discard this other than during a Clean-up phase, you may reveal it. If you do, gain a Gold."};
    }

    @Override
    public String toString() {
        return "Tunnel";
    }

}

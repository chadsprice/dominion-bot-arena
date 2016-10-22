package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

public class IllGottenGains extends Card {

    public IllGottenGains() {
        isTreasure = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public int treasureValue() {
        return 1;
    }

    @Override
    public void onPlay(Player player, Game game) {
        if (game.supply.get(Card.COPPER) != 0 && chooseGainCopper(player, game)) {
            game.message(player, "gaining " + Card.COPPER.htmlName() + ", putting it into your hand");
            game.messageOpponents(player, "gaining " + Card.COPPER.htmlName() + ", putting it into their hand");
            game.gainToHand(player, Card.COPPER);
        }
    }

    private boolean chooseGainCopper(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).illGottenGainsGainCopper();
        }
        int choice = game.promptMultipleChoice(player, "Ill-Gotten Gains: Gain " + Card.COPPER.htmlName() + ", putting it into your hand?", new String[] {"Yes", "No"});
        return (choice == 0);
    }

    @Override
    public String[] description() {
        return new String[] {"$1", "When you play this, you may gain a Copper, putting it into your hand.", "When you gain this, each other player gains a Curse."};
    }

    @Override
    public String toString() {
        return "Ill-Gotten Gains";
    }

    @Override
    public String plural() {
        return toString();
    }

}

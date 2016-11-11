package cards;

import server.*;

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
        if (game.supply.get(Cards.COPPER) != 0 && chooseGainCopper(player, game)) {
            game.message(player, "gaining " + Cards.COPPER.htmlName() + ", putting it into your hand");
            game.messageOpponents(player, "gaining " + Cards.COPPER.htmlName() + ", putting it into their hand");
            game.gainToHand(player, Cards.COPPER);
        }
    }

    private boolean chooseGainCopper(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).illGottenGainsGainCopper();
        }
        int choice = game.promptMultipleChoice(player, "Ill-Gotten Gains: Gain " + Cards.COPPER.htmlName() + ", putting it into your hand?", new String[] {"Yes", "No"});
        return (choice == 0);
    }

    @Override
    public void onGain(Player player, Game game) {
        // each other player gains a Curse
        game.getOpponents(player).forEach(opponent -> {
            if (game.supply.get(Cards.CURSE) != 0) {
                game.message(opponent, "You gain " + Cards.CURSE.htmlName() + " because of " + this.htmlNameRaw());
                game.messageOpponents(opponent, opponent.username + " gains " + Cards.CURSE.htmlName() + " because of " + this.htmlNameRaw());
                game.gain(opponent, Cards.CURSE);
            }
        });
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

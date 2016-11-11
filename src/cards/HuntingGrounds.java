package cards;

import server.*;

public class HuntingGrounds extends Card {

    public HuntingGrounds() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 6;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 4);
    }

    @Override
    public void onTrash(Player player, Game game) {
        if (chooseGainDuchy(player, game)) {
            if (game.supply.get(Cards.DUCHY) != 0) {
                game.messageAll("gaining " + Cards.DUCHY.htmlName() + " because of " + this.htmlNameRaw());
                game.gain(player, Cards.DUCHY);
            }
        } else {
            int numEstates = Math.min(3, game.supply.get(Cards.ESTATE));
            if (numEstates != 0) {
                game.messageAll("gaining " + Cards.ESTATE.htmlName(numEstates) + " because of " + this.htmlNameRaw());
                for (int i = 0; i < numEstates; i++) {
                    game.gain(player, Cards.ESTATE);
                }
            }
        }
    }

    private boolean chooseGainDuchy(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).huntingGroundsGainDuchy();
        }
        int choice = game.promptMultipleChoice(player, "Hunting Grounds: Choose one", new String[] {"Gain Duchy", "Gain 3 Estates"});
        return (choice == 0);
    }

    @Override
    public String[] description() {
        return new String[] {"+4 Cards", "When you trash this, gain a Duchy or 3 Estates."};
    }

    @Override
    public String toString() {
        return "Hunting Grounds";
    }

    @Override
    public String plural() {
        return toString();
    }

}

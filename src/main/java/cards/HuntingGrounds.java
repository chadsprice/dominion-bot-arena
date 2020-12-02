package cards;

import server.*;

import java.util.Set;

public class HuntingGrounds extends Card {

    @Override
    public String name() {
        return "Hunting Grounds";
    }

    @Override
    public String plural() {
        return name();
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 6;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+4_Cards>",
                "When you trash_this, gain a_[Duchy] or 3_[Estates]."
        };
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
        int choice = new Prompt(player, game)
                .message(this.toString() + ": Choose one")
                .multipleChoices(new String[] {"Gain Duchy", "Gain 3 Estates"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}

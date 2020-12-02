package cards;

import server.*;

import java.util.Set;

public class IllGottenGains extends Card {

    @Override
    public String name() {
        return "Ill-Gotten Gains";
    }

    @Override
    public String plural() {
        return name();
    }

    @Override
    public Set<Type> types() {
        return types(Type.TREASURE);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "1$",
                "When you play_this, you_may gain a_[Copper], putting_it into your_hand.",
                "When you gain_this, each other_player gains a_[Curse]."
        };
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
        int choice = new Prompt(player, game)
                .message(this.toString() + ": Gain " + Cards.COPPER.htmlName() + ", putting it into your hand?")
                .multipleChoices(new String[] {"Yes", "No"})
                .responseMultipleChoiceIndex();
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

}

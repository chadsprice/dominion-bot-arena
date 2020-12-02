package cards;

import server.*;

import java.util.Set;

public class Explorer extends Card {

    @Override
    public String name() {
        return "Explorer";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {"You may reveal a_[Province] card from your_hand. If_you_do, gain a_[Gold] card, putting_it into your_hand. Otherwise, gain a_[Silver] card, putting_it into your_hand."};
    }

    @Override
    public void onPlay(Player player, Game game) {
        boolean revealedProvince = false;
        // if you have a province in hand, choose to reveal it or not
        if (player.getHand().contains(Cards.PROVINCE)) {
            if (chooseRevealProvince(player, game)) {
                revealedProvince = true;
                game.message(player, "revealing " + Cards.PROVINCE.htmlName() + " from your hand");
                game.messageOpponents(player, "revealing " + Cards.PROVINCE.htmlName() + " from their hand");
            }
        }
        // gain the respective card, putting it into your hand
        Card toGain = revealedProvince ? Cards.GOLD : Cards.SILVER;
        if (game.supply.get(toGain) > 0) {
            game.message(player, "gaining " + toGain.htmlName() + ", putting it into your hand");
            game.messageOpponents(player, "gaining " + toGain.htmlName() + ", putting it into their hand");
            game.gainToHand(player, toGain);
        } else {
            game.messageAll("gaining nothing");
        }
    }

    private boolean chooseRevealProvince(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).explorerRevealProvince();
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": Reveal " + Cards.PROVINCE.htmlName() + " from your hand?")
                .multipleChoices(new String[] {"Reveal Province", "Don't"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}

package cards;

import server.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Count extends Card {

    @Override
    public String name() {
        return "Count";
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
        return new String[] {
                "Choose_one:",
                "* Discard_2_cards",
                "* Put a_card from your_hand on_top of your_deck",
                "* Gain a_[Copper]",
                "Choose_one:",
                "* <+$3>",
                "* Trash your_hand",
                "* Gain a_[Duchy]"
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        switch (chooseBenefit(player, game, true)) {
            case 0:
                // discard 2 cards
                if (!player.getHand().isEmpty()) {
                    List<Card> toDiscard = promptDiscardNumber(
                            player,
                            game,
                            2
                    );
                    game.messageAll("discarding " + Card.htmlList(toDiscard));
                    player.putFromHandIntoDiscard(toDiscard);
                } else {
                    game.messageAll("revealing an empty hand, discarding nothing");
                }
                break;
            case 1:
                // put a card from your hand on top of your deck
                if (!player.getHand().isEmpty()) {
                    Card toPutOnDeck = promptChoosePutOnDeck(
                            player,
                            game,
                            new HashSet<>(player.getHand()),
                            this.toString() + ": Choose a card to put on top of your deck."
                    );
                    game.message(player, "putting " + toPutOnDeck + " on top of your deck");
                    game.message(player, "putting a card on top of their deck");
                    player.putFromHandOntoDraw(toPutOnDeck);
                } else {
                    game.message(player, "revealing an empty hand, putting nothing on top of your deck");
                    game.messageOpponents(player, "revealing an empty hand, putting nothing on top of their deck");
                }
                break;
            default: // 2
                // gain a Copper
                gain(player, game, Cards.COPPER);
        }
        switch (chooseBenefit(player, game, false)) {
            case 0:
                // +$3
                plusCoins(player, game, 3);
                break;
            case 1:
                if (!player.getHand().isEmpty()) {
                    List<Card> toTrash = new ArrayList<>(player.getHand());
                    game.messageAll("trashing " + Card.htmlList(toTrash));
                    player.removeFromHand(toTrash);
                    game.trash(player, toTrash);
                } else {
                    game.messageAll("revealing an empty hand, trashing nothing");
                }
                break;
            default: // 2
                // gain a Duchy
                gain(player, game, Cards.DUCHY);
        }
    }

    private int chooseBenefit(Player player, Game game, boolean isFirst) {
        if (player instanceof Bot) {
            int choice;
            if (isFirst) {
                choice = ((Bot) player).countFirstBenefit();
            } else {
                choice = ((Bot) player).countSecondBenefit();
            }
            checkMultipleChoice(3, choice);
            return choice;
        }
        if (isFirst) {
            return new Prompt(player, game)
                    .message(this.toString() + ": Choose one")
                    .multipleChoices(new String[] {"Discard 2 cards", "Put card on deck", "Gain a Copper"})
                    .responseMultipleChoiceIndex();
        } else {
            return new Prompt(player, game)
                    .message(this.toString() + ": Choose one")
                    .multipleChoices(new String[] {"+$3", "Trash your hand", "Gain a Duchy"})
                    .responseMultipleChoiceIndex();
        }
    }

}

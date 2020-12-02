package cards;

import server.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Catacombs extends Card {

    @Override
    public String name() {
        return "Catacombs";
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
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Look_at the_top 3_cards of your_deck.",
                "Choose_one:",
                "* Put_them into_your_hand",
                "* Discard_them and <+3_Cards>",
                "When_you trash_this, gain a_cheaper_card."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        List<Card> drawn = player.takeFromDraw(3);
        if (!drawn.isEmpty()) {
            if (choosePutIntoHand(player, game, drawn)) {
                game.message(player, "looking at and putting " + Card.htmlList(drawn) + " into your hand");
                game.messageOpponents(player, "looking at and putting " + Card.numCards(drawn.size()) + " into their hand");
                player.addToHand(drawn);
            } else {
                game.messageAll("looking at and discarding " + Card.htmlList(drawn));
                player.addToDiscard(drawn);
                plusCards(player, game, 3);
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    private boolean choosePutIntoHand(Player player, Game game, List<Card> cards) {
        if (player instanceof Bot) {
            return ((Bot) player).catacombsPutIntoHand(Collections.unmodifiableList(cards));
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": Put " + Card.htmlList(cards) + " into your hand, or discard them and then +3 cards?")
                .multipleChoices(new String[] {"Put into hand", "Discard, then +3 cards"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

    @Override
    public void onTrash(Player player, Game game) {
        // gain a cheaper card
        Set<Card> gainable = game.cardsCostingAtMost(this.cost(game) - 1);
        if (!gainable.isEmpty()) {
            Card toGain = promptChooseGainFromSupply(
                    player,
                    game,
                    gainable,
                    this.toString() + ": Choose a card to gain."
            );
            game.messageAll("gaining " + toGain.htmlName() + " because of " + this.htmlNameRaw());
        }
    }

}

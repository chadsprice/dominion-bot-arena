package cards;

import server.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Survivors extends Card {

    @Override
    public String name() {
        return "Survivors";
    }

    @Override
    public String plural() {
        return name();
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.RUINS);
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Look at the top 2 cards of your_deck.",
                "Discard them or put_them back in any_order."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        List<Card> drawn = player.takeFromDraw(2);
        if (!drawn.isEmpty()) {
            game.message(player, "drawing " + Card.htmlList(drawn));
            game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
            if (chooseDiscard(player, game, drawn)) {
                game.message(player, "discarding them");
                game.messageOpponents(player, "discarding " + Card.htmlList(drawn));
                player.addToDiscard(drawn);
            } else {
                game.messageAll("putting them back");
                putOnDeckInAnyOrder(player, game, drawn, "Survivors: Put them back in any order");
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    private boolean chooseDiscard(Player player, Game game, List<Card> cards) {
        if (player instanceof Bot) {
            return ((Bot) player).survivorsDiscard(Collections.unmodifiableList(cards));
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": You draw " + Card.htmlList(cards) + ". Discard them or put them back?")
                .multipleChoices(new String[] {"Discard", "Put back"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}

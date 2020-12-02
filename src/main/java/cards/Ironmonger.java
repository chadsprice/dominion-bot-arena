package cards;

import server.*;

import java.util.List;
import java.util.Set;

public class Ironmonger extends Card {

    @Override
    public String name() {
        return "Ironmonger";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+1_Action>",
                "Reveal the top_card of your_deck.",
                "You may discard_it.",
                "Either_way, if_it is_an...",
                "* Action_card, <+1_Action>",
                "* Treasure_card, <+1$>",
                "* Victory_card, <+1_Card>"
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // reveal the top card of your deck
        List<Card> top = player.takeFromDraw(1);
        if (!top.isEmpty()) {
            Card revealed = top.get(0);
            game.messageAll("drawing " + revealed.htmlName());
            // you may discard it
            if (chooseDiscard(player, game, revealed)) {
                game.messageAll("discarding it");
                player.addToDiscard(revealed);
            } else {
                game.messageAll("putting it back on top");
                player.putOnDraw(revealed);
            }
            if (revealed.isAction()) {
                plusActions(player, game, 1);
            }
            if (revealed.isTreasure()) {
                plusCoins(player, game, 1);
            }
            if (revealed.isVictory()) {
                plusCards(player, game, 1);
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    private boolean chooseDiscard(Player player, Game game, Card card) {
        if (player instanceof Bot) {
            return ((Bot) player).ironmongerDiscardTopOfDeck(card);
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": You draw " + card.htmlName() + ". Discard it or put it back?")
                .multipleChoices(new String[] {"Discard", "Put back"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}

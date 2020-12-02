package cards;

import server.*;

import java.util.List;
import java.util.Set;

public class Duchess extends Card {

    @Override
    public String name() {
        return "Duchess";
    }

    @Override
    public String plural() {
        return "Duchesses";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+2$>",
                "Each player (including_you) looks_at the_top_card of their_deck, and discards_it or puts_it_back.",
                "In games using_this, when you gain a_[Duchy], you_may gain a_[Duchess]."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 2);
        // each player looks at the top card of their deck and discards it or puts it back
        List<Player> playerOrder = game.getOpponents(player);
        playerOrder.add(0, player);
        for (Player eachPlayer : playerOrder) {
            List<Card> drawn = eachPlayer.takeFromDraw(1);
            if (!drawn.isEmpty()) {
                Card card = drawn.get(0);
                if (chooseDiscardTopOfDeck(eachPlayer, game, card)) {
                    game.message(eachPlayer, "You discard " + card.htmlName() + " from the top of your deck");
                    game.messageOpponents(eachPlayer, eachPlayer.username + " discards " + card.htmlName() + " from the top of their deck");
                    eachPlayer.addToDiscard(card);
                } else {
                    game.message(eachPlayer, "You leave the " + card.htmlNameRaw() + " on top of your deck");
                    game.messageOpponents(eachPlayer, eachPlayer.username + " leaves the card on top of their deck");
                    eachPlayer.putOnDraw(card);
                }
            } else {
                game.message(eachPlayer, "Your deck is empty");
                game.messageOpponents(eachPlayer, eachPlayer.username + "'s deck is empty");
            }
        }
    }

    private boolean chooseDiscardTopOfDeck(Player player, Game game, Card card) {
        if (player instanceof Bot) {
            return ((Bot) player).duchessDiscardTopOfDeck(card);
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": The top card of your deck is " + card.htmlName() + ". Discard it, or leave it on top?")
                .multipleChoices(new String[] {"Discard", "Leave on top"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}

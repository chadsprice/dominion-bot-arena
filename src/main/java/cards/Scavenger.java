package cards;

import server.*;

import java.util.*;

public class Scavenger extends Card {

    @Override
    public String name() {
        return "Scavenger";
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
                "<+2$>",
                "You may put your_deck into your discard_pile. Look_through your discard_pile and put one_card from_it on_top of your_deck."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 2);
        // you may put your deck into your discard pile
        if (!player.getDraw().isEmpty() && choosePutDeckIntoDiscard(player, game)) {
            game.message(player, "putting your deck into your discard pile");
            game.messageOpponents(player, "putting their deck into their discard pile");
            // this does not trigger the Tunnel reaction
            player.addToDiscard(player.takeFromDraw(player.getDraw().size()), false);
        }
        if (!player.getDiscard().isEmpty()) {
            Card toPutOnDeck = choosePutFromDiscardOntoDeck(player, game, new HashSet<>(player.getDiscard()));
            game.message(player, "putting " + toPutOnDeck.htmlName() + " from your discard on top of your deck");
            game.messageOpponents(player, "putting " + toPutOnDeck.htmlName() + " from their discard on top of their deck");
            player.removeFromDiscard(toPutOnDeck, 1);
            player.putOnDraw(toPutOnDeck);
        } else {
            game.message(player, "your discard is empty");
            game.messageOpponents(player, "their discard is empty");
        }
    }

    private boolean choosePutDeckIntoDiscard(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).scavengerPutDeckIntoDiscard();
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": Put your deck into your discard pile?")
                .multipleChoices(new String[] {"Yes", "No"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

    private Card choosePutFromDiscardOntoDeck(Player player, Game game, Set<Card> cards) {
        if (player instanceof Bot) {
            Card card = ((Bot) player).scavengerPutFromDiscardOntoDeck(Collections.unmodifiableSet(cards));
            checkContains(cards, card);
            return card;
        }
        return promptMultipleChoiceCard(
                player,
                game,
                Prompt.Type.NORMAL,
                this.toString() + ": Choose a card in your discard to put on top of your deck.",
                cards
        );
    }

}

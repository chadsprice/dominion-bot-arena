package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Mandarin extends Card {

    @Override
    public String name() {
        return "Mandarin";
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
                "<+3$>",
                "Put a_card from your_hand on_top of your_deck.",
                "When you gain_this, put all Treasures you have in_play on_top of your_deck in_any_order."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 3);
        if (!player.getHand().isEmpty()) {
            Card toPutOnDeck = promptChoosePutOnDeck(
                    player,
                    game,
                    new HashSet<>(player.getHand()),
                    this.toString() + ": Put a card from your hand on top of your deck."
            );
            game.message(player, "putting " + toPutOnDeck.htmlName() + " on top of your deck");
            game.messageOpponents(player, "putting a card on top of their deck");
            player.putFromHandOntoDraw(toPutOnDeck);
        }
    }

    @Override
    public void onGain(Player player, Game game) {
        // put all treasures you have in play on top of your deck in any order
        List<Card> treasuresInPlay = player.getPlay().stream()
                .filter(Card::isTreasure)
                .collect(Collectors.toList());
        player.removeFromPlay(treasuresInPlay);
        game.message(player, "putting " + Card.htmlList(treasuresInPlay) + " on top of your deck");
        game.messageOpponents(player, "putting " + Card.htmlList(treasuresInPlay) + " on top of their deck");
        putOnDeckInAnyOrder(
                player,
                game,
                treasuresInPlay,
                this.toString() + ": Put all Treasures you have in play on top of your deck in any order"
        );
    }

}

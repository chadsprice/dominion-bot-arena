package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class Vagrant extends Card {

    @Override
    public String name() {
        return "Vagrant";
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
                "<+1_Card>",
                "<+1_Action>",
                "Reveal the top card of your_deck. If_it's a_Curse, Ruins, Shelter, or Victory card, put_it into your_hand."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        List<Card> drawn = player.takeFromDraw(1);
        if (!drawn.isEmpty()) {
            Card revealed = drawn.get(0);
            if (revealed == Cards.CURSE || revealed.isRuins() || revealed.isShelter() || revealed.isVictory()) {
                game.message(player, "drawing " + revealed.htmlName() + ", putting it into your hand");
                game.messageOpponents(player, "drawing " + revealed.htmlName() + ", putting it into their hand");
                player.addToHand(revealed);
            } else {
                game.messageAll("drawing " + revealed.htmlName() + ", discarding it");
                player.addToDiscard(revealed);
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

}

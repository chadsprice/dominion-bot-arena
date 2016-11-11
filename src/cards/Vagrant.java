package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;

public class Vagrant extends Card {

    public Vagrant() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        List<Card> drawn = player.takeFromDraw(1);
        if (!drawn.isEmpty()) {
            Card revealed = drawn.get(0);
            if (revealed == Cards.CURSE || revealed.isRuins || revealed.isShelter || revealed.isVictory) {
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

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "Reveal the top card of your deck. If it's a Curse, Ruins, Shelter, or Victory card, put it into your hand."};
    }

    @Override
    public String toString() {
        return "Vagrant";
    }

}

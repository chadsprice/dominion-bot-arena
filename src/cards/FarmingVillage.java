package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.List;

public class FarmingVillage extends Card {

    public FarmingVillage() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 2);
        // reveal cards from the top of your deck until revealing an action or treasure
        List<Card> revealed = new ArrayList<Card>();
        Card actionOrTreasure = null;
        for (;;) {
            List<Card> drawn = player.takeFromDraw(1);
            if (drawn.isEmpty()) {
                break;
            }
            Card card = drawn.get(0);
            revealed.add(card);
            if (card.isAction || card.isTreasure) {
                actionOrTreasure = card;
                break;
            }
        }
        if (!revealed.isEmpty()) {
            game.messageAll("revealing " + Card.htmlList(revealed));
            if (actionOrTreasure != null) {
                game.message(player, "putting the " + actionOrTreasure.htmlNameRaw() + " into your hand");
                game.messageOpponents(player, "putting the " + actionOrTreasure.htmlNameRaw() + " into their hand");
                revealed.remove(actionOrTreasure);
                player.addToHand(actionOrTreasure);
            }
            if (!revealed.isEmpty()) {
                game.messageAll("discarding the rest");
                player.addToDiscard(revealed);
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+2 Actions", "Reveal cards from the top of your deck until you reveal an Action or Treasure card. Put that card into your hand and discard the other cards."};
    }

    @Override
    public String toString() {
        return "Farming Village";
    }

}

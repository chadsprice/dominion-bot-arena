package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.List;

public class FortuneTeller extends Card {

    public FortuneTeller() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        plusCoins(player, game, 2);
        for (Player target : targets) {
            List<Card> revealed = new ArrayList<Card>();
            Card victoryOrCurse = null;
            for (;;) {
                List<Card> drawn = target.takeFromDraw(1);
                if (drawn.isEmpty()) {
                    break;
                }
                Card card = drawn.get(0);
                revealed.add(card);
                if (card.isVictory || card == Card.CURSE) {
                    victoryOrCurse = card;
                    break;
                }
            }
            if (!revealed.isEmpty()) {
                game.message(target, "you reveal " + Card.htmlList(revealed));
                game.messageOpponents(target, target.username + " reveals " + Card.htmlList(revealed));
                game.messageIndent++;
                if (victoryOrCurse != null) {
                    game.messageAll("putting the " + victoryOrCurse.htmlNameRaw() + " back on top");
                    revealed.remove(victoryOrCurse);
                    target.putOnDraw(victoryOrCurse);
                }
                if (!revealed.isEmpty()) {
                    game.messageAll("discarding the rest");
                    target.addToDiscard(revealed);
                }
                game.messageIndent--;
            } else {
                game.message(target, "Your deck is empty");
                game.messageOpponents(target, target.username + "'s deck is empty");
            }
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+$2", "Each other player reveals cards from the top of their deck until they reveal a Victory or Curse card. They put it on top and discard the other revealed cards."};
    }

    @Override
    public String toString() {
        return "Fortune Teller";
    }

}

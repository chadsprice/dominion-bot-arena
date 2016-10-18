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
            // reveal cards from the top of your deck until revealing victory or curse
            // put it top
            revealUntil(target, game,
                    c -> c.isVictory || c == Card.CURSE,
                    c -> putRevealedOnDeck(target, game, c),
                    true);
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

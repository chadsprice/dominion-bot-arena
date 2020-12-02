package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;

public class FortuneTeller extends Card {

    @Override
    public String name() {
        return "Fortune Teller";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+2$>",
                "Each other_player reveals cards from the_top of their_deck until they reveal a_Victory or Curse card. They put_it on_top and discard the_other revealed_cards."
        };
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        plusCoins(player, game, 2);
        for (Player target : targets) {
            // reveal cards from the top of your deck until revealing victory or curse
            // put it top
            revealUntil(
                    target,
                    game,
                    c -> c.isVictory() || c == Cards.CURSE,
                    c -> putRevealedOnDeck(target, game, c),
                    true
            );
        }
    }

}

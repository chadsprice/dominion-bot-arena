package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class MasqueradeFirstEdition extends Card {

    @Override
    public String name() {
        return "Masquerade (1st ed.)";
    }

    @Override
    public String plural() {
        return "Masquerades (1st ed.)";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+2_Cards>",
                "Each player passes a_card from their_hand to_the_left at_once. Then_you may trash a_card from_your_hand."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        onMasqueradeVariant(player, game, true);
    }

}

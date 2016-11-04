package cards;

import server.Card;
import server.Game;
import server.Player;

public class MasqueradeFirstEdition extends Card {

    public MasqueradeFirstEdition() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        onMasqueradeVariant(player, game, true);
    }

    @Override
    public String[] description() {
        return new String[] {"+2 Cards", "Each player passes a card from their hand to the left at once. Then you may trash a card from your hand."};
    }

    @Override
    public String toString() {
        return "Masquerade (1st ed.)";
    }

    @Override
    public String plural() {
        return "Masquerades (1st ed.)";
    }

}

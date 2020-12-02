package cards;

import server.Card;
import server.Game;
import server.Player;
import server.Prompt;

import java.util.List;
import java.util.Set;

public class Mercenary extends Card {

    @Override
    public String name() {
        return "Mercenary";
    }

    @Override
    public String plural() {
        return "Mercenaries";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public String[] description() {
        return new String[] {
                "You may trash 2_cards from your_hand.",
                "If_you_do, <+2_Cards>, <+2$>, and each other_player discards down_to 3_cards in_hand.",
                "(This_is_not in the_supply.)"};
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        if (player.getHand().size() >= 2) {
            // you may trash 2 cards from your hand
            List<Card> toTrash = promptTrashNumber(
                    player,
                    game,
                    2,
                    Prompt.Amount.EXACT_OR_NONE
            );
            if (toTrash.size() == 2) {
                game.messageAll("trashing " + Card.htmlList(toTrash));
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
                // benefits for trashing 2 cards
                plusCards(player, game, 2);
                plusCoins(player, game, 2);
                // each other player discards down to 3
                handSizeAttack(targets, game, 3);
            } else {
                game.messageAll("trashing nothing");
            }
        } else if (player.getHand().size() == 1) {
            // special case: if you have only one card in hand, you are allowed to trash it
            List<Card> toTrash = promptTrashNumber(
                    player,
                    game,
                    1,
                    Prompt.Amount.UP_TO
            );
            if (toTrash.size() == 1) {
                game.messageAll("trashing " + Card.htmlList(toTrash));
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
            } else {
                game.messageAll("trashing nothing");
            }
        } else {
            game.messageAll("having no cards in hand to trash");
        }
    }

}

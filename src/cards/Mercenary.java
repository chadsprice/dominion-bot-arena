package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Mercenary extends Card {

    public Mercenary() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 0;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        if (!player.getHand().isEmpty()) {
            // you may trash 2 cards from your hand
            List<Card> toTrash = game.promptTrashNumber(player, 2, false, this.toString());
            if (toTrash.size() == 2) {
                game.messageAll("trashing " + Card.htmlList(toTrash));
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
                // benefits for trashing 2 cards
                plusCards(player, game, 2);
                plusCoins(player, game, 2);
                targets.forEach(target -> {
                    if (target.getHand().size() > 3) {
                        int count = target.getHand().size() - 3;
                        List<Card> discarded = game.promptDiscardNumber(target, count, "Mercenary");
                        game.message(target, "You discard " + Card.htmlList(discarded));
                        game.messageOpponents(target, target.username + " discards " + Card.htmlList(discarded));
                        target.putFromHandIntoDiscard(discarded);
                    }
                });
            } else if (player.getHand().size() == 1 && toTrash.size() == 1) {
                // if you have only one card in hand, you are allowed to trash it
                game.messageAll("trashing " + Card.htmlList(toTrash));
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
            } else {
                if (toTrash.size() == 1) {
                    // you tried to trash just one card when you weren't allowed to
                    player.sendHand();
                }
                game.messageAll("trashing nothing");
            }
        }
    }

    @Override
    public String[] description() {
        return new String[] {"You may trash 2 cards from your hand.", "If you do, +2 Cards, +$2, and each other player discards down to 3 cards in hand.", "(This is not in the supply.)"};
    }

    @Override
    public String toString() {
        return "Mercenary";
    }

    @Override
    public String plural() {
        return "Mercenaries";
    }

}

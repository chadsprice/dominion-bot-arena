package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Rats extends Card {

    public Rats() {
        isAction = true;
    }

    @Override
    public int startingSupply(int numPlayers) {
        return 20;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // gain a Rats
        if (game.supply.get(this) != 0) {
            game.messageAll("gaining " + this.htmlName());
            game.gain(player, this);
        }
        Set<Card> trashable = player.getHand().stream().filter(c -> c != this).collect(Collectors.toSet());
        if (!trashable.isEmpty()) {
            // trash a card from your hand other than Rats
            Card toTrash = game.promptChooseTrashFromHand(player, trashable, "Rats: Trash a card from your hand other than " + this.htmlNameRaw() + ".");
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.addToTrash(player, toTrash);
        } else {
            // reveal a hand of all Rats
            revealHand(player, game);
        }
    }

    @Override
    public void onTrash(Player player, Game game) {
        List<Card> drawn = player.drawIntoHand(1);
        if (!drawn.isEmpty()) {
            Card card = drawn.get(0);
            game.message(player, "drawing " + card.htmlName() + " because of " + this.htmlNameRaw());
            game.messageOpponents(player, "drawing a card because of " + this.htmlNameRaw());
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Card", "+1 Action", "Gain a Rats. Trash a card from your hand other than Rats (or reveal a hand of all Rats).", "When you trash this, +1 Card."};
    }

    @Override
    public String toString() {
        return "Rats";
    }

    @Override
    public String plural() {
        return toString();
    }

}

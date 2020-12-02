package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Rats extends Card {

    @Override
    public String name() {
        return "Rats";
    }

    @Override
    public String plural() {
        return name();
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
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
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+1_Action>",
                "Gain a_[Rats]. Trash a_card from your_hand other_than [Rats] (or_reveal a_hand of_all_[Rats]).",
                "When you trash_this, <+1_Card>."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 1);
        plusActions(player, game, 1);
        // gain a Rats
        if (game.isAvailableInSupply(Cards.RATS)) {
            game.messageAll("gaining " + Cards.RATS.htmlName());
            game.gain(player, Cards.RATS);
        }
        Set<Card> trashable = player.getHand().stream()
                .filter(c -> !(c instanceof Rats))
                .collect(Collectors.toSet());
        if (!trashable.isEmpty()) {
            // trash a card from your hand other than Rats
            Card toTrash = promptChooseTrashFromHand(
                    player,
                    game,
                    trashable,
                    this.toString() + ": Trash a card from your hand other than " + Cards.RATS.htmlNameRaw() + "."
            );
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
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

}

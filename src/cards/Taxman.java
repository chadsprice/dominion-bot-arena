package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Taxman extends Card {

    public Taxman() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        // you may trash a treasure from your hand
        Set<Card> trashable = player.getHand().stream()
                .filter(c -> c.isTreasure)
                .collect(Collectors.toSet());
        if (!trashable.isEmpty()) {
            Card toTrash = chooseTrash(player, game, trashable);
            if (toTrash != null) {
                game.messageAll("trashing " + toTrash.htmlName());
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
                // each other player with 5 or more cards in hand discards a copy of it (or reveals a hand without it)
                targets.forEach(target -> {
                    if (target.getHand().size() >= 5) {
                        if (target.getHand().contains(toTrash)) {
                            game.message(target, "You discard " + toTrash.htmlName());
                            game.messageOpponents(target, target.username + " discards " + toTrash.htmlName());
                            target.putFromHandIntoDiscard(toTrash);
                        } else {
                            game.message(target, "You reveal your hand: " + Card.htmlList(target.getHand()));
                            game.messageOpponents(target, target.username + " reveals their hand: " + Card.htmlList(target.getHand()));
                        }
                    }
                });
                // gain a treasure card costing up to $3 more than the trashed card, putting it on top of your deck
                Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost(game) + 3).stream()
                        .filter(c -> c.isTreasure)
                        .collect(Collectors.toSet());
                if (!gainable.isEmpty()) {
                    Card toGain = game.promptChooseGainFromSupply(player, gainable,
                            this.toString() + ": Choose a treasure to gain onto your deck costing up to $3 more.");
                    game.message(player, "gaining " + toGain.htmlName() + " onto your deck");
                    game.messageOpponents(player, "gaining " + toGain.htmlName() + " onto their deck");
                    game.gainToTopOfDeck(player, toGain);
                }
            }
        }
    }

    private Card chooseTrash(Player player, Game game, Set<Card> trashable) {
        if (player instanceof Bot) {
            Card toTrash = ((Bot) player).taxmanTrash(trashable);
            if (toTrash != null && !trashable.contains(toTrash)) {
                throw new IllegalStateException();
            }
            return toTrash;
        }
        return game.promptChooseTrashFromHand(player, trashable,
                this.toString() + ": You may trash a treasure from your hand.",
                false, "Trash nothing");
    }

    @Override
    public String[] description() {
        return new String[] {"You may trash a Treasure from your hand. Each other player with 5 or more cards discards a copy of it (or reveals a hand without it). Gain a Treasure card costing up to $3 more than the trashed card, putting it on top of your deck."};
    }

    @Override
    public String toString() {
        return "Taxman";
    }

    @Override
    public String plural() {
        return "Taxmen";
    }

}

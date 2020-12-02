package cards;

import server.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Taxman extends Card {

    @Override
    public String name() {
        return "Taxman";
    }

    @Override
    public String plural() {
        return "Taxmen";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {"You may trash a_Treasure from your_hand. Each other_player with 5_or_more cards discards a_copy of_it (or_reveals a_hand without_it). Gain a_Treasure card costing up_to 3$_more than the_trashed card, putting_it on_top of your_deck."};
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        // you may trash a treasure from your hand
        Set<Card> trashable = player.getHand().stream()
                .filter(Card::isTreasure)
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
                        .filter(Card::isTreasure)
                        .collect(Collectors.toSet());
                if (!gainable.isEmpty()) {
                    Card toGain = promptChooseGainFromSupply(
                            player,
                            game,
                            gainable,
                            this.toString() + ": Choose a treasure to gain onto your deck costing up to $3 more."
                    );
                    game.message(player, "gaining " + toGain.htmlName() + " onto your deck");
                    game.messageOpponents(player, "gaining " + toGain.htmlName() + " onto their deck");
                    game.gainToTopOfDeck(player, toGain);
                }
            } else {
                game.messageAll("trashing nothing");
            }
        } else {
            game.messageAll("having no treasure to trash");
        }
    }

    private Card chooseTrash(Player player, Game game, Set<Card> trashable) {
        if (player instanceof Bot) {
            Card toTrash = ((Bot) player).taxmanTrash(Collections.unmodifiableSet(trashable));
            checkContains(trashable, toTrash, false);
            return toTrash;
        }
        return new Prompt(player, game)
                .type(Prompt.Type.DANGER)
                .message(this.toString() + ": You may trash a treasure from your hand.")
                .orNone("Trash nothing")
                .responseCard();
    }

}

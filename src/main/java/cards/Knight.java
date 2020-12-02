package cards;

import server.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Knight extends Card {

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

    @Override
    public String htmlType() {
        return "Action-Attack-Knight";
    }

    @Override
    public MixedPileId mixedPileId() {
        return MixedPileId.KNIGHTS;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        List<String> description = new ArrayList<>();
        description.add("Each other_player reveals the top 2_cards of their_deck, trashes one_of_them costing from_$3 to_$6, and discards the_rest. If a_Knight is_trashed by_this, trash this_card.");
        addUniqueDescription(description);
        return description.toArray(new String[0]);
    }

    protected abstract void addUniqueDescription(List<String> description);

    @Override
    public boolean onAttack(Player player, Game game, List<Player> targets, boolean hasMoved) {
        knightUniqueAction(player, game, targets);
        boolean movedToTrash = false;
        for (Player target : targets) {
            List<Card> drawn = target.takeFromDraw(2);
            if (!drawn.isEmpty()) {
                game.message(target, "You draw " + Card.htmlList(drawn));
                game.messageOpponents(target, target.username + " draws " + Card.htmlList(drawn));
                game.messageIndent++;
                Set<Card> trashable = drawn.stream()
                        .filter(c -> 3 <= c.cost(game) && c.cost(game) <= 6)
                        .collect(Collectors.toSet());
                if (!trashable.isEmpty()) {
                    Card toTrash;
                    if (trashable.size() == 1) {
                        toTrash = trashable.iterator().next();
                    } else {
                        toTrash = chooseTrash(target, game, trashable);
                    }
                    game.messageAll("trashing the " + toTrash.htmlNameRaw());
                    drawn.remove(toTrash);
                    game.trash(target, toTrash);
                    // if another knight was trashed and this hasn't been trashed yet
                    if (toTrash instanceof Knight && !hasMoved && !movedToTrash) {
                        // trash this
                        game.messageIndent++;
                        game.message(player, "You trash " + this.htmlName());
                        game.message(player, player.username + " trashes " + this.htmlName());
                        player.removeFromPlay(this);
                        game.trash(player, this);
                        movedToTrash = true;
                        game.messageIndent--;
                    }
                }
                if (!drawn.isEmpty()) {
                    game.messageAll("discarding the rest");
                    target.addToDiscard(drawn);
                }
                game.messageIndent--;
            } else {
                game.message(target, "Your deck is empty");
                game.messageOpponents(target, target.username + "'s deck is empty");
            }
        }
        return movedToTrash;
    }

    private Card chooseTrash(Player player, Game game, Set<Card> trashable) {
        if (player instanceof Bot) {
            Card toTrash = ((Bot) player).topTwoCardAttackTrash(Collections.unmodifiableSet(trashable));
            checkContains(trashable, toTrash);
            return toTrash;
        }
        return promptMultipleChoiceCard(
                player,
                game,
                Prompt.Type.DANGER,
                this.toString() + ": You draw " + Card.htmlList(new ArrayList<>(trashable)) + ". Choose one to trash.",
                trashable
        );
    }

    protected void knightUniqueAction(Player player, Game game, List<Player> targets) {
        // Dame Josephine and Sir Vander do just the common knight attack when played
    }

}

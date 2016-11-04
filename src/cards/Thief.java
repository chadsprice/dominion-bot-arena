package cards;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

public class Thief extends Card {

	public Thief() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		targets.forEach(target -> {
			// draw 2 cards
			List<Card> drawn = target.takeFromDraw(2);
			if (!drawn.isEmpty()) {
				// announce the drawn cards
				game.message(target, "You draw " + Card.htmlList(drawn));
				game.messageOpponents(target, target.username + " draws " + Card.htmlList(drawn));
				game.messageIndent++;
				// filter out the treasures
				Set<Card> trashable = drawn.stream().filter(c -> c.isTreasure).collect(Collectors.toSet());
				if (!trashable.isEmpty()) {
					// choose one to trash
					Card toTrash;
					if (trashable.size() == 1) {
						toTrash = trashable.iterator().next();
					} else {
						toTrash = chooseTrash(player, game, trashable, target);
					}
					// trash it
					game.messageAll("trashing the " + toTrash.htmlNameRaw());
					drawn.remove(toTrash);
					game.trash(target, toTrash);
                    // you may gain the trashed treasure
                    if (chooseGainTrashed(player, game, toTrash)) {
                        game.message(player, "You gain the trashed " + toTrash.htmlNameRaw());
                        game.messageOpponents(player, player.username + " gains the trashed " + toTrash.htmlNameRaw());
                        game.gainFromTrash(player, toTrash);
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
		});
	}

	private Card chooseTrash(Player player, Game game, Set<Card> trashable, Player target) {
        if (player instanceof Bot) {
            Card toTrash = ((Bot) player).thiefTrash(trashable);
            if (!trashable.contains(toTrash)) {
                throw new IllegalStateException();
            }
            return toTrash;
        }
        return game.promptMultipleChoiceCard(player, this.toString() + ": " + target.username + " draws " + Card.htmlList(new ArrayList<>(trashable)) + ". Choose which one they trash", "actionPrompt", trashable);
    }

    private boolean chooseGainTrashed(Player player, Game game, Card trashed) {
        if (player instanceof Bot) {
            return ((Bot) player).thiefGainTrashed(trashed);
        }
        int choice = game.promptMultipleChoice(player, this.toString() + ": Gain the trashed " + trashed.htmlNameRaw() + "?", new String[] {"Gain", "Don't"});
        return (choice == 0);
    }

	@Override
	public String[] description() {
		return new String[] {"Each other player reveals the top 2 cards of their deck.", "If they revealed any Treasure cards, they trash one of them that you choose.", "You may gain any or all of these trashed cards. They discard the other revealed cards."};
	}

	@Override
	public String toString() {
		return "Thief";
	}

	@Override
	public String plural() {
		return "Thieves";
	}

}

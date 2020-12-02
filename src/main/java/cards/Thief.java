package cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import server.*;

public class Thief extends Card {

	@Override
	public String name() {
		return "Thief";
	}

	@Override
	public String plural() {
		return "Thieves";
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
        return new String[] {
                "Each other player reveals the top 2 cards of their_deck.",
                "If they revealed any_Treasure cards, they_trash one of_them that you_choose.",
                "You may gain any or all of_these trashed_cards. They_discard the_other revealed_cards."
        };
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
				Set<Card> trashable = drawn.stream()
						.filter(Card::isTreasure)
						.collect(Collectors.toSet());
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
            Card toTrash = ((Bot) player).thiefTrash(Collections.unmodifiableSet(trashable));
            checkContains(trashable, toTrash);
            return toTrash;
        }
        return promptMultipleChoiceCard(
        		player,
				game,
				Prompt.Type.NORMAL,
				this.toString() + ": " + target.username + " draws " + Card.htmlList(new ArrayList<>(trashable)) + ". Choose which one they trash",
				trashable
		);
    }

    private boolean chooseGainTrashed(Player player, Game game, Card trashed) {
        if (player instanceof Bot) {
            return ((Bot) player).thiefGainTrashed(trashed);
        }
        int choice = new Prompt(player, game)
				.message(this.toString() + ": Gain the trashed " + trashed.htmlNameRaw() + "?")
				.multipleChoices(new String[] {"Gain", "Don't"})
				.responseMultipleChoiceIndex();
        return (choice == 0);
    }

}

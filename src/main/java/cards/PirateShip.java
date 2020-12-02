package cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import server.*;

public class PirateShip extends Card {

	@Override
	public String name() {
		return "Pirate Ship";
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
				"Choose_one:",
				"* Each other player reveals the_top 2_cards of their_deck, trashes a revealed Treasure that you_choose, and discards the_rest. If anyone trashed a_Treasure, gain a Pirate token.",
				"* <+1$> per Pirate token you have collected"
		};
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		if (chooseAttack(player, game)) {
			boolean trashedTreasure = false;
			for (Player target : targets) {
				// reveal the target's top two cards and count the distinct treasures
				List<Card> top = target.takeFromDraw(2);
				if (!top.isEmpty()) {
					game.message(target, "You draw " + Card.htmlList(top));
					game.messageOpponents(target, target.username + " draws " + Card.htmlList(top));
					game.messageIndent++;
					Set<Card> treasures = top.stream()
							.filter(Card::isTreasure)
							.collect(Collectors.toSet());
					if (!treasures.isEmpty()) {
						Card toTrash;
						if (treasures.size() == 1) {
							toTrash = treasures.iterator().next();
						} else {
							toTrash = chooseOpponentTrash(player, game, target, treasures);
						}
						game.messageAll("trashing the " + toTrash.htmlNameRaw());
						top.remove(toTrash);
						game.trash(target, toTrash);
						trashedTreasure = true;
					}
					// discard the rest
					if (!top.isEmpty()) {
						game.messageAll("discarding the rest");
						target.addToDiscard(top);
					}
					game.messageIndent--;
				} else {
					game.message(target, "Your deck is empty");
					game.messageOpponents(target, target.username + "'s deck is empty");
				}
			}
			// if any target trashed a treasure
			if (trashedTreasure) {
				game.message(player, "You gain a Pirate token");
				game.messageOpponents(player, player.username + " gains a Pirate token");
				player.addPirateShipToken();
			}
		} else {
			game.messageAll("getting +$" + player.getPirateShipTokens());
			player.coins += player.getPirateShipTokens();
		}
	}

	private boolean chooseAttack(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).pirateShipAttack();
		}
		int choice = new Prompt(player, game)
				.message(this.toString() + ": Choose one")
				.multipleChoices(new String[] {"Attack", "+$1 per token"})
				.responseMultipleChoiceIndex();
		return (choice == 0);
	}

	private Card chooseOpponentTrash(Player player, Game game, Player opponent, Set<Card> treasures) {
		if (player instanceof Bot) {
			Card toTrash = ((Bot) player).pirateShipOpponentTrash(Collections.unmodifiableSet(treasures));
			checkContains(treasures, toTrash);
			return toTrash;
		}
		return promptMultipleChoiceCard(
				player,
				game,
				Prompt.Type.NORMAL,
				this.toString() + ": " + opponent.username + " draws " + Card.htmlList(treasures) + ". Have them trash which one?",
				treasures
		);
	}

}

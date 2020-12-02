package cards;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import server.*;

public class Bishop extends Card {

	@Override
	public String name() {
		return "Bishop";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+1$>",
				"<+1_VP>",
				"Trash_a_card from_your_hand.",
				"<+VP> equal_to half its_cost in_coins rounded_down.",
				"Each_other_player may trash_a_card from_their_hand."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCoins(player, game, 1);
		plusVictoryTokens(player, game, 1);
		// trash a card for +VP equal to half its cost in coins rounded down
		if (!player.getHand().isEmpty()) {
			Card toTrash = promptChooseTrashFromHand(
				player,
				game,
				new HashSet<>(player.getHand()),
				this.toString() + ": Choose a card to trash for +VP equal to half its cost rounded down."
			);
			player.removeFromHand(toTrash);
			game.trash(player, toTrash);
			int victoryTokensToGain = toTrash.cost(game) / 2;
			player.addVictoryTokens(victoryTokensToGain);
			game.messageAll("trashing " + toTrash.htmlName() + " for +" + victoryTokensToGain + " VP");
		} else {
			game.messageAll("having no card to trash");
		}
		// each other player may trash a card
		game.getOpponents(player).stream()
			.filter(opponent -> !opponent.getHand().isEmpty())
			.forEach(opponent -> {
				Card toTrash = chooseOptionalTrash(opponent, game);
				if (toTrash != null) {
					game.message(opponent, "you trash " + toTrash.htmlName());
					game.messageOpponents(opponent, opponent.username + " trashes " + toTrash.htmlName());
					opponent.removeFromHand(toTrash);
					game.trash(opponent, toTrash);
				}
			});
	}

	private Card chooseOptionalTrash(Player player, Game game) {
		Set<Card> trashable = new HashSet<>(player.getHand());
		if (player instanceof Bot) {
			Bot bot = (Bot) player;
			Card toTrash = bot.bishopOptionalTrash(Collections.unmodifiableSet(trashable));
			checkContains(trashable, toTrash, false);
			return toTrash;
		}
		return new Prompt(player, game)
			.type(Prompt.Type.DANGER)
			.message(this.toString() + ": You may trash a card from your hand.")
			.handChoices(trashable)
			.orNone("Trash Nothing")
			.responseCard();
	}

}

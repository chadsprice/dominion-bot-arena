package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

public class Loan extends Card {

	public Loan() {
		isTreasure = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public int treasureValue(Game game) {
		return 1;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// reveal cards from your deck until you reveal a treasure
		revealUntil(player, game,
				c -> c.isTreasure,
				treasure -> {
					// discard or trash the revealed treasure
					if (chooseDiscardOverTrash(player, game, treasure)) {
						player.addToDiscard(treasure);
						game.messageAll("discarding the " + treasure.htmlNameRaw());
					} else {
						game.trash(player, treasure);
						game.messageAll("trashing the " + treasure.htmlNameRaw());
					}
				});
	}

	private boolean chooseDiscardOverTrash(Player player, Game game, Card card) {
		if (player instanceof Bot) {
			return ((Bot) player).loanDiscardOverTrash(card);
		}
		int choice = game.promptMultipleChoice(player, "You draw " + card.htmlName() + ", discard or trash it?", new String[] {"Discard", "Trash"});
		return (choice == 0);
	}

	@Override
	public String[] description() {
		return new String[]{"$1", "When you play this, reveal cards from your deck until you reveal a Treasure.", "Discard it or trash it.", "Discard the other cards."};
	}

	@Override
	public String toString() {
		return "Loan";
	}

}

package cards;

import server.*;

import java.util.Set;

public class Loan extends Card {

	@Override
	public String name() {
		return "Loan";
	}

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE);
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[]{
				"1$",
				"When you play_this, reveal_cards from your_deck until you reveal a_Treasure.",
				"Discard it or trash_it.",
				"Discard the other cards."
		};
	}

	@Override
	public int treasureValue(Game game) {
		return 1;
	}

	@Override
	public void onPlay(Player player, Game game) {
		// reveal cards from your deck until you reveal a treasure
		revealUntil(
		        player,
                game,
				Card::isTreasure,
				treasure -> {
					// discard or trash the revealed treasure
					if (chooseDiscardOverTrash(player, game, treasure)) {
						player.addToDiscard(treasure);
						game.messageAll("discarding the " + treasure.htmlNameRaw());
					} else {
						game.trash(player, treasure);
						game.messageAll("trashing the " + treasure.htmlNameRaw());
					}
				}
		);
	}

	private boolean chooseDiscardOverTrash(Player player, Game game, Card card) {
		if (player instanceof Bot) {
			return ((Bot) player).loanDiscardOverTrash(card);
		}
		int choice = new Prompt(player, game)
                .message(this.toString() + ": You draw " + card.htmlName() + ". Discard or trash it?")
                .multipleChoices(new String[] {"Discard", "Trash"})
                .responseMultipleChoiceIndex();
		return (choice == 0);
	}

}

package cards;

import server.*;

import java.util.Set;

public class Nobles extends Card {

	@Override
	public String name() {
		return "Nobles";
	}

	@Override
	public String plural() {
		return name();
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.VICTORY);
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public String[] description() {
		return new String[] {
				"Choose_one:",
				"* <+3_Cards>",
				"* <+2_Actions>",
				"2_VP"
		};
	}

	@Override
	public int victoryValue() {
		return 2;
	}

	@Override
	public void onPlay(Player player, Game game) {
		if (chooseCardsOverActions(player, game)) {
			plusCards(player, game, 3);
		} else {
			plusActions(player, game, 2);
		}
	}

	private boolean chooseCardsOverActions(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).noblesCardsOverActions();
		}
		int choice = new Prompt(player, game)
				.message(this.toString() + ": Choose one")
				.multipleChoices(new String[] {"+3 Cards", "+2 Actions"})
				.responseMultipleChoiceIndex();
		return (choice == 0);
	}

}

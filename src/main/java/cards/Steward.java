package cards;

import java.util.List;
import java.util.Set;

import server.*;

public class Steward extends Card {

	@Override
	public String name() {
		return "Steward";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 3;
	}

    @Override
    public String[] description() {
        return new String[] {
                "Choose_one:",
                "* <+2_Cards>",
                "* <+2$>",
                "* Trash 2_cards from your_hand"
        };
    }

	@Override
	public void onPlay(Player player, Game game) {
		switch (stewardBenefit(player, game)) {
			case 0:
				plusCards(player, game, 2);
				break;
			case 1:
				plusCoins(player, game, 2);
				break;
			default: // case 2
				// trash 2 cards from your hand
				if (!player.getHand().isEmpty()) {
					List<Card> toTrash = promptTrashNumber(player, game, 2);
					game.messageAll("trashing " + Card.htmlList(toTrash));
					player.removeFromHand(toTrash);
					game.trash(player, toTrash);
				} else {
					game.messageAll("having no cards in hand to trash");
				}
		}
	}

	private int stewardBenefit(Player player, Game game) {
		if (player instanceof Bot) {
			int choice = ((Bot) player).stewardBenefit();
			checkMultipleChoice(3, choice);
			return choice;
		}
		return new Prompt(player, game)
				.message(this.toString() + ": Choose one")
				.multipleChoices(new String[] {"+2 cards", "+$2", "Trash 2 cards"})
				.responseMultipleChoiceIndex();
	}

}

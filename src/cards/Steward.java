package cards;

import java.util.List;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

public class Steward extends Card {

	public Steward() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
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
					List<Card> toTrash = game.promptTrashNumber(player, 2, true, this.toString());
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
			int benefit = ((Bot) player).stewardBenefit();
			if (benefit < 0 || benefit > 2) {
				throw new IllegalStateException();
			}
			return benefit;
		}
		return game.promptMultipleChoice(player, this.toString() + ": Choose one", new String[] {"+2 cards", "+$2", "Trash 2 cards"});
	}

	@Override
	public String[] description() {
		return new String[] {"Choose one: +2 Cards; or +$2; or trash 2 cards from your hand."};
	}

	@Override
	public String toString() {
		return "Steward";
	}

}

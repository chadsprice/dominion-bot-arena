package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

public class Explorer extends Card {

	public Explorer() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		boolean revealedProvince = false;
		// if you have a province in hand, choose to reveal it or not
		if (player.getHand().contains(Cards.PROVINCE)) {
			int choice = game.promptMultipleChoice(player, "Explorer: Reveal " + Cards.PROVINCE.htmlName() + " from your hand?", new String[] {"Reveal Province", "Don't"});
			if (choice == 0) {
				revealedProvince = true;
				game.message(player, "revealing " + Cards.PROVINCE.htmlName() + " from your hand");
				game.messageOpponents(player, "revealing " + Cards.PROVINCE.htmlName() + " from their hand");
			}
		}
		// gain the respective card, putting it into your hand
		Card toGain = revealedProvince ? Cards.GOLD : Cards.SILVER;
		if (game.supply.get(toGain) > 0) {
			game.message(player, "gaining " + toGain.htmlName() + ", putting it into your hand");
			game.messageOpponents(player, "gaining " + toGain.htmlName() + ", putting it into their hand");
			game.gainToHand(player, toGain);
		} else {
			game.messageAll("gaining nothing");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"You may reveal a Province card from your hand. If you do, gain a Gold card, putting it into your hand. Otherwise, gain a Silver card, putting it into your hand."};
	}

	@Override
	public String toString() {
		return "Explorer";
	}

}

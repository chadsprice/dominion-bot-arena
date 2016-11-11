package cards;

import server.*;

public class Baron extends Card {

	public Baron() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusBuys(player, game, 1);
		// may discard an Estate for +$4
		if (player.getHand().contains(Cards.ESTATE) && chooseDiscardEstate(player, game)) {
			game.messageAll("discarding " + Cards.ESTATE.htmlName() + " for +$4");
			player.putFromHandIntoDiscard(Cards.ESTATE);
			player.addCoins(4);
		} else {
			// otherwise, gain an Estate
			gain(player, game, Cards.ESTATE);
		}
	}

	private boolean chooseDiscardEstate(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).baronDiscardEstate();
		}
		int choice = game.promptMultipleChoice(player, this.toString() + ": Discard " + Cards.ESTATE.htmlName() + " for +$4?", new String[] {"Yes", "No"});
		return (choice == 0);
	}

	@Override
	public String[] description() {
		return new String[] {"+1 Buy", "You may discard an Estate for +$4. If you don't, gain an Estate."};
	}

	@Override
	public String toString() {
		return "Baron";
	}

}

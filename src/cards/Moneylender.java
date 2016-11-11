package cards;

import server.*;

public class Moneylender extends Card {

	public Moneylender() {
		isAction = true;
	}
	
	@Override
	public int cost() {
		return 4;
	}
	
	@Override
	public void onPlay(Player player, Game game) {
		// if you have a Copper in hand
		if (player.getHand().contains(Cards.COPPER)) {
			// you may choose to trash it for +$3
            if (chooseTrashCopper(player, game)) {
                game.messageAll("trashing " + Cards.COPPER.htmlName() + " for +$3");
                player.removeFromHand(Cards.COPPER);
                game.trash(player, Cards.COPPER);
                player.addCoins(3);
            } else {
                game.messageAll("trashing nothing");
            }
		}
	}

	private boolean chooseTrashCopper(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).moneylenderTrashCopper();
		}
		int choice = game.promptMultipleChoice(player, "Moneylender: Trash " + Cards.COPPER.htmlName() + " for +$3?", new String[] {"Yes", "No"});
		return (choice == 0);
	}
	
	@Override
	public String[] description() {
		return new String[] {"You may trash a Copper from your hand for +$3."};
	}
	
	@Override
	public String toString() {
		return "Moneylender";
	}
	
}

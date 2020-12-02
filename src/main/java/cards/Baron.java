package cards;

import server.*;

import java.util.Set;

public class Baron extends Card {

	@Override
	public String name() {
		return "Baron";
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
		return new String[] {"<+1_Buy>", "You_may discard_an_[Estate] for_<+4$>. If_you_don't, gain_an_[Estate]."};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusBuys(player, game, 1);
		// may discard an Estate for +$4
		if (player.getHand().contains(Cards.ESTATE) && chooseDiscardEstate(player, game)) {
			game.messageAll("discarding " + Cards.ESTATE.htmlName() + " for +$4");
			player.putFromHandIntoDiscard(Cards.ESTATE);
			player.coins += 4;
		} else {
			// otherwise, gain an Estate
			gain(player, game, Cards.ESTATE);
		}
	}

	private boolean chooseDiscardEstate(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).baronDiscardEstate();
		}
		int choice = new Prompt(player, game)
                .message(this.toString() + ": Discard " + Cards.ESTATE.htmlName() + " for +$4, or gain" + Cards.ESTATE.htmlName() + "?")
                .multipleChoices(new String[] {"Discard Estate for +$4", "Gain Estate"})
                .responseMultipleChoiceIndex();
		return (choice == 0);
	}

}

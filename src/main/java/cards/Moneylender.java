package cards;

import server.*;

import java.util.Collections;
import java.util.Set;

public class Moneylender extends Card {

	@Override
	public String name() {
		return "Moneylender";
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
		return new String[] {"You may trash a_[Copper] from your_hand for_<+3$>."};
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
                player.coins += 3;
            } else {
                game.messageAll("trashing nothing");
            }
		}
	}

	private boolean chooseTrashCopper(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).moneylenderTrashCopper();
		}
		Card toTrash = new Prompt(player, game)
				.message(this.toString() + ": You may trash " + Cards.COPPER.htmlName() + " for +$3.")
				.handChoices(Collections.singleton(Cards.COPPER))
				.orNone("Don't")
				.responseCard();
		return (toTrash != null);
	}

}

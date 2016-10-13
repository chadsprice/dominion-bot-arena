package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class SeaHag extends Card {

	public SeaHag() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		for (Player target : targets) {
			List<Card> drawn = target.takeFromDraw(1);
			String discarded = "nothing";
			if (drawn.size() == 1) {
				target.addToDiscard(drawn.get(0));
				discarded = drawn.get(0).htmlName();
			}
			String gained = "nothing";
			if (game.supply.get(Card.CURSE) > 0) {
				game.gainToTopOfDeck(target, Card.CURSE);
				gained = Card.CURSE.htmlName();
			}
			game.message(target, "You discard " + discarded + " from the top of your deck and put " + gained + " on top");
			game.messageOpponents(target, target.username + " discards " + discarded + " from the top of their deck and puts " + gained + " on top");
		}
	}

	@Override
	public String[] description() {
		return new String[] {"Each other player discards the top card of their deck, then gains a Curse card, putting it on top of their deck."};
	}

	@Override
	public String toString() {
		return "Sea Hag";
	}

}

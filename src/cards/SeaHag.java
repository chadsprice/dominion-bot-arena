package cards;

import java.util.List;

import server.Card;
import server.Cards;
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
			String discarded = drawn.isEmpty() ? "nothing" : drawn.get(0).htmlName();
			String gained = game.supply.get(Cards.CURSE) == 0 ? "nothing" : Cards.CURSE.htmlName();
			game.message(target, "You discard " + discarded + " from the top of your deck and put " + gained + " on top");
			game.messageOpponents(target, target.username + " discards " + discarded + " from the top of their deck and puts " + gained + " on top");
			if (!drawn.isEmpty()) {
				target.addToDiscard(drawn.get(0));
			}
			if (game.supply.get(Cards.CURSE) != 0) {
				game.gainToTopOfDeck(target, Cards.CURSE);
			}
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

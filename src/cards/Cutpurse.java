package cards;

import java.util.List;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

public class Cutpurse extends Card {

	public Cutpurse() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCoins(player, game, 2);
		// targets discard a copper
		for (Player target : targets) {
			if (target.getHand().contains(Cards.COPPER)) {
				target.putFromHandIntoDiscard(Cards.COPPER);
				game.message(target, "You discard " + Cards.COPPER.htmlName());
				game.messageOpponents(target, target.username + " discards " + Cards.COPPER.htmlName());
			} else {
				game.message(target, "You reveal " + Card.htmlList(target.getHand()));
				game.messageOpponents(target, target.username + " reveals " + Card.htmlList(target.getHand()));
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+$2", "Each other player discards a Copper card (or reveals a hand with no Copper)."};
	}

	@Override
	public String toString() {
		return "Cutpurse";
	}

}

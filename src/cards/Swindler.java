package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;

public class Swindler extends Card {

	public Swindler() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCoins(player, game, 2);
		// Swindling
		for (Player target : targets) {
			List<Card> drawn = target.takeFromDraw(1);
			if (drawn.size() == 1) {
				Card toTrash = drawn.get(0);
				game.addToTrash(toTrash);
				game.message(target, "You reveal and trash " + toTrash.htmlName());
				game.messageOpponents(target, target.username + " reveals and trashes " + toTrash.htmlName());
				int cost = toTrash.cost(game);
				Set<Card> gainable = game.cardsCostingExactly(cost);
				if (gainable.size() > 0) {
					Card toGain = game.promptChooseOpponentGainFromSupply(player, gainable, "Swindler: " + target.username + " reveals and trashes " + toTrash.htmlName() + ". Choose a card for " + target.username + " to gain");
					game.message(target, "You gain " + toGain.htmlName());
					game.messageOpponents(target, target.username + " gains " + toGain.htmlName());
					game.gain(target, toGain);
				} else {
					game.message(target, "You gain nothing");
					game.messageOpponents(target, target.username + " gains nothing");
				}
			} else {
				game.message(target, "Your deck is empty");
				game.messageOpponents(target, target + "'s deck is empty");
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+$2", "Each other player trashes the top card of his deck and gains a card with the same cost that you choose."};
	}

	@Override
	public String toString() {
		return "Swindler";
	}

}

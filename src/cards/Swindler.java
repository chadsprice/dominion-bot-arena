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
		// swindle each target
		targets.forEach(target -> {
			List<Card> drawn = target.takeFromDraw(1);
			if (drawn.size() == 1) {
				Card toTrash = drawn.get(0);
				game.trash(target, toTrash);
				game.message(target, "You draw and trash " + toTrash.htmlName());
				game.messageOpponents(target, target.username + " draws and trashes " + toTrash.htmlName());
				game.messageIndent++;
				// choose a card for the target to gain costing the same as the trashed card
				Set<Card> gainable = game.cardsCostingExactly(toTrash.cost(game));
				if (!gainable.isEmpty()) {
					Card toGain = game.promptChooseOpponentGainFromSupply(player, gainable,
							this.toString() + ": " + target.username + " draws and trashes " + toTrash.htmlName() + ". Choose a card for " + target.username + " to gain.");
					game.messageAll("gaining " + toGain.htmlName());
					game.gain(target, toGain);
				} else {
					game.messageAll("gaining nothing");
				}
				game.messageIndent--;
			} else {
				game.message(target, "Your deck is empty");
				game.messageOpponents(target, target + "'s deck is empty");
			}
		});
	}

	@Override
	public String[] description() {
		return new String[] {"+$2", "Each other player trashes the top card of their deck and gains a card with the same cost that you choose."};
	}

	@Override
	public String toString() {
		return "Swindler";
	}

}

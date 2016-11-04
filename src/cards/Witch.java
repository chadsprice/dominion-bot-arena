package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Witch extends Card {

	public Witch() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCards(player, game, 2);
		// each other player gains a Curse
		junkingAttack(targets, game, Card.CURSE);
	}

	@Override
	public String[] description() {
		return new String[] {"+2 Cards", "Each other player gains a Curse."};
	}

	@Override
	public String toString() {
		return "Witch";
	}

	@Override
	public String plural() {
		return "Witches";
	}

}

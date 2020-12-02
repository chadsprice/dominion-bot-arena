package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

public class Witch extends Card {

	@Override
	public String name() {
		return "Witch";
	}

	@Override
	public String plural() {
		return "Witches";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.ATTACK);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+2_Cards>",
				"Each other player gains a_[Curse]."
		};
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCards(player, game, 2);
		// each other player gains a Curse
		junkingAttack(targets, game, Cards.CURSE);
	}

}

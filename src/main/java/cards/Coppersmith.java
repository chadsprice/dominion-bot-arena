package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.Set;

public class Coppersmith extends Card {

	@Override
	public String name() {
		return "Coppersmith";
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
		return new String[] {"Copper produces an_extra_1$ this_turn."};
	}

	@Override
	public void onPlay(Player player, Game game) {
		game.coppersmithsPlayedThisTurn++;
		game.messageAll(Cards.COPPER.htmlNameRaw() + " produces an extra $1 this turn");
	}

}

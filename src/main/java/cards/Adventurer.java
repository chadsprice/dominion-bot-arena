package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Adventurer extends Card {

	@Override
	public String name() {
		return "Adventurer";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 6;
	}

	@Override
	public String[] description() {
		return new String[] {"Reveal_cards from your_deck until you_reveal 2_Treasure_cards.", "Put_those Treasure_cards into your_hand and discard the other_revealed_cards."};
	}

	@Override
	public void onPlay(Player player, Game game) {
        // reveal cards from your deck until you have 2 treasure cards, put those into your hand and discard the rest
		revealUntil(
				player,
				game,
				Card::isTreasure,
				2,
				list -> putRevealedIntoHand(player, game, list)
		);
	}

}

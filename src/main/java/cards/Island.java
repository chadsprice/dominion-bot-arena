package cards;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import server.*;

public class Island extends Card {

	@Override
	public String name() {
		return "Island";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION, Type.VICTORY);
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[] {
				"Set aside this and another_card from your_hand. Return_them to_your deck at the_end of the_game.",
				"2_VP"
		};
	}

	@Override
	public int victoryValue() {
		return 2;
	}

	@Override
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		boolean movedToIsland = false;
		if (!hasMoved) {
			// move this to the island mat
            game.message(player, "putting the " + this.htmlNameRaw() + " on your island mat");
            game.messageOpponents(player, "putting the " + this.htmlNameRaw() + " on their island mat");
			player.removeFromPlay(this);
			player.putOnIslandMat(this);
			movedToIsland = true;
		}
		if (!player.getHand().isEmpty()) {
		    Card toIsland = chooseIslandFromHand(player, game, new HashSet<>(player.getHand()));
            game.message(player, "putting " + toIsland.htmlName() + " on your island mat");
            game.messageOpponents(player, "putting " + toIsland.htmlName() + " on their island mat");
			player.removeFromHand(toIsland);
			player.putOnIslandMat(toIsland);
		} else {
			game.message(player, "putting nothing else on your island mat because your hand is empty");
			game.messageOpponents(player, "putting nothing else on their island mat because their hand is empty");
		}
		return movedToIsland;
	}

	private Card chooseIslandFromHand(Player player, Game game, Set<Card> choices) {
	    if (player instanceof Bot) {
	        Card toIsland = ((Bot) player).islandFromHand(Collections.unmodifiableSet(choices));
	        checkContains(choices, toIsland);
	        return toIsland;
        }
        return new Prompt(player, game)
                .message(this.toString() + ": Choose a card to set aside on your island mat.")
                .handChoices(choices)
                .responseCard();
    }

}

package cards;

import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Militia extends Card {

	public Militia() {
		isAction = true;
		isAttack = true;
	}
	
	@Override
	public int cost() {
		return 4;
	}
	
	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		// +2
		player.addExtraCoins(2);
		game.message(player, "... You get +$2");
		game.messageOpponents(player, "... getting +$2");
		// other players discard down to 3
		for (Player target : targets) {
			if (target.getHand().size() > 3) {
				int count = target.getHand().size() - 3;
				List<Card> discarded = game.promptDiscardNumber(target, count, "Militia", "attackPrompt");
				target.putFromHandIntoDiscard(discarded);
				game.message(target, "... (You discard " + Card.htmlList(discarded) + ")");
				game.messageOpponents(target, "... (" + target.username + " discards " + Card.htmlList(discarded) + ")");
			}
		}
	}
	
	@Override
	public String[] description() {
		return new String[]{"+$2", "Each other player discards down to 3 cards in his hand."};
	}
	
	@Override
	public String toString() {
		return "Militia";
	}
	
}

package cards;

import java.util.List;
import java.util.Set;

import server.Card;
import server.Game;
import server.Player;
import server.Prompt;

public class Chapel extends Card {

	@Override
	public String name() {
		return "Chapel";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 2;
	}

	@Override
	public String[] description() {
		return new String[]{"Trash up_to 4_cards from your_hand."};
	}

	@Override
	public void onPlay(Player player, Game game) {
		// trash up to 4 cards
		List<Card> trashed = promptTrashNumber(
		        player,
                game,
                4,
                Prompt.Amount.UP_TO
        );
		game.messageAll("trashing " + Card.htmlList(trashed));
		if (!trashed.isEmpty()) {
			player.removeFromHand(trashed);
			game.trash(player, trashed);
		}
	}

}

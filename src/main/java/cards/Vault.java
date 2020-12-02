package cards;

import java.util.List;
import java.util.Set;

import server.*;

public class Vault extends Card {

	@Override
	public String name() {
		return "Vault";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+2_Cards>",
				"Discard any number of_cards.",
				"<+1$> per card discarded.",
				"Each other player may discard 2_cards. If_they_do, they draw a_card."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 2);
		if (!player.getHand().isEmpty()) {
			// discard any number of cards
			List<Card> discarded = chooseDiscardForCoins(player, game);
			game.messageAll("discarding " + Card.htmlList(discarded) + " for +$" + discarded.size());
			player.putFromHandIntoDiscard(discarded);
			// +$1 per card discarded
			player.coins += discarded.size();
		}
		// each other player may discard 2 cards to draw 1
		for (Player other : game.getOpponents(player)) {
			if (other.getHand().size() >= 2) {
				List<Card> toDiscard = promptDiscardNumber(
						other,
						game,
						2,
						Prompt.Amount.EXACT_OR_NONE
				);
				if (toDiscard.size() == 2) {
					game.message(other, "you discard " + Card.htmlList(toDiscard));
					game.messageOpponents(other, other.username + " discards " + Card.htmlList(toDiscard));
					other.putFromHandIntoDiscard(toDiscard);
					game.messageIndent++;
					plusCards(other, game, 1);
					game.messageIndent--;
				}
			} else if (other.getHand().size() == 1) {
				// special case: if you have only one card in hand, you are allowed to discard it
				List<Card> toDiscard = promptDiscardNumber(
						other,
						game,
						1,
						Prompt.Amount.UP_TO
				);
				if (toDiscard.size() == 1) {
					game.message(other, "you discard " + Card.htmlList(toDiscard));
					game.messageOpponents(other, other.username + " discards " + Card.htmlList(toDiscard));
					other.putFromHandIntoDiscard(toDiscard);
				}
			}
		}
	}

	private List<Card> chooseDiscardForCoins(Player player, Game game) {
		if (player instanceof Bot) {
			List<Card> toDiscard = ((Bot) player).vaultDiscardForCoins();
			checkContains(player.getHand(), toDiscard);
			return toDiscard;
		}
		return promptDiscardNumber(player, game, player.getHand().size(), Prompt.Amount.UP_TO);
	}

}

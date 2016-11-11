package cards;

import java.util.List;

import server.*;

public class Torturer extends Card {

	public Torturer() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCards(player, game, 3);
		// torture targets
		for (Player target : targets) {
			if (chooseDiscardTwoOverTakingCurse(target, game)) {
				// discard 2 cards
				if (target.getHand().size() != 0) {
					List<Card> toDiscard = game.promptDiscardNumber(target, 2, "Torturer");
					game.message(target, "You discard " + Card.htmlList(toDiscard));
					game.messageOpponents(target, target.username + " discards " + Card.htmlList(toDiscard));
					target.putFromHandIntoDiscard(toDiscard);
				} else {
					game.message(target, "You reveal an empty hand, discarding nothing");
					game.messageOpponents(target, target.username + " reveals an empty hand, discarding nothing");
				}
			} else {
				// gain a curse putting it into your hand
				if (game.supply.get(Cards.CURSE) != 0) {
					game.message(target, "You gain " + Cards.CURSE.htmlName() + " to your hand");
					game.messageOpponents(target, target.username + " gains " + Cards.CURSE.htmlName() + " to their hand");
					game.gainToHand(player, Cards.CURSE);
				} else {
					game.message(target, "You gain nothing");
					game.messageOpponents(target, target.username + " gains nothing");
				}
			}
		}
	}

	private boolean chooseDiscardTwoOverTakingCurse(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).torturerDiscardTwoOverTakingCurse();
		}
		int choice = game.promptMultipleChoice(player, this.toString() + ": Choose one", "attackPrompt", new String[] {"Discard 2 cards", "Gain a Curse, putting it into your hand"});
		return (choice == 0);
	}

	@Override
	public String[] description() {
		return new String[] {"+3 Cards", "Each other player either discards 2 cards or gains a Curse to their hand, their choice. (They may pick an option they can't do.)"};
	}

	@Override
	public String toString() {
		return "Torturer";
	}

}

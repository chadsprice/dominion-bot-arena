package cards;

import java.util.List;
import java.util.Set;

import server.*;

public class Torturer extends Card {

	@Override
	public String name() {
		return "Torturer";
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
                "<+3_Cards>",
                "Each other player chooses_one:",
                "* They discard 2_cards",
                "* They gain a_[Curse] to their_hand",
                "(If they make a choice they can't_complete, nothing happens to_them)"
        };
    }

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCards(player, game, 3);
		// torture targets
		for (Player target : targets) {
			if (chooseDiscardTwoOverTakingCurse(target, game)) {
				// discard 2 cards
				if (!target.getHand().isEmpty()) {
					List<Card> toDiscard = promptDiscardNumber(target, game, 2);
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
					game.gainToHand(target, Cards.CURSE);
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
		int choice = new Prompt(player, game)
				.type(Prompt.Type.DANGER)
				.message(this.toString() + ": Choose one")
				.multipleChoices(new String[] {"Discard 2 cards", "Gain a Curse, putting it into your hand"})
				.responseMultipleChoiceIndex();
		return (choice == 0);
	}

}

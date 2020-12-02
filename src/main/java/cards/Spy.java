package cards;

import java.util.List;
import java.util.Set;

import server.*;

public class Spy extends Card {

    @Override
    public String name() {
        return "Spy";
    }

    @Override
    public String plural() {
        return "Spies";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

	@Override
	public int cost() {
		return 4;
	}

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Card>",
                "<+1_Action>",
                "Each player (including_you) reveals the_top card of their_deck. For each_player, choose_one:",
                "* They discard it",
                "* They put it back"
        };
    }

	@Override
	public void onAttack(Player player, Game game, List<Player> targets) {
		plusCards(player, game, 1);
		plusActions(player, game, 1);
		// reveal top card of each player's deck and decide to keep it or discard it
		targets.add(0, player);
        targets.forEach(target -> {
            List<Card> top = target.takeFromDraw(1);
            if (!top.isEmpty()) {
                Card card = top.get(0);
                game.message(target, "You reveal " + card.htmlName());
                game.messageOpponents(target, target.username + " reveals " + card.htmlName());
                boolean isDiscarding;
                if (target == player) {
                    isDiscarding = chooseDiscardSelf(player, game, card);
                } else {
                    isDiscarding = chooseDiscardOpponent(player, game, target, card);
                }
                game.messageIndent++;
                if (isDiscarding) {
                    game.messageAll("discarding it");
                    target.addToDiscard(card);
                } else {
                    game.messageAll("putting it back");
                    target.putOnDraw(card);
                }
                game.messageIndent--;
            } else {
                game.message(target, "Your deck is empty");
                game.messageOpponents(target, target.username + "'s deck is empty");
            }
        });
	}

	private boolean chooseDiscardSelf(Player player, Game game, Card card) {
        if (player instanceof Bot) {
            return ((Bot) player).spyDiscardSelf(card);
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": You draw " + card.htmlName() + ". Discard it or put it back?")
                .multipleChoices(new String[] {"Discard", "Put back"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

    private boolean chooseDiscardOpponent(Player player, Game game, Player opponent, Card card) {
        if (player instanceof Bot) {
            return ((Bot) player).spyDiscardOpponent(card);
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": " + opponent.username + " draws " + card.htmlName() + ". Have them discard it or put it back?")
                .multipleChoices(new String[] {"They discard", "They put back"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}

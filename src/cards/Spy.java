package cards;

import java.util.List;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

public class Spy extends Card {

	public Spy() {
		isAction = true;
		isAttack = true;
	}

	@Override
	public int cost() {
		return 4;
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
            return ((Bot) player).wantToDiscard(card);
        }
        int choice = game.promptMultipleChoice(player, this.toString() + ": You draw " + card.htmlName() + ". Discard it or put it back?", new String[] {"Discard", "Put back"});
        return (choice == 0);
    }

    private boolean chooseDiscardOpponent(Player player, Game game, Player opponent, Card card) {
        if (player instanceof Bot) {
            return !((Bot) player).wantToDiscard(card);
        }
        int choice = game.promptMultipleChoice(player, this.toString() + ": " + opponent.username + " draws " + card.htmlName() + ". Have them discard it or put it back?", new String[] {"They discard", "They put back"});
        return (choice == 0);
    }

	@Override
	public String[] description() {
		return new String[] {"+1 Card", "+1 Action", "Each player (including you) reveals the top card of their deck and either discards it or puts it back, your choice."};
	}

	@Override
	public String toString() {
		return "Spy";
	}

	@Override
	public String plural() {
		return "Spies";
	}

}

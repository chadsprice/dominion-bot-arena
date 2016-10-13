package cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Rabble extends Card {

	public Rabble() {
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
		for (Player target : targets) {
			// reveal top 3 cards
			List<Card> revealed = target.takeFromDraw(3);
			if (!revealed.isEmpty()) {
				game.message(target, "you reveal " + Card.htmlList(revealed));
				game.messageOpponents(target, target.username + " reveals " + Card.htmlList(revealed));
			}
			// take the actions and treasures
			List<Card> actionsAndTreasures = new ArrayList<Card>();
			for (Iterator<Card> iter = revealed.iterator(); iter.hasNext(); ) {
				Card card  = iter.next();
				if (card.isAction || card.isTreasure) {
					iter.remove();
					actionsAndTreasures.add(card);
				}
			}
			game.messageIndent++;
			// discard the actions and treasures
			if (!actionsAndTreasures.isEmpty()) {
				game.messageAll("discarding " + Card.htmlList(actionsAndTreasures));
				target.addToDiscard(actionsAndTreasures);
			}
			// put the rest back on top in any order
			if (!revealed.isEmpty()) {
				Collections.sort(revealed, Player.HAND_ORDER_COMPARATOR);
				List<Card> toPutOnDeck = new ArrayList<Card>();
				while (revealed.size() > 0) {
					String[] choices = new String[revealed.size()];
					for (int i = 0; i < revealed.size(); i++) {
						choices[i] = revealed.get(i).toString();
					}
					int choice = game.promptMultipleChoice(target, "Rabble: Put the remaining cards on top of your deck (the first card you choose will be on top of your deck)", "attackPrompt", choices);
					toPutOnDeck.add(revealed.remove(choice));
				}
				if (toPutOnDeck.size() > 0) {
					game.messageAll("putting " + Card.htmlList(toPutOnDeck) + " back on top");
					target.putOnDraw(toPutOnDeck);
				}
			}
			game.messageIndent--;
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+3 Cards", "Each other player reveals the top 3 cards of their deck, discards the revealed Actions and Treasures, and puts the rest back on top in any order they choose."};
	}

	@Override
	public String toString() {
		return "Rabble";
	}

}

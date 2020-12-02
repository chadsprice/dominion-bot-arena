package cards;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import server.Card;
import server.Game;
import server.Player;
import server.Prompt;

public class Rabble extends Card {

	@Override
	public String name() {
		return "Rabble";
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
				"Each other player reveals the top_3 cards of their_deck, discards the revealed Actions and Treasures, and puts the_rest back on_top in any_order they_choose."
		};
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
			List<Card> actionsAndTreasures = revealed.stream()
					.filter(c -> c.isAction() || c.isTreasure())
					.collect(Collectors.toList());
			actionsAndTreasures.forEach(revealed::remove);
			game.messageIndent++;
			// discard the actions and treasures
			if (!actionsAndTreasures.isEmpty()) {
				game.messageAll("discarding " + Card.htmlList(actionsAndTreasures));
				target.addToDiscard(actionsAndTreasures);
			}
			// put the rest back on top in any order
			if (!revealed.isEmpty()) {
				game.messageAll("putting the rest back on top");
				putOnDeckInAnyOrder(
						player,
						game,
						revealed,
						this.toString() + "Put the rest back on top of your deck in any order",
						Prompt.Type.DANGER
				);
			}
			game.messageIndent--;
		}
	}

}

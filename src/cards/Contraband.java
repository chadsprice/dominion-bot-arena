package cards;

import server.Card;
import server.Game;
import server.Player;

public class Contraband extends Card {

	public Contraband() {
		isTreasure = true;
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusBuys(player, game, 1);
		Player playerOnLeft = game.getOpponents(player).get(0);
		Card toProhibit = game.promptNameACard(playerOnLeft, "Contraband", "Name a card. " + player.username + " will not be able to buy it this turn.");
		game.message(player, playerOnLeft.username + " names " + toProhibit.htmlNameRaw() + ", you cannot buy it this turn");
		game.message(playerOnLeft, "you name " + toProhibit.htmlNameRaw() + ", " + player.username + " cannot buy it this turn");
		for (Player other : game.players) {
			if (other != player && other != playerOnLeft) {
				game.message(playerOnLeft, playerOnLeft.username + " names " + toProhibit.htmlNameRaw() + ", " + player.username + " cannot buy it this turn");
			}
		}
		game.contrabandProhibited.add(toProhibit);
	}

	@Override
	public int treasureValue(Game game) {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[]{"$3", "+1 Buy", "When you play this, the player to your left names a card.", "You can't buy that card this turn."};
	}

	@Override
	public String toString() {
		return "Contraband";
	}

}

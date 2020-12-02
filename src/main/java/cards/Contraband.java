package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class Contraband extends Card {

	@Override
	public String name() {
		return "Contraband";
	}

	@Override
	public Set<Type> types() {
		return types(Type.TREASURE);
	}

	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[]{
				"3$",
				"<+1_Buy>",
				"When_you play_this, the_player to_your_left names_a_card.",
				"You_can't buy that_card this_turn."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusBuys(player, game, 1);
		Player playerOnLeft = game.getOpponents(player).get(0);
		Card toProhibit = chooseProhibit(playerOnLeft, game, player);
		game.message(player, playerOnLeft.username + " names " + toProhibit.htmlNameRaw() + ", you cannot buy it this turn");
		game.message(playerOnLeft, "you name " + toProhibit.htmlNameRaw() + ", " + player.username + " cannot buy it this turn");
		game.players.forEach(other -> {
			if (other != player && other != playerOnLeft) {
				game.message(playerOnLeft, playerOnLeft.username + " names " + toProhibit.htmlNameRaw() + ", " + player.username + " cannot buy it this turn");
			}
		});
		game.contrabandProhibited.add(toProhibit);
	}

	private Card chooseProhibit(Player playerOnLeft, Game game, Player player) {
	    if (playerOnLeft instanceof Bot) {
	        return ((Bot) playerOnLeft).contrabandProhibit();
        }
        return promptNameACard(
				playerOnLeft,
                game,
                this.toString(),
                "Name a card. " + player.username + " will not be able to buy it this turn."
        );
    }

	@Override
	public int treasureValue(Game game) {
		return 3;
	}

}

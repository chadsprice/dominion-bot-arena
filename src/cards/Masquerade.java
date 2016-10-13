package cards;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import server.Card;
import server.Game;
import server.Player;

public class Masquerade extends Card {

	public Masquerade() {
		isAction = true;
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCards(player, game, 2);
		// ask players in turn order which card they want to pass, starting with this player
		List<Player> passOrder = game.getOpponents(player);
		passOrder.add(0, player);
		// skip over players with no cards in hand
		for (Iterator<Player> iter = passOrder.iterator(); iter.hasNext(); ) {
			if (iter.next().getHand().isEmpty()) {
				iter.remove();
			}
		}
		// only bother to pass cards if there are at least 2 players that will pass cards
		if (passOrder.size() >= 2) {
			List<Card> cardsToPass = new ArrayList<Card>();
			int i = 0;
			for (Player eachPlayer : passOrder) {
				if (eachPlayer.getHand().size() > 0) {
					Player playerOnLeft = passOrder.get((i + 1) % passOrder.size());
					String promptType = (eachPlayer == player) ? "actionPrompt" : "attackPrompt";
					Card toPass = game.promptChoosePassToOpponent(eachPlayer, new HashSet<Card>(eachPlayer.getHand()), "Masquerade: Pass a card from your hand to " + playerOnLeft.username + ".", promptType);
					cardsToPass.add(toPass);
				} else {
					cardsToPass.add(null);
				}
				i++;
			}
			// pass cards
			for (i = 0; i < passOrder.size(); i++) {
				Player eachPlayer = passOrder.get(i);
				Player playerOnLeft = passOrder.get((i + 1) % passOrder.size());
				Card toPass = cardsToPass.get(i);
				if (toPass != null) {
					eachPlayer.removeFromHand(toPass);
					playerOnLeft.addToHand(toPass);
				}
				// message player who is giving
				String cardString = toPass == null ? "nothing" : toPass.htmlName();
				game.message(eachPlayer, "You pass " + cardString + " to " + playerOnLeft.username);
				// message player who is receiving
				game.message(playerOnLeft, eachPlayer.username + " passes " + cardString + " to you");
				// message other players (without naming the card passed)
				cardString = toPass == null ? "nothing" : "a card";
				for (Player eachUninvolved : passOrder) {
					if (eachUninvolved != eachPlayer && eachUninvolved != playerOnLeft) {
						game.message(eachUninvolved, eachPlayer.username + " passes " + cardString + " to " + playerOnLeft.username);
					}
				}
			}
		}
		// you may trash a card from your hand
		if (player.getHand().size() > 0){
			int choice = game.promptMultipleChoice(player, "Masquerade: Trash a card from your hand?", new String[] {"Yes", "No"});
			if (choice == 0) {
				Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), "Masquerade: Trash a card from your hand");
				player.removeFromHand(toTrash);
				game.addToTrash(toTrash);
				game.messageAll("trashing " + toTrash.htmlName());
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+2 Cards", "Each player with any cards in hand passes one to the next such player to their left, at once. Then you may trash a card from your hand."};
	}

	@Override
	public String toString() {
		return "Masquerade";
	}

}

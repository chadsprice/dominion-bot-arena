package cards;

import java.util.ArrayList;
import java.util.HashSet;
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
		// +2 cards
		List<Card> drawn = player.drawIntoHand(2);
		game.message(player, "... You draw " + Card.htmlList(drawn));
		game.messageOpponents(player, "... drawing " + drawn.size() + " card(s)");
		// Each player chooses a card to pass
		List<Player> passOrder = game.getOpponents(player);
		passOrder.add(0, player);
		List<Card> cardsToPass = new ArrayList<Card>();
		int i = 0;
		for (Player eachPlayer : passOrder) {
			if (eachPlayer.getHand().size() > 0) {
				Player playerOnLeft = passOrder.get((i + 1) % passOrder.size());
				Card toPass = game.promptChoosePassToOpponent(eachPlayer, new HashSet<Card>(eachPlayer.getHand()), "Masquerade: Pass a card from your hand to " + playerOnLeft.username, "attackPrompt");
				cardsToPass.add(toPass);
			} else {
				cardsToPass.add(null);
			}
			i++;
		}
		// Pass cards
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
			game.message(eachPlayer, "... You pass " + cardString + " to " + playerOnLeft.username);
			// message player who is receiving
			game.message(playerOnLeft, "... " + eachPlayer.username + " passes " + cardString + " to you");
			// message other players (without naming the card passed)
			cardString = toPass == null ? "nothing" : "a card";
			for (Player eachUninvolved : passOrder) {
				if (eachUninvolved != eachPlayer && eachUninvolved != playerOnLeft) {
					game.message(eachUninvolved, "... " + eachPlayer.username + " passes " + cardString + " to " + playerOnLeft.username);
				}
			}
		}
		// You may trash a card from your hand
		if (player.getHand().size() > 0){
			int choice = game.promptMultipleChoice(player, "Masquerade: Trash a card from your hand?", new String[] {"Yes", "No"});
			if (choice == 0) {
				Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), "Masquerade: Trash a card from your hand");
				player.removeFromHand(toTrash);
				game.trash.add(toTrash);
				game.message(player, "... You trash " + toTrash.htmlName());
				game.messageOpponents(player, "... trashing " + toTrash.htmlName());
			}
		}
	}

	@Override
	public String[] description() {
		return new String[] {"+2 Cards", "Each player passes a card from his hand to the left at once. Then you may trash a card from your hand."};
	}

	@Override
	public String toString() {
		return "Masquerade";
	}

}

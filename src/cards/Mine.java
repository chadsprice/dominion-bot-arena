package cards;

import java.util.Set;
import java.util.stream.Collectors;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

public class Mine extends Card {

	public Mine() {
		isAction = true;
	}
	
	@Override
	public int cost() {
		return 5;
	}
	
	@Override
	public void onPlay(Player player, Game game) {
		// you may trash a treasure from your hand
		Set<Card> treasures = player.getHand().stream().filter(c -> c.isTreasure).collect(Collectors.toSet());
		if (!treasures.isEmpty()) {
            Card toTrash = chooseTrash(player, game, treasures);
            if (toTrash != null) {
                // trash the chosen treasure
                game.messageAll("trashing " + toTrash.htmlName());
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
                // gain a treasure costing up to $3 more
                Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost() + 3).stream()
                        .filter(c -> c.isTreasure)
                        .collect(Collectors.toSet());
                if (!gainable.isEmpty()) {
                    Card toGain = game.promptChooseGainFromSupply(player, gainable, this.toString() + ": Gain a treasure to your hand costing up to $3 more.");
                    game.message(player, "gaining " + toGain.htmlName() + " to your hand");
                    game.messageOpponents(player, "gaining " + toGain.htmlName() + " to their hand");
                    game.gainToHand(player, toGain);
                } else {
                    game.messageAll("gaining nothing");
                }
            } else {
                game.messageAll("trashing nothing");
            }
		} else {
			game.messageAll("having no treasure to trash");
		}
	}
	
	private Card chooseTrash(Player player, Game game, Set<Card> trashable) {
		if (player instanceof Bot) {
			Card toTrash = ((Bot) player).mineTrash(trashable);
			if (toTrash != null && !trashable.contains(toTrash)) {
				throw new IllegalStateException();
			}
			return toTrash;
		}
		return game.promptChooseTrashFromHand(player, trashable, this.toString() + ": You may trash a treasure from your hand.", false, "None");
	}
	
	@Override
	public String[] description() {
		return new String[]{"You may trash a Treasure from your hand. Gain a Treasure to your hand costing up to $3 more than it."};
	}
	
	@Override
	public String toString() {
		return "Mine";
	}
	
}

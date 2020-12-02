package cards;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import server.*;

public class Mine extends Card {

	@Override
	public String name() {
		return "Mine";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}
	
	@Override
	public int cost() {
		return 5;
	}

	@Override
	public String[] description() {
		return new String[]{"You may trash a_Treasure from your_hand. Gain a_Treasure to_your_hand costing up_to 3$_more than_it."};
	}
	
	@Override
	public void onPlay(Player player, Game game) {
		// you may trash a treasure from your hand
		Set<Card> treasures = player.getHand().stream()
				.filter(Card::isTreasure)
                .collect(Collectors.toSet());
		if (!treasures.isEmpty()) {
            Card toTrash = chooseTrash(player, game, treasures);
            if (toTrash != null) {
                // trash the chosen treasure
                game.messageAll("trashing " + toTrash.htmlName());
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
                // gain a treasure costing up to $3 more
                Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost(game) + 3).stream()
                        .filter(Card::isTreasure)
                        .collect(Collectors.toSet());
                if (!gainable.isEmpty()) {
                    Card toGain = promptChooseGainFromSupply(
                            player,
                            game,
                            gainable,
                            this.toString() + ": Gain a treasure to your hand costing up to $3 more."
                    );
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
			Card toTrash = ((Bot) player).mineTrash(Collections.unmodifiableSet(trashable));
			checkContains(trashable, toTrash, false);
			return toTrash;
		}
		return new Prompt(player, game)
                .type(Prompt.Type.DANGER)
                .message(this.toString() + ": You may trash a treasure from your hand.")
                .orNone("None")
                .responseCard();
	}
	
}

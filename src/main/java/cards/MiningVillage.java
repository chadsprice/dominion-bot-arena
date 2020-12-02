package cards;

import server.*;

import java.util.Set;

public class MiningVillage extends Card {

	@Override
	public String name() {
		return "Mining Village";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 4;
	}

	@Override
	public String[] description() {
		return new String[] {
				"<+1_Card>",
				"<+2_Actions>",
				"You may trash_this for_<+2$>."
		};
	}

	@Override
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		plusCards(player, game, 1);
		plusActions(player, game, 2);
		// you may trash this from play for +$2
		if (!hasMoved && chooseTrash(player, game)) {
			game.messageAll("trashing the " + this.htmlNameRaw() + " for +$2");
			player.removeFromPlay(this);
			game.trash(player, this);
			player.coins += 2;
			return true;
		}
		return false;
	}

	private boolean chooseTrash(Player player, Game game) {
		if (player instanceof Bot) {
			return ((Bot) player).miningVillageTrash();
		}
		int choice = new Prompt(player, game)
                .message(this.toString() + ": Trash the " + this.htmlName() + " for +$2?")
                .multipleChoices(new String[] {"Trash", "Don't"})
                .responseMultipleChoiceIndex();
		return (choice == 0);
	}

}

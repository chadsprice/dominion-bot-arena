package cards;

import server.*;

import java.util.Set;

public class Chancellor extends Card {

	@Override
	public String name() {
		return "Chancellor";
	}

	@Override
	public Set<Type> types() {
		return types(Type.ACTION);
	}

	@Override
	public int cost() {
		return 3;
	}

	@Override
	public String[] description() {
		return new String[]{
				"<+$2>",
				"You_may immediately_put your_deck into your discard_pile."
		};
	}

	@Override
	public void onPlay(Player player, Game game) {
		plusCoins(player, game, 2);
		// you may immediately put your deck into your discard pile
		if (!player.getDraw().isEmpty() && choosePutDeckIntoDiscard(player, game)) {
            game.message(player, "putting your deck into your discard pile");
            game.messageOpponents(player, "putting their deck into their discard pile");
            // this does not trigger the Tunnel reaction
            player.addToDiscard(player.takeFromDraw(player.getDraw().size()), false);
		}
	}

	private boolean choosePutDeckIntoDiscard(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).chancellorPutDeckIntoDiscard();
        }
        int choice  = new Prompt(player, game)
                .message(this.toString() + ": Put your deck into your discard pile?")
                .multipleChoices(new String[] {"Put deck into discard pile", "Don't"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}

package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Replace extends Card {

    public Replace() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        if (!player.getHand().isEmpty()) {
            // trash a card from your hand
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), "Replace: Choose a card to trash.");
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
            // gain a card costing up to 2 more than the trashed card
            Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost(game) + 2);
            if (!gainable.isEmpty()) {
                Card toGain = game.promptChooseGainFromSupply(player, gainable, "Replace: Choose a card to gain.");
                // if it is an action or treasure, put it on top of your deck
                if (toGain.isAction || toGain.isTreasure) {
                    game.message(player, "gaining " + toGain.htmlName() + ", putting it on top of your deck");
                    game.messageOpponents(player, "gaining " + toGain.htmlName() + ", putting it on top of their deck");
                    game.gainToTopOfDeck(player, toGain);
                } else {
                    game.messageAll("gaining " + toGain.htmlName());
                    game.gain(player, toGain);
                }
                // if it is a victory card, every other player gains a curse
                if (toGain.isVictory) {
                    for (Player target : targets) {
                        if (game.supply.get(Card.CURSE) > 0) {
                            game.message(target, "You gain " + Card.CURSE.htmlName());
                            game.messageOpponents(target, target.username + " gains " + Card.CURSE.htmlName());
                            game.gain(target, Card.CURSE);
                        }
                    }
                }
            } else {
                game.messageAll("gaining nothing");
            }
        } else {
            game.messageAll("having nothing in hand to trash");
        }
    }

    @Override
    public String[] description() {
        return new String[] {"Trash a card from your hand. Gain a card costing up to $2 more than it. If the gained card is an Action or Treasure, put it onto your deck; If it's a Victory card, each other player gains a Curse."};
    }

    @Override
    public String toString() {
        return "Replace";
    }

}

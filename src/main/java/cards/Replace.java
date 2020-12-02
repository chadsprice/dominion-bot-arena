package cards;

import server.Card;
import server.Cards;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Replace extends Card {

    @Override
    public String name() {
        return "Replace";
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
        return new String[] {"Trash a card from your_hand. Gain a_card costing up_to 2$_more than_it. If the_gained card is an_Action or Treasure, put_it onto your_deck; If_it's a_Victory card, each other_player gains a_[Curse]."};
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        if (!player.getHand().isEmpty()) {
            // trash a card from your hand
            Card toTrash = promptChooseTrashFromHand(
                    player,
                    game,
                    new HashSet<>(player.getHand()),
                    this.toString() + ": Choose a card to trash."
            );
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
            // gain a card costing up to 2 more than the trashed card
            Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost(game) + 2);
            if (!gainable.isEmpty()) {
                Card toGain = promptChooseGainFromSupply(
                        player,
                        game,
                        gainable,
                        this.toString() + ": Choose a card to gain."
                );
                // if it is an action or treasure, put it on top of your deck
                if (toGain.isAction() || toGain.isTreasure()) {
                    game.message(player, "gaining " + toGain.htmlName() + ", putting it on top of your deck");
                    game.messageOpponents(player, "gaining " + toGain.htmlName() + ", putting it on top of their deck");
                    game.gainToTopOfDeck(player, toGain);
                } else {
                    game.messageAll("gaining " + toGain.htmlName());
                    game.gain(player, toGain);
                }
                // if it is a victory card, every other player gains a curse
                if (toGain.isVictory()) {
                    junkingAttack(targets, game, Cards.CURSE);
                }
            } else {
                game.messageAll("gaining nothing");
            }
        } else {
            game.messageAll("having nothing in hand to trash");
        }
    }

}

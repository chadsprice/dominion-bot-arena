package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class Procession extends Card {

    public Procession() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public boolean onPlay(Player player, Game game, boolean hasMoved) {
        boolean usedAsModifier = false;
        Set<Card> actions = player.getHand().stream().filter(c -> c.isAction).collect(Collectors.toSet());
        if (!actions.isEmpty()) {
            Card toPlay = game.promptChoosePlay(player, actions, "Procession: You may play an action from your hand twice.", false, "None");
            if (toPlay != null) {
                game.messageAll("choosing " + toPlay.htmlName());
                // put the chosen card into play
                player.putFromHandIntoPlay(toPlay);
                // play it twice
                boolean toPlayMoved = false;
                for (int i = 0; i < 2; i++) {
                    toPlayMoved |= game.playAction(player, toPlay, toPlayMoved);
                    // if the card was a duration card, and it was set aside, and this hasn't been moved
                    if (toPlay.isDuration && toPlayMoved && !hasMoved && !usedAsModifier) {
                        // set this aside as a modifier
                        player.removeFromPlay(this);
                        player.addDurationSetAside(this);
                        usedAsModifier = true;
                    }
                }
                // trash the played card
                if (toPlay.isDuration && toPlayMoved) {
                    // remove it from the set aside duration cards
                    game.messageAll("trashing the " + toPlay.htmlNameRaw());
                    player.removeDurationSetAside(toPlay);
                    game.addToTrash(player, toPlay);
                } else if (!toPlayMoved) {
                    // remove it from play
                    game.messageAll("trashing the " + toPlay.htmlNameRaw());
                    player.removeFromPlay(toPlay);
                    game.addToTrash(player, toPlay);
                }
                // gain a card costing exactly $1 more than the trashed card
                Set<Card> gainable = game.cardsCostingExactly(toPlay.cost(game) + 1);
                if (!gainable.isEmpty()) {
                    Card toGain = game.promptChooseGainFromSupply(player, gainable, "Procession: Choose a card to gain.");
                    game.messageAll("gaining " + toGain.htmlName());
                    game.gain(player, toGain);
                } else {
                    game.messageAll("gaining nothing");
                }
            } else {
                game.messageAll("choosing nothing");
            }
        } else {
            game.messageAll("having no actions");
        }
        return usedAsModifier;
    }

    @Override
    public String[] description() {
        return new String[] {"You may play an Action card from you hand twice. Trash it. Gain an Action card costing exactly $1 more than it."};
    }

    @Override
    public String toString() {
        return "Procession";
    }

}

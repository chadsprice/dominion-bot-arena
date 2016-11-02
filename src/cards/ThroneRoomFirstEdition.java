package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class ThroneRoomFirstEdition extends Card {

    public ThroneRoomFirstEdition() {
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
            Card toPlay = game.promptChoosePlay(player, actions, "Throne Room (1st ed.): Choose an action to play twice.");
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
        } else {
            game.messageAll("having no actions");
        }
        return usedAsModifier;
    }

    @Override
    public String[] description() {
        return new String[] {"Choose an action card in your hand.", "Play it twice."};
    }

    @Override
    public String toString() {
        return "Throne Room (1st ed.)";
    }

    @Override
    public String plural() {
        return "Throne Rooms (1st ed.)";
    }

}

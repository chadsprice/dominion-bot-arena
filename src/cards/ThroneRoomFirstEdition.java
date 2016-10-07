package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;

public class ThroneRoomFirstEdition extends Card {

    public ThroneRoomFirstEdition() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        Set<Card> actions = game.playableActions(player);
        if (!actions.isEmpty()) {
            Card toPlay = game.promptChoosePlay(player, actions, "Throne Room (1st ed.): Choose an action to play twice");
            // put the chosen card into play
            player.putFromHandIntoPlay(toPlay);
            game.messageAll("choosing " + toPlay.htmlName());
            // remember if the card moves itself
            // necessary for the "lose track" rule
            boolean hasMoved = game.playAction(player, toPlay, false);
            if (toPlay.isDuration && hasMoved) {
                player.setDurationModifier(this);
            }
            game.playAction(player, toPlay, hasMoved);
        } else {
            game.messageAll("having no actions");
        }
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

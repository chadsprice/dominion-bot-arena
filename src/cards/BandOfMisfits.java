package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class BandOfMisfits extends Card {

    public BandOfMisfits() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        Set<Card> imitable = game.cardsCostingAtMost(this.cost(game) - 1).stream()
                .filter(c -> c.isAction && c != this)
                .collect(Collectors.toSet());
        if (!imitable.isEmpty()) {
            Card toImitate = chooseImitate(player, game, imitable);
            // replace this in play with an imitator
            Card imitator = null;
            try {
                imitator = toImitate.getClass().newInstance();
                imitator.isBandOfMisfits = true;
            } catch (Exception e) {
                throw new IllegalStateException();
            }
            player.removeFromPlay(this);
            player.addToPlay(imitator);
            // play the imitator
            game.playAction(player, imitator, false);
        } else {
            game.messageAll("there are no cards it can be played as");
        }
    }

    private Card chooseImitate(Player player, Game game, Set<Card> imitable) {
        if (player instanceof Bot) {
            Card toImitate = ((Bot) player).bandOfMisfitsImitate(imitable);
            if (!imitable.contains(toImitate)) {
                throw new IllegalStateException();
            }
            return toImitate;
        }
        return game.sendPromptChooseFromSupply(player, imitable, "Band of Misfits: Choose a card to play this as.", "actionPrompt", true, null);
    }

    @Override
    public String[] description() {
        return new String[] {"Play this as if it were an Action card in the Supply costing less than it that you choose.", "This is that card until it leaves play."};
    }

    @Override
    public String toString() {
        return "Band of Misfits";
    }

    @Override
    public String plural() {
        return "Bands of Misfits";
    }
}

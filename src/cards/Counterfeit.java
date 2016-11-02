package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class Counterfeit extends Card {

    public Counterfeit() {
        isTreasure = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public int treasureValue() {
        return 1;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusBuys(player, game, 1);
        // you may play a treasure from your hand twice
        Set<Card> playable = player.getHand().stream()
                .filter(c -> c.isTreasure)
                .collect(Collectors.toSet());
        if (!playable.isEmpty()) {
            Card toPlay = game.promptChooseTrashFromHand(player, playable, "Counterfeit: You may play a treasure from your hand twice.", false, "None");
            if (toPlay != null) {
                game.messageAll("choosing " + toPlay.htmlName());
                player.putFromHandIntoPlay(toPlay);
                game.messageIndent++;
                // play it twice
                boolean toPlayMoved = false;
                for (int i = 0; i < 2; i++) {
                    game.message(player, "You play " + toPlay.htmlName());
                    game.messageOpponents(player, player.username + " plays " + toPlay.htmlName());
                    game.messageIndent++;
                    player.addCoins(toPlay.treasureValue(game));
                    toPlayMoved |= toPlay.onPlay(player, game, toPlayMoved);
                    game.messageIndent--;
                }
                game.messageIndent--;
                // trash it
                if (!toPlayMoved) {
                    game.messageAll("trashing the " + toPlay.htmlNameRaw());
                    player.removeFromPlay(toPlay);
                    game.addToTrash(player, toPlay);
                }
            }
        }
    }

    @Override
    public String[] description() {
        return new String[] {"$1", "+1 Buy", "When you play this, you may play a Treasure from your hand twice.", "If you do, trash that Treasure."};
    }

    @Override
    public String toString() {
        return "Counterfeit";
    }

}

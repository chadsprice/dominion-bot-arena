package cards;

import server.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class Counterfeit extends Card {

    @Override
    public String name() {
        return "Counterfeit";
    }

    @Override
    public Set<Type> types() {
        return types(Type.TREASURE);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "1$",
                "<+1_Buy>",
                "When you play_this, you_may play a_Treasure from your_hand twice.", "If_you_do, trash that_Treasure."
        };
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
                .filter(Card::isTreasure)
                .collect(Collectors.toSet());
        if (!playable.isEmpty()) {
            Card toPlay = choosePlayTwice(player, game, playable);
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
                    player.coins += toPlay.treasureValue(game);
                    toPlayMoved |= toPlay.onPlay(player, game, toPlayMoved);
                    game.messageIndent--;
                }
                game.messageIndent--;
                // trash it
                if (!toPlayMoved) {
                    game.messageAll("trashing the " + toPlay.htmlNameRaw());
                    player.removeFromPlay(toPlay);
                    game.trash(player, toPlay);
                }
            }
        }
    }

    private Card choosePlayTwice(Player player, Game game, Set<Card> playable) {
        if (player instanceof Bot) {
            Card toPlay = ((Bot) player).counterfeitPlayTwice(Collections.unmodifiableSet(playable));
            checkContains(playable, toPlay);
            return toPlay;
        }
        return new Prompt(player, game)
                .type(Prompt.Type.DANGER)
                .message(this.toString() + ": You may play a treasure from your hand twice.")
                .handChoices(playable)
                .orNone("None")
                .responseCard();
    }

}

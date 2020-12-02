package cards;

import server.*;

import java.util.Set;

public class Vassal extends Card {

    @Override
    public String name() {
        return "Vassal";
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
        return new String[] {
                "<+2$>",
                "Discard the top card of your_deck. If_it's an_Action card, you_may play_it."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 2);
        // reveal the top card of your deck; if it's an action, you may play it, otherwise discard it
        Card top = topCardOfDeck(player);
        if (top != null) {
            game.messageAll("drawing " + top.htmlName());
            game.messageIndent++;
            if (top.isAction() && choosePlay(player, game, top)) {
                player.putFromHandIntoPlay(top);
                game.playAction(player, top, false);
            } else {
                game.messageAll("discarding it");
            }
            game.messageIndent--;
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    private boolean choosePlay(Player player, Game game, Card card) {
        if (player instanceof Bot) {
            return ((Bot) player).vassalPlay(card);
        }
        int choice = new Prompt(player, game)
                .message(this.toString() + ": You draw " + card.htmlName() + ". Play it?")
                .multipleChoices(new String[] {"Play", "Discard"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}

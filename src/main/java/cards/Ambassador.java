package cards;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import server.*;

public class Ambassador extends Card {

    @Override
    public String name() {
        return "Ambassador";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK);
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public String[] description() {
        return new String[]{"Reveal a_card from your_hand.", "Return up_to 2_copies of_it from your_hand to the_Supply.", "Then each other_player gains a_copy of_it."};
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        if (!player.getHand().isEmpty()) {
            // reveal a card
            Card revealed = chooseReveal(
                    player,
                    game,
                    new HashSet<>(player.getHand())
            );
            if (game.canReturnToSupply(revealed)) {
                // return up to 2 of it to the supply
                int numToReturn = chooseNumToReturn(player, game, revealed);
                game.messageAll("returning " + revealed.htmlName(numToReturn) + " to the supply");
                for (int i = 0; i < numToReturn; i++) {
                    player.removeFromHand(revealed);
                }
                game.returnToSupply(revealed, numToReturn);
                // each other player gains a copy
                targets.forEach(target -> {
                    if (game.isAvailableInSupply(revealed)) {
                        game.message(target, "You gain " + revealed.htmlName());
                        game.messageOpponents(target, target.username + " gains " + revealed.htmlName());
                        game.gain(target, revealed);
                    }
                });
            } else {
                game.messageAll("revealing " + revealed.htmlName());
            }
        } else {
            game.message(player, "your hand is empty");
            game.messageOpponents(player, "their hand is empty");
        }
    }

    private Card chooseReveal(Player player, Game game, Set<Card> choices) {
        return game.prompt(
                player,
                bot -> {
                    Card toReturn = bot.ambassadorReveal(Collections.unmodifiableSet(choices));
                    checkContains(choices, toReturn);
                    return toReturn;
                },
                () -> new Prompt(player, game)
                        .message(this.toString() + ": Choose a card to reveal from your hand.")
                        .handChoices(choices)
                        .responseCard()
        );
    }

    private int chooseNumToReturn(Player player, Game game, Card revealed) {
        int maximum = Math.min(player.numberInHand(revealed), 2);
        return game.prompt(
                player,
                bot -> {
                    int numToReturn = ((Bot) player).ambassadorNumToReturn(revealed, maximum);
                    if (numToReturn < 0 || numToReturn > maximum) {
                        throw new IllegalStateException();
                    }
                    return numToReturn;
                },
                () -> {
                    String[] choices = new String[maximum + 1];
                    for (int i = 0; i < choices.length; i++) {
                        choices[i] = i + "";
                    }
                    return new Prompt(player, game)
                            .message(this.toString() + ": Choose how many " + revealed.htmlNameRawPlural() + " to return to the supply")
                            .multipleChoices(choices)
                            .responseMultipleChoiceIndex();
                }
        );
    }

}

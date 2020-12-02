package cards;

import server.*;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class BandOfMisfits extends Card {

    @Override
    public String name() {
        return "Band of Misfits";
    }

    @Override
    public String plural() {
        return "Bands of Misfits";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Play_this as_if_it_were an_Action_card in_the_Supply costing less_than_it that_you_choose.",
                "This is that_card until it_leaves_play."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        Set<Card> imitable = game.cardsCostingAtMost(this.cost(game) - 1).stream()
                .filter(c -> c.isAction() && c != this)
                .collect(Collectors.toSet());
        if (!imitable.isEmpty()) {
            // choose a card to imitate
            Card toImitate = chooseImitate(player, game, imitable);
            // replace this in play with an imitator
            Card imitator;
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

    public Card chooseImitate(Player player, Game game, Set<Card> imitable) {
        return game.prompt(
                player,
                bot -> {
                    Card toImitate = bot.bandOfMisfitsImitate(Collections.unmodifiableSet(imitable));
                    checkContains(imitable, toImitate);
                    return toImitate;
                },
                () -> new Prompt(player, game)
                        .message(this.toString() + ": Choose a card to play this as.")
                        .supplyChoices(imitable)
                        .responseCard()
        );
    }

}

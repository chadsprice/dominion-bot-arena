package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class Rebuild extends Card {

    @Override
    public String name() {
        return "Rebuild";
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
                "<+1_Action>",
                "Name a card. Reveal cards from the_top of your_deck until you_reveal a_Victory card that is_not the named_card. Discard the other_cards. Trash the_Victory card and gain a_Victory card costing up_to 3$_more than_it."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        Card namedCard = chooseNameACard(player, game);
        revealUntil(
                player,
                game,
                c -> c.isVictory() && c != namedCard,
                c -> chooseUpgrade(player, game, c)
        );
    }

    private Card chooseNameACard(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).rebuildNameACard();
        }
        return promptNameACard(player, game, this.toString(), "Name a card.");
    }

    private void chooseUpgrade(Player player, Game game, Card toTrash) {
        game.messageAll("trashing the " + toTrash.htmlNameRaw());
        game.trash(player, toTrash);
        Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost(game) + 3).stream()
                .filter(Card::isVictory)
                .collect(Collectors.toSet());
        if (!gainable.isEmpty()) {
            Card toGain = promptChooseGainFromSupply(
                    player,
                    game,
                    gainable,
                    this.toString() + ": You trash " + toTrash.htmlName() + ". Choose a victory card costing up to $3 more to gain."
            );
            game.messageAll("gaining " + toGain.htmlName());
            game.gain(player, toGain);
        }
    }

}

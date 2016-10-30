package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class Rebuild extends Card {

    public Rebuild() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        Card namedCard = game.promptNameACard(player, "Rebuild", "Name a card.");
        revealUntil(player, game,
                c -> c.isVictory && c != namedCard,
                c -> chooseUpgrade(player, game, c));
    }

    private void chooseUpgrade(Player player, Game game, Card toTrash) {
        game.messageAll("trashing the " + toTrash.htmlNameRaw());
        game.addToTrash(player, toTrash);
        Set<Card> gainable = game.cardsCostingAtMost(toTrash.cost(game) + 3).stream()
                .filter(c -> c.isVictory).collect(Collectors.toSet());
        if (!gainable.isEmpty()) {
            Card toGain = game.promptChooseGainFromSupply(player, gainable, "Rebuild: You trash " + toTrash.htmlName() + ". Choose a victory card costing up to $3 more to gain.");
            game.messageAll("gaining " + toGain.htmlName());
            game.gain(player, toGain);
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Action", "Name a card. Reveal cards from the top of your deck until you reveal a Victory card that is not the named card. Discard the other cards. Trash the Victory card and gain a Victory card costing up to $3 more than it."};
    }

    @Override
    public String toString() {
        return "Rebuild";
    }

}

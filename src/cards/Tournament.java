package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.HashSet;
import java.util.Set;

public class Tournament extends Card {

    public Tournament() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        boolean playerRevealedProvince = false;
        boolean opponentRevealedProvince = false;
        if (player.getHand().contains(Card.PROVINCE)) {
            if (chooseRevealProvince(player, game)) {
                game.messageAll("revealing " + Card.PROVINCE.htmlName());
                playerRevealedProvince = true;
            }
        }
        for (Player opponent : game.getOpponents(player)) {
            if (opponent.getHand().contains(Card.PROVINCE)) {
                if (chooseRevealProvince(opponent, game)) {
                    game.message(opponent, "You reveal " + Card.PROVINCE.htmlName());
                    game.messageOpponents(opponent, opponent.username + " reveals " + Card.PROVINCE.htmlName());
                    opponentRevealedProvince = true;
                    // the outcome doesn't change if additional opponents reveal provinces
                    break;
                }
            }
        }
        if (playerRevealedProvince) {
            game.messageAll("discarding the " + Card.PROVINCE.htmlNameRaw());
            player.putFromHandIntoDiscard(Card.PROVINCE);
            Set<Card> gainable = new HashSet<Card>();
            gainable.addAll(game.prizeCards);
            if (game.supply.get(Card.DUCHY) != 0) {
                gainable.add(Card.DUCHY);
            }
            Card toGain = null;
            if (!gainable.isEmpty()) {
                if (game.supply.get(Card.DUCHY) == 0 || game.prizeCards.isEmpty()) {
                    toGain = game.promptChooseGainFromSupply(player, gainable, "Tournament: Choose a prize or " + Card.DUCHY.htmlNameRaw() + " to gain. (Or choose to gain nothing because one of those piles is empty.)", false, "Gain nothing");
                } else {
                    toGain = game.promptChooseGainFromSupply(player, gainable, "Tournament: Choose a prize or " + Card.DUCHY.htmlNameRaw() + " to gain.");
                }
            }
            if (toGain != null) {
                game.message(player, "gaining " + toGain.htmlName() + " onto your deck");
                game.messageOpponents(player, "gaining " + toGain.htmlName() + " onto their deck");
                game.gainToTopOfDeck(player, toGain);
            } else {
                game.messageAll("gaining nothing");
            }
        }
        if (!opponentRevealedProvince) {
            plusCards(player, game, 1);
            plusCoins(player, game, 1);
        }
    }

    private boolean chooseRevealProvince(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).tournamentRevealProvince();
        }
        int choice = game.promptMultipleChoice(player, "Tournament: Reveal " + Card.PROVINCE.htmlName() + " from your hand?", new String[] {"Reveal", "Don't"});
        return (choice == 0);
    }

    @Override
    public String[] description() {
        return new String[] {"+1 Action", "Each player may reveal a Province from their hand.", "If you do, discard it and gain a Prize (from the Prize pile) or a Duchy, putting it on top of your deck.", "If no-one else does, +1 Card, +$1."};
    }

    @Override
    public String toString() {
        return "Tournament";
    }

}

package cards;

import server.*;

import java.util.HashSet;
import java.util.Set;

public class Tournament extends Card {

    @Override
    public String name() {
        return "Tournament";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 4;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1_Action>",
                "Each player may reveal a_[Province] from their_hand.",
                "If you do, discard_it and gain a_Prize (from the Prize_pile) or a_[Duchy], putting_it on_top of your_deck.",
                "If no-one else does, <+1_Card> and <+1$>."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusActions(player, game, 1);
        boolean playerRevealedProvince = false;
        boolean opponentRevealedProvince = false;
        if (player.getHand().contains(Cards.PROVINCE)) {
            if (chooseRevealProvince(player, game)) {
                game.messageAll("revealing " + Cards.PROVINCE.htmlName());
                playerRevealedProvince = true;
            }
        }
        for (Player opponent : game.getOpponents(player)) {
            if (opponent.getHand().contains(Cards.PROVINCE)) {
                if (chooseRevealProvince(opponent, game)) {
                    game.message(opponent, "You reveal " + Cards.PROVINCE.htmlName());
                    game.messageOpponents(opponent, opponent.username + " reveals " + Cards.PROVINCE.htmlName());
                    opponentRevealedProvince = true;
                    // the outcome doesn't change if additional opponents reveal Provinces
                    break;
                }
            }
        }
        if (playerRevealedProvince) {
            game.messageAll("discarding the " + Cards.PROVINCE.htmlNameRaw());
            player.putFromHandIntoDiscard(Cards.PROVINCE);
            Set<Card> gainable = new HashSet<>();
            gainable.addAll(game.prizeCards);
            if (game.supply.get(Cards.DUCHY) != 0) {
                gainable.add(Cards.DUCHY);
            }
            Card toGain = null;
            if (!gainable.isEmpty()) {
                if (game.supply.get(Cards.DUCHY) == 0 || game.prizeCards.isEmpty()) {
                    toGain = promptChooseGainFromSupply(
                            player,
                            game,
                            gainable,
                            this.toString() + ": Choose a prize or " + Cards.DUCHY.htmlNameRaw() + " to gain. (Or choose to gain nothing because one of those piles is empty.)",
                            "Gain nothing"
                    );
                } else {
                    toGain = promptChooseGainFromSupply(
                            player,
                            game,
                            gainable,
                            this.toString() + ": Choose a prize or " + Cards.DUCHY.htmlNameRaw() + " to gain."
                    );
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
        int choice = new Prompt(player, game)
                .message(this.toString() + ": Reveal " + Cards.PROVINCE.htmlName() + " from your hand?")
                .multipleChoices(new String[] {"Reveal Province", "Don't"})
                .responseMultipleChoiceIndex();
        return (choice == 0);
    }

}

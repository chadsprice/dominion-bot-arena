package cards;

import server.*;

import java.util.Set;
import java.util.stream.Collectors;

public class Squire extends Card {

    @Override
    public String name() {
        return "Squire";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 2;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+1$>",
                "Choose_one:",
                "* +2_Actions",
                "* +2_Buys",
                "* Gain a_[Silver]",
                "When you trash_this, gain an_Attack card."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCoins(player, game, 1);
        switch (chooseBenefit(player, game)) {
            case 0:
                plusActions(player, game, 2);
                break;
            case 1:
                plusBuys(player, game, 2);
                break;
            default: // 2
                if (game.supply.get(Cards.SILVER) != 0) {
                    game.messageAll("gaining " + Cards.SILVER.htmlName());
                    game.gain(player, Cards.SILVER);
                } else {
                    game.messageAll("gaining nothing");
                }
        }
    }

    private int chooseBenefit(Player player, Game game) {
        if (player instanceof Bot) {
            int choice = ((Bot) player).squireBenefit();
            checkMultipleChoice(3, choice);
            return choice;
        }
        return new Prompt(player, game)
                .message(this.toString() + ": Choose one")
                .multipleChoices(new String[] {"+2 Actions", "+2 Buys", "Gain a Silver"})
                .responseMultipleChoiceIndex();
    }

    @Override
    public void onTrash(Player player, Game game) {
        Set<Card> gainable = game.cardsInSupply().stream()
                .filter(Card::isAttack)
                .collect(Collectors.toSet());
        if (!gainable.isEmpty()) {
            Card toGain = promptChooseGainFromSupply(
                    player,
                    game,
                    gainable,
                    this.toString() + ": Choose an Attack card to gain."
            );
            game.messageAll("gaining " + toGain.htmlName() + " because of " + this.htmlNameRaw());
            game.gain(player, toGain);
        }
    }

}

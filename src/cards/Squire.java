package cards;

import server.*;

import java.util.Set;
import java.util.stream.Collectors;

public class Squire extends Card {

    public Squire() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 2;
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
            int benefit = ((Bot) player).squireBenefit();
            if (benefit < 0 || benefit >= 3) {
                throw new IllegalStateException();
            }
            return benefit;
        }
        return game.promptMultipleChoice(player, "Squire: Choose one", new String[] {"+2 Actions", "+2 Buys", "Gain a Silver"});
    }

    @Override
    public void onTrash(Player player, Game game) {
        Set<Card> gainable = game.cardsInSupply().stream().filter(c -> c.isAttack).collect(Collectors.toSet());
        if (!gainable.isEmpty()) {
            Card toGain = game.promptChooseGainFromSupply(player, gainable, "Squire: Choose an Attack card to gain.");
            game.messageAll("gaining " + toGain.htmlName() + " because of " + this.htmlNameRaw());
            game.gain(player, toGain);
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+$1", "Choose one: +2 Actions; or +2 Buys; or gain a Silver.", "When you trash this, gain an Attack card."};
    }

    @Override
    public String toString() {
        return "Squire";
    }

}

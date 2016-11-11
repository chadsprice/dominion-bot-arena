package cards;

import server.*;

import java.util.List;

public class Jester extends Card {

    public Jester() {
        isAction = true;
        isAttack = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        plusCoins(player, game, 2);
        // each other player discards the top card of their deck
        for (Player target : targets) {
            List<Card> drawn = target.takeFromDraw(1);
            if (!drawn.isEmpty()) {
                Card card = drawn.get(0);
                game.message(target, "You draw and discard " + card.htmlName());
                game.messageOpponents(target, target.username + " draws and discards " + card.htmlName());
                game.messageIndent++;
                if (card.isVictory) {
                    if (game.supply.get(Cards.CURSE) != 0) {
                        game.message(target, "You gain " + Cards.CURSE.htmlName());
                        game.messageOpponents(target, target.username + " gains " + Cards.CURSE.htmlName());
                        game.gain(target, Cards.CURSE);
                    }
                } else {
                    if (game.isAvailableInSupply(card)) {
                        if (chooseGainInsteadOfOpponent(player, game, card)) {
                            game.message(player, "You gain " + card.htmlName());
                            game.messageOpponents(player, player.username + " gains " + card.htmlName());
                            game.gain(player, card);
                        } else {
                            game.message(target, "You gain " + card.htmlName());
                            game.messageOpponents(target, target.username + " gains " + card.htmlName());
                            game.gain(target, card);
                        }
                    }
                }
                game.messageIndent--;
            } else {
                game.message(target, "Your deck is empty");
                game.message(target, target.username + "'s deck is empty");
            }
        }
    }

    private boolean chooseGainInsteadOfOpponent(Player player, Game game, Card card) {
        if (player instanceof Bot) {
            return ((Bot) player).jesterGainInsteadOfOpponent(card);
        }
        int choice = game.promptMultipleChoice(player, this.toString() + ": Gain " + card.htmlName() + ", or have your opponent gain it?", new String[] {"Gain", "Opponent gains"});
        return (choice == 0);
    }

    @Override
    public String[] description() {
        return new String[] {"+$2", "Each other player discards the top card of their deck. If it's a Victory card, they gain a Curse. Otherwise either they gain a copy of the discarded card or you do, your choice."};
    }

    @Override
    public String toString() {
        return "Jester";
    }

}

package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.List;

public class Cultist extends Card {

    public Cultist() {
        isAction = true;
        isAttack = true;
        isLooter = true;
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public void onAttack(Player player, Game game, List<Player> targets) {
        plusCards(player, game, 2);
        // each other player gains a ruins
        targets.forEach(target -> {
            if (!game.mixedPiles.get(MixedPileId.RUINS).isEmpty()) {
                Card ruins = game.mixedPiles.get(MixedPileId.RUINS).get(0);
                game.message(target, "You gain " + ruins.htmlName());
                game.messageOpponents(target, target.username + " gains " + ruins.htmlName());
                game.gain(target, ruins);
            }
        });
        // you may play a Cultist from your hand
        if (player.getHand().contains(this) && choosePlayAnotherCultist(player, game)) {
            player.putFromHandIntoPlay(this);
            game.playAction(player, this, false);
        }
    }

    private boolean choosePlayAnotherCultist(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).cultistPlayAnotherCultist();
        }
        int choice = game.promptMultipleChoice(player, "Cultist: Play another Cultist?", new String[] {"Play Cultist", "Don't"});
        return (choice == 0);
    }

    @Override
    public void onTrash(Player player, Game game) {
        List<Card> drawn = player.drawIntoHand(3);
        if (!drawn.isEmpty()) {
            game.message(player, "drawing " + Card.htmlList(drawn) + " because of " + this.htmlNameRaw());
            game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()) + " because of " + this.htmlNameRaw());
        }
    }

    @Override
    public String htmlType() {
        return "Action-Attack-Looter";
    }

    @Override
    public String[] description() {
        return new String[] {"+2 Cards", "Each other player gains a Ruins.", "You may play a Cultist from your hand.", "When you trash this, +3 Cards."};
    }

    @Override
    public String toString() {
        return "Cultist";
    }

}

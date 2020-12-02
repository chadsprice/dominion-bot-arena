package cards;

import server.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Cultist extends Card {

    @Override
    public String name() {
        return "Cultist";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK, Type.LOOTER);
    }

    @Override
    public String htmlType() {
        return "Action-Attack-Looter";
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+2_Cards>",
                "Each other_player gains a_Ruins.",
                "You may play a_[Cultist] from your_hand.",
                "When you trash_this, <+3_Cards>."
        };
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
        if (player.getHand().contains(Cards.CULTIST) && choosePlayAnotherCultist(player, game)) {
            player.putFromHandIntoPlay(Cards.CULTIST);
            game.playAction(player, Cards.CULTIST, false);
        }
    }

    private boolean choosePlayAnotherCultist(Player player, Game game) {
        if (player instanceof Bot) {
            return ((Bot) player).cultistPlayAnotherCultist();
        }
        Card choice = new Prompt(player, game)
                .message(this.toString() + ": You may play another " + Cards.CULTIST.htmlNameRaw() + ".")
                .handChoices(Collections.singleton(Cards.CULTIST))
                .orNone("Don't")
                .responseCard();
        return (choice != null);
    }

    @Override
    public void onTrash(Player player, Game game) {
        List<Card> drawn = player.drawIntoHand(3);
        if (!drawn.isEmpty()) {
            game.message(player, "drawing " + Card.htmlList(drawn) + " because of " + this.htmlNameRaw());
            game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()) + " because of " + this.htmlNameRaw());
        }
    }

}

package cards;

import server.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Doctor extends Card {

    @Override
    public String name() {
        return "Doctor";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public boolean isOverpayable() {
        return true;
    }

    @Override
    public String[] description() {
        return new String[] {
                "Name a_card. Reveal the top_3_cards of your_deck. Trash the_matches. Put the_rest back in_any_order.",
                "When you buy_this, you may overpay for_it. For each_1$ you overpaid, look_at the top_card of your_deck; trash_it, discard_it, or put_it_back."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        Card named = promptNameACard(
                player,
                game,
                this.toString(),
                "Name a card. You will trash any of the top 3 cards of your deck that match."
        );
        game.messageAll("naming " + named.htmlNameRaw());
        List<Card> drawn = player.takeFromDraw(3);
        if (!drawn.isEmpty()) {
            game.messageAll("drawing " + Card.htmlList(drawn));
            List<Card> matches = drawn.stream()
                    .filter(c -> c == named)
                    .collect(Collectors.toList());
            matches.forEach(drawn::remove);
            if (!matches.isEmpty()) {
                game.messageAll("trashing " + Card.htmlList(matches));
            }
            if (!drawn.isEmpty()) {
                game.messageAll("putting the rest back on top");
                putOnDeckInAnyOrder(
                        player,
                        game,
                        drawn,
                        this.toString() + ": Put the rest back on top in any order"
                );
            }
        } else {
            game.message(player, "your deck is empty");
            game.messageOpponents(player, "their deck is empty");
        }
    }

    @Override
    public void onOverpay(Player player, Game game, int amountOverpaid) {
        for (int i = 0; i < amountOverpaid; i++) {
            List<Card> drawn = player.takeFromDraw(1);
            if (!drawn.isEmpty()) {
                Card card = drawn.get(0);
                game.message(player, "drawing " + card.htmlName());
                game.messageOpponents(player, "drawing a card");
                game.messageIndent++;
                switch (chooseTrashDiscardOrPutBack(player, game, card)) {
                    case 0:
                        game.messageAll("trashing the " + card.htmlNameRaw());
                        game.trash(player, card);
                        break;
                    case 1:
                        game.messageAll("discarding the " + card.htmlNameRaw());
                        player.addToDiscard(card);
                        break;
                    default: // case 2
                        game.messageAll("putting it back");
                        player.putOnDraw(card);
                }
                game.messageIndent--;
            } else {
                game.message(player, "your deck is empty");
                game.messageOpponents(player, "their deck is empty");
                break;
            }
        }
    }

    private int chooseTrashDiscardOrPutBack(Player player, Game game, Card card) {
        if (player instanceof Bot) {
            int choice = ((Bot) player).doctorTrashDiscardOrPutBack(card);
            checkMultipleChoice(3, choice);
            return choice;
        }
        return new Prompt(player, game)
                .message(this.toString() + ": You draw " + card.htmlName() + ". Trash it, discard it, or put it back?")
                .multipleChoices(new String[] {"Trash", "Discard", "Put back"})
                .responseMultipleChoiceIndex();
    }

}

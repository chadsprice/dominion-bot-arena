package cards;

import server.Bot;
import server.Card;
import server.Game;
import server.Player;

import java.util.List;
import java.util.stream.Collectors;

public class Doctor extends Card {

    public Doctor() {
        isAction = true;
        isOverpayable = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        Card named = game.promptNameACard(player, this.toString(), "Name a card. You will trash any of the top 3 cards of your deck that match.");
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
                putOnDeckInAnyOrder(player, game, drawn, this.toString() + ": Put the rest back on top in any order");
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
            if (choice < 0 || choice > 2) {
                throw new IllegalStateException();
            }
            return choice;
        }
        return game.promptMultipleChoice(player, this.toString() + ": You draw " + card.htmlName() + ". Trash it, discard it, or put it back?", new String[] {"Trash", "Discard", "Put back"});
    }

    @Override
    public String[] description() {
        return new String[] {"Name a card. Reveal the top 3 cards of your deck. Trash the matches. Put the rest back in any order.", "When you buy this, you may overpay for it. For each $1 you overpaid, look at the top card of your deck; trash it, discard it, or put it back."};
    }

    @Override
    public String toString() {
        return "Doctor";
    }

}

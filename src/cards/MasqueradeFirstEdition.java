package cards;

import server.Card;
import server.Game;
import server.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MasqueradeFirstEdition extends Card {

    public MasqueradeFirstEdition() {
        isAction = true;
    }

    @Override
    public int cost() {
        return 3;
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 2);
        // Each player chooses a card to pass
        List<Player> passOrder = game.getOpponents(player);
        passOrder.add(0, player);
        List<Card> cardsToPass = new ArrayList<Card>();
        int i = 0;
        for (Player eachPlayer : passOrder) {
            if (eachPlayer.getHand().size() > 0) {
                Player playerOnLeft = passOrder.get((i + 1) % passOrder.size());
                String promptType = (eachPlayer == player) ? "actionPrompt" : "attackPrompt";
                Card toPass = game.promptChoosePassToOpponent(eachPlayer, new HashSet<Card>(eachPlayer.getHand()), "Masquerade (1st ed.): Pass a card from your hand to " + playerOnLeft.username + ".", promptType);
                cardsToPass.add(toPass);
            } else {
                cardsToPass.add(null);
            }
            i++;
        }
        // pass cards
        for (i = 0; i < passOrder.size(); i++) {
            Player eachPlayer = passOrder.get(i);
            Player playerOnLeft = passOrder.get((i + 1) % passOrder.size());
            Card toPass = cardsToPass.get(i);
            if (toPass != null) {
                eachPlayer.removeFromHand(toPass);
                playerOnLeft.addToHand(toPass);
            }
            // message player who is giving
            String cardString = toPass == null ? "nothing" : toPass.htmlName();
            game.message(eachPlayer, "You pass " + cardString + " to " + playerOnLeft.username);
            // message player who is receiving
            game.message(playerOnLeft, eachPlayer.username + " passes " + cardString + " to you");
            // message other players (without naming the card passed)
            cardString = toPass == null ? "nothing" : "a card";
            for (Player eachUninvolved : passOrder) {
                if (eachUninvolved != eachPlayer && eachUninvolved != playerOnLeft) {
                    game.message(eachUninvolved, eachPlayer.username + " passes " + cardString + " to " + playerOnLeft.username);
                }
            }
        }
        // you may trash a card from your hand
        if (player.getHand().size() > 0){
            int choice = game.promptMultipleChoice(player, "Masquerade (1st ed.): Trash a card from your hand?", new String[] {"Yes", "No"});
            if (choice == 0) {
                Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<Card>(player.getHand()), "Masquerade (1st ed.): Trash a card from your hand");
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
                game.messageAll("trashing " + toTrash.htmlName());
            }
        }
    }

    @Override
    public String[] description() {
        return new String[] {"+2 Cards", "Each player passes a card from their hand to the left at once. Then you may trash a card from your hand."};
    }

    @Override
    public String toString() {
        return "Masquerade (1st ed.)";
    }

    @Override
    public String plural() {
        return "Masquerades (1st ed.)";
    }

}

package cards;

import server.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Inn extends Card {

    @Override
    public String name() {
        return "Inn";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION);
    }

    @Override
    public int cost() {
        return 5;
    }

    @Override
    public String[] description() {
        return new String[] {
                "<+2_Cards>",
                "<+2_Actions>",
                "Discard 2_cards.",
                "When you gain_this, look_through your discard_pile (including_this), reveal any_number of Action_cards from_it, and shuffle_them into your_deck."
        };
    }

    @Override
    public void onPlay(Player player, Game game) {
        plusCards(player, game, 2);
        plusActions(player, game, 2);
        if (!player.getHand().isEmpty()) {
            List<Card> toDiscard = promptDiscardNumber(
                    player,
                    game,
                    2
            );
            game.messageAll("discarding " + Card.htmlList(toDiscard));
            player.putFromHandIntoDiscard(toDiscard);
        }
    }

    @Override
    public void onGain(Player player, Game game) {
        // shuffle any number of action cards from your discard back into your deck
        List<Card> innable = player.getDiscard().stream()
                .filter(Card::isAction)
                .collect(Collectors.toList());
        if (!innable.isEmpty()) {
            List<Card> toInn = chooseInn(player, game, innable);
            if (!toInn.isEmpty()) {
                game.message(player, "shuffling " + Card.htmlList(toInn) + " into your deck");
                game.messageOpponents(player, "shuffling " + Card.htmlList(toInn) + " into their deck");
                player.removeFromDiscard(toInn);
                player.shuffleIntoDraw(toInn);
            }
        }
    }

    private List<Card> chooseInn(Player player, Game game, List<Card> innable) {
        if (player instanceof Bot) {
            List<Card> toInn = ((Bot) player).innShuffleIntoDeck(Collections.unmodifiableList(innable));
            checkContains(innable, toInn);
            return toInn;
        }
        innable = new ArrayList<>(innable);
        Collections.sort(innable, Player.HAND_ORDER_COMPARATOR);
        List<Card> toInn = new ArrayList<>();
        while (!innable.isEmpty()) {
            Card nextToInn = sendPromptInn(player, game, innable);
            if (nextToInn != null) {
                innable.remove(nextToInn);
                toInn.add(nextToInn);
            } else {
                break;
            }
        }
        return toInn;
    }

    private Card sendPromptInn(Player player, Game game, List<Card> innable) {
        String[] choices = new String[innable.size() + 1];
        for (int i = 0; i < innable.size(); i++) {
            choices[i] = innable.get(i).toString();
        }
        choices[choices.length - 1] = "Done";
        int choice = new Prompt(player, game)
                .message(this.toString()+ ": Choose any number of action cards in your discard to shuffle into your deck")
                .multipleChoices(choices)
                .responseMultipleChoiceIndex();
        if (choice == choices.length - 1) {
            return null;
        } else {
            return innable.get(choice);
        }
    }

}

package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"unchecked", "WeakerAccess"})
public class Prompt {

    public enum Type {NORMAL, DANGER, PLAY, BUY, REACTION};

    public enum Destination {DISCARD, TRASH, DECK};

    public enum Amount {EXACT, EXACT_OR_NONE, UP_TO};

    public Player player;
    public Game game;

    public Type type = Type.NORMAL;
    public String message;
    public Set<Card> supplyChoices;
    public Set<Card.MixedPileId> mixedPileChoices;
    public Set<Card> handChoices;
    public String noneText;
    public String[] multipleChoices;
    public Set<Integer> disabledMultipleChoiceIndexes;
    public int numberFromHand;
    public Destination destination;
    public Amount amount;

    public Prompt(Player player, Game game) {
        this.player = player;
        this.game = game;
    }

    public Prompt type(Type type) {
        this.type = type;
        return this;
    }

    public Prompt message(String message) {
        this.message = message;
        return this;
    }

    public Prompt supplyChoices(Set<Card> supplyChoices) {
        this.supplyChoices = supplyChoices;
        return this;
    }

    public Prompt mixedPileChoices(Set<Card.MixedPileId> mixedPileChoices) {
        this.mixedPileChoices = mixedPileChoices;
        return this;
    }

    public Prompt handChoices(Set<Card> handChoices) {
        this.handChoices = handChoices;
        return this;
    }

    public Prompt orNone(String noneText) {
        this.noneText = noneText;
        return this;
    }

    public Prompt multipleChoices(String[] multipleChoices) {
        return multipleChoices(multipleChoices, null);
    }

    public Prompt multipleChoices(String[] multipleChoices, Set<Integer> disabledMultipleChoiceIndexes) {
        this.multipleChoices = multipleChoices;
        this.disabledMultipleChoiceIndexes = disabledMultipleChoiceIndexes;
        return this;
    }

    public Prompt numberFromHand(int numberOfCards, Destination destination, Amount amount) {
        this.numberFromHand = numberOfCards;
        this.destination = destination;
        this.amount = amount;
        return this;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("command", "prompt");
        json.put("type", type.name().toLowerCase());
        if (message != null) {
            json.put("message", message);
        }
        if (supplyChoices != null) {
            json.put("supplyChoices", toJSONPileIds(supplyChoices));
        }
        if (mixedPileChoices != null) {
            json.put("mixedPileChoices", toJSON(mixedPileChoices));
        }
        if (handChoices != null) {
            json.put("handChoices", toJSON(handChoices));
        }
        if (multipleChoices != null) {
            JSONArray jsonMultipleChoices = new JSONArray();
            jsonMultipleChoices.addAll(Arrays.asList(multipleChoices));
            json.put("multipleChoices", jsonMultipleChoices);
        }
        if (disabledMultipleChoiceIndexes != null) {
            JSONArray jsonDisabledMultipleChoiceIndexes = new JSONArray();
            jsonDisabledMultipleChoiceIndexes.addAll(disabledMultipleChoiceIndexes);
            json.put("disabledMultipleChoiceIndexes", jsonDisabledMultipleChoiceIndexes);
        }
        if (numberFromHand > 0) {
            json.put("numberFromHand", numberFromHand);
            json.put("destination", destination.name().toLowerCase());
            json.put("amount", amount.name().toLowerCase());
        }
        if (noneText != null) {
            json.put("noneText", noneText);
        }
        return json;
    }

    private static JSONArray toJSON(Collection<?> collection) {
        JSONArray json = new JSONArray();
        collection.stream()
                .map(Object::toString)
                .forEach(json::add);
        return json;
    }

    private static JSONArray toJSONPileIds(Collection<Card> cards) {
        JSONArray json = new JSONArray();
        cards.forEach(card -> {
            if (card.inMixedPile()) {
                json.add(card.mixedPileId().toString());
            } else {
                json.add(card.toString());
            }
        });
        return json;
    }

    public Card responseCard() {
        PromptResponse response = response();
        if (response.supplyChoice != null) {
            return response.supplyChoice;
        }
        if (response.handChoice != null) {
            return response.handChoice;
        }
        return null;
    }

    public List<Card> responseCards() {
        PromptResponse response = response();
        return response.handChoices;
    }

    public int responseMultipleChoiceIndex() {
        PromptResponse response = response();
        return response.multipleChoiceIndex;
    }

    public PromptResponse response() {
        Object responseJSON = game.sendPromptAndGetResponse(player, this);
        if (responseJSON instanceof JSONObject) {
            PromptResponse response = new PromptResponse((JSONObject) responseJSON, game);
            return validated(response);
        } else {
            return makeUpValidResponse();
        }
    }

    private PromptResponse validated(PromptResponse response) {
        if (supplyChoices != null
                && response.supplyChoice != null
                && supplyChoices.contains(response.supplyChoice)) {
            return PromptResponse.supplyChoice(response.supplyChoice);
        }
        if (mixedPileChoices != null
                && response.mixedPileChoice != null
                && mixedPileChoices.contains(response.mixedPileChoice)) {
            return PromptResponse.mixedPileChoice(response.mixedPileChoice);
        }
        if (handChoices != null
                && response.handChoice != null
                && handChoices.contains(response.handChoice)) {
            return PromptResponse.handChoice(response.handChoice);
        }
        if (multipleChoices != null
                && 0 <= response.multipleChoiceIndex
                && response.multipleChoiceIndex < multipleChoices.length
                && !(disabledMultipleChoiceIndexes != null && isDisabledMultipleChoice(response.multipleChoiceIndex))) {
            return PromptResponse.multipleChoiceIndex(response.multipleChoiceIndex);
        }
        if (numberFromHand > 0
                && response.handChoices != null
                && player.handContains(response.handChoices)) {
            if (response.handChoices.size() == numberFromHand
                    || (amount == Amount.UP_TO && response.handChoices.size() < numberFromHand)
                    || (amount == Amount.EXACT_OR_NONE && response.handChoices.size() == 0)) {
                return PromptResponse.handChoices(response.handChoices);
            }
        }
        if (noneText != null) {
            return PromptResponse.none();
        }
        return makeUpValidResponse();
    }

    private PromptResponse makeUpValidResponse() {
        if (noneText != null) {
            return PromptResponse.none();
        }
        if (supplyChoices != null) {
            return PromptResponse.supplyChoice(supplyChoices.iterator().next());
        }
        if (mixedPileChoices != null) {
            return PromptResponse.mixedPileChoice(mixedPileChoices.iterator().next());
        }
        if (handChoices != null) {
            return PromptResponse.handChoice(handChoices.iterator().next());
        }
        if (multipleChoices != null) {
            for (int i = 0; i < multipleChoices.length; i++) {
                if (!isDisabledMultipleChoice(i)) {
                    return PromptResponse.multipleChoiceIndex(i);
                }
            }
        }
        if (numberFromHand > 0) {
            return PromptResponse.handChoices(player.getHand().subList(0, numberFromHand));
        }
        // we can't make up a valid response, so the prompt is impossible to answer
        throw new IllegalStateException();
    }

    private boolean isDisabledMultipleChoice(int multipleChoiceIndex) {
        if (disabledMultipleChoiceIndexes == null) {
            return false;
        }
        for (Integer disabledMultipleChoiceIndex : disabledMultipleChoiceIndexes) {
            if (multipleChoiceIndex == disabledMultipleChoiceIndex) {
                return true;
            }
        }
        return false;
    }
}

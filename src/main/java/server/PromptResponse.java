package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PromptResponse {

    public Card supplyChoice;
    public Card.MixedPileId mixedPileChoice;
    public Card handChoice;
    public int multipleChoiceIndex;
    public List<Card> handChoices;

    public PromptResponse(JSONObject response, Game game) {
        if (response == null) {
            return;
        }
        supplyChoice = validateStringType(response, "supplyChoice", string -> parseSupplyChoice(string, game));
        mixedPileChoice = validateStringType(response, "mixedPileChoice", Card.MixedPileId::fromString);
        handChoice = validateStringType(response, "handChoice", Cards::fromName);
        multipleChoiceIndex = validateLongType(response, "multipleChoiceIndex");
        handChoices = parseCards(response, "handChoices");
    }

    private static <R> R validateStringType(JSONObject object, String name, Function<String, R> consumer) {
        Object value = object.get(name);
        if (value instanceof String) {
            return consumer.apply((String) value);
        }
        return null;
    }

    private static int validateLongType(JSONObject object, String name) {
        Object value = object.get(name);
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        return -1;
    }

    private static Card parseSupplyChoice(String pileId, Game game) {
        // card
        Card card = Cards.fromName(pileId);
        if (card != null) {
            return card;
        }
        // mixed pile top card
        Card.MixedPileId mixedPileId = Card.MixedPileId.fromString(pileId);
        if (mixedPileId != null
                && game.mixedPiles.containsKey(mixedPileId)
                && !game.mixedPiles.get(mixedPileId).isEmpty()) {
            return game.mixedPiles.get(mixedPileId).get(0);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<Card> parseCards(JSONObject response, String key) {
        Object value = response.get(key);
        if (value instanceof JSONArray) {
            Stream<Card> cardStream = ((JSONArray) value).stream()
                    .filter(String.class::isInstance)
                    .map(cardName -> Cards.fromName((String) cardName))
                    .filter(Objects::nonNull);
            return cardStream.collect(Collectors.toList());
        }
        return null;
    }

    private PromptResponse() {}

    public static PromptResponse none() {
        return new PromptResponse();
    }

    static PromptResponse supplyChoice(Card supplyChoice) {
        PromptResponse response = new PromptResponse();
        response.supplyChoice = supplyChoice;
        return response;
    }

    static PromptResponse mixedPileChoice(Card.MixedPileId mixedPileChoice) {
        PromptResponse response = new PromptResponse();
        response.mixedPileChoice = mixedPileChoice;
        return response;
    }

    static PromptResponse handChoice(Card handChoice) {
        PromptResponse response = new PromptResponse();
        response.handChoice = handChoice;
        return response;
    }

    static PromptResponse multipleChoiceIndex(int multipleChoiceIndex) {
        PromptResponse response = new PromptResponse();
        response.multipleChoiceIndex = multipleChoiceIndex;
        return response;
    }

    static PromptResponse handChoices(List<Card> handChoices) {
        PromptResponse response = new PromptResponse();
        response.handChoices = handChoices;
        return response;
    }

}

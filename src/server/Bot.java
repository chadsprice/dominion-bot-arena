package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

public class Bot extends Player {

	@Override
	protected void sendCommand(JSONObject command, boolean autoIssue) {}

	@Override
	public void issueCommands() {}

	public Bot() {
		super(null);
		username = "<span class=\"botName\">BigMoney[Bot]</span>";
	}

	public Card chooseBuy(Set<Card> choiceSet) {
		return chooseGainFromSupply(choiceSet, false);
	}

	public Card chooseGainFromSupply(Set<Card> choiceSet, boolean isMandatory) {
		if (choiceSet.contains(Card.PROVINCE)) {
			return Card.PROVINCE;
		} else if (choiceSet.contains(Card.DUCHY) && game.supply.get(Card.PROVINCE) <= 2) {
			return Card.DUCHY;
		} else if (choiceSet.contains(Card.GOLD)) {
			return Card.GOLD;
		} else if (choiceSet.contains(Card.SILVER)) {
			return Card.SILVER;
		} else if (choiceSet.contains(Card.COPPER)) {
			return Card.COPPER;
		} else {
			return choiceSet.iterator().next();
		}
	}

	public Card chooseOpponentGainFromSupply(Set<Card> choiceSet) {
		if (choiceSet.contains(Card.CURSE)) {
			return Card.CURSE;
		} else {
			return choiceSet.iterator().next();
		}
	}

	public Card choosePlay(Set<Card> choiceSet, boolean isMandatory) {
		return choiceSet.iterator().next();
	}

	public Card chooseTrashFromHand(Set<Card> choiceSet) {
		if (choiceSet.contains(Card.CURSE)) {
			return Card.CURSE;
		} else if (choiceSet.contains(Card.ESTATE)) {
			return Card.ESTATE;
		} else if (choiceSet.contains(Card.COPPER)) {
			return Card.COPPER;
		} else {
			return choiceSet.iterator().next();
		}
	}

	public Card choosePutOnDeck(Set<Card> choiceSet) {
		return choiceSet.iterator().next();
	}

	public Card choosePassToOpponent(Set<Card> choiceSet) {
		return chooseTrashFromHand(choiceSet);
	}

	public Card chooseRevealAttackReaction(Set<Card> choiceSet) {
		if (choiceSet.contains(Card.MOAT)) {
			return Card.MOAT;
		} else {
			return null;
		}
	}

	public Card chooseFromHand(Set<Card> choiceSet) {
		return choiceSet.iterator().next();
	}

	public List<Card> discardNumber(int number) {
		return new ArrayList<Card>(getHand().subList(0, Math.min(number, getHand().size())));
	}

	public int multipleChoice(String[] choices, int[] disabledIndexes) {
		for (int i = 0; i < choices.length; i++) {
			boolean disabled = false;
			if (disabledIndexes != null) {
				for (int j = 0; j < disabledIndexes.length; j++) {
					if (disabledIndexes[j] == i) {
						disabled = true;
						break;
					}
				}
			}
			if (!disabled) {
				return i;
			}
		}
		throw new IllegalStateException();
	}

}

package bots;

import server.Bot;
import server.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Mimic extends Bot {

	private List<Card> gainStrategy;

	@Override
	public String botName() {
		return "Mimic";
	}

	public void setStrategy(List<Card> gainStrategy) {
		this.gainStrategy = new ArrayList<>(gainStrategy);
	}

	public Card chooseGainFromSupply(Set<Card> choiceSet, boolean isMandatory) {
		Card toGain = null;
		for (Card card : gainStrategy) {
			if (choiceSet.contains(card)) {
				toGain = card;
				break;
			}
		}
		// if there is card that we want to gain, choose it
		if (toGain != null) {
			gainStrategy.remove(toGain);
			return toGain;
		} else {
			// otherwise, fall back on big money
			toGain = super.chooseGainFromSupply(choiceSet, isMandatory);
			if (toGain != null) {
				gainStrategy.remove(toGain);
			}
			return toGain;
		}
	}

}

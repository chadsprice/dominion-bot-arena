package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MimicBot extends Bot {

	private List<Card> gainStrategy;

	public MimicBot() {
		super();
		username = "<span class=\"botName\">Mimic[Bot]</span>";
	}

	public void setStrategy(List<Card> gainStrategy) {
		this.gainStrategy = new ArrayList<Card>(gainStrategy);
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

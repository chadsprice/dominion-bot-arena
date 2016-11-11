package server;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import cards.*;

public class Card {

	public boolean isAction;
	public boolean isTreasure;
	public boolean isVictory;
	public boolean isAttack;
	public boolean isAttackReaction;
	public boolean isDuration;
	public boolean isRuins;
	public boolean isShelter;
	public boolean isLooter;
	public boolean isOverpayable;

    public boolean isBandOfMisfits;

	public int startingSupply(int numPlayers) {
		if (isVictory) {
			return numPlayers == 2 ? 8 : 12;
		} else {
			return 10;
		}
	}

	public int cost(Game game) {
		int computedCost = cost();
		computedCost -=  game.cardCostReduction;
		if (isAction) {
			computedCost -= 2 * game.numberInPlay(Cards.QUARRY);
		}
		computedCost -= 2 * game.numberInPlay(Cards.PRINCESS);
		computedCost -= game.numberInPlay(Cards.HIGHWAY);
		// cost can never be less than zero
		return Math.max(computedCost, 0);
	}
	public int cost() {
		throw new UnsupportedOperationException();
	}

	public int treasureValue(Game game) {
		return treasureValue();
	}
	public int treasureValue() {
		throw new UnsupportedOperationException();
	}

	public int victoryValue(List<Card> deck) {
		return victoryValue();
	}
	public int victoryValue() {
		throw new UnsupportedOperationException();
	}

	public void onPlay(Player player, Game game) {
		// do nothing (this should only be called by basic treasures)
	}
	public boolean onPlay(Player player, Game game, boolean hasMoved) {
		onPlay(player, game);
		return false;
	}

	public void onAttack(Player player, Game game, List<Player> targets) {
		throw new UnsupportedOperationException();
	}
	public boolean onAttack(Player player, Game game, List<Player> targets, boolean hasMoved) {
		onAttack(player, game, targets);
		return false;
	}

	public boolean onAttackReaction(Player player, Game game) {
		return false;
	}

	public boolean onDurationPlay(Player player, Game game, List<Card> toHaven) {
		onPlay(player, game);
		return true;
	}
	public void onDurationEffect(Player player, Game game) {
		throw new UnsupportedOperationException();
	}
	public void onDurationEffect(Player player, Game game, DurationEffect duration) {
		onDurationEffect(player, game);
	}

	public void onGain(Player player, Game game) {}

    public void onTrash(Player player, Game game) {}
	public boolean onTrashIsTrashed(Player player, Game game) {
        onTrash(player, game);
        return true;
    }

    public void onOverpay(Player player, Game game, int amountOverpaid) {}

    // common modular effects

	protected void plusCards(Player player, Game game, int numCards) {
        if (numCards == 0) {
            return;
        }
        List<Card> drawn = player.drawIntoHand(numCards);
        game.message(player, "drawing " + Card.htmlList(drawn));
        game.messageOpponents(player, "drawing " + Card.numCards(drawn.size()));
	}
	protected void plusActions(Player player, Game game, int numActions) {
		if (numActions == 0) {
			return;
		}
		game.messageAll("getting +" + numActions + ((numActions == 1) ? " action" : " actions"));
		player.addActions(numActions);
	}
	protected void plusBuys(Player player, Game game, int numBuys) {
		if (numBuys == 0) {
			return;
		}
		game.messageAll("getting +" + numBuys + ((numBuys == 1) ? " buy" : " buys"));
		player.addBuys(numBuys);
	}
	protected void plusCoins(Player player, Game game, int numCoins) {
		if (numCoins == 0) {
			return;
		}
		game.messageAll("getting +$" + numCoins);
		player.addCoins(numCoins);
	}
	protected void plusCoinTokens(Player player, Game game, int numTokens) {
		if (numTokens == 0) {
			return;
		}
		game.messageAll("getting +" + numTokens + " coin " + (numTokens == 1 ? "token" : "tokens"));
		player.addCoinTokens(numTokens);
	}
	protected void plusVictoryTokens(Player player, Game game, int numTokens) {
		if (numTokens == 0) {
			return;
		}
		game.messageAll("getting +" + numTokens + " VP");
		player.addVictoryTokens(numTokens);
	}

    protected void gain(Player player, Game game, Card card) {
        if (game.supply.get(card) != 0) {
            game.messageAll("gaining " + card.htmlName());
            game.gain(player, card);
        }
    }

    protected void gain(Player player, Game game, Card card, int number) {
		int numGainable = Math.min(number, game.supply.get(card));
		game.messageAll("gaining " + card.htmlName(numGainable));
		for (int i = 0; i < numGainable && game.supply.get(card) != 0; i++) {
			game.gain(player, card);
		}
	}

	protected void gainOntoDeck(Player player, Game game, Card card) {
        if (game.supply.get(card) != 0) {
            game.message(player, "gaining " + card.htmlName() + " onto your deck");
            game.messageOpponents(player, "gaining " + card.htmlName() + " onto their deck");
            game.gainToTopOfDeck(player, card);
        }
    }

    protected void discardNumber(Player player, Game game, int number) {
        if (player.getHand().isEmpty() || number == 0) {
            return;
        }
        List<Card> toDiscard = game.promptDiscardNumber(player, number, this.toString());
        game.messageAll("discarding " + Card.htmlList(toDiscard));
        player.putFromHandIntoDiscard(toDiscard);
    }

	protected List<Card> discardAnyNumber(Player player, Game game) {
		if (player.getHand().isEmpty()) {
			return new ArrayList<>();
		}
        List<Card> discarded = game.promptDiscardNumber(player, player.getHand().size(), false, this.toString());
        game.messageAll("discarding " + Card.htmlList(discarded));
        player.putFromHandIntoDiscard(discarded);
        return discarded;
    }

    protected Card gainCardCostingUpTo(Player player, Game game, int cost) {
        Set<Card> gainable = game.cardsCostingAtMost(cost);
        if (!gainable.isEmpty()) {
            Card toGain = game.promptChooseGainFromSupply(player, gainable, this.toString() + ": Choose a card to gain.");
            game.messageAll("gaining " + toGain.htmlName());
            boolean replaced = game.gain(player, toGain);
			if (!replaced) {
				return toGain;
			}
        } else {
            game.messageAll("gaining nothing");
        }
        return null;
    }

    protected void handSizeAttack(List<Player> targets, Game game, int handSize) {
        targets.forEach(target -> {
            if (target.getHand().size() > handSize) {
                int numToDiscard = target.getHand().size() - handSize;
                List<Card> discarded = game.promptDiscardNumber(target, numToDiscard, this.toString());
                game.message(target, "You discard " + Card.htmlList(discarded));
                game.messageOpponents(target, target.username + " discards " + Card.htmlList(discarded));
                target.putFromHandIntoDiscard(discarded);
            }
        });
    }

    protected void junkingAttack(List<Player> targets, Game game, Card junk) {
        targets.forEach(target -> {
            if (game.isAvailableInSupply(junk)) {
                game.message(target, "You gain " + junk.htmlName());
                game.messageOpponents(target, target.username + " gains " + junk.htmlName());
                game.gain(target, junk);
            }
        });
    }

    protected void topTwoCardsAttack(List<Player> targets, Game game, Predicate<Card> trashablePredicate) {
        targets.forEach(target -> {
            // draw 2 cards
            List<Card> drawn = target.takeFromDraw(2);
            if (!drawn.isEmpty()) {
                // announce the drawn cards
                game.message(target, "You draw " + Card.htmlList(drawn));
                game.messageOpponents(target, target.username + " draws " + Card.htmlList(drawn));
                game.messageIndent++;
                // filter out those that can be trashed
                Set<Card> trashable = drawn.stream().filter(trashablePredicate).collect(Collectors.toSet());
                if (!trashable.isEmpty()) {
                    // choose one to trash
                    Card toTrash;
                    if (trashable.size() == 1) {
                        toTrash = trashable.iterator().next();
                    } else {
                        toTrash = topTwoCardsAttackChooseTrash(target, game, trashable);
                    }
                    // trash it
                    game.messageAll("trashing the " + toTrash.htmlNameRaw());
                    drawn.remove(toTrash);
                    game.trash(target, toTrash);
                }
                if (!drawn.isEmpty()) {
                    game.messageAll("discarding the rest");
                    target.addToDiscard(drawn);
                }
                game.messageIndent--;
            } else {
                game.message(target, "Your deck is empty");
                game.messageOpponents(target, target.username + "'s deck is empty");
            }
        });
    }
    private Card topTwoCardsAttackChooseTrash(Player player, Game game, Set<Card> trashable) {
        if (player instanceof Bot) {
            Card toTrash = ((Bot) player).topTwoCardAttackTrash(trashable);
            if (!trashable.contains(toTrash)) {
                throw new IllegalStateException();
            }
            return toTrash;
        }
        return game.promptMultipleChoiceCard(player, this.toString() + ": You draw " + Card.htmlList(new ArrayList<>(trashable)) + ". Choose one to trash.", "attackPrompt", trashable);
    }

    protected Card topCardOfDeck(Player player) {
        List<Card> drawn = player.takeFromDraw(1);
        if (!drawn.isEmpty()) {
            return drawn.get(0);
        } else {
            return null;
        }
    }

    protected void onRemodelVariant(Player player, Game game, int coins, boolean isExact) {
        if (!player.getHand().isEmpty()) {
            // trash a card from your hand
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<>(player.getHand()), this.toString() + ": Choose a card to trash.");
            game.messageAll("trashing " + toTrash.htmlName());
            player.removeFromHand(toTrash);
            game.trash(player, toTrash);
            // gain a card from the supply costing more
            Set<Card> gainable;
            if (isExact) {
                gainable = game.cardsCostingExactly(toTrash.cost(game) + coins);
            } else {
                gainable = game.cardsCostingAtMost(toTrash.cost(game) + coins);
            }
            if (!gainable.isEmpty()) {
                Card toGain = game.promptChooseGainFromSupply(player, gainable, this.toString() + ": Choose a card to gain.");
                game.messageAll("gaining " + toGain.htmlName());
				game.gain(player, toGain);
            } else {
                game.messageAll("gaining nothing");
            }
        } else {
            game.messageAll("having no card to trash");
        }
    }

	protected boolean onThroneRoomVariant(Player player, Game game, int multiplier, boolean isMandatory, boolean hasMoved) {
        boolean usedAsModifier = false;
        Set<Card> actions = player.getHand().stream()
                .filter(c -> c.isAction)
                .collect(Collectors.toSet());
        if (!actions.isEmpty()) {
            Card toPlay;
            if (isMandatory) {
                toPlay = game.promptChoosePlay(player, actions,
                        this.toString() + ": Choose an action from your hand to play " + (multiplier == 2 ? "twice" : "three times") + ".");
            } else {
                toPlay = game.promptChoosePlay(player, actions,
                        this.toString() + ": You may play an action from your hand " + (multiplier == 2 ? "twice" : "three times") + ".",
                        false, "None");
            }
            if (toPlay != null) {
                game.messageAll("choosing " + toPlay.htmlName());
                // put the chosen card into play
                player.putFromHandIntoPlay(toPlay);
                // handle Band of Misfits entirely separately
                if (toPlay == Cards.BAND_OF_MISFITS) {
                    return onThroneRoomVariantBandOfMisfits(player, game, multiplier, hasMoved);
                }
                // play it multiple times
                boolean toPlayMoved = false;
                for (int i = 0; i < multiplier; i++) {
                    toPlayMoved |= game.playAction(player, toPlay, toPlayMoved);
                    // if the card was a duration card, and it was set aside, and this hasn't been moved
                    if (toPlay.isDuration && toPlayMoved && !hasMoved && !usedAsModifier) {
                        // set this aside as a modifier
                        player.removeFromPlay(this);
                        player.addDurationSetAside(this);
                        usedAsModifier = true;
                    }
                }
                afterThroneRoomVariant(player, game, toPlay, toPlayMoved);
            } else {
                game.messageAll("choosing nothing");
            }
        } else {
            game.messageAll("having no actions");
        }
        return usedAsModifier;
    }
    private boolean onThroneRoomVariantBandOfMisfits(Player player, Game game, int multiplier, boolean hasMoved) {
        boolean usedAsModifier = false;
        Set<Card> imitable = game.cardsCostingAtMost(Cards.BAND_OF_MISFITS.cost(game) - 1).stream()
                .filter(c -> c.isAction && c != Cards.BAND_OF_MISFITS)
                .collect(Collectors.toSet());
        if (!imitable.isEmpty()) {
            Card toImitate = BandOfMisfits.chooseImitate(player, game, imitable);
            // replace this in play with an imitator
            Card imitator;
            try {
                imitator = toImitate.getClass().newInstance();
                imitator.isBandOfMisfits = true;
            } catch (Exception e) {
                throw new IllegalStateException();
            }
            player.removeFromPlay(this);
            player.addToPlay(imitator);
            // play it multiple times
            boolean imitatorMoved = false;
            // if Band of Misfits leaves play, choose a new card for it to become, but only do this once
            // (Band of Misfits doesn't become that card since it has already left play, but it still has the on-play effect of the chosen card)
            // (yes, this is an official rule, no, I don't know what they were thinking)
            boolean madeNewChoiceAfterLeavingPlay = false;
            for (int i = 0; i < multiplier; i++) {
                // if the card has left play, choose a different card for it to imitate
                if (imitatorMoved && !imitator.isDuration && !(imitator instanceof ThroneRoom || imitator instanceof KingsCourt || imitator instanceof Procession) && !madeNewChoiceAfterLeavingPlay) {
                    imitable = game.cardsCostingAtMost(Cards.BAND_OF_MISFITS.cost(game) - 1).stream()
                            .filter(c -> c.isAction && c != Cards.BAND_OF_MISFITS)
                            .collect(Collectors.toSet());
                    if (!imitable.isEmpty()) {
                        toImitate = BandOfMisfits.chooseImitate(player, game, imitable);
                        // create a new card that has the on-play effect of the chosen card, but isn't technically in the game anywhere
                        try {
                            imitator = toImitate.getClass().newInstance();
                            imitator.isBandOfMisfits = true;
                        } catch (Exception e) {
                            throw new IllegalStateException();
                        }
                        madeNewChoiceAfterLeavingPlay = true;
                    } else {
                        game.messageAll(Cards.BAND_OF_MISFITS.htmlNameRaw() + " has left play and there are no cards it can be played as");
                        break;
                    }
                }
                imitatorMoved |= game.playAction(player, imitator, imitatorMoved);
                // if the card was a duration card, and it was set aside, and this hasn't been moved
                if (imitatorMoved && imitator.isDuration && !hasMoved && !usedAsModifier) {
                    // set this aside as a modifier
                    player.removeFromPlay(this);
                    player.addDurationSetAside(this);
                    usedAsModifier = true;
                }
            }
            afterThroneRoomVariant(player, game, imitator, imitatorMoved);
        } else {
            game.messageAll("there are no cards it can be played as");
        }
        return usedAsModifier;
    }
    protected void afterThroneRoomVariant(Player player, Game game, Card played, boolean playedMoved) {}

	protected void onMasqueradeVariant(Player player, Game game, boolean isSecondEdition) {
        plusCards(player, game, 2);
        // ask players in turn order which card they want to pass, starting with this player
        List<Player> passOrder = game.getOpponents(player);
        passOrder.add(0, player);
        if (isSecondEdition) {
            // skip over players with no cards in hand
            passOrder = passOrder.stream()
                    .filter(p -> !p.getHand().isEmpty())
                    .collect(Collectors.toList());
        }
        // only bother to pass cards if there are at least 2 players that will pass cards
        if (passOrder.size() >= 2) {
            // choose all cards to pass first
            List<Card> cardsToPass = new ArrayList<>();
            int i = 0;
            for (Player passingPlayer : passOrder) {
                Player receivingPlayer = passOrder.get((i + 1) % passOrder.size());
                String promptType = (passingPlayer == player) ? "actionPrompt" : "attackPrompt";
                Card toPass = game.promptChoosePassToOpponent(passingPlayer, new HashSet<>(passingPlayer.getHand()),
                        this.toString() + ": Pass a card from your hand to " + receivingPlayer.username + ".", promptType);
                cardsToPass.add(toPass);
                i++;
            }
            // then actually pass them
            for (i = 0; i < passOrder.size(); i++) {
                Player passingPlayer = passOrder.get(i);
                Player receivingPlayer = passOrder.get((i + 1) % passOrder.size());
                Card toPass = cardsToPass.get(i);
                if (toPass != null) {
                    passingPlayer.removeFromHand(toPass);
                    receivingPlayer.addToHand(toPass);
                }
                String cardString = (toPass != null) ? toPass.htmlName() : "nothing";
                // message player who is passing
                game.message(passingPlayer, "You pass " + cardString + " to " + receivingPlayer.username);
                // message player who is receiving
                game.message(receivingPlayer, passingPlayer.username + " passes " + cardString + " to you");
                // message other players (without naming the card passed)
                cardString = (toPass != null) ? "a card" : "nothing";
                for (Player uninvolvedPlayer : passOrder) {
                    if (uninvolvedPlayer != passingPlayer && uninvolvedPlayer != receivingPlayer) {
                        game.message(uninvolvedPlayer, passingPlayer.username + " passes " + cardString + " to " + receivingPlayer.username);
                    }
                }
            }
        }
        // you may trash a card from your hand
        if (player.getHand().size() != 0){
            Card toTrash = game.promptChooseTrashFromHand(player, new HashSet<>(player.getHand()),
                    this.toString() + ": You may trash a card from your hand",
                    false, "Trash nothing");
            if (toTrash != null) {
                game.messageAll("trashing " + toTrash.htmlName());
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
            }
        }
    }

	protected void putOnDeckInAnyOrder(Player player, Game game, List<Card> cards, String prompt) {
		List<Card> toPutOnDeck;
		// if there is no order to decide
		if (new HashSet<Card>(cards).size() < 2) {
			// put them on the deck as-is
			toPutOnDeck = cards;
		} else {
			// otherwise, prompt the user for the order
			toPutOnDeck = new ArrayList<Card>();
			Collections.sort(cards, Player.HAND_ORDER_COMPARATOR);
			while (!cards.isEmpty()) {
				String[] choices = new String[cards.size()];
				for (int i = 0; i < cards.size(); i++) {
					choices[i] = cards.get(i).toString();
				}
				int choice = game.promptMultipleChoice(player, prompt + " (the first card you choose will be on top of your deck)", choices);
				toPutOnDeck.add(cards.remove(choice));
			}
		}
		if (!toPutOnDeck.isEmpty()) {
			player.putOnDraw(toPutOnDeck);
		}
	}

	protected void revealHand(Player player, Game game) {
		if (!player.getHand().isEmpty()) {
			game.message(player, "revealing your hand: " + Card.htmlList(player.getHand()));
			game.messageOpponents(player, "revealing their hand: " + Card.htmlList(player.getHand()));
		} else {
			game.messageAll("revealing an empty hand");
		}
	}

	protected void revealUntil(Player player, Game game, Predicate<Card> condition, Consumer<Card> onFound) {
		revealUntil(player, game, condition, onFound, false);
	}

	protected void revealUntil(Player player, Game game, Predicate<Card> condition, Consumer<Card> onFound, boolean isTarget) {
        revealUntil(player, game, condition, 1, list -> onFound.accept(list.get(0)), isTarget);
	}

    protected void revealUntil(Player player, Game game, Predicate<Card> condition, int numToFind, Consumer<List<Card>> onFound) {
        revealUntil(player, game, condition, numToFind, onFound, false);
    }

    protected void revealUntil(Player player, Game game, Predicate<Card> condition, int numToFind, Consumer<List<Card>> onFound, boolean isTarget) {
        List<Card> revealed = new ArrayList<>();
        List<Card> found = new ArrayList<>();
        for (;;) {
            List<Card> drawn = player.takeFromDraw(1);
            if (drawn.isEmpty()) {
                break;
            }
            Card card = drawn.get(0);
            revealed.add(card);
            if (condition.test(card)) {
                found.add(card);
                if (found.size() == numToFind) {
                    break;
                }
            }
        }
        if (!revealed.isEmpty()) {
            if (isTarget) {
                game.message(player, "You draw " + Card.htmlList(revealed));
                game.messageOpponents(player, player.username + " draws " + Card.htmlList(revealed));
                game.messageIndent++;
            } else {
                game.messageAll("drawing " + Card.htmlList(revealed));
            }
            if (!found.isEmpty()) {
                found.forEach(revealed::remove);
                onFound.accept(found);
            }
            if (!revealed.isEmpty()) {
                game.messageAll("discarding the rest");
                player.addToDiscard(revealed);
            }
            if (isTarget) {
                game.messageIndent--;
            }
        } else {
            if (!isTarget) {
                game.message(player, "your deck is empty");
                game.messageOpponents(player, "their deck is empty");
            } else {
                game.message(player, "Your deck is empty");
                game.messageOpponents(player, player.username + "'s deck is empty");
            }
        }
    }

	protected void putRevealedIntoHand(Player player, Game game, Card card) {
		game.message(player, "putting the " + card.htmlNameRaw() + " into your hand");
		game.messageOpponents(player, "putting the " + card.htmlNameRaw() + " into their hand");
		player.addToHand(card);
	}

    protected void putRevealedIntoHand(Player player, Game game, List<Card> cards) {
        game.message(player, "putting " + Card.htmlList(cards) + " into your hand");
        game.messageOpponents(player, "putting " + Card.htmlList(cards) + " into their hand");
        player.addToHand(cards);
    }

	protected void putRevealedOnDeck(Player player, Game game, Card card) {
		game.messageAll("putting the " + card.htmlNameRaw() + " back on top");
		player.putOnDraw(card);
	}

	protected void gainFromTrashSatisfying(Player player, Game game, Predicate<Card> predicate, String promptMessage) {
		gainFromTrashSatisfying(player, game, predicate, promptMessage, false);
	}

	protected void gainFromTrashSatisfying(Player player, Game game, Predicate<Card> predicate, String promptMessage, boolean toDeck) {
		Set<Card> gainable = game.getTrash().stream().filter(predicate).collect(Collectors.toSet());
		if (!gainable.isEmpty()) {
			Card toGain = chooseGainFromTrash(player, game, gainable, promptMessage);
			game.messageAll("gaining " + toGain.htmlName() + " from the trash");
			game.gainFromTrash(player, toGain, toDeck);
		} else {
			game.messageAll("gaining nothing");
		}
	}

	protected Card chooseGainFromTrash(Player player, Game game, Set<Card> gainable, String promptMessage) {
		if (player instanceof Bot) {
			Card toGain = ((Bot) player).chooseGainFromSupply(gainable, true);
			if (!gainable.contains(toGain)) {
				throw new IllegalStateException();
			}
			return toGain;
		}
		List<Card> gainableSorted = new ArrayList<>(gainable);
		Collections.sort(gainableSorted, Player.HAND_ORDER_COMPARATOR);
		String[] choices = new String[gainableSorted.size()];
		for (int i = 0; i < gainableSorted.size(); i++) {
			choices[i] = gainableSorted.get(i).toString();
		}
		int choice = game.promptMultipleChoice(player, promptMessage, choices);
		return gainableSorted.get(choice);
	}

	protected void tryToNameTopCardOfDeck(Player player, Game game) {
		Card namedCard = game.promptNameACard(player, this.toString(), "Name a card. If that is the top card of your deck, it will go into your hand");
		List<Card> drawn = player.takeFromDraw(1);
		if (!drawn.isEmpty()) {
			Card revealedCard = drawn.get(0);
			if (namedCard == revealedCard) {
				game.message(player, "naming " + namedCard.htmlName() + " and reveal " + revealedCard.htmlName() + ", putting it into your hand");
				game.messageOpponents(player, "naming " + namedCard.htmlName() + " and revealing " + revealedCard.htmlName() + ", putting it into their hand");
				player.addToHand(revealedCard);
			} else {
				game.message(player, "naming " + namedCard.htmlName() + " and reveal " + revealedCard.htmlName() + ", putting it back");
				game.messageOpponents(player, "naming " + namedCard.htmlName() + " and revealing " + revealedCard.htmlName() + ", putting it back");
				player.putOnDraw(revealedCard);
			}
		} else {
			game.message(player, "your deck is empty");
			game.messageOpponents(player, "their deck is empty");
		}
	}

	protected void putACardFromYourHandOntoYourDeck(Player player, Game game) {
        if (!player.getHand().isEmpty()) {
            Card toPutOnDeck = game.promptChoosePutOnDeck(player, new HashSet<>(player.getHand()), this.toString() + ": Put a card from your hand onto your deck.");
            game.message(player, "putting " + toPutOnDeck.htmlName() + " from your hand onto your deck");
            game.messageOpponents(player, "putting a card from their hand onto their deck");
            player.putFromHandOntoDraw(toPutOnDeck);
        }
    }

    protected int[] chooseTwoDifferentBenefits(Player player, Game game, String[] benefits) {
        int firstChoice = game.promptMultipleChoice(player, this.toString() + ": Choose two (the choices must be different)", benefits);
        int secondChoice = game.promptMultipleChoice(player, this.toString() + ": Choose two (the choices must be different)", benefits, new int[] {firstChoice});
        return new int[] {firstChoice, secondChoice};
    }

	public String htmlClass() {
		if (isAction && isVictory) {
			return "action-victory";
		} else if (isTreasure && isVictory) {
			return "treasure-victory";
		} else if (isAttackReaction) {
			return "reaction";
		} else if (isDuration) {
			return "duration";
		} else if (isRuins) {
			return "ruins";
		} else if (isAction) {
			return "action";
		} else if (isTreasure) {
			return "treasure";
		} else if (isVictory) {
			return "victory";
		} else {
			return "curse";
		}
	}
	private String indefiniteArticle() {
		char firstLetter = this.toString().toLowerCase().charAt(0);
		return isVowel(firstLetter) ? "an" : "a";
	}
	private static char[] vowels = new char[] {'a', 'e', 'i', 'o', 'u'};
	private static boolean isVowel(char letter) {
		for (int i = 0; i < vowels.length; i++) {
			if (letter == vowels[i]) {
				return true;
			}
		}
		return false;
	}
	public String plural() {
		return this.toString() + "s";
	}
	public String htmlName(int count) {
		return ((count > 1 || count == 0) ? count : indefiniteArticle()) + " <span class=\"" + htmlClass() + "\">" + ((count > 1 || count == 0) ? plural() : toString()) + "</span>";
	}
	public String htmlName() {
		return htmlName(1);
	}
	public String htmlNameRaw() {
		return "<span class=\"" + htmlClass() + "\">" + toString() + "</span>";
	}
	public static String htmlList(List<Card> cards) {
		if (cards.size() == 0) {
			return "0 cards";
		}
		Map<Card, Integer> counts = new HashMap<Card, Integer>();
		for (Card card : cards) {
			if (!counts.containsKey(card)) {
				counts.put(card, 1);
			} else {
				counts.put(card, counts.get(card) + 1);
			}
		}
		List<Card> order = new ArrayList<Card>(counts.keySet());
		Collections.sort(order, Player.HAND_ORDER_COMPARATOR);
		// construct comma separated list
		StringBuilder builder = new StringBuilder();
		Iterator<Card> iter = order.iterator();
		while (iter.hasNext()) {
			Card card = iter.next();
			builder.append(card.htmlName(counts.get(card)));
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}
	public static String htmlSet(Set<Card> cards) {
		if (cards.size() == 0) {
			return "";
		}
		// construct comma separated list
		StringBuilder builder = new StringBuilder();
		Iterator<Card> iter = cards.iterator();
		while (iter.hasNext()) {
			Card card = iter.next();
			builder.append(card.htmlNameRaw());
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}
	public static String numCards(int count) {
		return count + ((count > 1 || count == 0) ? " cards" : " card");
	}

	public String htmlType() {
		if (isAction && isVictory) {
			return "Action-Victory";
		} else if (isTreasure && isVictory) {
			return "Treasure-Victory";
		} else if (isAttackReaction) {
			return "Action-Reaction";
		} else if (isAction) {
			if (isAttack) {
				return "Action-Attack";
			} else if (isDuration) {
				return "Action-Duration";
			} else if (isRuins) {
				return "Action-Ruins";
			} else {
				return "Action";
			}
		} else if (isTreasure) {
			return "Treasure";
		} else if (isVictory) {
			return "Victory";
		} else {
			return "Curse";
		}
	}

	public String[] description() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Card && this.toString().equals(other.toString());
	}

	public enum MixedPileId {
		RUINS("Ruins"),
		KNIGHTS("Knights");

		private String str;

		MixedPileId(String str) {
			this.str = str;
		}

		@Override
		public String toString() {
			return str;
		}

		public static MixedPileId fromString(String str) {
			if ("Ruins".equals(str)) {
				return RUINS;
			} else if ("Knights".equals(str)) {
				return KNIGHTS;
			} else {
				return null;
			}
		}
	}

	public boolean inMixedPile() {
		return mixedPileId() != null;
	}

	public MixedPileId mixedPileId() {
		if (isRuins) {
			return MixedPileId.RUINS;
		} else {
			return null;
		}
	}

}

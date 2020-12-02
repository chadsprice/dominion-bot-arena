package server;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import cards.*;

public abstract class Card {

    protected enum Type {ACTION, TREASURE, VICTORY, ATTACK, ATTACK_REACTION, DURATION, RUINS, SHELTER, LOOTER}

    protected static Set<Type> types(Type ...types) {
        return new HashSet<>(Arrays.asList(types));
    }

    public boolean isAction() {
        return types().contains(Type.ACTION);
    }

    public boolean isTreasure() {
        return types().contains(Type.TREASURE);
    }

    public boolean isVictory() {
        return types().contains(Type.VICTORY);
    }

    public boolean isAttack() {
        return types().contains(Type.ATTACK);
    }

    public boolean isAttackReaction() {
        return types().contains(Type.ATTACK_REACTION);
    }

    public boolean isDuration() {
        return types().contains(Type.DURATION);
    }

    public boolean isRuins() {
        return types().contains(Type.RUINS);
    }

    public boolean isShelter() {
        return types().contains(Type.SHELTER);
    }

    public boolean isLooter() {
        return types().contains(Type.LOOTER);
    }

    public boolean isOverpayable() {
        return false;
    }

    public boolean isBandOfMisfits;

    public abstract String name();
    public abstract Set<Type> types();
    public abstract int cost();
    public abstract String[] description();

    @Override
    public String toString() {
        return name();
    }

    public String plural() {
        return this.toString() + "s";
    }

	public int startingSupply(int numPlayers) {
		if (isVictory()) {
			return numPlayers == 2 ? 8 : 12;
		} else {
			return 10;
		}
	}

	public int cost(Game game) {
		int computedCost = cost();
		computedCost -=  game.cardCostReduction;
		if (isAction()) {
			computedCost -= 2 * game.numberInPlay(Cards.QUARRY);
		}
		computedCost -= 2 * game.numberInPlay(Cards.PRINCESS);
		computedCost -= game.numberInPlay(Cards.HIGHWAY);
		// cost can never be less than zero
		return Math.max(computedCost, 0);
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
		player.actions += numActions;
	}
	protected void plusBuys(Player player, Game game, int numBuys) {
		if (numBuys == 0) {
			return;
		}
		game.messageAll("getting +" + numBuys + ((numBuys == 1) ? " buy" : " buys"));
		player.buys += numBuys;
	}
	protected void plusCoins(Player player, Game game, int numCoins) {
		if (numCoins == 0) {
			return;
		}
		game.messageAll("getting +$" + numCoins);
		player.coins  += numCoins;
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
        List<Card> toDiscard = promptDiscardNumber(player, game, number);
        game.messageAll("discarding " + Card.htmlList(toDiscard));
        player.putFromHandIntoDiscard(toDiscard);
    }

	protected List<Card> discardAnyNumber(Player player, Game game) {
		if (player.getHand().isEmpty()) {
			return Collections.emptyList();
		}
        List<Card> discarded = promptDiscardNumber(player, game, player.getHand().size(), Prompt.Amount.UP_TO);
        game.messageAll("discarding " + Card.htmlList(discarded));
        player.putFromHandIntoDiscard(discarded);
        return discarded;
    }

    protected Card gainCardCostingUpTo(Player player, Game game, int cost) {
        Set<Card> gainable = game.cardsCostingAtMost(cost);
        if (!gainable.isEmpty()) {
            Card toGain = promptChooseGainFromSupply(
                    player,
                    game,
                    gainable,
                    this.toString() + ": Choose a card to gain."
            );
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
                List<Card> discarded = promptDiscardNumber(target, game, numToDiscard);
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
                Set<Card> trashable = drawn.stream()
                        .filter(trashablePredicate)
                        .collect(Collectors.toSet());
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
            checkContains(trashable, toTrash);
            return toTrash;
        }
        return promptMultipleChoiceCard(
                player,
                game,
                Prompt.Type.DANGER,
                this.toString() + ": You draw " + Card.htmlList(new ArrayList<>(trashable)) + ". Choose one to trash.",
                trashable
        );
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
            Card toTrash = promptChooseTrashFromHand(
                    player,
                    game,
                    new HashSet<>(player.getHand()),
                    this.toString() + ": Choose a card to trash."
            );
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
                Card toGain = promptChooseGainFromSupply(
                        player,
                        game,
                        gainable,
                        this.toString() + ": Choose a card to gain."
                );
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
                .filter(Card::isAction)
                .collect(Collectors.toSet());
        if (!actions.isEmpty()) {
            Card toPlay;
            if (isMandatory) {
                toPlay = game.promptChoosePlay(
                        player,
                        actions,
                        this.toString() + ": Choose an action from your hand to play " + (multiplier == 2 ? "twice" : "three times") + "."
                );
            } else {
                toPlay = game.promptChoosePlay(
                        player,
                        actions,
                        this.toString() + ": You may play an action from your hand " + (multiplier == 2 ? "twice" : "three times") + ".",
                        "None"
                );
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
                    if (toPlay.isDuration() && toPlayMoved && !hasMoved && !usedAsModifier) {
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
                .filter(c -> c.isAction() && c != Cards.BAND_OF_MISFITS)
                .collect(Collectors.toSet());
        if (!imitable.isEmpty()) {
            Card toImitate = ((BandOfMisfits) Cards.BAND_OF_MISFITS).chooseImitate(player, game, imitable);
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
                if (imitatorMoved && !imitator.isDuration() && !(imitator instanceof ThroneRoom || imitator instanceof KingsCourt || imitator instanceof Procession) && !madeNewChoiceAfterLeavingPlay) {
                    imitable = game.cardsCostingAtMost(Cards.BAND_OF_MISFITS.cost(game) - 1).stream()
                            .filter(c -> c.isAction() && c != Cards.BAND_OF_MISFITS)
                            .collect(Collectors.toSet());
                    if (!imitable.isEmpty()) {
                        toImitate = ((BandOfMisfits) Cards.BAND_OF_MISFITS).chooseImitate(player, game, imitable);
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
                if (imitatorMoved && imitator.isDuration() && !hasMoved && !usedAsModifier) {
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
                Prompt.Type promptType = (passingPlayer == player) ? Prompt.Type.NORMAL : Prompt.Type.DANGER;
                Card toPass = promptChoosePassToOpponent(
                        passingPlayer,
                        game,
                        new HashSet<>(passingPlayer.getHand()),
                        this.toString() + ": Pass a card from your hand to " + receivingPlayer.username + ".",
                        promptType
                );
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
        if (player.getHand().size() != 0) {
            Card toTrash = promptChooseTrashFromHand(
                    player,
                    game,
                    new HashSet<>(player.getHand()),
                    this.toString() + ": You may trash a card from your hand",
                    "Trash nothing"
            );
            if (toTrash != null) {
                game.messageAll("trashing " + toTrash.htmlName());
                player.removeFromHand(toTrash);
                game.trash(player, toTrash);
            }
        }
    }

    protected void putOnDeckInAnyOrder(Player player, Game game, List<Card> cards, String prompt) {
        putOnDeckInAnyOrder(player, game, cards, prompt, Prompt.Type.NORMAL);
    }
	protected void putOnDeckInAnyOrder(Player player, Game game, List<Card> cards, String prompt, Prompt.Type promptType) {
		List<Card> toPutOnDeck;
		// if there is no order to decide
		if (new HashSet<>(cards).size() < 2) {
			// put them on the deck as-is
			toPutOnDeck = cards;
		} else {
			// otherwise, prompt the user for the order
			toPutOnDeck = new ArrayList<>();
			while (!cards.isEmpty()) {
			    Card next = promptMultipleChoiceCard(
			            player,
                        game,
                        promptType,
                        prompt + " (the first card you choose will be on top of your deck)",
                        cards
                );
			    cards.remove(next);
			    toPutOnDeck.add(next);
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
		Set<Card> gainable = game.getTrash().stream()
                .filter(predicate)
                .collect(Collectors.toSet());
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
			checkContains(gainable, toGain);
			return toGain;
		}
		// TODO: prompt trash CardZoneDisplay
        return promptMultipleChoiceCard(
                player,
                game,
                Prompt.Type.NORMAL,
                promptMessage,
                gainable
        );
	}

	protected void tryToNameTopCardOfDeck(Player player, Game game) {
	    if (!(player.getDraw().isEmpty() && player.getDiscard().isEmpty())) {
            Card namedCard = promptNameACard(
                    player,
                    game,
                    this.toString(),
                    "Name a card. If that is the top card of your deck, it will go into your hand"
            );
            Card revealedCard = player.takeFromDraw(1).get(0);
            if (namedCard.equals(revealedCard)) {
                game.message(player, "naming " + namedCard.htmlName() + " and revealing " + revealedCard.htmlName() + ", putting it into your hand");
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
            Card toPutOnDeck = promptChoosePutOnDeck(
                    player,
                    game,
                    new HashSet<>(player.getHand()),
                    this.toString() + ": Put a card from your hand onto your deck."
            );
            game.message(player, "putting " + toPutOnDeck.htmlName() + " from your hand onto your deck");
            game.messageOpponents(player, "putting a card from their hand onto their deck");
            player.putFromHandOntoDraw(toPutOnDeck);
        }
    }

    protected int[] chooseTwoDifferentBenefits(Player player, Game game, String[] benefits) {
	    int firstChoice = new Prompt(player, game)
                .message(this.toString() + ": Choose two (the choices must be different)")
                .multipleChoices(benefits)
                .responseMultipleChoiceIndex();
	    int secondChoice = new Prompt(player, game)
                .message(this.toString() + ": Choose two (the choices must be different)")
                .multipleChoices(benefits, Collections.singleton(firstChoice))
                .responseMultipleChoiceIndex();
        return new int[] {firstChoice, secondChoice};
    }

	public String htmlHighlightType() {
		if (isAction() && isVictory()) {
			return "action-victory";
		} else if (isTreasure() && isVictory()) {
			return "treasure-victory";
		} else if (isAttackReaction()) {
			return "reaction";
		} else if (isDuration()) {
			return "duration";
		} else if (isRuins()) {
			return "ruins";
		} else if (isAction()) {
			return "action";
		} else if (isTreasure()) {
			return "treasure";
		} else if (isVictory()) {
			return "victory";
		} else {
			return "curse";
		}
	}
	private String indefiniteArticle() {
		char firstLetter = this.toString().toLowerCase().charAt(0);
		return isVowel(firstLetter) ? "an" : "a";
	}
	private static boolean isVowel(char letter) {
        return Arrays.stream(new Character[] {'a', 'e', 'i', 'o', 'u'})
                .anyMatch(v -> v == letter);
	}
    public String htmlName() {
        return htmlName(1);
    }
	public String htmlName(int count) {
		return ((count > 1 || count == 0) ? count : indefiniteArticle()) + " <span class=\"" + htmlHighlightType() + "\">" + ((count > 1 || count == 0) ? plural() : toString()) + "</span>";
	}
	public String htmlNameRaw() {
		return "<span class=\"" + htmlHighlightType() + "\">" + toString() + "</span>";
	}
	public String htmlNameRawPlural() {
		return "<span class=\"" + htmlHighlightType() + "\">" + plural() + "</span>";
	}
	public static String htmlList(Collection<Card> cards) {
		if (cards.size() == 0) {
			return "0 cards";
		}
		Map<Card, Integer> counts = new HashMap<>();
		for (Card card : cards) {
			if (!counts.containsKey(card)) {
				counts.put(card, 1);
			} else {
				counts.put(card, counts.get(card) + 1);
			}
		}
		List<Card> order = new ArrayList<>(counts.keySet());
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
	static String htmlSet(Set<Card> cards) {
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
		return count + " card" + ((count != 1) ? "s" : "");
	}

	public String htmlType() {
		if (isAction() && isVictory()) {
			return "Action-Victory";
		} else if (isTreasure() && isVictory()) {
			return "Treasure-Victory";
		} else if (isAttackReaction()) {
			return "Action-Reaction";
		} else if (isAction()) {
			if (isAttack()) {
				return "Action-Attack";
			} else if (isDuration()) {
				return "Action-Duration";
			} else if (isRuins()) {
				return "Action-Ruins";
			} else {
				return "Action";
			}
		} else if (isTreasure()) {
			return "Treasure";
		} else if (isVictory()) {
			return "Victory";
		} else {
			return "Curse";
		}
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof Card
                && this.toString().equals(other.toString())
                // don't confuse a Band of Misfits with the card it's imitating
                && this.isBandOfMisfits == ((Card) other).isBandOfMisfits;
	}

	@Override
    public int hashCode() {
	    return Objects.hash(toString(), isBandOfMisfits);
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

	boolean inMixedPile() {
		return mixedPileId() != null;
	}

	public MixedPileId mixedPileId() {
		if (isRuins()) {
			return MixedPileId.RUINS;
		} else {
			return null;
		}
	}

    protected Card promptChooseGainFromSupply(Player player, Game game, Set<Card> choices, String message) {
        return promptChooseGainFromSupply(player, game, choices, message, null);
    }

    protected Card promptChooseGainFromSupply(Player player, Game game, Set<Card> choices, String message, String noneText) {
        return promptChooseGainFromSupply(player, game, choices, Prompt.Type.NORMAL, message, noneText);
    }

    protected Card promptChooseGainFromSupply(Player player, Game game, Set<Card> choices, Prompt.Type type, String message, String noneText) {
        boolean isMandatory = (noneText == null);
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            Card card = bot.chooseGainFromSupply(Collections.unmodifiableSet(choices), isMandatory);
            checkContains(choices, card, isMandatory);
            return card;
        }
        Prompt prompt = new Prompt(player, game)
                .type(type)
                .message(message)
                .supplyChoices(choices);
        if (!isMandatory) {
            prompt.orNone(noneText);
        }
        return prompt.responseCard();
    }

    protected Card promptChooseOpponentGainFromSupply(Player player, Game game, Set<Card> choices, String message) {
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            Card card = bot.chooseOpponentGainFromSupply(Collections.unmodifiableSet(choices));
            checkContains(choices, card);
            return card;
        }
        return new Prompt(player, game)
                .message(message)
                .supplyChoices(choices)
                .responseCard();
    }

    protected Card promptChoosePlay(Player player, Game game, Set<Card> choices, String message) {
        return promptChoosePlay(player, game, choices, message, null);
    }

    protected Card promptChoosePlay(Player player, Game game, Set<Card> choices, String message, String noneText) {
        boolean isMandatory = (noneText == null);
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            Card card = bot.choosePlay(Collections.unmodifiableSet(choices), isMandatory);
            checkContains(choices, card, isMandatory);
            return card;
        }
        Prompt prompt = new Prompt(player, game)
                .message(message)
                .handChoices(choices);
        if (!isMandatory) {
            prompt.orNone(noneText);
        }
        return prompt.responseCard();
    }

    protected Card promptChooseTrashFromHand(Player player, Game game, Set<Card> choices, String message) {
        return promptChooseTrashFromHand(player, game, choices, message, null);
    }

    protected Card promptChooseTrashFromHand(Player player, Game game, Set<Card> choices, String message, String noneText) {
        boolean isMandatory = (noneText == null);
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            Card card = bot.chooseTrashFromHand(Collections.unmodifiableSet(choices), isMandatory);
            checkContains(choices, card, isMandatory);
            return card;
        }
        Prompt prompt = new Prompt(player, game)
                .type(Prompt.Type.DANGER)
                .message(message)
                .handChoices(choices);
        if (!isMandatory) {
            prompt.orNone(noneText);
        }
        return prompt.responseCard();
    }

    /*
    protected Card promptChooseIslandFromHand(Player player, Game game, Set<Card> choices, String message) {
      if (player instanceof Bot) {
        Bot bot = (Bot) player;
        Card card = bot.chooseIslandFromHand(Collections.unmodifiableSet(choices));
        checkContains(choices, card);
        return card;
      }
      return new Prompt(player, game)
        .message(message)
        .handChoices(choices)
        .responseCard();
    }
    */

    protected Card promptChoosePutOnDeck(Player player, Game game, Set<Card> choices, String message) {
        return promptChoosePutOnDeck(player, game, choices, message, Prompt.Type.NORMAL);
    }

    protected Card promptChoosePutOnDeck(Player player, Game game, Set<Card> choices, String message, Prompt.Type type) {
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            Card card = bot.choosePutOnDeck(Collections.unmodifiableSet(choices));
            checkContains(choices, card);
            return card;
        }
        return new Prompt(player, game)
                .type(type)
                .message(message)
                .handChoices(choices)
                .responseCard();
    }

    protected Card promptChoosePassToOpponent(Player player, Game game, Set<Card> choices, String message, Prompt.Type type) {
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            Card card = bot.choosePassToOpponent(Collections.unmodifiableSet(choices));
            checkContains(choices, card);
            return card;
        }
        return new Prompt(player, game)
                .type(type)
                .message(message)
                .handChoices(choices)
                .responseCard();
    }

    protected Card promptChooseRevealAttackReaction(Player player, Game game, Set<Card> choices) {
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            Card card = bot.chooseRevealAttackReaction(Collections.unmodifiableSet(choices));
            checkContains(choices, card, false);
            return card;
        }
        return new Prompt(player, game)
                .type(Prompt.Type.REACTION)
                .message("Choose a reaction")
                .handChoices(choices)
                .orNone("No Reaction")
                .responseCard();
    }

    protected Card promptChooseGainCopyOfCardInHand(Player player, Game game, Set<Card> choices, String message, boolean isMandatory) {
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            Card card = bot.chooseGainFromSupply(Collections.unmodifiableSet(choices), isMandatory);
            checkContains(choices, card, isMandatory);
            return card;
        }
        Prompt prompt = new Prompt(player, game)
                .message(message)
                .handChoices(choices);
        if (!isMandatory) {
            prompt.orNone("None");
        }
        return prompt.responseCard();
    }

    protected List<Card> promptDiscardNumber(Player player, Game game, int number) {
        return promptDiscardNumber(player, game, number, Prompt.Amount.EXACT);
    }

    protected List<Card> promptDiscardNumber(Player player, Game game, int number, Prompt.Amount amount) {
        return promptChooseNumberInHand(player, game, number, Prompt.Destination.DISCARD, amount);
    }

    protected List<Card> promptTrashNumber(Player player, Game game, int number) {
        return promptTrashNumber(player, game, number, Prompt.Amount.EXACT);
    }

    protected List<Card> promptTrashNumber(Player player, Game game, int number, Prompt.Amount amount) {
        return promptChooseNumberInHand(player, game, number, Prompt.Destination.TRASH, amount);
    }

    protected List<Card> promptPutNumberOnDeck(Player player, Game game, int number) {
        return promptPutNumberOnDeck(player, game, number, Prompt.Amount.EXACT);
    }

    protected List<Card> promptPutNumberOnDeck(Player player, Game game, int number, Prompt.Amount amount) {
        return promptChooseNumberInHand(player, game, number, Prompt.Destination.DECK, amount);
    }

    protected List<Card> promptChooseNumberInHand(Player player, Game game, int number, Prompt.Destination destination, Prompt.Amount amount) {
        if (player.getHand().size() < number) {
            number = player.getHand().size();
        }
        if (player instanceof Bot) {
            Bot bot = (Bot) player;
            List<Card> chosen;
            if (destination == Prompt.Destination.DISCARD) {
                chosen = bot.discardNumber(number, amount);
            } else if (destination == Prompt.Destination.TRASH) {
                chosen = bot.trashNumber(number, amount);
            } else { // Prompt.Type.DECK
                chosen = bot.putNumberOnDeck(number);
            }
            checkDiscard(player.hand, chosen, number, amount);
            return chosen;
        }
        return new Prompt(player, game)
                .type(Prompt.Type.DANGER)
                .numberFromHand(number, destination, amount)
                .responseCards();
    }

    protected Card promptNameACard(Player player, Game game, String cause, String message) {
        Card namedCard = new Prompt(player, game)
                .message(cause + ": " + message)
                .supplyChoices(game.cardsChoosableInSupplyUI())
                .orNone("Name a card that is not in the supply")
                .responseCard();
        if (namedCard == null) {
            // find all cards not in the supply
            Set<Card> cardsNotInSupply = new HashSet<>(Cards.cardsByName.values());
            cardsNotInSupply.removeAll(game.cardsChoosableInSupplyUI());
            // create an alphabet of only the first letters of cards not in the supply
            Set<Character> letters = cardsNotInSupply.stream()
                    .map(c -> c.toString().charAt(0))
                    .collect(Collectors.toSet());
            List<Character> orderedLetters = new ArrayList<>(letters);
            Collections.sort(orderedLetters);
            String[] choices = new String[orderedLetters.size()];
            for (int i = 0; i < orderedLetters.size(); i++) {
                choices[i] = orderedLetters.get(i) + "";
            }
            int chosenLetterIndex = new Prompt(player, game)
                    .message(cause + ": Select the first letter of the card you want to name")
                    .multipleChoices(choices)
                    .responseMultipleChoiceIndex();
            char chosenLetter = orderedLetters.get(chosenLetterIndex);
            // find all cards not in the supply starting with the chosen letter
            List<String> names = cardsNotInSupply.stream()
                    .map(Card::toString)
                    .filter(name -> name.charAt(0) == chosenLetter)
                    .collect(Collectors.toList());
            Collections.sort(names);
            choices = new String[names.size()];
            choices = names.toArray(choices);
            int chosenNameIndex = new Prompt(player, game)
                    .message(cause + ": Select the card you want to name")
                    .multipleChoices(choices)
                    .responseMultipleChoiceIndex();
            String chosenName = choices[chosenNameIndex];
            namedCard = Cards.fromName(chosenName);
        }
        return namedCard;
    }

    protected Card promptMultipleChoiceCard(Player player, Game game, Prompt.Type type, String message, Collection<Card> cards) {
        return promptMultipleChoiceCard(player, game, type, message, cards, null);
    }

    protected Card promptMultipleChoiceCard(Player player, Game game, Prompt.Type type, String message, Collection<Card> cards, String noneText) {
        boolean isMandatory = (noneText == null);
        List<Card> cardsSorted = new ArrayList<>(cards);
        cardsSorted.sort(Player.HAND_ORDER_COMPARATOR);
        String[] choices = new String[isMandatory ? cards.size() : (cards.size() + 1)];
        for (int i = 0; i < cardsSorted.size(); i++) {
            choices[i] = cardsSorted.get(i).toString();
        }
        if (!isMandatory) {
            choices[choices.length - 1] = noneText;
        }
        int choiceIndex = new Prompt(player, game)
                .type(type)
                .message(message)
                .multipleChoices(choices)
                .responseMultipleChoiceIndex();
        if (choiceIndex < cardsSorted.size()) {
            return cardsSorted.get(choiceIndex);
        } else {
            return null;
        }
    }

    protected Object promptChoosePile(Player player, Game game, Prompt.Type type, String message, String noneText) {
        boolean isMandatory = (noneText == null);
        Prompt prompt = new Prompt(player, game)
                .type(type)
                .message(message);
		Set<Card> uniformPiles = game.supply.keySet();
		if (!uniformPiles.isEmpty()) {
			prompt.supplyChoices(uniformPiles);
		}
		Set<Card.MixedPileId> mixedPiles = game.mixedPiles.keySet();
		if (!mixedPiles.isEmpty()) {
			prompt.mixedPileChoices(mixedPiles);
		}
        if (!isMandatory) {
            prompt.orNone(noneText);
        }
        PromptResponse response = prompt.response();
		if (response.supplyChoice != null) {
			return response.supplyChoice;
		} else { // response.mixedPileChoice != null
			return response.mixedPileChoice;
		}
    }

    public static <T> void checkContains(Collection<T> collection, T elem) {
        checkContains(collection, elem, true);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static <T> void checkContains(Collection<T> collection, T elem, boolean isMandatory) {
        if (collection.contains(elem)) {
            // valid choice
        } else if (!isMandatory && elem == null) {
            // allowed if optional
        } else {
            throw new IllegalStateException();
        }
    }

    public static <T> void checkDiscard(List<T> hand, List<T> toDiscard, int numToDiscard, Prompt.Amount amount) {
        if (!checkContains(hand, toDiscard)) {
            throw new IllegalStateException();
        }
        if (toDiscard.size() == numToDiscard) {
            // correct amount
        } else if (amount == Prompt.Amount.UP_TO && toDiscard.size() < numToDiscard) {
            // allowed to be less
        } else if (amount == Prompt.Amount.EXACT_OR_NONE && toDiscard.size() == 0) {
            // allowed to be none
        } else {
            throw new IllegalStateException();
        }
    }

    public static <T> boolean checkContains(List<T> container, List<T> contained) {
        List<T> containerCopy = new ArrayList<>(container);
        return contained.stream()
                .allMatch(containerCopy::remove);
    }

    public static void checkMultipleChoice(int numChoices, int choiceIndex) {
        if (choiceIndex < 0 || choiceIndex > numChoices-1) {
            throw new IllegalStateException();
        }
    }

}

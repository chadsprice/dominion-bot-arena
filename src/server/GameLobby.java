package server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class GameLobby implements Comparable<GameLobby> {

	public String name;
	// the number of openings to fill before the game can begin
	int numOpenings;
	Set<Set<Card>> sets;
	Set<Card> requiredCards;
	Set<Card> forbiddenCards;
	// the openings for players to join, null if open
	Player[] players;
	// the readiness of players to start the game
	boolean[] isPlayerReady;

	GameLobby(String name, int numOpenings, Set<Set<Card>> sets, Set<Card> requiredCards, Set<Card> forbiddenCards, List<Player> bots) {
		this.name = name;
		this.numOpenings = numOpenings;
		this.sets = sets;
		this.requiredCards = requiredCards;
		this.forbiddenCards = forbiddenCards;
		players = new Player[numOpenings];
		isPlayerReady = new boolean[numOpenings];
		for (int i = 0; i < bots.size(); i++) {
			players[i] = bots.get(i);
			isPlayerReady[i] = true;
		}
	}

	/**
	 * Creates a new lobby for players who are being automatched.
	 * @param numOpenings the number of openings in the automatched game
	 * @return a new lobby ready for automatch players to be added
	 */
	static GameLobby automatchLobby(int numOpenings) {
		Set<Set<Card>> sets = new HashSet<>();
		sets.add(Card.BASE_SET);
		sets.add(Card.INTRIGUE_SET);
		sets.add(Card.SEASIDE_SET);
		return new GameLobby("Automatch", numOpenings, sets, new HashSet<>(), new HashSet<>(), new ArrayList<>());
	}

	void addPlayer(Player player) {
		for (int i = 0; i < numOpenings; i++) {
			if (players[i] == null) {
				players[i] = player;
				isPlayerReady[i] = false;
				return;
			}
		}
	}

	void removePlayer(Player player) {
		for (int i = 0; i < numOpenings; i++) {
			if (players[i] == player) {
				players[i] = null;
				isPlayerReady[i] = false;
				return;
			}
		}
	}

	public int numPlayers() {
		int numPlayers = 0;
		for (int i = 0; i < numOpenings; i++) {
			if (players[i] != null) {
				numPlayers++;
			}
		}
		return numPlayers;
	}

	boolean isFull() {
		for (int i = 0; i < numOpenings; i++) {
			if (players[i] == null) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		for (int i = 0; i < numOpenings; i++) {
			if (players[i] != null && !(players[i] instanceof Bot)) {
				return false;
			}
		}
		return true;
	}

	void setIsPlayerReady(Player player, boolean isReady) {
		for (int i = 0; i < numOpenings; i++) {
			if (players[i] == player) {
				isPlayerReady[i] = isReady;
				return;
			}
		}
	}

	boolean isReady() {
		for (int i = 0; i < numOpenings; i++) {
			if (!isPlayerReady[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int compareTo(GameLobby other) {
		// order game lobbies alphabetically
		return other.name.compareTo(this.name);
	}

}

package server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mindrot.BCrypt;

public class GameServer {

	private static final int DEFAULT_HTTP_PORT = 8080;
	// in milliseconds (minutes * seconds/minute * milliseconds/second)
	private static final int DEFAULT_WEBSOCKET_TIMEOUT = 10 * 60 * 1000;

	private static final String LOGINS_FILE_NAME = "logins";
	private static final Path LOGINS_FILE_PATH = Paths.get(LOGINS_FILE_NAME);
	private static final Charset LOGINS_FILE_CHARSET = Charset.forName("UTF-8");

	private static final Path CONFIG_FILE_PATH = Paths.get("config");

	static GameServer INSTANCE;

	private int httpPort;
	private int websocketTimeout;

	private Map<PlayerWebSocketHandler, Player> players = new HashMap<>();
	private Map<String, Player> loggedInPlayers = new HashMap<>();
	private Set<Player> playersInLobby = new HashSet<>();
	private Map<String, GameLobby> gameLobbies = new HashMap<>();
	private Map<Player, GameLobby> playersInGameLobbies = new HashMap<>();

	private Set<Player>
			automatch2 = new HashSet<>(),
			automatch3 = new HashSet<>(),
			automatch4 = new HashSet<>();

	private Map<String, String> validLogins = new HashMap<>();

	private int anonymousLoginCounter = 1;
	private static Pattern anonymousNamePattern = Pattern.compile("^Anonymous\\d+$");

	private GameServer() {}

	private void run() {
		loadLogins();
		loadServerConfiguration();
		startServer();
	}

	private void loadServerConfiguration() {
		// set default configuration
		httpPort = DEFAULT_HTTP_PORT;
		websocketTimeout = DEFAULT_WEBSOCKET_TIMEOUT;
		// read configuration file
		try {
			Scanner scanner = new Scanner(CONFIG_FILE_PATH);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}
				String[] tokens = line.split("\\s+");
				if (tokens.length != 2) {
					configurationError(line);
					continue;
				}
				String setting = tokens[0];
				String value = tokens[1];
				if ("port".equals(setting)) {
					try {
						httpPort = Integer.parseInt(value);
					} catch (NumberFormatException e) {
						configurationError(line);
					}
				} else if ("websocketTimeout".equals(setting)) {
					try {
						websocketTimeout = Integer.parseInt(value);
					} catch (NumberFormatException e) {
						configurationError(line);
					}
				} else {
					// unrecognized setting
					configurationError(line);
				}
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void configurationError(String line) {
		System.err.println("Error reading server configuration: \"" + line + "\"");
	}

	private void startServer() {
		Server server = new Server(httpPort);

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] {"main.html"});
		resourceHandler.setResourceBase("html");

		GzipHandler gzip = new GzipHandler();
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {resourceHandler, new DefaultHandler()});
		gzip.setHandler(handlers);

		WebSocketHandler webSocketHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.getPolicy().setIdleTimeout(websocketTimeout);
				factory.register(PlayerWebSocketHandler.class);
			}
		};

		HandlerCollection handlerCollection = new HandlerCollection();
		handlerCollection.setHandlers(new Handler[] {webSocketHandler, gzip});
		server.setHandler(handlerCollection);

		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	synchronized void addConnection(PlayerWebSocketHandler conn) {
		Player player = new Player(conn);
		players.put(conn, player);
		anonymousLogin(player);
		sendToLobby(player);
	}

	synchronized void removeConnection(PlayerWebSocketHandler conn) {
		if (!players.containsKey(conn)) {
			return;
		}
		Player player = players.get(conn);
		// remove from active players
		players.remove(conn);
		// if logged in, log out
		if (player.username != null) {
			loggedInPlayers.remove(player.username);
		}
		// remove player from the general lobby
		removeFromLobby(player);
		// if in game lobby, remove from game lobby
		if (playersInGameLobbies.containsKey(player)) {
			removeFromGameLobby(player);
		}
		// if in game, forfeit
		if (player.game != null) {
			player.game.forfeit(player, true);
		}
	}

	void receiveMessage(PlayerWebSocketHandler conn, String message) {
		try {
			// allow JSON to be parsed in parallel
			JSONObject request = (JSONObject) JSONValue.parse(message);
			handleRequest(conn, request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized void handleRequest(PlayerWebSocketHandler conn, JSONObject request) {
		// make sure that the connection is still associated with an active player
		if (players.containsKey(conn)) {
			handleRequest(players.get(conn), request);
		}
	}

	private void handleRequest(Player player, JSONObject request) {
		String type = (String) request.get("type");
		if ("response".equals(type)) {
			handleResponse(player, request);
		} else if ("login".equals(type)) {
			handleLogin(player, request);
		} else if ("quickGame".equals(type)) {
			handleQuickGame(player, request);
		} else if ("createCustomGame".equals(type)) {
			handleCreateCustomGame(player, request);
		} else if ("joinGame".equals(type)) {
			handleJoinGame(player, request);
		} else if ("gameLobbyLeave".equals(type)) {
			handleGameLobbyLeave(player);
		} else if ("gameLobbyReady".equals(type)) {
			handleGameLobbyReady(player, request);
		} else if ("returnToLobby".equals(type)) {
			handleReturnToLobby(player);
		} else if ("hurryUp".equals(type)) {
			handleHurryUp(player);
		} else if ("forfeit".equals(type)) {
			forfeit(player);
		} else if ("automatch".equals(type)) {
			handleAutomatch(player, request);
		} else if ("chat".equals(type)) {
			handleChat(player, request);
		}
	}

	private void handleResponse(Player player, JSONObject request) {
		Object response = request.get("response");
		if (response != null) {
			player.receiveResponse(response);
		}
	}

	private void anonymousLogin(Player player) {
		String username = "Anonymous" + (anonymousLoginCounter++);
		while (loggedInPlayers.containsKey(username)) {
			username = "Anonymous" + (anonymousLoginCounter++);
		}
		player.username = username;
		loggedInPlayers.put(username, player);
	}

	private void handleLogin(Player player, JSONObject request) {
		// player must be in the lobby to change logins
		if (!playersInLobby.contains(player)) {
			return;
		}
		String username = (String) request.get("username");
		String password = (String) request.get("password");
		Boolean newLogin = (Boolean) request.get("newLogin");
		// sanitize username
		username = cleanName(username);
		// username cannot be empty
		if (username.isEmpty()) {
			loginError(player, "Username is empty.");
			return;
		}
		// check that this username is not already logged in
		if (loggedInPlayers.containsKey(username)) {
			loginError(player, "Duplicate login.");
			return;
		}
		// if the user is creating a new account
		if (newLogin != null) {
			// check that the username is not already taken
			if (validLogins.containsKey(username)) {
				loginError(player, "Username already taken.");
				return;
			} else if (anonymousNamePattern.matcher(username).find()) {
				// the username also can be "Anonymous#", since those are reserved
				loginError(player, "Must provide unique username.");
				return;
			}
			// save the new account
			String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
			validLogins.put(username, hashedPassword);
			saveLogin(username, hashedPassword);
		} else {
			// if this username is password-protected
			if (validLogins.containsKey(username)) {
				// if no password was given, error
				if (password == null) {
					loginError(player, "Username or password is incorrect.");
					return;
				}
				String hashed = validLogins.get(username);
				// if the password is wrong, error
				if (!BCrypt.checkpw(password, hashed)) {
					loginError(player, "Username or password is incorrect.");
					return;
				}
			} else if (password != null) {
				// the username is unrecognized, but the user entered a password (probably a typo entering their username)
				loginError(player, "Given username has no associated password.");
				return;
			}
			// else, okay non-password-protected login
		}
		// set new username
		loggedInPlayers.remove(player.username);
		player.username = username;
		loggedInPlayers.put(username, player);
		loginAccepted(player);
	}

	private static String cleanName(String name) {
		// trim whitespace
		name = name.trim();
		// maximum length of 25 characters
		if (name.length() > 25) {
			name = name.substring(0, 25);
		}
		// escape html
		name = org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(name);
		// trim whitespace again
		name = name.trim();
		// remove any return character
		name = name.replace("\n", "");
		return name;
	}

	@SuppressWarnings("unchecked")
	private void loginError(Player player, String errorMessage) {
		JSONObject command = new JSONObject();
		command.put("command", "loginError");
		command.put("message", errorMessage);
		player.issueCommand(command);
	}

	@SuppressWarnings("unchecked")
	private void loginAccepted(Player player) {
		JSONObject command = new JSONObject();
		command.put("command", "loginAccepted");
		command.put("username", player.username);
		player.issueCommand(command);
	}

	@SuppressWarnings("unchecked")
	private void handleQuickGame(Player player, JSONObject request) {
		// if player isn't in the lobby, ignore the request
		if (!playersInLobby.contains(player)) {
			return;
		}
		// remove the player from the lobby
		removeFromLobby(player);
		// parse the requested bot
		Bot bot = Bot.newBotFromName((String) request.get("bot"));
		// create new game
		// use all of the known card sets
		Set<Set<Card>> cardSets = new HashSet<>(Card.setsByName.values());
		Set<Player> players = new HashSet<>(Arrays.asList(player, bot));
		startGame(players, cardSets, Collections.emptySet(), Collections.emptySet());
	}

	private void startGame(Set<Player> players, Set<Set<Card>> cardSets, Set<Card> requiredCards, Set<Card> forbiddenCards) {
		Game game = new Game(this, players, cardSets, requiredCards, forbiddenCards);
		Thread gameThread = new Thread(game);
		gameThread.start();
	}

	private void handleCreateCustomGame(Player player, JSONObject request) {
		// if player isn't in the lobby, ignore the request
		if (!playersInLobby.contains(player)) {
			return;
		}
		// custom game name
		String name = (String) request.get("name");
		name = cleanName(name);
		if (name.equals("")) {
			name = player.username + "'s Game";
			int counter = 2;
			while (gameLobbies.containsKey(name)) {
				name = player.username + "'s Game " + counter++;
			}
		}
		if (gameLobbies.containsKey(name)) {
			customGameError(player, "That name is already taken.");
			return;
		}
		// num players
		int numPlayers = Integer.parseInt((String) request.get("numPlayers"));
		if (numPlayers < 2 || numPlayers > 4) {
			// silent error, client should only request games of size 2-4
			return;
		}
		// sets
		JSONArray setArray = (JSONArray) request.get("sets");
		Set<Set<Card>> cardSets = new HashSet<>();
		for (Object setNameObject : setArray) {
			String setName = (String) setNameObject;
			Set<Card> cardSet = Card.setsByName.get(setName);
			if (cardSet != null) {
				cardSets.add(cardSet);
			}
		}
		// cards
		String cards = (String) request.get("cards");
		Set<Card> requiredCards = new HashSet<>();
		Set<Card> forbiddenCards = new HashSet<>();
		if (!cards.isEmpty()) {
			String[] cardArray = cards.split(",");
			for (String cardName : cardArray) {
				cardName = cardName.trim();
				boolean isForbidden = false;
				if (cardName.startsWith("!")) {
					isForbidden = true;
					cardName = cardName.substring(1).trim();
				}
				// simplify name to ignore capitalization and punctuation
				Card card = Card.fromName(Card.simplifiedName(cardName));
				// if the card exists in a kingdom card set
				if (card != null && Card.setsByName.values().stream().anyMatch(set -> set.contains(card))) {
					if (isForbidden) {
						forbiddenCards.add(card);
					} else {
						requiredCards.add(card);
					}
				} else {
					customGameError(player, org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(cardName) + " is not a kingdom card.");
					return;
				}
			}
		}
		// bots
		List<String> botNames = new ArrayList<>();
		JSONArray botNameArray = (JSONArray) request.get("bots");
		for (Object botName : botNameArray) {
			botNames.add((String) botName);
		}
		if (botNames.size() >= numPlayers) {
			customGameError(player, "Increase the number of players to make room for yourself!");
			return;
		}
		List<Player> bots = botNames.stream()
				.map(Bot::newBotFromName)
				.collect(Collectors.toList());
		// create the lobby
		GameLobby lobby = new GameLobby(name, numPlayers, cardSets, requiredCards, forbiddenCards, bots);
		gameLobbies.put(name, lobby);
		// send the player to the game lobby they created
		sendToGameLobby(player, lobby);
		// announce new game to all waiting in the lobby
		if (!lobby.isFull()) {
			announceGameListing(lobby);
		}
	}

	@SuppressWarnings("unchecked")
	private void customGameError(Player player, String message) {
		JSONObject command = new JSONObject();
		command.put("command", "customGameError");
		command.put("message", message);
		player.issueCommand(command);
	}

	private void handleJoinGame(Player player, JSONObject request) {
		// get the game lobby by name
		String name = (String) request.get("name");
		GameLobby lobby;
		if (!gameLobbies.containsKey(name)) {
			// no such game game lobby currently available
			return;
		}
		lobby = gameLobbies.get(name);
		// send the player to the game lobby
		sendToGameLobby(player, lobby);
	}

	private void handleGameLobbyLeave(Player player) {
		removeFromGameLobby(player);
	}

	private void handleGameLobbyReady(Player player, JSONObject request) {
		boolean isReady = (boolean) request.get("isReady");
		setPlayerReadiness(player, isReady);
	}

	private void handleReturnToLobby(Player player) {
		// check that the player is leaving a game that has ended normally
		if (player.game != null && player.game.isGameOver) {
			player.game = null;
			sendToLobby(player);
		}
	}

	private void handleHurryUp(Player player) {
		if (player.game != null) {
			player.game.hurryUp(player);
		}
	}

	private void handleAutomatch(Player player, JSONObject request) {
		removeFromAutomatch(player);
		boolean isOn = (boolean) request.get("isOn");
		if (isOn) {
			boolean join2 = (boolean) request.get("2");
			boolean join3 = (boolean) request.get("3");
			boolean join4 = (boolean) request.get("4");
			if (join2) {
				automatch2.add(player);
			}
			if (join3) {
				automatch3.add(player);
			}
			if (join4) {
				automatch4.add(player);
			}
			tryAutomatch();
		}
	}

	private void tryAutomatch() {
		@SuppressWarnings("unchecked")
		Set<Player>[] pools = new Set[] {automatch2, automatch3, automatch4};
		for (int i = 0; i < pools.length; i++) {
			Set<Player> pool = pools[i];
			int size = i + 2;
			if (pool.size() == size) {
				GameLobby lobby = GameLobby.automatchLobby(size);
				List<Player> copy = new ArrayList<>(pool);
				for (Player player : copy) {
					sendToGameLobby(player, lobby);
				}
				pool.clear();
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void handleChat(Player player, JSONObject request) {
		if (player.game == null) {
			return;
		}
		String chatMessage = (String) request.get("message");
		if (chatMessage.isEmpty()) {
			return;
		}
		chatMessage = org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(chatMessage);
		JSONObject command = new JSONObject();
		command.put("command", "chat");
		command.put("username", player.username);
		command.put("message", chatMessage);
		for (Player playerInGame : player.game.players) {
			playerInGame.issueCommand(command);
		}
	}

	private void removeFromLobby(Player player) {
		playersInLobby.remove(player);
		removeFromAutomatch(player);
	}

	private void removeFromAutomatch(Player player) {
		automatch2.remove(player);
		automatch3.remove(player);
		automatch4.remove(player);
	}

	@SuppressWarnings("unchecked")
	private void setPlayerReadiness(Player player, boolean isReady) {
		// if the player is not in a game lobby, ignore the request
		if (!playersInGameLobbies.containsKey(player)) {
			return;
		}
		GameLobby lobby = playersInGameLobbies.get(player);
		lobby.setIsPlayerReady(player, isReady);
		// if all players are ready
		if (lobby.isReady()) {
			// remove the players from the game lobby
			for (Player playerInGameLobby : lobby.players) {
				playersInGameLobbies.remove(playerInGameLobby);
			}
			// remove the lobby
			removeGameLobby(lobby);
			// start a new game
			Set<Player> playerSet = new HashSet<>();
			Collections.addAll(playerSet, lobby.players);
			startGame(playerSet, lobby.cardSets, lobby.requiredCards, lobby.forbiddenCards);
		} else {
			// send the new lobby state to the players
			for (Player playerInLobby : lobby.players) {
				if (playerInLobby != null) {
					updateGameLobby(playerInLobby, lobby);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void announceGameListing(GameLobby lobby) {
		JSONObject command = new JSONObject();
		command.put("command", "addGameListing");
		command.put("gameListing", gameLobbyToJson(lobby));
		// announce game lobby to all players in the general lobby
		for (Player playerInLobby : playersInLobby) {
			playerInLobby.issueCommand(command);
		}
	}

	private String setsToString(Set<Set<Card>> sets) {
		// make a list of the sets' names, ordered by release date (i.e. Card.setOrder)
		List<String> setNames = new ArrayList<>();
		for (int i = 0; i < Card.setOrder.size(); i++) {
			if (sets.contains(Card.setOrder.get(i))) {
				setNames.add(Card.setNames.get(i));
			}
		}
		// concatenate them in a comma separated list
		StringBuilder builder = new StringBuilder();
		Iterator<String> iter = setNames.iterator();
		while (iter.hasNext()) {
			builder.append(iter.next());
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	private void removeGameLobby(GameLobby lobby) {
		gameLobbies.remove(lobby.name);
		// announce that the game lobby has closed
		JSONObject command = new JSONObject();
		command.put("command", "updateGameListing");
		command.put("name", lobby.name);
		command.put("isClosed", true);
		for (Player playerInLobby : playersInLobby) {
			playerInLobby.issueCommand(command);
		}
	}

	@SuppressWarnings("unchecked")
	private void sendToGameLobby(Player player, GameLobby lobby) {
		// if this player is not in the general lobby, or the game lobby is full, ignore the request
		if (!playersInLobby.contains(player) || lobby.isFull()) {
			return;
		}
		// move the player from the general lobby to the game lobby
		removeFromLobby(player);
		playersInGameLobbies.put(player, lobby);
		lobby.addPlayer(player);
		// send the player the current state of the game lobby
		JSONObject command = new JSONObject();
		command.put("command", "enterGameLobby");
		command.put("name", lobby.name);
		command.put("sets", setsToString(lobby.cardSets));
		if (!lobby.requiredCards.isEmpty()) {
			command.put("requiredCards", Card.htmlSet(lobby.requiredCards));
		}
		if (!lobby.forbiddenCards.isEmpty()) {
			command.put("forbiddenCards", Card.htmlSet(lobby.forbiddenCards));
		}
		player.issueCommand(command);
		// send the new state of the game lobby to other players waiting in the game lobby
		for (int i = 0; i < lobby.players.length; i++) {
			if (lobby.players[i] != null) {
				updateGameLobby(lobby.players[i], lobby);
			}
		}
		// send the new number of players in the game lobby to players waiting in the general lobby
		updateGameListing(lobby);
	}

	@SuppressWarnings("unchecked")
	private void updateGameListing(GameLobby lobby) {
		JSONObject command = new JSONObject();
		command.put("command", "updateGameListing");
		command.put("name", lobby.name);
		command.put("isClosed", false);
		command.put("numOpenings", lobby.numOpenings);
		command.put("numPlayers", lobby.numPlayers());
		// send the new state to the players waiting in the general lobby
		for (Player playerInLobby : playersInLobby) {
			playerInLobby.issueCommand(command);
		}
	}

	private void removeFromGameLobby(Player player) {
		// get the game lobby that the player is currently in
		GameLobby lobby = playersInGameLobbies.get(player);
		// if they are not in a game lobby, ignore the request
		if (lobby == null) {
			return;
		}
		// remove the player from the game lobby
		lobby.removePlayer(player);
		playersInGameLobbies.remove(player);
		// tell the other players in the game lobby that a player has left
		for (int i = 0; i < lobby.players.length; i++) {
			if (lobby.players[i] != null) {
				updateGameLobby(lobby.players[i], lobby);
			}
		}
		// if only bots are left, remove the game lobby
		if (lobby.isEmpty()) {
			removeGameLobby(lobby);
		} else {
			// send new number of players in this game to others in lobby
			updateGameListing(lobby);
		}
		// send the player back to the general lobby
		sendToLobby(player);
	}

	@SuppressWarnings("unchecked")
	private void updateGameLobby(Player player, GameLobby lobby) {
		JSONObject command = new JSONObject();
		command.put("command", "updateGameLobby");
		JSONArray playerArray = new JSONArray();
		// for each opening in the lobby
		for (int i = 0; i < lobby.players.length; i++) {
			JSONObject state = new JSONObject();
			// if the opening is filled
			if (lobby.players[i] != null) {
				state.put("username", lobby.players[i].username);
				// if it is filled by a bot
				if (lobby.players[i] instanceof Bot) {
					state.put("type", "bot");
				} else {
					// filled by a human player
					state.put("type", "player");
				}
				state.put("isReady", lobby.isPlayerReady[i]);
			} else {
				// opening with no player
				state.put("type", "open");
			}
			playerArray.add(state);
		}
		command.put("players", playerArray);
		player.issueCommand(command);
	}

	@SuppressWarnings("unchecked")
	private void sendToLobby(Player player) {
		playersInLobby.add(player);
		// order all of the available game lobbies alphabetically
		List<GameLobby> availableLobbies = new ArrayList<>();
		availableLobbies.addAll(gameLobbies.values());
		Collections.sort(availableLobbies);
		JSONArray gameListings = new JSONArray();
		availableLobbies.forEach(lobby -> gameListings.add(gameLobbyToJson(lobby)));
		// send all available bots
		List<String> availableBotNames = new ArrayList<>(Bot.botsByName.keySet());
		// first, order bots alphabetically
		Collections.sort(availableBotNames);
		// then, put BigMoney first for convenience
		availableBotNames.remove("BigMoney");
		availableBotNames.add(0, "BigMoney");
		JSONArray availableBots = new JSONArray();
		availableBots.addAll(availableBotNames);
		// send command to enter lobby
		JSONObject command = new JSONObject();
		command.put("command", "enterLobby");
		command.put("username", player.username);
		command.put("gameListings", gameListings);
		command.put("availableBots", availableBots);
		player.issueCommand(command);
	}

	@SuppressWarnings("unchecked")
	private JSONObject gameLobbyToJson(GameLobby lobby) {
		JSONObject game = new JSONObject();
		game.put("name", lobby.name);
		game.put("numOpenings", lobby.numOpenings);
		game.put("numPlayers", lobby.numPlayers());
		game.put("sets", setsToString(lobby.cardSets));
		if (!lobby.requiredCards.isEmpty()) {
			game.put("requiredCards", Card.htmlSet(lobby.requiredCards));
		}
		if (!lobby.forbiddenCards.isEmpty()) {
			game.put("forbiddenCards", Card.htmlSet(lobby.forbiddenCards));
		}
		return game;
	}

	private void loadLogins() {
		// if there is no logins file, do nothing
		if (!LOGINS_FILE_PATH.toFile().exists()) {
			return;
		}
		// load the username, hashed password pairs
		try {
			InputStream is = new FileInputStream(LOGINS_FILE_NAME);
			InputStreamReader isr = new InputStreamReader(is, LOGINS_FILE_CHARSET);
			BufferedReader br = new BufferedReader(isr);
			String username;
			while ((username = br.readLine()) != null) {
				String hashedPassword = br.readLine();
				validLogins.put(username, hashedPassword);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveLogin(String username, String hashedPassword) {
		List<String> lines = new ArrayList<>();
		lines.add(username);
		lines.add(hashedPassword);
		try {
			// if there is no logins file
			if (!LOGINS_FILE_PATH.toFile().exists()) {
				// create the file with this first login
				Files.write(LOGINS_FILE_PATH, lines, LOGINS_FILE_CHARSET);
			} else {
				// append this new login to the end of the file
				Files.write(LOGINS_FILE_PATH, lines, LOGINS_FILE_CHARSET, StandardOpenOption.APPEND);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	synchronized void forfeit(Player forfeitPlayer) {
		// if the player is not in a game, ignore the request
		if (forfeitPlayer.game == null) {
			return;
		}
		// tell the game that the player has forfeited
		forfeitPlayer.game.forfeit(forfeitPlayer, false);
		// reassign the connection to a new player object
		PlayerWebSocketHandler conn = forfeitPlayer.conn;
		forfeitPlayer.conn = null;
		Player newPlayer = new Player(conn);
		players.remove(conn);
		players.put(conn, newPlayer);
		// transfer the user's login to the new player object
		loggedInPlayers.remove(forfeitPlayer.username);
		loggedInPlayers.put(forfeitPlayer.username, newPlayer);
		newPlayer.username = forfeitPlayer.username;
		// send the player back to the lobby
		sendToLobby(newPlayer);
	}

	// record successful strategies that can be used by MimicBot
	private Map<Set<Card>, List<Card>> winningStrategies = new HashMap<>();

	synchronized void recordWinningStrategy(Set<Card> kingdom, List<Card> gainStrategy) {
		winningStrategies.put(kingdom, gainStrategy);
	}

	synchronized boolean hasAnyWinningStrategy() {
		return !winningStrategies.keySet().isEmpty();
	}

	synchronized Set<Card> randomWinningStrategyCardSet() {
		List<Set<Card>> choices = new ArrayList<>(winningStrategies.keySet());
		return choices.get((int) (Math.random() * choices.size()));
	}

	synchronized List<Card> winningStrategyForCardSet(Set<Card> cardSet) {
		return winningStrategies.get(cardSet);
	}

	public static void main(String[] args) {
		Card.initializeCardSets();
		Bot.initializeBots();

		INSTANCE = new GameServer();
		INSTANCE.run();

		System.out.println("Server running. Type \"exit\" to terminate.");
		Scanner scanner = new Scanner(System.in);
		for (;;) {
			if ("exit".equals(scanner.nextLine())) {
				break;
			}
		}
		scanner.close();
		System.exit(0);
	}

}

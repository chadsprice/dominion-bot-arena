package server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
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

	public static GameServer INSTANCE;

	private static final int DEFAULT_HTTP_PORT = 8080;
	private static final int DEFAULT_WEBSOCKET_PORT = 8081;
	private static final int DEFAULT_WEBSOCKET_TIMEOUT = 600000;

	private Map<PlayerWebSocketHandler, Player> players;
	private Map<String, Player> loggedInPlayers;
	private Set<Player> playersInLobby;
	private Map<String, GameLobby> gameLobbies;
	private Map<Player, GameLobby> playersInGameLobbies;

	private Set<Player> automatch2, automatch3, automatch4;

	private Map<String, String> validLogins;

	private int anonymousNumber = 1;
	private static Pattern anonymousNamePattern = Pattern.compile("^Anonymous\\d+$");

	public GameServer() {}

	public void run() {
		players = new HashMap<PlayerWebSocketHandler, Player>();
		loggedInPlayers = new HashMap<String, Player>();
		playersInLobby = new HashSet<Player>();
		playersInGameLobbies = new HashMap<Player, GameLobby>();
		automatch2 = new HashSet<Player>();
		automatch3 = new HashSet<Player>();
		automatch4 = new HashSet<Player>();
		validLogins = new HashMap<String, String>();
		loadLogins();
		gameLobbies = new HashMap<String, GameLobby>();
		winningStrategies = new HashMap<Set<Card>, List<Card>>();
		startHttpServer();
		startWebSocketServer();
	}

	public void startHttpServer() {
		Server server = new Server(DEFAULT_HTTP_PORT);

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] {"main.html"});
		resourceHandler.setResourceBase("html");

		GzipHandler gzip = new GzipHandler();
		server.setHandler(gzip);
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] {resourceHandler, new DefaultHandler()});
		gzip.setHandler(handlers);

		try {
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startWebSocketServer() {
		Server webSocketServer = new Server(DEFAULT_WEBSOCKET_PORT);

		WebSocketHandler webSocketHandler = new WebSocketHandler() {
			@Override
			public void configure(WebSocketServletFactory factory) {
				factory.getPolicy().setIdleTimeout(DEFAULT_WEBSOCKET_TIMEOUT);
				factory.register(PlayerWebSocketHandler.class);
			}
		};
		webSocketServer.setHandler(webSocketHandler);

		try {
			webSocketServer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void addConnection(PlayerWebSocketHandler conn) {
		players.put(conn, new Player(conn));
	}

	public synchronized void removeConnection(PlayerWebSocketHandler conn) {
		Player player = players.get(conn);
		if (player == null) {
			return;
		}
		// remove from active players
		players.remove(conn);
		// if logged in, log out
		if (player.username != null) {
			loggedInPlayers.remove(player.username);
		}
		// remove player from the general lobby
		playersInLobby.remove(player);
		removeFromAutomatch(player);
		// if in game lobby, remove from game lobby
		if (playersInGameLobbies.get(player) != null) {
			removeFromGameLobby(player);
		}
		// if in game, forfeit
		if (player.game != null) {
			player.game.forfeit(player, true);
		}
	}

	public synchronized void receiveMessage(PlayerWebSocketHandler conn, String message) {
		try {
			JSONObject request = (JSONObject) JSONValue.parse(message);
			handleRequest(conn, request);
		} catch (Exception e) {
			// ignore failed requests
			e.printStackTrace();
		}
	}

	private synchronized void handleRequest(PlayerWebSocketHandler conn, JSONObject request) {
		Player player = players.get(conn);
		if (player == null) {
			return;
		}
		String type = (String) request.get("type");
		if ("response".equals(type)) {
			handleResponse(player, request);
		} else if ("login".equals(type)) {
			handleLogin(player, request);
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
		} else if (type.equals("forfeit")) {
			forfeit(player);
		} else if (type.equals("automatch")) {
			handleAutomatch(player, request);
		} else if (type.equals("chat")) {
			handleChat(player, request);
		}
	}

	private void handleResponse(Player player, JSONObject request) {
		player.receiveResponse(request.get("response"));
	}

	private void handleLogin(Player player, JSONObject request) {
		// ignore the request if the player is already logged in
		if (player.username != null) {
			return;
		}
		String username = (String) request.get("username");
		String password = (String) request.get("password");
		Boolean newLogin = (Boolean) request.get("newLogin");
		if (username == null) {
			// silent error, the client should send no username
			return;
		}
		// sanitize username
		username = cleanName(username);
		// if no username given, assign "Anonymous#" username
		if (username.equals("")) {
			username = "Anonymous" + (anonymousNumber++);
			while (loggedInPlayers.containsKey(username)) {
				username = "Anonymous" + (anonymousNumber++);
			}
		}
		// check that this username is not already logged in
		if (loggedInPlayers.containsKey(username)) {
			loginError(player, "Duplicate login.");
			return;
		}
		// if not creating a new login
		if (newLogin == null) {
			// if this is an existing login, check the password
			if (validLogins.containsKey(username)) {
				if (password == null) {
					loginError(player, "Username or password is incorrect.");
					return;
				}
				String hashed = validLogins.get(username);
				if (!BCrypt.checkpw(password, hashed)) {
					loginError(player, "Username or password is incorrect.");
					return;
				}
			} else if (password != null) {
				// password provided when none was needed
				loginError(player, "Given username has no associated password.");
				return;
			}
			/// else, okay non-password-protected login
		} else {
			// creating a new login
			// if this is an existing username
			if (validLogins.containsKey(username)) {
				loginError(player, "Username already taken.");
				return;
			} else if (anonymousNamePattern.matcher(username).find()) {
				loginError(player, "Must provide unique username.");
				return;
			}
			String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
			validLogins.put(username, hashedPassword);
			saveLogin(username, hashedPassword);
		}
		player.username = username;
		loggedInPlayers.put(username, player);
		sendToLobby(player);
	}

	@SuppressWarnings("unchecked")
	private void loginError(Player player, String errorMessage) {
		JSONObject command = new JSONObject();
		command.put("command", "loginError");
		command.put("message", errorMessage);
		player.issueCommand(command);
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
		Set<Set<Card>> sets = new HashSet<Set<Card>>();
		for (Object setNameObject : setArray) {
			String setName = (String) setNameObject;
			Set<Card> set = Card.setsByName.get(setName);
			if (set != null) {
				sets.add(set);
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
				Card card = Card.fromName(cardName);
				if (card != null && !Card.BASIC_CARDS.contains(card)) {
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
		List<String> botNames = new ArrayList<String>();
		JSONArray botNameArray = (JSONArray) request.get("bots");
		for (Object botName : botNameArray) {
			botNames.add((String) botName);
		}
		if (botNames.size() >= numPlayers) {
			customGameError(player, "Too many bots.");
			return;
		}
		List<Player> bots = new ArrayList<Player>();
		for (String botName : botNames) {
			if ("Mimic".equals(botName)) {
				bots.add(new MimicBot());
			} else {
				bots.add(new Bot());
			}
		}
		// create the lobby
		GameLobby lobby = new GameLobby(name, numPlayers, sets, requiredCards, forbiddenCards, bots);
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

	private void tryAutomatch() {
		@SuppressWarnings("unchecked")
		Set<Player>[] pools = new Set[] {automatch2, automatch3, automatch4};
		for (int i = 0; i < pools.length; i++) {
			Set<Player> pool = pools[i];
			int size = i + 2;
			if (pool.size() == size) {
				GameLobby lobby = GameLobby.automatchLobby(size);
				List<Player> copy = new ArrayList<Player>(pool);
				for (Player player : copy) {
					sendToGameLobby(player, lobby);
				}
				pool.clear();
			}
		}
	}

	private void removeFromAutomatch(Player player) {
		automatch2.remove(player);
		automatch3.remove(player);
		automatch4.remove(player);
	}

	private String cleanName(String name) {
		// trim whitespace
		name = name.trim();
		// maximum length of 25 characters
		if (name.length() > 25) {
			name = name.substring(0, 25);
		}
		// escape html
		name = org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(name);
		// trim again
		name = name.trim();
		return name;
	}

	@SuppressWarnings("unchecked")
	public void setPlayerReadiness(Player player, boolean isReady) {
		// if the player is not in a game lobby, ignore the request
		if (!playersInGameLobbies.containsKey(player)) {
			return;
		}
		GameLobby lobby = playersInGameLobbies.get(player);
		lobby.setIsPlayerReady(player, isReady);
		// if all players are ready
		boolean gameStart = false;
		if (lobby.isReady()) {
			// start a new game
			gameStart = true;
			// remove the players from the game lobby
			for (Player playerInGameLobby : lobby.players) {
				playersInGameLobbies.remove(playerInGameLobby);
			}
		}
		if (gameStart) {
			// remove the lobby
			removeGameLobby(lobby);
			// create new game
			Game game = new Game();
			// add the game lobby players to the game
			for (Player inGame : lobby.players) {
				inGame.game = game;
			}
			// send the players to the game screen
			for (Player inGame : lobby.players) {
				JSONObject command = new JSONObject();
				command.put("command", "enterGame");
				inGame.issueCommand(command);
			}
			Set<Player> playerSet = new HashSet<Player>();
			Collections.addAll(playerSet, lobby.players);
			// TODO move all of this setup to the game thread
			setupGame(game, lobby.sets, lobby.requiredCards, lobby.forbiddenCards, playerSet);
			Thread gameThread = new Thread(game);
			gameThread.start();
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
	public void announceGameListing(GameLobby lobby) {
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
		List<String> setNames = new ArrayList<String>();
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
	public void removeGameLobby(GameLobby lobby) {
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
		playersInLobby.remove(player);
		removeFromAutomatch(player);
		playersInGameLobbies.put(player, lobby);
		lobby.addPlayer(player);
		// send the player the current state of the game lobby
		JSONObject command = new JSONObject();
		command.put("command", "enterGameLobby");
		command.put("name", lobby.name);
		command.put("sets", setsToString(lobby.sets));
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
		List<GameLobby> availableLobbies = new ArrayList<GameLobby>();
		availableLobbies.addAll(gameLobbies.values());
		Collections.sort(availableLobbies);
		JSONArray gameListings = new JSONArray();
		for (GameLobby lobby : availableLobbies) {
			gameListings.add(gameLobbyToJson(lobby));
		}
		// send all available bots
		JSONArray availableBots = new JSONArray();
		availableBots.add("BigMoney");
		availableBots.add("Mimic");
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
		game.put("sets", setsToString(lobby.sets));
		if (!lobby.requiredCards.isEmpty()) {
			game.put("requiredCards", Card.htmlSet(lobby.requiredCards));
		}
		if (!lobby.forbiddenCards.isEmpty()) {
			game.put("forbiddenCards", Card.htmlSet(lobby.forbiddenCards));
		}
		return game;
	}

	private static final String LOGINS_FILE_NAME = "logins";
	private static final Path LOGINS_FILE_PATH = Paths.get(LOGINS_FILE_NAME);
	private static final Charset LOGINS_FILE_CHARSET = Charset.forName("UTF-8");

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
			String line;
			while ((line = br.readLine()) != null) {
				String username = line;
				String hashedPassword = br.readLine();
				validLogins.put(username, hashedPassword);
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveLogin(String username, String hashedPassword) {
		List<String> lines = new ArrayList<String>();
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

	public synchronized void forfeit(Player forfeitPlayer) {
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

	private void setupGame(Game game, Set<Set<Card>> sets, Set<Card> required, Set<Card> forbidden, Set<Player> players) {
		if (containsMimicBot(players)) {
			boolean successful = setupGameWithMimicBot(game, players);
			if (successful) {
				return;
			}
		}
		// start with the required cards
		Set<Card> chosen = required;
		if (chosen.size() > 10) {
			chosen = new HashSet<Card>((new ArrayList<Card>(chosen)).subList(0, 10));
		}
		// create a list of available kingdom cards to fill in the rest
		Set<Card> available = new HashSet<Card>();
		for (Set<Card> set : sets) {
			available.addAll(set);
		}
		// take out the forbidden cards
		available.removeAll(forbidden);
		// shuffle and draw the remaining
		List<Card> availableList = new ArrayList<Card>(available);
		Collections.shuffle(availableList);
		int toDraw = Math.max(10 - chosen.size(), 0);
		chosen.addAll(availableList.subList(0, Math.min(toDraw, availableList.size())));
		// if there are not 10, fill in the rest with the basic set
		if (chosen.size() < 10) {
			Set<Card> filler = new HashSet<Card>(Card.BASE_SET);
			filler.removeAll(chosen);
			List<Card> fillerList = new ArrayList<Card>(filler);
			Collections.shuffle(fillerList);
			chosen.addAll(fillerList.subList(0, 10 - chosen.size()));
		}
		if (chosen.size() != 10) {
			// there should always be 10 unique kingdom cards
			throw new IllegalStateException();
		}
		// basic cards
		Set<Card> basicSet = new HashSet<Card>();
		basicSet.add(Card.PROVINCE);
		basicSet.add(Card.DUCHY);
		basicSet.add(Card.ESTATE);
		basicSet.add(Card.GOLD);
		basicSet.add(Card.SILVER);
		basicSet.add(Card.COPPER);
		basicSet.add(Card.CURSE);
		// initialize the game with this kingdom and basic set
		game.init(this, players, chosen, basicSet);
	}

	private boolean setupGameWithMimicBot(Game game, Set<Player> players) {
		// if there are no strategies to mimic, replace all of the mimic bots with big money
		if (winningStrategies.keySet().isEmpty()) {
			int numToReplace = 0;
			for (Iterator<Player> iter = players.iterator(); iter.hasNext(); ) {
				if (iter.next() instanceof MimicBot) {
					iter.remove();
					numToReplace++;
				}
			}
			for (int n = 0; n < numToReplace; n++) {
				Bot bot = new Bot();
				bot.game = game;
				players.add(bot);
			}
			return false;
		}
		Set<Card> kingdomSet = winningStrategies.keySet().iterator().next();
		for (Player player : players) {
			if (player instanceof MimicBot) {
				MimicBot mimicBot = (MimicBot) player;
				mimicBot.setStrategy(winningStrategies.get(kingdomSet));
			}
		}
		// basic cards
		Set<Card> basicSet = new HashSet<Card>();
		basicSet.add(Card.PROVINCE);
		basicSet.add(Card.DUCHY);
		basicSet.add(Card.ESTATE);
		basicSet.add(Card.GOLD);
		basicSet.add(Card.SILVER);
		basicSet.add(Card.COPPER);
		basicSet.add(Card.CURSE);
		// initialize the the necessary kingdom and basic set
		game.init(this, players, kingdomSet, basicSet);
		return true;
	}

	private boolean containsMimicBot(Set<Player> players) {
		for (Player player : players) {
			if (player instanceof MimicBot) {
				return true;
			}
		}
		return false;
	}

	// record successful strategies that can be used by MimicBot
	private Map<Set<Card>, List<Card>> winningStrategies;

	public void recordWinningStrategy(Set<Card> kingdom, List<Card> gainStrategy) {
		winningStrategies.put(kingdom, gainStrategy);
	}

	public static void main(String[] args) {
		INSTANCE = new GameServer();
		INSTANCE.run();

		System.out.println("Server running. Type \"exit\" to terminate.");
		Scanner scanner = new Scanner(System.in);
		while (!scanner.nextLine().equals("exit")) {}
		scanner.close();
		System.exit(0);
	}

}

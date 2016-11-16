![Screenshot of a game against a BigMoney bot.](/screenshot.png?raw=true)

# Dominion Bot Arena
Dominion Bot Arena is a server for playing the card game Dominion against bots, and a framework for developing new AI. You can see a live demo at [dominionbotarena.com](http://dominionbotarena.com).

Implemented Sets:
* Base (1st and 2nd ed.)
* Intrigue (1st and 2nd ed.)
* Seaside
* Prosperity
* Cornucopia
* Hinterlands
* Dark Ages
* Guilds

## Running
You can run the server yourself by downloading [dominion-bot-arena.zip](https://github.com/chadsprice/dominion-bot-arena/raw/master/dominion-bot-arena.zip). Extract the contents and execute `java -jar dominion-bot-arena.jar` in the main directory. The default port is 8080, so navigate to `localhost:8080`. You can configure server settings via the `config` file in the main directory.

## Legal
The original Dominion card game is created by Donald X. Vaccarino and published by Rio Grande Games. This fan project is in no way affiliated with either.

This code is released by Chad Price under an MIT license.

Included Libraries:
* [Jetty](http://www.eclipse.org/jetty/)
* [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/)
* [json-simple](https://github.com/fangyidong/json-simple)
* [jBCrypt](https://github.com/jeremyh/jBCrypt)
* [bcrypt.js](https://github.com/dcodeIO/bcrypt.js)

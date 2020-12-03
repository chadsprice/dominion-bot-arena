![Screenshot of a game against a BigMoney bot.](/screenshot.jpg?raw=true)

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
You can run the server yourself by downloading [the latest release zip](https://github.com/chadsprice/dominion-bot-arena/releases), extracting the contents and executing `java -jar dba.jar` in the main directory. The default port is 8080, so navigate to `localhost:8080`. You can configure server settings via the `config` file in the main directory.

## Building

### Install Babel

The build process expects Babel to be installed in the project folder. You can install Babel from NPM by running the following command in the project root folder:

`npm install --save-dev babel-cli babel-preset-env`

Check that `node_modules/.bin/babel` was created.

If you want to run Babel from somewhere else, change `def babel = 'node_modules/.bin/babel'` in `build.gradle`.

### Install Sass

The build process expects the `sass` command to be available.

On Linux with the APT package manager, you can install Sass by running:

`sudo apt-get install ruby-sass`

On Windows, you can download the binaries from [https://sass-lang.com/](https://sass-lang.com/), then add them to your `PATH`.

If you want to run Sass from somewhere else, change `def sass = 'sass'` in `build.gradle`.

## Legal
The original Dominion card game is created by Donald X. Vaccarino and published by Rio Grande Games. This fan project is in no way affiliated with either.

This code is released by Chad Price under an MIT license.

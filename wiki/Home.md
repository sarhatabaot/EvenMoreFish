# Welcome to the EvenMoreFish wiki

This is a feature-rich plugin providing a new experience to fishing on Minecraft servers and this wiki provides information on interacting with the plugin's API, setting it up for server admistrators and for players playing on a server using EvenMoreFish.

## Pages

* [Competition Types](https://github.com/EvenMoreFish/EvenMoreFish/wiki/Competition-Types)
* [Competitions.yml](https://github.com/EvenMoreFish/EvenMoreFish/wiki/competitions.yml)
* [Fish.yml](https://github.com/EvenMoreFish/EvenMoreFish/wiki/Fish.yml)
* [Rarities](https://github.com/EvenMoreFish/EvenMoreFish/wiki/Rarities.yml)
* [Messages.yml](https://github.com/EvenMoreFish/EvenMoreFish/wiki/Messages.yml)
* [Requirements](https://github.com/EvenMoreFish/EvenMoreFish/wiki/Requirements)
* [Developers](https://github.com/EvenMoreFish/EvenMoreFish/wiki/Developers)

## Where/how to download/compile the plugin

### Downloading

The latest version of the plugin will always be available at the official [Modrinth page](https://modrinth.com/plugin/evenmorefish). To run it on your server, insert the .jar file into your server's `plugin` folder and **restart** the server completely.

### Development Builds
Untested, bleeding edge builds are available at [Jenkins](https://ci.codemc.io/job/EvenMoreFish/job/EvenMoreFish/)

### Compiling

This is for more advanced users, though it comes with the added benefit of a more up-to-date version of the plugin. Please note that self-compiled versions of the plugin may only partially work, or not work atall. In most cases, support won't be offered to versions that haven't been fully released but bug reports would be massively appreciated.

To get the files needed to compile, run `git clone https://github.com/EvenMoreFish/EvenMoreFish.git` on your computer, which provides you with the source code for the plugin. Go into the newly created directory and run `./gradlew build` and locate the `even-more-fish-plugin/build/libs` folder where you will see a .jar file called `even-more-fish-{version}-{compile-timestamp}.jar`.

### Configuring
Once installed, you will most likely want to make changes to how the plugin runs: to do this, you will need to configure the plugin using its config files. There are guides for most of these files in this wiki.
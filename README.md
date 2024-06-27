# Fables & Fantasy Kotlin plugins
These are all the core plugins that I wrote while I was active on Fables and Fantasy RP. I've since then long left the place, and have decided to release my work as open source software under the GNU Affero General Public License v3.0.

See the LICENSE file for more information.

## Useful links
- [Latest Spigot Javadocs](https://hub.spigotmc.org/javadocs/spigot/)
- H2 Java SQL Database
    - [Data Types](http://www.h2database.com/html/datatypes.html)
    - [Grammar](http://www.h2database.com/html/grammar.html)
    - [Functions](http://www.h2database.com/html/functions.html)

## Structure
The root project directory contains a number of Gradle subprojects, each one of which will produce a single Spigot plugin.
the `buildSrc`, `examples` and `lib` directories are the only exceptions to this.
- `buildSrc` is our Gradle 'conventions plugin', which allows us to deduplicate and centralize Gradle behavioral rules between our different subprojects.
- `examples` contains code examples
- `lib` is a git submodule that contains third party plugin jars that are used as dependencies

## Cloning
To clone as well as set up all submodules:
```
git clone --recurse-submodules https://github.com/martijn-heil/fables-and-fantasy-rp.git
```

If you cloned and forgot `--recurse-submodules`, you can set up the submodules like this:
```
cd lib && git submodule update --init
```

## Building
Running `gradle build` in the project root will build all subprojects.
You can subsequently find the produced plugin jars at `./*/build/libs/Fables*-SNAPSHOT.jar`

On \*nix systems you can run the `deployto.sh` script to copy all plugin jars to a directory somewhere else.
Example: `./deployto.sh /to/my/testserver/plugins/`

## Code style
We use tabs for indentation. Yes, really. blame 9front for making me this way.

When using Intellij IDEA it should automatically take over the preconfigured style options.

Try to keep line length below 120 columns.

## Making your first plugin/system
I will assume the name of your new system will be `MySystem`. Everywhere I use `MySystem` here
you should replace with your own system name.
1. Copy the `BlankSystem` directory from `examples/BlankSystem` to the project root and make sure it is named `MySystem`.
2. Modify `settings.gradle.kts` and add `MySystem` to the big list of strings.
3. In Intellij IDEA: hit the "Reload all gradle projects" spinny wheel button in your gradle sidebar.
4. Navigate into `MySystem/` and make sure to change all references you find that are still called `BlankSystem` to your new `MySystem`.
   If Intellij IDEA prompts you anywhere whether you want to rename *everything* or *all in current module*, you should choose *all in current module*.
   The main things you should rename are:
   - in `plugin.yml`
   - The plugin/system kotlin package location from `com.fablesfantasyrp.plugin.blanksystem` to `com.fablesfantasyrp.plugin.mysystem`
   - The main plugin class.
   - The SYSPREFIX global variable.
5. You should now be able to build and deploy all the plugins using the instructions given above under Building.

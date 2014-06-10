fx2048Auto - PR2 project
========================
An automatic version of the game 2048 built using JavaFX and Java 8. This is a 
fork based on the [fx2048 project](https://github.com/brunoborges/fx2048) by Bruno Boges <bruno.borges@oracle.com>.
The original game is a [Javascript version](https://github.com/gabrielecirulli/2048) by Gabriele Cirulli.

Structure and building
=======================
The most modular packaging has been chosed for the project structure, avoiding 
code repetitions or the presence of the same file in more than one project or 
jar. Another way was putting both the packages giocatoreAutomatico and game2048
in the same jar, with the package giocatoreAutomatico.player in the other jar. 
This implementation would be easier but troubles may rise when playing the 
giocatoreAutomatico.player with another implementation of the gui wich not 
provides the right interfaces (package giocatoreAutomatico) in his jar file.

So the project is deliberately divided into two parts, one for the graphical 
interface (game2048 project, containing the game2048 package), and the other for 
the automatic player (giocatoreAutomatico project, containing the 
giocatoreAutomatico and the giocatoreAutomatico.player packages). Each part is a
separated Netbeans 8 project: the game2048 depends directly on the other project,
while giocatoreAutomatico depends on a jar library automatically placed in 
/giocatoreAutomatico/lib/Game2048.jar while building game2048, in order to break 
the cyclic dependency between the projects (see the ant build file in 
game2048/nbproject/build-impl.xml, under the post-jar section). However, for 
more compatibility, a copy of the interfaces GiocatoreAutomatico and Griglia has 
been included in the game2048.jar too.

You will need [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
and [ANT](http://ant.apache.org/) installed to build the project. You can build 
the two components separately, launching ant in each project root (both 
giocatoreAutomatico/ and fx2048/ folders) with the following command:

```bash
ant
```

Running fx2048Auto
===================
In order to work, the game needs each of the two components is avaible in the 
classpath, and each component is packed in a separatred jar file. Note that
giocatoreAutomatico.jar is only a non-runnable Java library, while the 
game2048.jar is a runnable jar packaging a JavaFX8 application, but it's useless
while the other jar is not in the java classpath (the application will probably 
throw some ClassNotFoundException).

After building the project, you can run the application launching the following 
command in the project root folder:

```bash
java -cp giocatoreAutomatico/dist/giocatoreAutomatico.jar:fx2048/dist/Game2048.jar game2048.Game2048
```

The debug output on the console is avaible passing the -d option to the 
class game2048.Game2048

Playing instructions
====================
The game is played as usual, with the arrow keys or swiping on the touchscreen 
(from ARM devices). If you want to let the computer play himself, select the 
"Need help?" checkbox, located in the main window. After selecting it, you cannot
move the tiles anymore with arrow keys, but you can ask the computer to do an 
automatic move by pressing the "n" (next) key. If you select the 
"Auto move" checkbox, the game plays a move on a regular time 
interval, customizable through the choichebox on the main window, located under 
the two checkboxes.

In the "Settings" menu you can choose the winning value and to stop or not to 
stop the game when the winning value is reached.

Some technical details
======================
The application has a menubar with two menus. The first offers the common menu 
commands (save, restore, exit) and the second provides some options. The 
player has three different playing styles, and the playing style may be chosen 
in the "Settings" menu, only by enabling "Advanced options". Theese are 
features going out of the project specifications, so they are disabled by 
default. Indeed, when the Advanced options are enabled, a value is passed to the 
automatic player through the grid, mapped in the location (-1,-1), so the 
grid has more than 16 keys indexed. When the Advanced options are disabled, the
grid contains only 16 keys. In a similar manner, an integer representing the 
search depth is passed through the (-1,-2) location (only with minimax playing 
styles). 

When Advanced options are disabled, each component is fully compatible with 
other implementations of the project. When no value is passed, the automatic 
player is automatically set to minimax style with a depth of 6.

The graphical application generates a grid for the automatic player. 
The grid object is definited once in the GameManager and the grid maker method 
adjourns that object every time is needed. This way has been chosed because 
some other methods need access to the grid (in order to write the advanced 
settings, see the previous explaination). However, this grid object is fully
independent from the grid rapresentation used in the graphical application and
is a write only object for the GameManager, so an eventual compromission of the 
grid by an automatic player should not compromise the application status.

The game termination event is handled by a method in the Game2048 class, which 
asks confirmationt with a popup window.

Playing Styles
==============
The automatic player has three different playing styles:<br />
*random move<br />
*blind (following a blind strategy)<br />
*minimax

Superseeding on the first two, the third is a simple implementation of a search 
algorithm. The algorithm tries each possible move recursively, and for each move
every possible tile adding is tested. The algorithm searches for the best move 
against the worst posible adding (in this aspect is a minimax algorithm).
The implementation uses an own board representation, fully 
independent from the GUI.

The algorithm has not been tested in a statistically significative way, however 
it usually passes the 2048 tile (more than 20.000 points) and often reaches 
80.000 points. It's a quite simple implementation and there is a lot of space
for optimization and improvement of the AI.


License
===================
The project is licensed under GPL 3. See [LICENSE](https://raw.githubusercontent.com/brunoborges/fx2048/master/LICENSE)
file for the full license.

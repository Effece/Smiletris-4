# Smiletris4
I remade Smiletris, a variation of the iconic Tetris game. Made in Java, executable in console or in a window using javax.swing and java.awt.

## Concept
Tetris is a game where pieces of various forms fall from the top, one by one. They can be rotated and moved left and right. Whenever you fill an entire row, this row is deleted and the pieces above fall. Your goal is to make the game last as long as possible.
Smiletris is a twisted Tetris. The game starts with smileys all over the grid. It takes away the various forms and replaces them with 2x1 capsules made of two cells that can be of three different colors: yellow, blue and purple. Your goal is to get rid of every smiley by aligning them with cells you place and other smileys. Precisely, a cell (or a smiley) dies when aligned with at least three other cells of the same color.
Smiletris3 additionally brings a new feature, random events. Every once in a while, a random thing on the board happens, and it can affect your game positively or negatively. What if instead of aligning four cells, you only had to align three? Or if every smiley gets its color randomly changed?

## Additions
What did I bring new here? A better start menu, that allows you to customize the game more. New events, such as the joker, the ghost capsule or the time speeder. A shadow that makes lining up your capsule easier.

## Java
This version of Smiletris was made using Java, for the object-oriented side that makes the program more organized and for my own practice. By changing just a few lines, the game can be ran in console. However if you want to keep the beauty of the game, you need javax.swing for its JPanel object, java.awt for its Graphics2D and Frame objects, Math for its logarithm function and java.util for its Timer and Random objects. Those librairies are usually downloaded by default.

## History
Smiletris is a pretty addictive game and it runs surprisingly smoothly. After a few games I began to wonder how it was coded and a few games after that I was slowly understanding everything. All the mechanics are explained as well as possible in the code. I wanted to make my own version to understand everything and especially to create my own twists to the game, freely. I also wanted to start a project to feel like my holidays were productive and to practice and learn more about Java.

# Search_And_Pathfinding

This program is used to perform the following searched on a map:
1. Breadth-first Search
2. Iterative Deepening Search
3. A* Search

## Running the Program
In order to run the program, enter the following command in the terminal:

```
java -jar SearchAndPathfinding.jar mapfile.txt search
```
- mapfile.txt is a .txt file that contains the map
- search is a command to indicate which search to run
  - `BFS` runs breadth-first search
  - `IDS` runs iterative deepending search
  - `AS` runs A* search
  
## Making a Map File
The map file must be written in a certain way.

The first line will be the size of the map where the first value is the number of rows and the second value is the number of columns.

The second line is the starting position.

The third line is the goal position.

The remaining lines represent the map where 0 is an impassable terrain and the number 1 - 5 are the path cost to move.

Below is an example of a map file.

```
5 7
1 2
4 3
2 4 2 1 4 5 2
0 1 2 3 5 3 1
2 0 4 4 1 2 4
2 5 5 3 2 0 1
4 3 3 2 1 0 1
```

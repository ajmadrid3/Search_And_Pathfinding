/*
 * Andrew Madrid, Peter Hanson
 * CS 4320
 * HW2
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchAndPathfinding {

    public static int[][] map;                      // The map imported from the text file
    public static Node tree;                        // The starting Node for the tree
    public static int[] startPos = new int[2];      // The starting position imported from the text file
    public static int[] endPos = new int[2];        // The goal position imported from the text file
    public static long startTime;                   // The time the search algorithm begins
    public static int nodesInMemory;                // The number of nodes created during the serach

    private static int rows;                        // The number of rows in the map
    private static int cols;                        // The number of columns in the map

    /*
     * Author: Andrew Madrid
     * Looks for the given text file and creates the map, start position, and goal position
     * Input: String fileName - The name of the text file
    */
    public static void readMap(String fileName) {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            
            String line = reader.readLine();
            String[] sizeOfMap = line.split(" ");
            rows = Integer.parseInt(sizeOfMap[0]);
            cols = Integer.parseInt(sizeOfMap[1]);
            map = new int[rows][cols];
            
            line = reader.readLine();
            startPos = getCoordinate(line);

            line = reader.readLine();
            endPos = getCoordinate(line);

            line = reader.readLine();
            int row = 0;
            while (line != null) {
                String[] values = line.split(" ");
                for (int col = 0; col < cols; col++) {
                    map[row][col] = Integer.parseInt(values[col]);
                }
                line = reader.readLine();
                row++;
            }
        } catch (IOException e) {
            System.out.println("File not found.");
        }
    }

    /*
     * Author: Andrew Madrid
     * Prints the map into the console
    */
    public static void printMap() {
        System.out.println("Imported Map:");
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /*
     * Author: Andrew Madrid
     * Converts the coordinates from a String to an integer array
     * Input: String position - The coordinate
     * Output: int[] coordinates - The coordinates, [0] being the row and [1] being the column
    */
    public static int[] getCoordinate(String position) {
        String[] strCoord = position.split(" ");
        int[] coordinates = new int[2];
        coordinates[0] = Integer.parseInt(strCoord[0]);
        coordinates[1] = Integer.parseInt(strCoord[1]);
        return coordinates;
    }

    /*
     * Author: Andrew Madrid
     * Looks for the given text file and creates the map, start position, and goal position and runs Breadth First Search
     * Input:  int[][] map      - The map that BFS is applied to
     *         int[] start      - The starting coordinate / The current coordinate
     *         int[] goal       - The goal coordinate
     *         List<Node> queue - A list of Nodes that the search has gone through
     * Output: boolean pass     - 0 if the search failed, 1 if the search succeeded
    */
    public static boolean breathFirstSearch(int[][] map, int[] start, int[] goal, List<Node> queue) {
        // Checks to see if 3 minutes have passed
        if(startTime - System.currentTimeMillis() > 180000.0) {
            return false;
        }
        
        // Checks to see if the current position is passed an edge or if it is a repeated state
        if(start[0] < 0 || start[1] < 0 || start[0] > map.length -1 || start[1] > map[0].length-1 || map[start[0]][start[1]] < 1 ) {
            nodesInMemory--;
            return false;
        }

        // Adds the visited Node to the queue
        Node NewNode = new Node(start[0],start[1]);
        queue.add(NewNode);
        map[start[0]][start[1]] *= -1;
        
        // Checks to see if the current node is the goal
        if(start[0] == goal[0] && start[1] == goal[1])
            return true;

        // Create the neighbor nodes
        int[] upStart = {start[0]-1,start[1]};
        int[] downStart = {start[0]+1,start[1]};
        int[] leftStart = {start[0],start[1]-1};
        int[] rightStart = {start[0],start[1]+1};
        nodesInMemory += 4;
        if(breathFirstSearch(map, upStart, goal, queue) || breathFirstSearch(map, downStart, goal, queue) || breathFirstSearch(map, leftStart, goal, queue) || breathFirstSearch(map, rightStart, goal, queue)) {
            return true;
        }
        queue.remove(queue.size() - 1);
        return false;
    }

    /*
     * Author: Andrew Madrid
     * Runs iterative deepening by going down a depth and calling depth limited search
     * Input:  int[][] map      - The map that BFS is applied to
     *         int[] start      - The starting coordinate / The current coordinate
     *         int[] goal       - The goal coordinate
     *         List<Node> queue - A list of Nodes that the search has gone through
     * Output: boolean pass     - 0 if the search failed, 1 if the search succeeded
    */
    public static boolean iterativeDeepening(int[][] map, int[] startPos, int[] endPos, List<Node> queue) {
        // Creating Source and Destination States
        Node start = new Node(startPos[0], startPos[1]);
        Node goal = new Node(endPos[0], endPos[1]);
        queue.add(start);
        boolean complete = false;
    
        // Increase the limit until a path is found
        for (int limit = 0; limit <= (rows * cols); limit++) {
            if (startTime - System.currentTimeMillis() > 180000.0) {
                return false;
            }
            nodesInMemory = 0;
            complete = depthLimited(start, goal, limit, queue);
            if (complete) {
                return complete;
            }
        }
        return complete;
    }

    /*
     * Author: Andrew Madrid
     * Runs a depth limited search starting at the current node and depth
     * Input:  Node current     - The current Node at the map
     *         Node goal        - The goal Node
     *         int limit        - The current depth of the search
     *         List<Node> queue - A list of Nodes that the search has gone through
     * Output: boolean pass     - 0 if the search failed, 1 if the search succeeded
    */
    public static boolean depthLimited(Node current, Node goal, int limit, List<Node> queue) {
        // Checks to see if 3 minutes have passed
        if (startTime - System.currentTimeMillis() > 180000.0) {
            return false;
        }
    
        int row = current.row;
        int col = current.col;
    
        // If current Node is out of bounds or has already been visited
        if (limit <= 0 || row < 0 || col < 0 || row >= rows || col >= cols || map[row][col] < 1) {
            nodesInMemory--;
            return false;
        }
    
        queue.add(current);
        map[row][col] *= -1;

        // Checks if the current node is the goal node
        if (row == goal.row && col==goal.col ) {
            return true;
        }
    
        // Gets all Nodes around current Node
        Node upStart = new Node(row - 1, col);
        Node downStart = new Node(row + 1, col);
        Node leftStart = new Node(row, col - 1);
        Node rightStart = new Node(row, col + 1);
        nodesInMemory += 4;

        // Decrease the limit
        limit = limit - 1;

        // Check each of the neighbor Nodes
        if (depthLimited(upStart, goal, limit, queue) || depthLimited(downStart, goal, limit, queue) || depthLimited(leftStart, goal, limit, queue) || depthLimited(rightStart, goal, limit, queue)) {
            return true;
        }
    
        // If nothing is on current depth, return false
        queue.remove(queue.size() - 1);
        map[row][col] *= -1;
        return false;
    }

   public static boolean aStarSearch(int[][] map, int[] start, int[] goal, List<Node> queue) {
	int distance = 0;
	int tempN = 0;
	int tempE = 0;
	int tempS = 0;
	int tempW = 0;
	int r = 0;
	int c = 0;
	int x = start[0];
	int y = start[1];
	int[] path = new int[map[0].length * map[1].length];
	while((x != goal[0]) && (y != goal[1])) {
		path[distance] = map[x][y];
		map[x][y] = -1;
		if ((x == 0) && (y == 0)) {
			tempN = 0;
			tempE = map[x + 1][y];
			tempS = map[x][y + 1];
			tempW = 0;
		}
		else if ((x == 0) && (y == map[1].length)) {
			tempN = 0;
			tempE = 0;
			tempS = map[x + 1][y];
			tempW = map[x][y - 1];			
		}
		else if ((x == map[0].length) && (y == 0)) {
			tempN = map[x - 1][y];
			tempE = map[x][y + 1];
			tempS = 0;
			tempW = 0;
		}
		else if (x == 0) {
			tempN = map[x - 1][y];
			tempE = map[x][y + 1];
			tempS = map[x + 1][y];
			tempW = 0;
		}
		else if (y == 0) {
			tempN = 0;
			tempE = map[x][y + 1];
			tempS = map[x + 1][y];
			tempW = map[x][y - 1];
		}
		else if (x == map[0].length) {
			tempN = map[x + 1][y];
			tempE = map[x][y + 1];
			tempS = 0;
			tempW = map[x][y - 1];
		}
		else if (y == map[1].length) {
			tempN = map[x + 1][y];
			tempE = 0;
			tempS = map[x - 1][y];
			tempW = map[x][y - 1];
        }
        else {
			tempN = map[x + 1][y];
			tempE = map[x][y + 1];
			tempS = map[x - 1][y];
			tempW = map[x][y - 1];
		}

		if ((tempN < tempE) && (tempN < tempS) && (tempN < tempW) && (tempN != 0) && (tempN != -1)) {
			x = x - 1;
			r = x;
			distance = distance + 1;
		}
		else if ((tempE < tempN) && (tempE < tempS) && (tempE < tempW) && (tempE != 0) && (tempE != -1)) { 
			y = y + 1;
			c = y;
			distance = distance + 1;
		}
		else if ((tempS < tempE) && (tempS < tempN) && (tempS < tempW) && (tempS != 0) && (tempS != -1)) {
			x = x + 1;
			r = x;
			distance = distance + 1;
		}
		else if ((tempW < tempE) && (tempW < tempS) && (tempW < tempE) && (tempW != 0) && (tempW != -1)) {			
			y = y - 1;
			c = y;
			distance = distance + 1;
		}
		else {
			x = r;
			y = c;
			distance = distance - 1;
		}
	}
	return true;
   }
		
   // Author: Andrew Madrid
   public static void main(String[] args) {
        if (args.length > 0) {
            String fileName = args[0];
            String searchType = args[1];
            readMap(fileName);
            printMap();

            List<Node> queue = new ArrayList<Node>();
            if (searchType.toLowerCase().equals("bfs")) {
                startTime = System.currentTimeMillis();
                nodesInMemory = 1;
                System.out.print("Results of BFS: ");
                if (breathFirstSearch(map, startPos, endPos, queue)) {
                    System.out.println("Success");
                    int cost = 0;
                    for (int i = 0; i < queue.size(); i++) {
                        cost += map[queue.get(i).row][queue.get(i).col];
                    }
                    System.out.println("Path Cost: " + cost * -1);
                    System.out.println("Number of Nodes Expanded: " + queue.size());
                    System.out.println("Maximum Number of Nodes in Memory: " + nodesInMemory);
                    System.out.print("Path: ");
                    for (int i = 0; i < queue.size(); i++) {
                        System.out.print("{" + queue.get(i).row + ", " + queue.get(i).col + "} ");
                    }
                    System.out.println();
                } else {
                    System.out.println("Fail");
                    System.out.println("Path Cost: -1");
                    System.out.println("Number of Nodes Expanded: " + queue.size());
                    System.out.println("Maximum Number of Nodes in Memory: " + nodesInMemory);
                    System.out.println("Path: NULL");
                }
                System.out.println("Time of BFS: " + (System.currentTimeMillis() - startTime) + " milliseconds");
            } else if (searchType.toLowerCase().equals("ids")) {
                startTime = System.currentTimeMillis();
                nodesInMemory = 1;
                System.out.print("Results of IDS: ");
                if (iterativeDeepening(map, startPos, endPos, queue)) {
                    System.out.println("Success");
                    int cost = 0;
                    for (int i = 0; i < queue.size(); i++) {
                        cost += map[queue.get(i).row][queue.get(i).col];
                    }
                    System.out.println("Path Cost: " + cost * -1);
                    System.out.println("Number of Nodes Expanded: " + queue.size());
                    System.out.println("Maximum Number of Nodes in Memory: " + nodesInMemory);
                    System.out.print("Path: ");
                    for (int i = 0; i < queue.size(); i++) {
                        System.out.print("{" + queue.get(i).row + ", " + queue.get(i).col + "} ");
                    }
                    System.out.println();
                } else {
                    System.out.println("Fail");
                    System.out.println("Path Cost: -1");
                    System.out.println("Number of Nodes Expanded: " + queue.size());
                    System.out.println("Maximum Number of Nodes in Memory: " + nodesInMemory);
                    System.out.println("Path: NULL");
                }
                System.out.println("Time of IDS: " + (System.currentTimeMillis() - startTime) + " milliseconds");
            } else if (searchType.toLowerCase().equals("as")) {
                startTime = System.currentTimeMillis();
                nodesInMemory = 1;
                System.out.print("Results of AS: ");
                if (aStarSearch(map, startPos, endPos, queue)) {
                    System.out.println("Success");
                    int cost = 0;
                    for (int i = 0; i < queue.size(); i++) {
                        cost += map[queue.get(i).row][queue.get(i).col];
                    }
                    System.out.println("Path Cost: " + cost * -1);
                    System.out.println("Number of Nodes Expanded: " + queue.size());
                    System.out.println("Maximum Number of Nodes in Memory: " + nodesInMemory);
                    System.out.print("Path: ");
                    for (int i = 0; i < queue.size(); i++) {
                        System.out.print("{" + queue.get(i).row + ", " + queue.get(i).col + "} ");
                    }
                    System.out.println();
                } else {
                    System.out.println("Fail");
                    System.out.println("Path Cost: -1");
                    System.out.println("Number of Nodes Expanded: " + queue.size());
                    System.out.println("Maximum Number of Nodes in Memory: " + nodesInMemory);
                    System.out.println("Path: NULL");
                }
                System.out.println("Time of AS: " + (System.currentTimeMillis() - startTime) + " milliseconds");
            }
        } else
            System.out.println("No command line arguments found.");
    }    

}
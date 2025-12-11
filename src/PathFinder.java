import java.util.*;

/**
 * PathFinder class containing Dijkstra's shortest path algorithm.
 * Students must implement the algorithm using a PriorityQueue.
 */
public class PathFinder {

    private Graph graph;

    public PathFinder(Graph graph) {
        this.graph = graph;
    }

    /**
     * Implements Dijkstra's shortest path algorithm using a PriorityQueue.
     *
     * @param startIndex the index of the starting node
     * @param endIndex   the index of the destination node
     * @return a DijkstraResult containing all steps and the final path
     */
    public DijkstraResult findShortestPath(int startIndex, int endIndex) {
        DijkstraResult result = new DijkstraResult();

        // 1. INITIALIZATION
        // TODO: Initialize distances array with Integer.MAX_VALUE for all nodes
        int[] distances = new int[Graph.NUM_NODES];
        Arrays.fill(distances, Integer.MAX_VALUE);

        // TODO: Initialize previous array with -1 for all nodes
        int[] previous = new int[Graph.NUM_NODES];
        Arrays.fill(previous, -1);

        // TODO: Initialize visited set
        HashSet<Integer> visited = new HashSet<>();

        // TODO: Set distance to start node as 0
        distances[0] = 0;


        // TODO: Create a PriorityQueue of NodeDistance objects
        // The PriorityQueue should order by distance (smallest first)
        // Hint: Use a Comparator or make NodeDistance implement Comparable
        PriorityQueue<NodeDistance> priorityQueue = new PriorityQueue<>();

        // TODO: Add the starting node to the priority queue with distance 0
        priorityQueue.add();

        // 2. MAIN LOOP
        // TODO: While the priority queue is not empty...

        // a. Poll the node with smallest distance from the priority queue

        // b. If this node has already been visited, skip it (continue)

        // c. If this node is the endIndex, we've found the shortest path - stop.

        // d. Mark current node as visited

        // e. VITAL: Record the step for the visualizer!
        // result.addStep(current, visited, distances, previous);

        // f. Iterate through all neighbors of the current node

        // For each neighbor:
        // - Calculate new distance: distances[current] + edge weight
        // - If new distance < distances[neighbor]:
        //     * Update distances[neighbor]
        //     * Update previous[neighbor] = current
        //     * Add neighbor to priority queue with new distance

        // 3. RECONSTRUCT PATH
        // TODO: Call helper method to get the path list
        // TODO: result.setFinalPath(path, distances[endIndex]);

        return result;
    }


    //Helper Method - getMinDistanceNode()
    //Loop through all nodes. You are looking for a node that is NOT visited, but has the lowest value in your distances array.
    //private ... getMinDistanceNode();



    /**
     * Helper method to reconstruct the path from start to end.
     *
     * @param previous   array where previous[i] is the node before i
     * @param startIndex the starting node index
     * @param endIndex   the ending node index
     * @return list of node indices representing the path from start to end
     */
    private List reconstructPath(int[] previous, int startIndex, int endIndex) {
        List path = new ArrayList<>();
        // TODO: Trace backwards from endIndex using the previous[] array
        // TODO: Don't forget to reverse the list so it goes Start -> End!
        return path;
    }

    /**
     * Inner class to represent a node and its distance in the priority queue.
     * Students must implement Comparable to allow PriorityQueue ordering.
     */
    private static abstract class NodeDistance implements Comparable
    {
        int nodeIndex;
        int distance;

        public NodeDistance(int nodeIndex, int distance) {
            this.nodeIndex = nodeIndex;
            this.distance = distance;
        }

        public int getNodeIndex() {
            return nodeIndex;
        }

        public void setNodeIndex(int nodeIndex) {
            this.nodeIndex = nodeIndex;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }

        //@Override
        public int compareTo(NodeDistance other) {
            // TODO: Compare based on distance
            // Return negative if this distance < other distance
            if( < other.getDistance())
            {
                return -1;
            }
            // Return positive if this distance > other distance
            if( > other.getDistance())
            {
                return 1;
            }
            // Return 0 if equal
            return 0;
        }
    }
}
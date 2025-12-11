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
        Set<Integer> visited = new HashSet<>();

        // TODO: Set distance to start node as 0
        distances[startIndex] = 0;


        // TODO: Create a PriorityQueue of NodeDistance objects
        // The PriorityQueue should order by distance (smallest first)
        // Hint: Use a Comparator or make NodeDistance implement Comparable
        PriorityQueue<NodeDistance> priorityQueue = new PriorityQueue<>();

        // TODO: Add the starting node to the priority queue with distance 0
        priorityQueue.add(new NodeDistance(startIndex, 0));

        // 2. MAIN LOOP
        // TODO: While the priority queue is not empty...
        while(!priorityQueue.isEmpty()) {
            // a. Poll the node with smallest distance from the priority queue
            int current = priorityQueue.poll().getNodeIndex();

            // b. If this node has already been visited, skip it (continue)
            if(!visited.contains(current))
            {
                // c. If this node is the endIndex, we've found the shortest path - stop.
                if(!(current == endIndex))
                {
                    // d. Mark current node as visited
                    visited.add(current);

                    // e. VITAL: Record the step for the visualizer!
                    result.addStep(current, visited, distances, previous);

                    // f. Iterate through all neighbors of the current node

                    List<Integer> neighbors = graph.getNeighbors(current);
                    int newDistance;

                    // For each neighbor:
                    for(int neighbor: neighbors)
                    {
                        // - Calculate new distance: distances[current] + edge weight
                        newDistance = distances[current] + graph.getEdgeWeight(current, neighbor);

                        // - If new distance < distances[neighbor]:
                        if(newDistance < distances[neighbor])
                        {
                            //     * Update distances[neighbor]
                            //     * Update previous[neighbor] = current
                            //     * Add neighbor to priority queue with new distance

                            distances[neighbor] = newDistance;
                            previous[neighbor] = current;
                            priorityQueue.add(new NodeDistance(neighbor, newDistance));
                        }
                    }
                }
            }
        }


        // 3. RECONSTRUCT PATH
        // TODO: Call helper method to get the path list
        // TODO: result.setFinalPath(path, distances[endIndex]);

        List path = reconstructPath(previous, startIndex, endIndex);

        result.setFinalPath(path, distances[endIndex]);

        return result;
    }


    //Didn't do helper method



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

        int currentIndex = endIndex;

        while(previous[currentIndex] != -1)
        {
            path.add(graph.getNodeName(currentIndex));
        }

        // TODO: Don't forget to reverse the list so it goes Start -> End!
        Collections.reverse(path);

        return path;
    }

    /**
     * Inner class to represent a node and its distance in the priority queue.
     * Students must implement Comparable to allow PriorityQueue ordering.
     */
    private static class NodeDistance implements Comparable<NodeDistance>
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


        @Override
        public int compareTo(NodeDistance other) {
            // TODO: Compare based on distance
            // Return negative if this distance < other distance
            if(this.distance < other.getDistance())
            {
                return -1;
            }
            // Return positive if this distance > other distance
            if(this.distance > other.getDistance())
            {
                return 1;
            }
            // Return 0 if equal
            return 0;
        }
    }
}
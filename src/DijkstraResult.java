import java.util.*;

/**
 * Holds the results of Dijkstra's algorithm for visualization.
 * DO NOT MODIFY THIS CLASS - The visualizer depends on this structure.
 */
public class DijkstraResult {

    // Each step records the state of the algorithm at that point
    private List<AlgorithmStep> steps;

    // The final shortest path from start to end
    private List<Integer> finalPath;

    // The total distance of the shortest path
    private int totalDistance;

    public DijkstraResult() {
        this.steps = new ArrayList<>();
        this.finalPath = new ArrayList<>();
        this.totalDistance = -1;
    }

    /**
     * Records a single step of the algorithm.
     * Call this each time you process a node in Dijkstra's algorithm.
     * * @param currentNode the node currently being processed
     * @param visitedNodes set of all nodes that have been fully processed
     * @param distances current shortest distances from start to each node
     * @param previous the previous node in the shortest path to each node
     */
    public void addStep(int currentNode, Set<Integer> visitedNodes,
                        int[] distances, int[] previous) {
        steps.add(new AlgorithmStep(
                currentNode,
                new HashSet<>(visitedNodes),
                distances.clone(),
                previous.clone()
        ));
    }

    /**
     * Sets the final path once the algorithm completes.
     * * @param path list of node indices from start to end
     * @param distance total distance of the path
     */
    public void setFinalPath(List<Integer> path, int distance) {
        this.finalPath = new ArrayList<>(path);
        this.totalDistance = distance;
    }

    public List<AlgorithmStep> getSteps() {
        return steps;
    }

    public List<Integer> getFinalPath() {
        return finalPath;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    /**
     * Represents the state of the algorithm at a single step.
     */
    public static class AlgorithmStep {
        public final int currentNode;
        public final Set<Integer> visitedNodes;
        public final int[] distances;
        public final int[] previous;

        public AlgorithmStep(int currentNode, Set<Integer> visitedNodes,
                             int[] distances, int[] previous) {
            this.currentNode = currentNode;
            this.visitedNodes = visitedNodes;
            this.distances = distances;
            this.previous = previous;
        }
    }
}
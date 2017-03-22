package uk.ac.bris.cs.scotlandyard.ui.ai.Dijkstra;

import java.util.*;

/**
 * Created by mt16435 on 20/03/17.
 */
public class DijkstraAlgorithm {

    private final List<DijkstraVertex> nodes;
    private List<DijkstraEdge> DijkstraEdges;
    private Set<DijkstraVertex> settledNodes;
    private Set<DijkstraVertex> unSettledNodes;
    private Map<DijkstraVertex, DijkstraVertex> predecessors;
    public Map<DijkstraVertex, Integer> distance;

    public DijkstraAlgorithm(DijkstraGraph DijkstraGraph) {
        // create a copy of the array so that we can operate on this array
        this.nodes = new ArrayList<DijkstraVertex>(DijkstraGraph.getDijkstraVertexes());
        this.DijkstraEdges = new ArrayList<DijkstraEdge>(DijkstraGraph.getDijkstraEdges());

    }

    public Map<DijkstraVertex, Integer> getDistances(){
        return distance;
    }
    public void execute(DijkstraVertex source) {
        settledNodes = new HashSet<DijkstraVertex>();
        unSettledNodes = new HashSet<DijkstraVertex>();
        distance = new HashMap<DijkstraVertex, Integer>();
        predecessors = new HashMap<DijkstraVertex, DijkstraVertex>();
        distance.put(source, 0);
        unSettledNodes.add(source);
        while (unSettledNodes.size() > 0) {
            DijkstraVertex node = getMinimum(unSettledNodes);
            settledNodes.add(node);
            unSettledNodes.remove(node);
            findMinimalDistances(node);
        }
    }

    private void findMinimalDistances(DijkstraVertex node) {
        List<DijkstraVertex> adjacentNodes = getNeighbors(node);
        for (DijkstraVertex target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node)
                    + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node)
                        + getDistance(node, target));
                predecessors.put(target, node);
                unSettledNodes.add(target);
            }
        }

    }

    private int getDistance(DijkstraVertex node, DijkstraVertex target) {
        for (DijkstraEdge DijkstraEdge : DijkstraEdges) {
            if (DijkstraEdge.getSource().equals(node)
                    && DijkstraEdge.getDestination().equals(target)) {
                return DijkstraEdge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    private List<DijkstraVertex> getNeighbors(DijkstraVertex node) {
        List<DijkstraVertex> neighbors = new ArrayList<DijkstraVertex>();
        for (DijkstraEdge DijkstraEdge : DijkstraEdges) {
            if (DijkstraEdge.getSource().equals(node)
                    && !isSettled(DijkstraEdge.getDestination())) {
                neighbors.add(DijkstraEdge.getDestination());
            }
        }
        return neighbors;
    }

    private DijkstraVertex getMinimum(Set<DijkstraVertex> DijkstraVertexes) {
        DijkstraVertex minimum = null;
        for (DijkstraVertex DijkstraVertex : DijkstraVertexes) {
            if (minimum == null) {
                minimum = DijkstraVertex;
            } else {
                if (getShortestDistance(DijkstraVertex) < getShortestDistance(minimum)) {
                    minimum = DijkstraVertex;
                }
            }
        }
        return minimum;
    }

    private boolean isSettled(DijkstraVertex DijkstraVertex) {
        return settledNodes.contains(DijkstraVertex);
    }

    private int getShortestDistance(DijkstraVertex destination) {
        Integer d = distance.get(destination);
        if (d == null) {
            return Integer.MAX_VALUE;
        } else {
            return d;
        }
    }

    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public LinkedList<DijkstraVertex> getPath(DijkstraVertex target) {
        LinkedList<DijkstraVertex> path = new LinkedList<DijkstraVertex>();
        DijkstraVertex step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

}

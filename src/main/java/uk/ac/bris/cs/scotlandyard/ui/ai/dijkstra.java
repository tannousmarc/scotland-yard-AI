package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.*;

/**
 * Created by marc on 20/03/2017.
 */
public class Dijkstra {
    public class Vertex {
        final private String id;
        final private String name;


        public Vertex(String id, String name) {
            this.id = id;
            this.name = name;
        }
        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Vertex other = (Vertex) obj;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public class Edge  {
        private final String id;
        private final Vertex source;
        private final Vertex destination;
        private final int weight;

        public Edge(String id, Vertex source, Vertex destination, int weight) {
            this.id = id;
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }

        public String getId() {
            return id;
        }
        public Vertex getDestination() {
            return destination;
        }

        public Vertex getSource() {
            return source;
        }
        public int getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return source + " " + destination;
        }


    }
    public class Graph {
        private final List<Vertex> Vertexes;
        private final List<Edge> Edges;

        public Graph(List<Vertex> Vertexes, List<Edge> Edges) {
            this.Vertexes = Vertexes;
            this.Edges = Edges;
        }

        public List<Vertex> getVertexes() {
            return Vertexes;
        }

        public List<Edge> getEdges() {
            return Edges;
        }



    }
    public class DijkstraAlgorithm {

        private final List<Vertex> nodes;
        private final List<Edge> Edges;
        private Set<Vertex> settledNodes;
        private Set<Vertex> unSettledNodes;
        private Map<Vertex, Vertex> predecessors;
        private Map<Vertex, Integer> distance;

        public DijkstraAlgorithm(Graph Graph) {
            // create a copy of the array so that we can operate on this array
            this.nodes = new ArrayList<Vertex>(Graph.getVertexes());
            this.Edges = new ArrayList<Edge>(Graph.getEdges());
        }

        public void execute(Vertex source) {
            settledNodes = new HashSet<Vertex>();
            unSettledNodes = new HashSet<Vertex>();
            distance = new HashMap<Vertex, Integer>();
            predecessors = new HashMap<Vertex, Vertex>();
            distance.put(source, 0);
            unSettledNodes.add(source);
            while (unSettledNodes.size() > 0) {
                Vertex node = getMinimum(unSettledNodes);
                settledNodes.add(node);
                unSettledNodes.remove(node);
                findMinimalDistances(node);
            }
        }

        private void findMinimalDistances(Vertex node) {
            List<Vertex> adjacentNodes = getNeighbors(node);
            for (Vertex target : adjacentNodes) {
                if (getShortestDistance(target) > getShortestDistance(node)
                        + getDistance(node, target)) {
                    distance.put(target, getShortestDistance(node)
                            + getDistance(node, target));
                    predecessors.put(target, node);
                    unSettledNodes.add(target);
                }
            }

        }

        private int getDistance(Vertex node, Vertex target) {
            for (Edge Edge : Edges) {
                if (Edge.getSource().equals(node)
                        && Edge.getDestination().equals(target)) {
                    return Edge.getWeight();
                }
            }
            throw new RuntimeException("Should not happen");
        }

        private List<Vertex> getNeighbors(Vertex node) {
            List<Vertex> neighbors = new ArrayList<Vertex>();
            for (Edge Edge : Edges) {
                if (Edge.getSource().equals(node)
                        && !isSettled(Edge.getDestination())) {
                    neighbors.add(Edge.getDestination());
                }
            }
            return neighbors;
        }

        private Vertex getMinimum(Set<Vertex> Vertexes) {
            Vertex minimum = null;
            for (Vertex Vertex : Vertexes) {
                if (minimum == null) {
                    minimum = Vertex;
                } else {
                    if (getShortestDistance(Vertex) < getShortestDistance(minimum)) {
                        minimum = Vertex;
                    }
                }
            }
            return minimum;
        }

        private boolean isSettled(Vertex Vertex) {
            return settledNodes.contains(Vertex);
        }

        private int getShortestDistance(Vertex destination) {
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
        public LinkedList<Vertex> getPath(Vertex target) {
            LinkedList<Vertex> path = new LinkedList<Vertex>();
            Vertex step = target;
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
    public int EdgeToWeight(Object e){
        switch (e.toString()){
            case "Bus": return 15;
            case "Taxi": return 10;
            case "Underground": return 30;
            case "Secret": return 24;
        }
        return 0;
    }
}

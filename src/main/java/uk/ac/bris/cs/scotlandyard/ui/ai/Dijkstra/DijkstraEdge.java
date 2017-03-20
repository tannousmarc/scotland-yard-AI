package uk.ac.bris.cs.scotlandyard.ui.ai.Dijkstra;

/**
 * Created by mt16435 on 20/03/17.
 */
public class DijkstraEdge {
        private final String id;
        private final DijkstraVertex source;
        private final DijkstraVertex destination;
        private final int weight;

        public DijkstraEdge(String id, DijkstraVertex source, DijkstraVertex destination, int weight) {
            this.id = id;
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }

        public String getId() {
            return id;
        }
        public DijkstraVertex getDestination() {
            return destination;
        }

        public DijkstraVertex getSource() {
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

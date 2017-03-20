package uk.ac.bris.cs.scotlandyard.ui.ai.Dijkstra;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mt16435 on 20/03/17.
 */
public class DijkstraGraph {
    private final List<DijkstraVertex> DijkstraVertexes;
    private final List<DijkstraEdge> DijkstraEdges;

    public DijkstraGraph(List<DijkstraVertex> DijkstraVertexes, List<DijkstraEdge> DijkstraEdges) {
        List<DijkstraEdge> aux = new ArrayList<>();
        for(DijkstraEdge e : DijkstraEdges){
            aux.add(e);
        }
        this.DijkstraVertexes = DijkstraVertexes;
        for(DijkstraEdge e : DijkstraEdges){
            String s = "0" + e.getId();
            aux.add(new DijkstraEdge(s, e.getDestination(), e.getSource(), e.getWeight()));
        }
        this.DijkstraEdges = aux;
    }

    public List<DijkstraVertex> getDijkstraVertexes() {
        return DijkstraVertexes;
    }

    public List<DijkstraEdge> getDijkstraEdges() {
        return DijkstraEdges;
    }



}


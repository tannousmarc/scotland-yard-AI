package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.scotlandyard.ai.ManagedAI;
import uk.ac.bris.cs.scotlandyard.ai.PlayerFactory;
import uk.ac.bris.cs.scotlandyard.model.Colour;
import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Player;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYardView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Node;
import uk.ac.bris.cs.scotlandyard.ai.ManagedAI;
import uk.ac.bris.cs.scotlandyard.ai.PlayerFactory;
import uk.ac.bris.cs.scotlandyard.model.*;
import uk.ac.bris.cs.scotlandyard.ui.ai.Dijkstra.DijkstraAlgorithm;
import uk.ac.bris.cs.scotlandyard.ui.ai.Dijkstra.DijkstraEdge;
import uk.ac.bris.cs.scotlandyard.ui.ai.Dijkstra.DijkstraGraph;
import uk.ac.bris.cs.scotlandyard.ui.ai.Dijkstra.DijkstraVertex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

// TODO name the AI
@ManagedAI("Detectives.AI")

public class DetectivesAI implements PlayerFactory{
    private static List<DijkstraVertex> nodes = new ArrayList<>();
    private static List<DijkstraEdge> edges = new ArrayList<>();
    static DijkstraGraph graph;
    static DijkstraAlgorithm dijkstraAlgorithm;
    static Move bestMove;
    static int potentialLocation;

    // TODO create a new player here
    @Override
    public Player createPlayer(Colour colour) {
        return new MyPlayer();
    }

    // TODO A sample player that selects a random move
    private static class MyPlayer implements Player, MoveVisitor{

        private int edgeToWeight(Object e){
            switch (e.toString()){
                case "Bus": return 15;
                case "Taxi": return 10;
                case "Underground": return 30;
            }
            return 100000;
        }
        private void initiateDijkstra(ScotlandYardView view){
            // All edges of our graph
            Collection<Edge<Integer, Transport>> initialEdges = view.getGraph().getEdges();
            // All nodes of our graph
            List<Node<Integer>> initialNodes = view.getGraph().getNodes();

            // Initiate a graph to apply the dijkstra algorithm on
            for (Node n : initialNodes) {
                nodes.add(new DijkstraVertex(n.value().toString(), n.value().toString()));
            }
            Integer aux = 0;
            for(Edge e : initialEdges){
                edges.add(new DijkstraEdge(aux.toString(),nodes.get((int)e.source().value()-1),nodes.get((int)e.destination().value()-1) , edgeToWeight(e.data())));
                aux++;
            }
            graph = new DijkstraGraph(nodes, edges);
            dijkstraAlgorithm = new DijkstraAlgorithm(graph);
        }
        private int DijkstraDistance (int source, int destination){
            // System.out.println(source + " " + destination);
            dijkstraAlgorithm.execute(nodes.get(source-1));
            return dijkstraAlgorithm.getDistances().get(nodes.get(destination-1));
        }

        //private final Random random = new Random();

        private int detectiveScore(ScotlandYardView view, int location){
            if (view.getPlayerLocation(Colour.Black) != 0)
                return DijkstraDistance(location, view.getPlayerLocation(Colour.Black));
            else
                return 5000;
        }
        @Override
        public void visit(TicketMove t){
            System.out.println("Move : " + t);
            potentialLocation = t.destination();
        }


        @Override
        public void makeMove(ScotlandYardView view, int location, Set<Move> moves,
                             Consumer<Move> callback) {
            initiateDijkstra(view);
            int min = Integer.MAX_VALUE;

            for (Move m : moves){
                m.visit(this);
                int score = detectiveScore(view, potentialLocation);
                System.out.println("Score : " + score);
                System.out.println("min : " + min);
                if (score < min) {
                    bestMove = m;
                    min = score;
                }
            }

            callback.accept(bestMove);

            // TODO do something interesting here; find the best move
            // picks a random move
            //callback.accept(new ArrayList<>(moves).get(random.nextInt(moves.size())));

        }
    }
}

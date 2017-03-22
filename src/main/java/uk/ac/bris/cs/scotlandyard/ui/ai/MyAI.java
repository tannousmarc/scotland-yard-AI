package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Node;
import uk.ac.bris.cs.scotlandyard.ai.ManagedAI;
import uk.ac.bris.cs.scotlandyard.ai.PlayerFactory;
import uk.ac.bris.cs.scotlandyard.model.*;
import uk.ac.bris.cs.scotlandyard.ui.ai.Dijkstra.DijkstraAlgorithm;
import uk.ac.bris.cs.scotlandyard.ui.ai.Dijkstra.DijkstraEdge;
import uk.ac.bris.cs.scotlandyard.ui.ai.Dijkstra.DijkstraGraph;
import uk.ac.bris.cs.scotlandyard.ui.ai.Dijkstra.DijkstraVertex;

import java.util.*;
import java.util.function.Consumer;

// TODO name the AI
@ManagedAI("Elodia")

public class MyAI implements PlayerFactory {

	static int maxScore = 0;
	static ScotlandYardView globalView;
	static Move bestMove;
	// TODO create a new player here
	/*@Override
	public DijkstraGraph<Integer, Transport> DijkstraGraph;
	*/
	public Player createPlayer(Colour colour) {
		return new MyPlayer();
	}

	// TODO A sample player that selects a random move
	private static class MyPlayer implements Player, MoveVisitor {

		public int edgeToWeight(Object e){
			switch (e.toString()){
				case "Bus": return 15;
				case "Taxi": return 10;
				case "Underground": return 30;
			}
			return 100000;
		}
		private final int DijkstraDistance (ScotlandYardView view, int source, int destination){
			// All edges of our graph
			Collection<Edge<Integer, Transport>> initialEdges = view.getGraph().getEdges();
			// All nodes of our graph
			List<Node<Integer>> initialNodes = view.getGraph().getNodes();
			List<DijkstraVertex> nodes = new ArrayList<>();
			List<DijkstraEdge> edges = new ArrayList<>();
			// Initiate a graph to apply the dijkstra algorithm on
			for (Node n : initialNodes) {
				nodes.add(new DijkstraVertex(n.value().toString(), n.value().toString()));
			}
			Integer aux = 0;
			for(Edge e : initialEdges){
				edges.add(new DijkstraEdge(aux.toString(),nodes.get((int)e.source().value()-1),nodes.get((int)e.destination().value()-1) , edgeToWeight(e.data())));
				aux++;
			}
			DijkstraGraph graph = new DijkstraGraph(nodes, edges);
			DijkstraAlgorithm algorithm = new DijkstraAlgorithm(graph);

			algorithm.execute(nodes.get(source-1));
			return algorithm.getDistances().get(nodes.get(destination-1));
		}
		private int getDetectiveDistance(ScotlandYardView view, int location){
			int sum = 0;
			for(Colour detective : view.getPlayers()){
				if(detective.isDetective()) {
					int distance = DijkstraDistance(view, location, view.getPlayerLocation(detective));
					// If a detective is on top of our next move, completely disregard this move
					if (distance == 0)
						return -10000;
					sum+=distance;
				}
			}
			return sum;
		}
		// gets # of available moves from a location. 0 means that location's stuck
		private int getNumberOfMoves(ScotlandYardView view, int location){
			int sum = 0;
			for(Edge e : view.getGraph().getEdgesFrom(new Node<>(location))) {
				boolean occupied = false;
				for(Colour detective : view.getPlayers()){
					if(detective.isDetective())
						if(view.getPlayerLocation(detective) == (int)e.destination().value()) occupied = true;
				}
				if(!occupied) sum++;
			}
			return sum;
		}
		private final Random random = new Random();
		private void setView(ScotlandYardView view){
			globalView = view;
		}
		@Override
		public void makeMove(ScotlandYardView view, int location, Set<Move> moves,
				Consumer<Move> callback) {
			// Calculate scores for all possible next moves Mr.X can make
			// Pick the best one
			// Add distance to detectives to score
			globalView = view;
			for(Move move : moves)
				move.visit(this);
			System.out.println(bestMove);
			callback.accept(bestMove);

		}
		@Override
		public void visit(TicketMove t){
			if(!t.ticket().equals(Ticket.Secret))
				if(getDetectiveDistance(globalView, t.destination())+15*getNumberOfMoves(globalView, t.destination()) > maxScore){
					maxScore = getDetectiveDistance(globalView, t.destination())+15*getNumberOfMoves(globalView, t.destination());
					bestMove = t;
				}
		}
		@Override
		public void visit(DoubleMove d){
			// -60 because using a doubleMove ticket is not optimal
			if(getDetectiveDistance(globalView, d.finalDestination())+15*getNumberOfMoves(globalView, d.finalDestination()) - 60 > maxScore){
				maxScore = getDetectiveDistance(globalView, d.finalDestination())+15*getNumberOfMoves(globalView, d.finalDestination())-60;
				bestMove = d;
			}
		}
	}
}

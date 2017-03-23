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

	static int maxScore;
	static int detectiveMaxScore;
	static ScotlandYardView globalView;
	static Move bestMove;
	static List<DijkstraVertex> nodes = new ArrayList<>();
	static List<DijkstraEdge> edges = new ArrayList<>();
	static DijkstraGraph graph;
	static DijkstraAlgorithm dijkstraAlgorithm;
	static int potentialLocation;
	static int deficit;
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
		private final int DijkstraDistance (ScotlandYardView view, int source, int destination){
			dijkstraAlgorithm.execute(nodes.get(source-1));
			return dijkstraAlgorithm.getDistances().get(nodes.get(destination-1));
		}
		private final int getNextDetectiveMove(ScotlandYardView view, int source, int destination){
			dijkstraAlgorithm.execute(nodes.get(source-1));
			return Integer.parseInt(dijkstraAlgorithm.getPath(nodes.get(destination-1)).get(1).toString());
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
		private int getSpecificDetectiveDistance(ScotlandYardView view, int location, Colour detective){
			return DijkstraDistance(view, location, view.getPlayerLocation(detective));
		}
		// gets # of available moves from a location. 0 means that location's stuck
		private int getNumberOfMoves(ScotlandYardView view, int location){
			int sum = 0;
			List<Integer> visited = new ArrayList<>();
			for(Edge e : view.getGraph().getEdgesFrom(new Node<>(location))) {
				boolean occupied = false;
				for(Colour detective : view.getPlayers()){
					if(detective.isDetective())
						if(view.getPlayerLocation(detective) == (int)e.destination().value()) occupied = true;
				}
				if(!occupied){
						if(!visited.contains(e.destination().value())) {
							visited.add((int) e.destination().value());
							sum++;
						}
				}
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
			maxScore = Integer.MIN_VALUE;
			detectiveMaxScore = Integer.MAX_VALUE;
			// Calculate scores for all possible next moves Mr.X can make
			// Pick the best one
			// Add distance to detectives to score
			globalView = view;
			System.out.println("MR.X is at location : " + location);
			// For each of Mr.X's potential moves, calculate the optimal detective play
			// Then choose the Mr.X move which has the least powerful detective play
			initiateDijkstra(view);
			for(Move move : moves) {
				System.out.println("Evaluating move "+ move + "...");
				deficit = 0;
				move.visit(this);
				int currentDistanceSum = deficit;
				for(Colour player : view.getPlayers()){
					if(player.isDetective()) {
						int toEvaluate = DijkstraDistance(view, getNextDetectiveMove(view, view.getPlayerLocation(player), potentialLocation), potentialLocation);
						if (toEvaluate == 10 || toEvaluate == 15 || toEvaluate == 30)
							// If detective can catch within one move, detective has a very good move
							toEvaluate -= 300;
						currentDistanceSum += toEvaluate;
					}
				}
				System.out.println("For move " + move + ", detectives have " + currentDistanceSum + "points");
				if(currentDistanceSum < detectiveMaxScore){
					System.out.println("CURRENT DETECTIVE MOVE HAS SCORE "+currentDistanceSum+" BEST DETECTIVE MOVE SO FAR HAS SCORE "+detectiveMaxScore+"BEST MR.X MOVE SO FAR HAS SCORE "+maxScore);
					detectiveMaxScore = currentDistanceSum;
					if(detectiveMaxScore > maxScore){
						maxScore = detectiveMaxScore;
						bestMove = move;
					}
				}

			}
			System.out.println("Selected move : " + bestMove + "with a detective score of"+ maxScore);
			callback.accept(bestMove);
		}
		@Override
		public void visit(TicketMove t){
			if(t.ticket().equals(Ticket.Secret)) deficit-=24;
			potentialLocation = t.destination();
	/*		int currentScore = getDetectiveDistance(globalView, t.destination())+15*getNumberOfMoves(globalView, t.destination());
			if(t.ticket().equals(Ticket.Secret)) currentScore-=24;
			//System.out.println("Evaluating move " + t + "with score " + currentScore);
			if(currentScore > maxScore){
				maxScore =currentScore;
				bestMove = t;
			}
			*/
		}
		@Override
		public void visit(DoubleMove d){
			deficit-=60;
			if(d.firstMove().equals(Ticket.Secret)) deficit-=24;
			if(d.secondMove().equals(Ticket.Secret)) deficit-=24;
			potentialLocation = d.finalDestination();
			/*// -60 because using a doubleMove ticket is not optimal
			int currentScore = getDetectiveDistance(globalView, d.finalDestination())+15*getNumberOfMoves(globalView, d.finalDestination()) - 60;
			// -24 for each secret move
			if(d.firstMove().ticket().equals(Ticket.Secret)) currentScore-=24;
			if(d.secondMove().ticket().equals(Ticket.Secret)) currentScore-=24;
			//System.out.println("Evaluating move " + d + "with score " + currentScore);
			if( currentScore> maxScore){
				maxScore = currentScore;
				bestMove = d;
			}*/
		}
	}
}

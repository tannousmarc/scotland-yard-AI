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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

// TODO name the AI
@ManagedAI("MrX.AI")

public class MyAI implements PlayerFactory {
	// TODO : Singleton, Flyweight, Recursive Minimax (Mini calls Max, Max calls Mini, stop function), Pruning
	// TODO : Documentation (javadoc?)
	static int currentScore;
	static int mrXMaxScore;
	static int detectiveMaxScore;
	static ScotlandYardView globalView;
	static Move bestMove;
	private static List<DijkstraVertex> nodes = new ArrayList<>();
	private static List<DijkstraEdge> edges = new ArrayList<>();
	static DijkstraGraph graph;
	static DijkstraAlgorithm dijkstraAlgorithm;
	static int potentialLocation;
	static int deficit;
	static List<Ticket> ticketsUsed = new ArrayList<>();
	static long startTime ;
	static long estimatedTime;
	static int usedSecrets;
	// TODO create a new player here
	/*@Override
	public DijkstraGraph<Integer, Transport> DijkstraGraph;
	*/
	public Player createPlayer(Colour colour) {
		return new MyPlayer();
	}

	// TODO A sample player that selects a random move
	private static class MyPlayer implements Player, MoveVisitor {

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
		private int getNextDetectiveMove(int source, int destination){
			if(source==destination) return source;
			dijkstraAlgorithm.execute(nodes.get(source-1));
			return Integer.parseInt(dijkstraAlgorithm.getPath(nodes.get(destination-1)).get(1).toString());
		}
		private int getDetectiveDistance(ScotlandYardView view, int location){
			int sum = 0;

			for(Colour detective : view.getPlayers()){
				if(detective.isDetective()) {
					int distance = DijkstraDistance(location, view.getPlayerLocation(detective));
					// If a detective is on top of our next move, completely disregard this move
					if (distance == 0)
						return -15000;
					sum+=distance;
				}
			}
			return sum;
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
						if(!visited.contains((Integer)e.destination().value())) {
							visited.add((int) e.destination().value());
							sum++;
						}
				}
			}
			return sum;
		}

		private int mrxScore(ScotlandYardView view, int deficit, int usedSecrets){
			int potentialMoves = getNumberOfMoves(view, view.getPlayerLocation(Colour.Black));
			int detectiveDistances = getDetectiveDistance(view, view.getPlayerLocation(Colour.Black));
			//System.out.println(detectiveDistances);


			if(detectiveDistances<150 && usedSecrets > 0) {
				//System.out.println("acum");
				return 2*potentialMoves*potentialMoves + detectiveDistances + 700;
			}

			//System.out.println("Best move : "+bestMove+"Mr.X Position : "+view.getPlayerLocation(Colour.Black)+"Move score : "+potentialMoves+ " Distance score: "+detectiveDistances+" Deficit: "+deficit);
			return 2*potentialMoves*potentialMoves + detectiveDistances + deficit;
		}
		private int detectiveScore(ScotlandYardView view){
			int sum=0;
			for(Colour player : view.getPlayers()) {
				if(player.isDetective()) {
					//System.out.println("aici");
					int toEvaluate = DijkstraDistance(view.getPlayerLocation(player), potentialLocation);
					//System.out.println("Acolo");
					if (toEvaluate == 10 || toEvaluate == 15 || toEvaluate == 30)
						// If detective can catch within one move, detective has a very good move
						toEvaluate -= 10000;
					sum += toEvaluate;
				}
			}
			return sum;
		}
		private int minimax(boolean isMrXTurn, int depth, ScotlandYardView view, Set<Move> moves, int alpha, int beta, int deficitTotal) {
			// Breaking condition of our recursive function

			if(System.nanoTime()-startTime>55000000000L) return 0;
			if (depth <= 0) {
				//.out.println("Alpha : "+alpha+"Beta: "+beta);
				return mrxScore(view, deficitTotal, usedSecrets);
			}
			else {
				if (isMrXTurn) {
					estimatedTime = System.nanoTime() - startTime;


					for (Move move : moves) {
						deficit = 0;
						usedSecrets = 0;
						ticketsUsed.clear();
						move.visit(this);
						NewScotlandYardView newView = new NewScotlandYardView(view);
						newView.setPlayerLocation(Colour.Black, potentialLocation);
						for(Ticket t: ticketsUsed)
							newView.removeTicket(Colour.Black, t);
						if(estimatedTime>55000000000L)
							return 0;
						currentScore = minimax(false, depth -1, newView, newView.validMoves(Colour.Black), alpha, beta, deficit+deficitTotal);
						//System.out.println("We have propagated upwards a score of "+currentScore+" alpha is "+alpha+ ", beta is:" + beta +" move is "+move);
						if (currentScore > alpha) {

							alpha = currentScore;
							bestMove = move;
						}
						// Beta cut-off
						if(beta<=alpha) break;
						//System.out.println("Alpha : "+alpha+"Depth : "+depth+"Current score : "+currentScore);
					}

					return alpha;
				}
				// Detective turn TODO : Write in report about how good this pruning is
				else {
					estimatedTime = System.nanoTime() - startTime;
					NewScotlandYardView newView = new NewScotlandYardView(view);
					for(Colour player : view.getPlayers()){
						if(player.isDetective()){
							int nextMove= getNextDetectiveMove(view.getPlayerLocation(player), potentialLocation);

							//System.out.println("Ceva e futut : " + nextMove+"numarul detectivului: "+player+"potentialLocation: "+potentialLocation);
							newView.setPlayerLocation(player, nextMove);
							switch(nextMove){
								case 10 : {
									newView.removeTicket(player, Ticket.Taxi);
									newView.addTicket(Colour.Black, Ticket.Taxi);
									break;
								}
								case 15 : {
									newView.removeTicket(player, Ticket.Bus);
									newView.addTicket(Colour.Black, Ticket.Bus);
									break;
								}
								case 30 : {
									newView.removeTicket(player, Ticket.Underground);
									newView.addTicket(Colour.Black, Ticket.Underground);
									break;
								}
							}

						}
					}
					//System.out.println("Valid moves of Mr.X : " + newView.validMoves(Colour.Black));
					int toEvaluate = detectiveScore(newView);
					if(toEvaluate<beta) beta = toEvaluate;
					if(estimatedTime>55000000000L)
						return 0;
					System.out.println("Alpha : " + alpha + ", beta : " + beta);
					currentScore = minimax(true, depth -1, newView, newView.validMoves(Colour.Black), alpha, beta, deficitTotal);

					// No Alpha cut-off because we have already pruned moves by directly picking the best one

					return currentScore;
				}
			}
		}
		@Override
		public void makeMove(ScotlandYardView view, int location, Set<Move> moves,
				Consumer<Move> callback) {
			initiateDijkstra(view);
			currentScore = 0;
			startTime =  System.nanoTime();
			usedSecrets = 0;
			minimax(true, 3, view, moves, Integer.MIN_VALUE, Integer.MAX_VALUE,0);
			// Calculate scores for all possible next moves Mr.X can make
			// Pick the best one
			// Add distance to detectives to score
			globalView = view;


			// For each of Mr.X's potential moves, calculate the optimal detective play
			// Then choose the Mr.X move which has the least powerful detective play

			 //System.out.println("Selected move : " + bestMove);

			callback.accept(bestMove);
		}
		@Override
		public void visit(TicketMove t){
			ticketsUsed.add(t.ticket());
			if(t.ticket().equals(Ticket.Secret)) {deficit-=300; usedSecrets++;}
			potentialLocation = t.destination();
		}

		@Override
		public void visit(DoubleMove d){
			ticketsUsed.add(Ticket.Double);
			ticketsUsed.add(d.firstMove().ticket());
			ticketsUsed.add(d.secondMove().ticket());
			deficit-=150;

			if(d.firstMove().equals(Ticket.Secret)) {deficit -= 300; usedSecrets++;}
			if(d.secondMove().equals(Ticket.Secret)) {deficit-= 300; usedSecrets++;}
			potentialLocation = d.finalDestination();

		}
	}
}

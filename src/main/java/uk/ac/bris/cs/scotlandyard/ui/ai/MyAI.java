package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.scotlandyard.ui.ai.Dijkstra;
import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.scotlandyard.ai.ManagedAI;
import uk.ac.bris.cs.scotlandyard.ai.PlayerFactory;
import uk.ac.bris.cs.scotlandyard.model.*;

import java.util.*;
import java.util.function.Consumer;

// TODO name the AI
@ManagedAI("Elodia")

public class MyAI implements PlayerFactory {



	// TODO create a new player here
	/*@Override
	public Graph<Integer, Transport> Graph;
	*/
	public Player createPlayer(Colour colour) {
		return new MyPlayer();
	}

	// TODO A sample player that selects a random move
	private static class MyPlayer implements Player {

		private final int DijkstraDistance (ScotlandYardView view, int source, int destination){
			// Collection<Edge<Integer, Transport>> Edges = view.getGraph().getEdgesFrom(view.getGraph().getNode(source));
			// PUTEM COPIA EXACT ALGORITMUL DE PE SITE SI FACEM SUS WEIGHT = SUMA AIA DE DISTANCETUPLE SI MERGE FARA SCHIMBARI
			// TODO : DE FACUT TEST PT DIJKSTRA
			List<Dijkstra.Vertex> nodes = new ArrayList<>();
			List<Dijkstra.Edge> edges = new ArrayList<>();
			for (int i = 0; i < 4; i++) {
				nodes.add(new Dijkstra().new Vertex("Node_" + i, "Node_" + i));
			}
			System.out.println("Created nodes : " + nodes);
			edges.add(new Dijkstra().new Edge("Edge1", nodes.get(0), nodes.get(3), new Dijkstra().EdgeToWeight("Underground")));
			edges.add(new Dijkstra().new Edge("Edge2", nodes.get(0), nodes.get(1), new Dijkstra().EdgeToWeight("Taxi")));
			edges.add(new Dijkstra().new Edge("Edge3", nodes.get(0), nodes.get(2), new Dijkstra().EdgeToWeight("Taxi")));
			edges.add(new Dijkstra().new Edge("Edge1", nodes.get(1), nodes.get(3), new Dijkstra().EdgeToWeight("Bus")));
			edges.add(new Dijkstra().new Edge("Edge1", nodes.get(2), nodes.get(3), new Dijkstra().EdgeToWeight("Taxi")));
			Dijkstra.Graph graph = new Dijkstra().new Graph(nodes, edges);
			Dijkstra.DijkstraAlgorithm dijkstra = new Dijkstra().new DijkstraAlgorithm(graph);
			dijkstra.execute(nodes.get(0));
			LinkedList<Dijkstra.Vertex> path = dijkstra.getPath(nodes.get(3));
			for (Dijkstra.Vertex vertex : path) {
				System.out.println("Drum : " + 	vertex);
			}
			return 0;
		}
		private final Random random = new Random();

		@Override
		public void makeMove(ScotlandYardView view, int location, Set<Move> moves,
				Consumer<Move> callback) {
			// TODO do something interesting here; find the best move
			// picks a random move
			DijkstraDistance(view, location, 100);
			callback.accept(new ArrayList<>(moves).get(random.nextInt(moves.size())));

		}
	}
}

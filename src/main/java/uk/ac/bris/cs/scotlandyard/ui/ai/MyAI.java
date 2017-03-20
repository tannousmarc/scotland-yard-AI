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



	// TODO create a new player here
	/*@Override
	public DijkstraGraph<Integer, Transport> DijkstraGraph;
	*/
	public Player createPlayer(Colour colour) {
		return new MyPlayer();
	}

	// TODO A sample player that selects a random move
	private static class MyPlayer implements Player {

		public int edgeToWeight(Object e){
			switch (e.toString()){
				case "Bus": return 15;
				case "Taxi": return 10;
				case "Underground": return 30;
			}
			return 100000;
		}
		private final int DijkstraDistance (ScotlandYardView view, int source, int destination){
			Collection<Edge<Integer, Transport>> initialEdges = view.getGraph().getEdges();
			List<Node<Integer>> initialNodes = view.getGraph().getNodes();
			//System.out.println(initialEdges);
			// PUTEM COPIA EXACT ALGORITMUL DE PE SITE SI FACEM SUS WEIGHT = SUMA AIA DE DISTANCETUPLE SI MERGE FARA SCHIMBARI
			// TODO : DE FACUT TEST PT DIJKSTRA
			List<DijkstraVertex> nodes = new ArrayList<>();
			List<DijkstraEdge> edges = new ArrayList<>();
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
			algorithm.execute(nodes.get(0));
			LinkedList<DijkstraVertex> path = algorithm.getPath(nodes.get(174));
			if(path!=null)
				for (DijkstraVertex v : path) {
					System.out.println("Drum : " + 	v);
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

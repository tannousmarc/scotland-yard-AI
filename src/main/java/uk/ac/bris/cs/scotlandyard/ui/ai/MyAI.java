package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.scotlandyard.ai.ManagedAI;
import uk.ac.bris.cs.scotlandyard.ai.PlayerFactory;
import uk.ac.bris.cs.scotlandyard.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

// TODO name the AI
@ManagedAI("Elodia")

public class MyAI implements PlayerFactory {


	// TODO create a new player here
	@Override
	public Graph<Integer, Transport> graph;
	public Player createPlayer(Colour colour) {
		return new MyPlayer();
	}

	// TODO A sample player that selects a random move
	private static class MyPlayer implements Player {


		private final int DijkstraDistance (ScotlandYardView view, int source, int destination){
			Collection<Edge<Integer, Transport>> edges = view.getGraph().getEdgesFrom(view.getGraph().getNode(source));
		}
		private final Random random = new Random();

		@Override
		public void makeMove(ScotlandYardView view, int location, Set<Move> moves,
				Consumer<Move> callback) {
			// TODO do something interesting here; find the best move
			// picks a random move
			callback.accept(new ArrayList<>(moves).get(random.nextInt(moves.size())));

		}
	}
}

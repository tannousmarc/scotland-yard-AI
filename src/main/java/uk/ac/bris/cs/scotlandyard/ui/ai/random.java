package uk.ac.bris.cs.scotlandyard.ui.ai;

import java.util.*;
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

// TODO name the AI
@ManagedAI("randomAI")
public class random implements PlayerFactory {

    // TODO create a new player here
    @Override
    public Player createPlayer(Colour colour) {
        return new MyPlayer();
    }

    // TODO A sample player that selects a random move
    private static class MyPlayer implements Player {


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

package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.scotlandyard.ai.ManagedAI;
import uk.ac.bris.cs.scotlandyard.ai.PlayerFactory;
import uk.ac.bris.cs.scotlandyard.model.Colour;
import uk.ac.bris.cs.scotlandyard.model.Move;
import uk.ac.bris.cs.scotlandyard.model.Player;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYardView;

import java.util.*;
import java.util.function.Consumer;

// TODO name the AI
@ManagedAI("Elodia")

public class MyAI implements PlayerFactory {


	// TODO create a new player here
	/*@Override
	public Graph<Integer, Transport> graph;
	*/
	public Player createPlayer(Colour colour) {
		return new MyPlayer();
	}

	// TODO A sample player that selects a random move
	private static class MyPlayer implements Player {

		class distanceTuple{
			public int taxiTickets;
			public int busTickets;
			public int undergroundTickets;
			// for boat rides
			public int secretTickets;
			public distanceTuple(int taxiTickets, int busTickets, int undergroundTickets, int secretTickets){
				this.taxiTickets = taxiTickets;
				this.busTickets = busTickets;
				this.undergroundTickets = undergroundTickets;
				this.secretTickets = secretTickets;
			}
			public distanceTuple(){
				this.taxiTickets = 0;
				this.busTickets = 0;
				this.undergroundTickets = 0;
				this.secretTickets = 0;
			}
			public distanceTuple add( distanceTuple b){
				return new distanceTuple(this.getTaxiTickets()+b.getTaxiTickets(),
						this.getBusTickets()+b.getBusTickets(),
						this.getUndergroundTickets()+b.getUndergroundTickets(),
						this.getSecretTickets()+b.getSecretTickets());
			}
			// Taxi = 10 Bus = 15 Underground = 30 Secret = 24
			public int value(){
				return 10*this.getTaxiTickets()+15*this.getBusTickets()+30*this.getUndergroundTickets()+24*this.getSecretTickets();
			}
			public boolean isGreaterThan(distanceTuple b){
				return this.value() > b.value();
			}
			public boolean equals(distanceTuple b){
				return this.value() == b.value();
			}
			public boolean isLowerThan(distanceTuple b){
				return this.value() < b.value();
			}

			public int getTaxiTickets(){return taxiTickets;}
			public int getBusTickets(){return busTickets;}
			public int getUndergroundTickets(){return undergroundTickets;}
			public int getSecretTickets(){return secretTickets;}
		}
		public class dijkstraVertex {
			final private String id;
			final private String name;


			public dijkstraVertex(String id, String name) {
				this.id = id;
				this.name = name;
			}
			public String getId() {
				return id;
			}

			public String getName() {
				return name;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + ((id == null) ? 0 : id.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				dijkstraVertex other = (dijkstraVertex) obj;
				if (id == null) {
					if (other.id != null)
						return false;
				} else if (!id.equals(other.id))
					return false;
				return true;
			}

			@Override
			public String toString() {
				return name;
			}

		}
		public class dijkstraEdge  {
			private final String id;
			private final dijkstraVertex source;
			private final dijkstraVertex destination;
			private final distanceTuple weight;

			public dijkstraEdge(String id, dijkstraVertex source, dijkstraVertex destination, distanceTuple weight) {
				this.id = id;
				this.source = source;
				this.destination = destination;
				this.weight = weight;
			}

			public String getId() {
				return id;
			}
			public dijkstraVertex getDestination() {
				return destination;
			}

			public dijkstraVertex getSource() {
				return source;
			}
			public distanceTuple getWeight() {
				return weight;
			}

			@Override
			public String toString() {
				return source + " " + destination;
			}


		}
		public class dijkstraGraph {
			private final List<dijkstraVertex> vertexes;
			private final List<dijkstraEdge> edges;

			public dijkstraGraph(List<dijkstraVertex> vertexes, List<dijkstraEdge> edges) {
				this.vertexes = vertexes;
				this.edges = edges;
			}

			public List<dijkstraVertex> getVertexes() {
				return vertexes;
			}

			public List<dijkstraEdge> getEdges() {
				return edges;
			}

		}
		public class DijkstraAlgorithm {

			private final List<dijkstraVertex> nodes;
			private final List<dijkstraEdge> edges;
			private Set<dijkstraVertex> settledNodes;
			private Set<dijkstraVertex> unSettledNodes;
			private Map<dijkstraVertex, dijkstraVertex> predecessors;
			private Map<dijkstraVertex, distanceTuple> distance;

			public DijkstraAlgorithm(dijkstraGraph graph) {
				// create a copy of the array so that we can operate on this array
				this.nodes = new ArrayList<dijkstraVertex>(graph.getVertexes());
				this.edges = new ArrayList<dijkstraEdge>(graph.getEdges());
			}

			public void execute(dijkstraVertex source) {
				settledNodes = new HashSet<dijkstraVertex>();
				unSettledNodes = new HashSet<dijkstraVertex>();
				HashMap<dijkstraVertex,distanceTuple> distance = new HashMap<dijkstraVertex, distanceTuple>();
				predecessors = new HashMap<dijkstraVertex, dijkstraVertex>();
				distance.put(source, new distanceTuple());
				unSettledNodes.add(source);
				while (unSettledNodes.size() > 0) {
					dijkstraVertex node = getMinimum(unSettledNodes);
					settledNodes.add(node);
					unSettledNodes.remove(node);
					findMinimalDistances(node);
				}
			}
			private void findMinimalDistances(dijkstraVertex node) {
				List<dijkstraVertex> adjacentNodes = getNeighbors(node);
				for (dijkstraVertex target : adjacentNodes) {
					if (getShortestDistance(target).isGreaterThan(getShortestDistance(node).add(getDistance(node,target)))){
						distance.put(target, getShortestDistance(node).add(getDistance(node, target)));
						predecessors.put(target, node);
						unSettledNodes.add(target);
					}
				}

			}

			private distanceTuple getDistance(dijkstraVertex node, dijkstraVertex target) {
				for (dijkstraEdge edge : edges) {
					if (edge.getSource().equals(node)
							&& edge.getDestination().equals(target)) {
						return edge.getWeight();
					}
				}
				throw new RuntimeException("Should not happen");
			}

			private List<dijkstraVertex> getNeighbors(dijkstraVertex node) {
				List<dijkstraVertex> neighbors = new ArrayList<dijkstraVertex>();
				for (dijkstraEdge edge : edges) {
					if (edge.getSource().equals(node)
							&& !isSettled(edge.getDestination())) {
						neighbors.add(edge.getDestination());
					}
				}
				return neighbors;
			}
			private dijkstraVertex getMinimum(Set<dijkstraVertex> vertexes) {
				dijkstraVertex minimum = null;
				for (dijkstraVertex vertex : vertexes) {
					if (minimum == null) {
						minimum = vertex;
					} else {
						if (getShortestDistance(vertex).isLowerThan(getShortestDistance(minimum))) {
							minimum = vertex;
						}
					}
				}
				return minimum;
			}

			private boolean isSettled(dijkstraVertex vertex) {
				return settledNodes.contains(vertex);
			}

			private distanceTuple getShortestDistance(dijkstraVertex destination) {
				distanceTuple d = distance.get(destination);
				if (d.equals(new distanceTuple())) {
					return new distanceTuple(1000,1000,1000,1000);
				} else {
					return d;
				}
			}

			/*
             * This method returns the path from the source to the selected target and
             * NULL if no path exists
             */
			public LinkedList<dijkstraVertex> getPath(dijkstraVertex target) {
				LinkedList<dijkstraVertex> path = new LinkedList<dijkstraVertex>();
				dijkstraVertex step = target;
				// check if a path exists
				if (predecessors.get(step) == null) {
					return null;
				}
				path.add(step);
				while (predecessors.get(step) != null) {
					step = predecessors.get(step);
					path.add(step);
				}
				// Put it into the correct order
				Collections.reverse(path);
				return path;
			}

		}
		private final int DijkstraDistance (ScotlandYardView view, int source, int destination){
			// Noi n-ar trebui sa facem pe graf cu edge-uri si node-uri
			// ci pe graf cu move-uri si node-uri. ???
			//Collection<Edge<Integer, Transport>> edges = view.getGraph().getEdgesFrom(view.getGraph().getNode(source));
			//System.out.println("Avem muchiile : " + edges);
			// TODO : DE FACUT TEST PT DIJKSTRA
			List<dijkstraVertex> nodes = new ArrayList<dijkstraVertex>();
			List<dijkstraEdge> edges = new ArrayList<dijkstraEdge>();
					dijkstraVertex location = new dijkstraVertex("Node_01" , "Node_01");
					nodes.add(location);
					//edges.add()
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

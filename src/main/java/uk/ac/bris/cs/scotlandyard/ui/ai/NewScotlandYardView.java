package uk.ac.bris.cs.scotlandyard.ui.ai;

import uk.ac.bris.cs.gamekit.graph.Edge;
import uk.ac.bris.cs.gamekit.graph.Graph;
import uk.ac.bris.cs.gamekit.graph.UndirectedGraph;
import uk.ac.bris.cs.scotlandyard.model.*;

import java.util.*;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;



public class NewScotlandYardView implements ScotlandYardView {

    private static class Player {

        private int location;
        private final Map<Ticket, Integer> tickets;

        Player(int location,
               Map<Ticket, Integer> tickets) {
            this.location = location;
            this.tickets = tickets;
        }
    }

    private final List<Colour> colours;
    private final Map<Colour, Player> players;
    private final Set<Colour> winning;
    private final boolean gameOver;
    private final Colour currentPlayer;
    private final int currentRound;
    private final boolean revealRound;
    private final List<Boolean> rounds;
    private final Graph<Integer, Transport> graph;
    public List<ScotlandYardPlayer> scotlandYardPlayers = new ArrayList<>();

    public NewScotlandYardView(ScotlandYardView view) {
        colours = view.getPlayers();
        players = colours
                .stream()
                .collect(toMap(identity(), p -> new Player(
                                view.getPlayerLocation(p),
                                Stream.of(Ticket.values()).collect(
                                        toMap(identity(), t -> view.getPlayerTickets(p, t)))),
                        (l, r) -> {
                            throw new AssertionError();
                        }, LinkedHashMap::new));

        winning = new HashSet<>(view.getWinningPlayers());
        gameOver = view.isGameOver();
        currentRound = view.getCurrentRound();
        revealRound = true;
        currentPlayer = view.getCurrentPlayer();
        rounds = new ArrayList<>(view.getRounds());
        graph = new UndirectedGraph<>(view.getGraph());

        for(Colour c : colours){
            scotlandYardPlayers.add(new ScotlandYardPlayer(null,c,getPlayerLocation(c),players.get(c).tickets));
        }
    }
    public void setPlayerLocation(Colour colour, int newLocation){ players.get(colour).location = newLocation; }
    public void removeTicket(Colour colour, Ticket ticket){
        int ticketCount = players.get(colour).tickets.get(ticket);
        players.get(colour).tickets.put(ticket, ticketCount-1);}
    public void addTicket(Colour colour, Ticket ticket){
        int ticketCount = players.get(colour).tickets.get(ticket);
        players.get(colour).tickets.put(ticket, ticketCount+1);
    }
    @Override
    public List<Colour> getPlayers() {
        return colours;
    }

    @Override
    public Set<Colour> getWinningPlayers() {
        return winning;
    }

    @Override
    public int getPlayerLocation(Colour colour) {
        return players.get(colour).location;
    }

    @Override
    public int getPlayerTickets(Colour colour, Ticket ticket) {
        return players.get(colour).tickets.get(ticket);
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    @Override
    public Colour getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public int getCurrentRound() {
        return currentRound;
    }

    @Override
    public boolean isRevealRound() {
        return revealRound;
    }

    @Override
    public List<Boolean> getRounds() {
        return rounds;
    }

    @Override
    public Graph<Integer, Transport> getGraph() {
        return graph;
    }


    // Generate various tickets
    private Move generateTicket(Edge<Integer,Transport> edge){
        return new TicketMove(getCurrentPlayer(), Ticket.fromTransport(edge.data()), edge.destination().value());
    }
    private Move generateSecretTicket(Edge<Integer, Transport> edge){
        return new TicketMove(getCurrentPlayer(), Ticket.Secret, edge.destination().value());
    }
    private Move generateDoubleTicket(Edge<Integer, Transport> edge, Edge<Integer, Transport> doubleEdge){
        return new DoubleMove(getCurrentPlayer(), Ticket.fromTransport(edge.data()), edge.destination().value(),
                Ticket.fromTransport(doubleEdge.data()), doubleEdge.destination().value());
    }
    // First parameter is list of moves to add onto, second parameter is current player
    private void generateBasicMoves(Set<Move> s, ScotlandYardPlayer p){
        for(Edge<Integer, Transport> edge : getGraph().getEdgesFrom(getGraph().getNode(p.location()))){
            // Add this ticket if a detective is not at the location and player has this ticket
            boolean positionOccupied = false;

            for (ScotlandYardPlayer player : scotlandYardPlayers)
                if (player.isDetective() && player.location() == edge.destination().value())
                    positionOccupied = true;

            if(!positionOccupied && p.hasTickets(Ticket.fromTransport(edge.data())))
                s.add(generateTicket(edge));
        }
    }
    // First parameter is list of moves to add onto, second parameter is current player
    private void generateSecretMoves(Set<Move> s, ScotlandYardPlayer p){
        for(Edge<Integer, Transport> edge : getGraph().getEdgesFrom(getGraph().getNode(p.location()))) {
            // Add this ticket if a detective is not at the location and Mr.X has secret ticket
            boolean positionOccupied = false;
            for (ScotlandYardPlayer player : scotlandYardPlayers)
                if (player.colour().isDetective() && player.location() == edge.destination().value())
                    positionOccupied = true;

            if (!positionOccupied && p.hasTickets(Ticket.Secret))
                s.add(generateSecretTicket(edge));
        }
    }
    // First parameter is list of moves to add onto, second parameter is current player
    private void generateDoubleMoves(Set<Move> s, ScotlandYardPlayer p){
        for(Edge<Integer, Transport> edge : getGraph().getEdgesFrom(getGraph().getNode(p.location()))) {
            // Procure edges than can be reached from this new edge
            Collection<Edge<Integer, Transport>> doubleEdges = this.graph.getEdgesFrom(graph.getNode(edge.destination().value()));
            // Check if detective is at the first location of the double move
            boolean intermediaryPositionOccupied = false;
            for (ScotlandYardPlayer player : scotlandYardPlayers)
                if (player.colour().isDetective() && player.location() == edge.destination().value())
                    intermediaryPositionOccupied = true;
            // Check if Mr.X has a 2x ticket and the intermediary position is not occupied
            if (p.hasTickets(Ticket.Double) && !intermediaryPositionOccupied) {
                for (Edge<Integer, Transport> doubleEdge : doubleEdges) {
                    // Check if destination is occupied
                    boolean finalPositionOccupied = false;
                    for (ScotlandYardPlayer player : scotlandYardPlayers) {
                        if (player.colour().isDetective() && player.location() == doubleEdge.destination().value())
                            finalPositionOccupied = true;
                    }
                    if (!finalPositionOccupied) {
                        // Add move : (A and A)
                        if (Ticket.fromTransport(edge.data()).equals(Ticket.fromTransport(doubleEdge.data()))
                                && p.hasTickets(Ticket.fromTransport(doubleEdge.data()), 2))
                            s.add(generateDoubleTicket(edge, doubleEdge));
                            // Add move : (A and B), A!=B
                        else if (!Ticket.fromTransport(edge.data()).equals(Ticket.fromTransport(doubleEdge.data()))
                                && p.hasTickets(Ticket.fromTransport(doubleEdge.data())))
                            s.add(generateDoubleTicket(edge, doubleEdge));

                        // Single/Double Secret moves
                        if (p.hasTickets(Ticket.Secret)) {
                            // Add moves : (Secret and X), (X and Secret)
                            s.add(new DoubleMove(getCurrentPlayer(), Ticket.Secret, edge.destination().value(),
                                    Ticket.fromTransport(doubleEdge.data()), doubleEdge.destination().value()));
                            s.add(new DoubleMove(getCurrentPlayer(), Ticket.fromTransport(edge.data()),
                                    edge.destination().value(), Ticket.Secret, doubleEdge.destination().value()));
                            // Check for two Secret tickets, add (Secret and Secret)
                            if (p.hasTickets(Ticket.Secret, 2))
                                s.add(new DoubleMove(getCurrentPlayer(), Ticket.Secret, edge.destination().value(),
                                        Ticket.Secret, doubleEdge.destination().value()));
                        }
                    }
                }
            }
        }
    }
    // Generates valid moves for Mr.X and the Detectives
    public Set<Move> validMoves(Colour c){
        // Add tickets for all players
        Set<Move> s = new HashSet<>();
        ScotlandYardPlayer p = new ScotlandYardPlayer(null, c, getPlayerLocation(c), players.get(c).tickets);
        // Add ticketMoves to all players
        generateBasicMoves(s, p);
        if(p.isMrX()) {
            // Add secretMoves to mrX
            generateSecretMoves(s, p);
            // If there are enough rounds remaining, add doubleMoves to mrX
            if (rounds.size()>currentRound+2) {
                generateDoubleMoves(s, p);
            }
        }
        // Add passMoves to detectives if they're stuck
        if(p.colour().isDetective() && s.isEmpty()){
            s.add(new PassMove(p.colour()));
        }
        return s;
    }
}

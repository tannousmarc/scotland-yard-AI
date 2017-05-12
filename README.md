# About
Scotland Yard is a board game popular in Europe. First step was implementing the logic in Java, then coming up with AIs for the two sides, detectives and Mr.X

The game is based on graph theory : players can freely move around nodes on weighted edges, have a limited number of tickets, Mr.X's location is periodically revealed.

# Solution
- For graph traversal, we've looked at Dijkstra's algorithm/A star/Lookup table, each one with their pros and cons : our concerns were performance and how well the AI behaves.
- For decision making, a recursive minimax algorithm with variable depth was the obvious choice. The scoring of game states is done by a (carefully weighted?) polynomial function that takes into account distance to opponents, possible escape routes and "dead states". 
- Performance-wise, minimax (going to depths >3) will take a LONG time. In order to speed this up, we're using alpha-beta pruning.

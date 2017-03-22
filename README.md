# Step 1 DONE (40%)
Scoring function for given board state (view, location). Score depends on distance to detectives and available next moves at given location.  

=> Dijkstra's algorithm for shortest path from each detective to location. Weights we're implementing are based on the number of tickets received by each detective at the start of the game.Hence, Taxi costs 10, Bus costs 15, Underground costs 30 and Boat costs 24.

=> Each Node that can be reached from location adds 15 to the score.

# Step 2 DONE (55%)
Look-ahead-one AI that considers all possible moves Mr.X can do given (view,location) and picks the best one for this round. (Note : Best for this round != best move)

=> For all possible moves Mr.X can make, calculate the score of the board. Pick the best scoring move. (DoubleMove reduces the score by 60, SecretMove reduces the score by 24, those numbers are mathematically calculated given the number of tickets Mr.X receives at the start of the game).

# Step 3 (65%)
MiniMax algorithm that chooses a move assuming all opponents play optimally (Look-ahead-one for each detective, then). 

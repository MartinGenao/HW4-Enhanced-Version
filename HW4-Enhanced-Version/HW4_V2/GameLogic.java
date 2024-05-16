import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class GameLogic implements Serializable 
{
    private int gameState;
    private Map<Integer, Map<String, GameResponse>> gameMap;
    private final ReentrantLock lock = new ReentrantLock();
    private int numGuesses;
    private String playerName;
    /**
     * @param name of the player
     */
    public GameLogic(String playerName) 
    {
        this.playerName = playerName;
        initializeGameMap();
        gameState = 0; 
        numGuesses = 0;
    }
    /*
     * Hash Map to traverse through the game based on specific responses
     */
    private void initializeGameMap() 
    {
        gameMap = new HashMap<>();

        // State 0: Introduction
        Map<String, GameResponse> initialState = new HashMap<>();
        initialState.put("start", new GameResponse("Welcome, brave adventurer!\nYou venture into the enchanted forest in search of the Lost Treasure.\nYou come across a mysterious door with a riddle inscribed on it...\n\nI have keys but open no locks,\nI have space but no room,\nYou can enter but can't go inside.\nWhat am I? (type your answer)", 1));
        gameMap.put(0, initialState);

        // State 1: The Mysterious Door
        Map<String, GameResponse> state1 = new HashMap<>();
        state1.put("keyboard", new GameResponse("Correct! The mysterious door swings open...\nYou proceed deeper into the forest and encounter an enchanted lake guarded by a mythical creature.\n\nThe creature challenges you with another riddle...\n\nWhat has cities, but no houses;\nForests, but no trees;\nAnd rivers, but no water? (type your answer)", 2));
        gameMap.put(1, state1);

        // State 2: The Enchanted Lake
        Map<String, GameResponse> state2 = new HashMap<>();
        state2.put("map", new GameResponse("Well done! The creature allows you to pass...\nYou continue your journey and reach the treasure chamber.\n\nIn the treasure chamber, you encounter the final puzzle...\n\nI speak without a mouth and hear without ears. I have no body, but I come alive with the wind. What am I? (type your answer)", 3));
        gameMap.put(2, state2);

        // State 3: The Treasure Chamber
        Map<String, GameResponse> state3 = new HashMap<>();
        state3.put("echo", new GameResponse("Congratulations! You have solved all the challenges and found the Lost Treasure!\nThe adventure comes to an end, and you emerge from the enchanted forest victorious.", -1));
        gameMap.put(3, state3);
    }
    /**
     * @param user's input
     * @return response
     */
    public String processGameInput(Object input) 
    {
        lock.lock();
        try 
        {
            Map<String, GameResponse> currentState = gameMap.get(gameState);
            if (currentState != null && input != null && currentState.containsKey(input.toString().toLowerCase())) {
                GameResponse response = currentState.get(input.toString().toLowerCase());
                gameState = response.getNextState();
                numGuesses++;

                // check if the game has ended
                if (gameState == -1) 
                {
                    return response.getResponse() + "\nYou completed the game in " + numGuesses + " guesses.";
                }

                return response.getResponse();
            }
            numGuesses++;
            return "Invalid input. Please try again.";
        } finally 
        {
            lock.unlock();
        }
    }
    /**
     * Getter method to retrieve the number of guesses made during the game.
     *
     * @return The number of guesses made.
     */
    public int getNumGuesses() 
    {
        return numGuesses;
    }
    /**
     * Getter method to retrieve the player's name associated with the game.
     *
     * @return The player's name.
     */
    public String getPlayerName() 
    {
        return playerName;
    }
    /**
     * Inner class representing a game response associated with a specific game state transition.
     * Each GameResponse contains a response message and the next game state.
     */
    private static class GameResponse implements Serializable 
    {
        private String response;
        private int nextState;
        /**
         * Constructor to create a new GameResponse object.
         *
         * @param response The response message associated with this game state.
         * @param nextState The next game state to transition to after this response.
         */
        public GameResponse(String response, int nextState) 
        {
            this.response = response;
            this.nextState = nextState;
        }
        /**
         * Getter method to retrieve the response message.
         *
         * @return The response message.
         */
        public String getResponse() 
        {
            return response;
        }
        /**
         * Getter method to retrieve the next game state.
         *
         * @return The next game state.
         */
        public int getNextState() 
        {
            return nextState;
        }
    }
}


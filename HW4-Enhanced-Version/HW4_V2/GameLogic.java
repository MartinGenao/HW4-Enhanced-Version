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
     * hash Map to traverse through the game based on specific responses
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
     * @param users input
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
     * @return number of guesses
     */
    public int getNumGuesses() 
    {
        return numGuesses;
    }
    /**
     * @return players name
     */
    public String getPlayerName() 
    {
        return playerName;
    }
    private static class GameResponse implements Serializable 
    {
        private String response;
        private int nextState;
        /**
         * @param response message
         * @param next game state 
         */
        public GameResponse(String response, int nextState) 
        {
            this.response = response;
            this.nextState = nextState;
        }
        public String getResponse() 
        {
            return response;
        }
        public int getNextState() 
        {
            return nextState;
        }
    }
}


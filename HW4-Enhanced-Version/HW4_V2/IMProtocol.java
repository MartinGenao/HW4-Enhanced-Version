import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;

public class IMProtocol implements Serializable 
{
    // constants for different states of the protocol
    private static final int WAITING = 0;
    private static final int IN_CONVERSATION = 1;
    private static final int IN_GAME = 2;
    private int state = WAITING;
    // gameLogic instance for managing game
    private transient GameLogic gameLogic;
    // thread safety when accessing shared resources
    private final ReentrantLock lock = new ReentrantLock();
    // SharedLeaderboard instance for updating leaderboard
    private SharedLeaderboard sharedLeaderboard;
    /**
     *
     * @param The SharedLeaderboard instance for updating leaderboard .
     */
    public IMProtocol(SharedLeaderboard sharedLeaderboard) 
    {
        this.sharedLeaderboard = sharedLeaderboard;
    }
    /**
     * @param The input from the client.
     * @param sharedLeaderboard instance for updating leaderboard
     * @return The response sent back to the client.
     */
    public Object processInput(Object input, SharedLeaderboard sharedLeaderboard) 
    {
        lock.lock();
        try {
            String output;

            if (state == WAITING) 
            {
                // initial state
                output = "Connection Established. Please enter your name:";
                state = IN_CONVERSATION;
            } else if (state == IN_CONVERSATION) 
            {
                if (input != null) 
                {
                    // start the game with player name
                    String playerName = input.toString();
                    gameLogic = new GameLogic(playerName);
                    output = "Great! Let's begin the game...\n\n" +
                             "Instructions:\n" +
                             "You'll be presented with a series of prompts and decisions.\n" +
                             "Type your decision and press Enter to proceed.\n" +
                             "Your goal is to find the treasure by making the right choices.\n" +
                             "Type 'Start' to play the game.";
                    state = IN_GAME;
                    output += gameLogic.processGameInput(null);
                } else 
                {
                    // prompt client to enter a name
                    output = "Please enter a valid name.";
                }
            } else if (state == IN_GAME) 
            {
                // process game input and update protocol state
                output = gameLogic.processGameInput(input);
                if (output.startsWith("Congratulations!")) 
                {
                    // completed game, update leaderboard
                    sharedLeaderboard.addEntry(gameLogic.getPlayerName(), gameLogic.getNumGuesses());
                    state = WAITING;
                }
            } else 
            {
                // invalid state check
                output = "Invalid state. Please try again.";
            }

            return output;
        } finally 
        {
            lock.unlock();
        }
    }
}

/*
 * This has been modified to handle the conversation along with game integration.
 * 
 */

public class IMProtocol {
    private static final int WAITING = 0;
    private static final int IN_CONVERSATION = 1;
    private static final int IN_GAME = 2;

    private int state = WAITING;
    private GameLogic gameLogic;
    /*
     * Creates new instance of GameLogic
     * Initializes IMProtocol
     */
    public IMProtocol() {
        gameLogic = new GameLogic();
    }
    /*
     * Prcoess user input based on the conversation state
     * @param The users input
     * @return The response based on current state
     */
    public String processInput(String input) {
        String output;

        if (state == WAITING) {
            output = "Connection Established. Would you like to play a game? (yes/no)";
            state = IN_CONVERSATION;
        } else if (state == IN_CONVERSATION) {
            if (input.equalsIgnoreCase("yes")) {
                output = "Great! Let's begin the game...\n\n" +
                         "Instructions:\n" +
                         "You'll be presented with a series of prompts and decisions.\n" +
                         "Type your decision and press Enter to proceed.\n" +
                         "Your goal is to find the treasure by making the right choices.\n" +
                         "Type 'Start' to play the game.";
                state = IN_GAME;
                output += gameLogic.processGameInput(null);
            } else if (input.equalsIgnoreCase("no")) {
                output = "Bye.";
                state = WAITING;
            } else {
                output = "I didn't understand that. Please respond with 'yes' or 'no'.";
            }
        } else if (state == IN_GAME) {
            output = gameLogic.processGameInput(input);
            if (output.startsWith("Congratulations!")) {
                state = WAITING;
            }
        } else {
            output = "Invalid state. Please try again.";
        }

        return output;
    }
}
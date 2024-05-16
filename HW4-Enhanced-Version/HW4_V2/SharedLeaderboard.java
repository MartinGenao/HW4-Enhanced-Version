import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class SharedLeaderboard 
{
    // map to store player names and their number of guesses
    private Map<String, Integer> leaderboard;
    // thread safety when accessing the leaderboard
    private final ReentrantLock lock = new ReentrantLock();

    public SharedLeaderboard() 
    {
        leaderboard = new ConcurrentHashMap<>();
    }
    /**
     * @param name of the player
     * @param number of guesses made
     */
    public void addEntry(String playerName, int numGuesses) 
    {
        lock.lock();
        try 
        {
            leaderboard.put(playerName, numGuesses);
        } finally 
        {
            lock.unlock();
        }
    }
    /**
     * @return list of formatted strings
     */
    public List<String> getLeaderboard() 
    {
        lock.lock();
        try 
        {
            return leaderboard.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(entry -> entry.getKey() + " " + entry.getValue())
                    .collect(Collectors.toList());
        } finally 
        {
            lock.unlock();
        }
    }
}

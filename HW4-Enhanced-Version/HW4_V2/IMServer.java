import java.net.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class IMServer 
{
    private static Map<Socket, ClientHandler> clients = new ConcurrentHashMap<>();
    private static SharedLeaderboard sharedLeaderboard = new SharedLeaderboard();
    public static void main(String[] args) throws IOException 
    {
        if (args.length != 1) 
        {
            System.err.println("Usage: java IMServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) 
        {
            System.out.println("Server started. Listening on port " + portNumber);

            // thread pool for handling clients
            ExecutorService executorService = Executors.newCachedThreadPool();

            while (true) 
            {
                
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // new thread for handling the clients
                ClientHandler clientHandler = new ClientHandler(clientSocket, sharedLeaderboard);
                clients.put(clientSocket, clientHandler);
                executorService.execute(clientHandler);
            }
        } catch (IOException e) 
        {
            System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
    /**
     * handler for client connections.
     */
    private static class ClientHandler implements Runnable 
    {
        private Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private IMProtocol protocol;
        private final ReentrantLock lock = new ReentrantLock();

        public ClientHandler(Socket socket, SharedLeaderboard sharedLeaderboard) 
        {
            this.clientSocket = socket;
            this.protocol = new IMProtocol(sharedLeaderboard);
        }
        /**
         * handles client interaction.
         */
        public void run() {

            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                // initiate conversation with client
                Object outputObject = protocol.processInput(null, sharedLeaderboard);
                out.writeObject(outputObject);

                Object inputObject;
                while ((inputObject = in.readObject()) != null) 
                {
                    System.out.println("Received input from client: " + inputObject);
                    lock.lock();
                    try {
                        if (inputObject.toString().equalsIgnoreCase("leaderboard")) 
                        {
                            outputObject = sharedLeaderboard.getLeaderboard();
                        } else 
                        {
                            outputObject = protocol.processInput(inputObject, sharedLeaderboard);
                        }
                    } finally 
                    {
                        lock.unlock(); 
                    }
                    System.out.println("Server response: " + outputObject);
                    out.writeObject(outputObject);
                }
            } catch (IOException | ClassNotFoundException e) 
            {
                System.out.println("Exception caught while handling client: " + e.getMessage());
            } finally 
            {
                try 
                {
                    clients.remove(clientSocket);
                    clientSocket.close();
                } catch (IOException e) 
                {
                    e.printStackTrace();
                }
            }
        }
    }
}

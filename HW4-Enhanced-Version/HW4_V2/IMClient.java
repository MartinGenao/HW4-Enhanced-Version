import java.io.*;
import java.net.*;

public class IMClient 
{
    public static void main(String[] args) throws IOException 
    {
        if (args.length != 2) 
        {
            System.err.println("Usage: java IMClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            // establish a socket connection to the host and port
            Socket kkSocket = new Socket(hostName, portNumber);
            ObjectOutputStream out = new ObjectOutputStream(kkSocket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(kkSocket.getInputStream());
        ) 
        {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // receive initial message
            Object serverResponse = in.readObject();
            System.out.println(serverResponse);

            Object userInput;
            while ((userInput = getUserInput(stdIn)) != null) 
            {
                out.writeObject(userInput);
                out.flush();
                
                // response from the server
                serverResponse = in.readObject();
                System.out.println(serverResponse);
                // loop stops if 'bye'
                if (serverResponse.toString().equalsIgnoreCase("Bye")) 
                {
                    break;
                }
            }
        } catch (UnknownHostException e) 
        {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException | ClassNotFoundException e) 
        {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
    /**
     * Helper method to read user input
     * @param BufferedReader for reading user input
     * @return users input as object
     * @throws IOException 
     */
    private static Object getUserInput(BufferedReader stdIn) throws IOException 
    {
        System.out.print("Your input: ");
        String input = stdIn.readLine();
        if (input != null && !input.isEmpty()) {
            return input;
        }
        return null;
    }
}

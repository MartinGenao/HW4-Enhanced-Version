/*
 * This remained unchanged.
 */
import java.net.*;
import java.io.*;

public class IMServer {
    public static void main(String[] args) throws IOException {
        
        //command line argument checks if user provided port number
        //ensures its 1 argument otherwise it will display error message
        if (args.length != 1) 
        {
            System.err.println("Usage: java IMServer <port number>");
            System.exit(1);
        }
        //if user provided port number it gets stored here as an int
        int portNumber = Integer.parseInt(args[0]);
        
        
        try ( 
            //1.) listening on portNumber for a client request
            ServerSocket serverSocket = new ServerSocket(portNumber);
            //3.) Server accepts connection request
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
        
            String inputLine, outputLine;
            IMProtocol protocol = new IMProtocol();
            
            // Initiate conversation with client
            outputLine = protocol.processInput(null);
            //4.) Send initial message to client
            out.println(outputLine);
            //9.) Recieve response from client
            while ((inputLine = in.readLine()) != null) {
                //10.) Determine server's reply
                System.out.println("Client: " + inputLine);
                //System.out.println("Received input from client: " + inputLine);
                outputLine = protocol.processInput(inputLine);
                //System.out.println("Server response: " + outputLine);
                System.out.println(outputLine);
                //11.) Send server's reply to client
                out.println(outputLine);
                if (outputLine.equalsIgnoreCase("bye"))
                    break;
            }
        } catch (IOException e) 
        {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}

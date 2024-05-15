/*
 * This remained unchanged.
 */
import java.io.*;
import java.net.*;

public class IMClient {
    public static void main(String[] args) throws IOException {
        
        if (args.length != 2) {
            System.err.println(
                "Usage: java IMClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
            //2.) Client initiates connection request
            Socket kkSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(kkSocket.getInputStream()));
        ) {
            BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
                
            String fromServer;
            String fromUser;
            //5.) Receieve message from server
            while ((fromServer = in.readLine()) != null) 
            {
                //6.) Print message from server
                System.out.println(fromServer);
                if (fromServer.equals("Bye"))
                    break;
                //7.) Read response from client
                fromUser = stdIn.readLine();
                if (fromUser != null) 
                {
                    System.out.println("Client: " + fromUser);
                    //8.) Send response to server
                    out.println(fromUser);
                }
            }
        } catch (UnknownHostException e) 
        {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) 
        {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }
}

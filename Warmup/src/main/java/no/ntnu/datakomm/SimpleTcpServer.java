package no.ntnu.datakomm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A Simple TCP client, used as a warm-up exercise for assignment A4.
 */
public class SimpleTcpServer
{
        private static final int PORT = 1301;
        private ServerSocket serverSocket;
        private Socket clientSocket;
        private PrintWriter output;
        private BufferedReader input;
        private boolean keepRunning = true;
        
        public static void main(String[] args) {
                SimpleTcpServer server = new SimpleTcpServer();
                log("Simple TCP server starting");
                server.run();
                log("ERROR: the server should never go out of the run() method! After handling one client");
        }
        
        /**
         * Log a message to the system console.
         *
         * @param message The message to be logged (printed).
         */
        private static void log(String message) {
                System.out.println(message);
        }
        
        public void run() {
                try {
                        serverSocket = new ServerSocket(PORT);
                        System.out.println("[SERVER]: Listening for connections on " + PORT);
                        while (keepRunning) {
                                clientSocket = serverSocket.accept();
                                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                                output = new PrintWriter(clientSocket.getOutputStream(), true);
                                String clientData = input.readLine();
                                System.out.print("[SERVER}: Data " + clientData + " received! - ");
                                if (clientData !=null) {
                                        if (clientData.matches("game over")) {
                                                System.out.println("Killin!!!");
                                                clientSocket.close();
                                        }
                                        else if (clientData.matches("[0-9]\\+[0-9]")) {
                                                System.out.print("Calculating reply...");
                                                String calculatedResult = "10";
                                                System.out.println(calculatedResult);
                                                output.println(calculatedResult.toString()+"\n");
                                        }
                                        else {
                                                System.out.println(" Unknown request");
                                                output.println("error");
                                        }
                                } else {
                                        System.out.println("[SERVER]: Received null, killing!");
                                        keepRunning = false;
                                }
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}

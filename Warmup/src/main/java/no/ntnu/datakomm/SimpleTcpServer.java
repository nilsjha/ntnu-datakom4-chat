package no.ntnu.datakomm;

import java.io.BufferedReader;
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
                while (keepRunning) {
                
                }
        }
}

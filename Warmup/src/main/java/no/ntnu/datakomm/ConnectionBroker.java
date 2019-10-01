package no.ntnu.datakomm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionBroker extends Thread {
        private final Socket clientSocket;
        private boolean keepClientOpen = true;
        
        public ConnectionBroker (Socket clientSocket)
        {
                this.clientSocket = clientSocket;
        }
        
        @Override
        public void run ()
        {
                try {
                        while (keepClientOpen) {
                                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                                String clientData = input.readLine();
                                System.out.print("[CLIENT-" + this.getId() + "]: Data " + clientData + " received! - ");
                                if ((clientData != null)) {
                                        if (clientData.matches("game over")) {
                                                System.out.println("Killin!!!");
                                                keepClientOpen = false;
                                        } else if (clientData.matches("[0-9]\\+[0-9]")) {
                                                System.out.print("Calculating reply...");
                                                String calculatedResult = "10";
                                                System.out.println(calculatedResult);
                                                output.println(calculatedResult.toString() + "\n");
                                        } else {
                                                System.out.println(" Unknown request");
                                                output.println("error");
                                        }
                                } else {
                                        System.out.println("[SERVER]: Received null, killing!");
                                        keepClientOpen = false;
                                }
                                if (keepClientOpen == false) {
                                        this.clientSocket.close();
                                        System.out.println("[CLIENT-" + this.getId() +"]: "
                                         + "ConnectionBroker " + this.getId() + " closed:" + this.clientSocket.isClosed());
                                }
                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}

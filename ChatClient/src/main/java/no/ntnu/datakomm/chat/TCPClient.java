package no.ntnu.datakomm.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class TCPClient {
    private final List<ChatListener> listeners = new LinkedList<>();
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private Socket connection;
    // Hint: if you want to store a message for the last error, store it here
    private String lastError = null;
    
    /**
     * Connect to a chat server.
     *
     * @param host host name or IP address of the chat server
     * @param port TCP port of the chat server
     * @return True on success, false otherwise
     */
    public boolean connect(String host, int port) {
        try {
            connection = new Socket(host, port);
            toServer = new PrintWriter(connection.getOutputStream(), true);
            fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.println("[CONNOK-" + connection.hashCode() + "]: " +
                "Established " + connection.getInetAddress() + ":" + connection.getPort() + ", srcPort:" + connection.getLocalPort());
            return true;
        } catch (IOException e) {
            if (connection == null) {
                System.out.println("[CONNER-NULLSOCKET]: Socket error:" + e.getMessage());
            } else {
                System.out.println("[CONNER-" + connection.hashCode() + "]: " +
                    "Socket error:" + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Close the socket. This method must be synchronized, because several
     * threads may try to call it. For example: When "Disconnect" button is
     * pressed in the GUI thread, the connection will get closed. Meanwhile, the
     * background thread trying to read server's response will get error in the
     * input stream and may try to call this method when the socket is already
     * in the process of being closed. with "synchronized" keyword we make sure
     * that no two threads call this method in parallel.
     */
    public synchronized void disconnect() {
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //calls only on onDisconnect() if the connection is really closed
        if (connection.isClosed()) onDisconnect();
    }
    
    /**
     * @return true if the connection is active (opened), false if not.
     */
    public boolean isConnectionActive() {
        return connection != null;
    }
    
    /**
     * Send a command to server.
     *
     * @param cmd A command. It should include the command word and
     *            optional attributes, according to the protocol.
     * @return true on success, false otherwise
     */
    private boolean sendCommand(String cmd) {
        // TODO Step 2: Implement this method
        // Hint: Remember to check if connection is active
        boolean clearToTransmit = false;
        if (cmd == null);
        else if (cmd.equals(""));
        else if (connection.isClosed() == false) {
            // matches only if command+space+argument"
            if(cmd.matches("^\\w+\\s(\\w+)(.+)")) {
                clearToTransmit = true;
            }
        }
        // Transmit only if message the above conditions are valid
        if (clearToTransmit) toServer.println(cmd);
        System.out.println("[PWRITR-" + connection.hashCode() + "]: TX:" + cmd);
        return clearToTransmit;
    }
    
    /**
     * Send a public message to all the recipients.
     *
     * @param message Message to send
     * @return true if message sent, false on error
     */
    public boolean sendPublicMessage(String message) {
        // TODO Step 2: implement this method
        // Hint: Reuse sendCommand() method
        // Hint: update lastError if you want to store the reason for the error.
        boolean clearToTransmit = true;
        if (message == null) {
            lastError = "Message is null, therefore not sent";
            clearToTransmit = false;
        }
        else if(message.equals("")) {
            lastError = "Message is empty, therefore not sent";
            clearToTransmit = false;
        }
        if (clearToTransmit) sendCommand("msg " + message);
        return clearToTransmit;
    }
    
    /**
     * Send a login request to the chat server.
     *
     * @param username Username to use
     */
    public void tryLogin(String username) {
        // TODO Step 3: implement this method
        // Hint: Reuse sendCommand() method
        boolean readyToLogon = true;
        if (username == null) {
            lastError = "username is null, ignoring";
            readyToLogon = false;
        }
        else if(username.equals("")) {
            lastError = "username is empty, ignoring";
            readyToLogon = false;
        }
        if (readyToLogon) sendCommand("login " + username);
        
    }
    
    /**
     * Send a request for latest user list to the server. To get the new users,
     * clear your current user list and use events in the listener.
     */
    public void refreshUserList() {
        // TODO Step 5: implement this method
        // Hint: Use Wireshark and the provided chat client reference app to find out what commands the
        // client and server exchange for user listing.
    }
    
    /**
     * Send a private message to a single recipient.
     *
     * @param recipient username of the chat user who should receive the message
     * @param message   Message to send
     * @return true if message sent, false on error
     */
    public boolean sendPrivateMessage(String recipient, String message) {
        // TODO Step 6: Implement this method
        // Hint: Reuse sendCommand() method
        // Hint: update lastError if you want to store the reason for the error.
        return false;
    }
    
    
    /**
     * Send a request for the list of commands that server supports.
     */
    public void askSupportedCommands() {
        // TODO Step 8: Implement this method
        // Hint: Reuse sendCommand() method
    }
    
    
    /**
     * Wait for chat server's response
     *
     * @return one line of text (one command) received from the server
     */
    private String waitServerResponse() {
        // TODO Step 3: Implement this method
        String serverResponse = null;
        try {
            // Try to read the BufferedReader from the server
            serverResponse = fromServer.readLine();
            if (serverResponse != null) {
                System.out.println("[BUFFRD-" + connection.hashCode()+ "]: " +
                    "RX:" + serverResponse );
            } else {
                serverResponse = null;
            }
        } catch (IOException e) {
            // e.printStackTrace();
            // Close the connection & reset the connection state
            disconnect();
            System.out.println("[IOEXCP-" + connection.hashCode() + "]: " +
                "Closed=" + connection.isClosed() + " resetting state...");
            connection = null;
            
        }
        return serverResponse;
    
        // TODO Step 4: If you get I/O Exception or null from the stream, it means that something has gone wrong
        // with the stream and hence the socket. Probably a good idea to close the socket in that case.
        
    }
    
    /**
     * Get the last error message
     *
     * @return Error message or "" if there has been no error
     */
    public String getLastError() {
        if (lastError != null) {
            return lastError;
        } else {
            return "";
        }
    }
    
    /**
     * Start listening for incoming commands from the server in a new CPU thread.
     */
    public void startListenThread() {
        // Call parseIncomingCommands() in the new thread.
        Thread t = new Thread(() ->
        {
            parseIncomingCommands();
        });
        t.start();
    }
    
    /**
     * Read incoming messages one by one, generate events for the listeners. A loop that runs until
     * the connection is closed.
     */
    private void parseIncomingCommands() {
        while (isConnectionActive()) {
            // TODO Step 3: Implement this method
            // Hint: Reuse waitServerResponse() method
            // Hint: Have a switch-case (or other way) to check what type of response is received from the server
            // and act on it.
            // Hint: In Step 3 you need to handle only login-related responses.
            // Hint: In Step 3 reuse onLoginResult() method
            String responseFromServer = waitServerResponse();
            if (responseFromServer == null) {
            }
            else {
                // regex to split command towards two sub array strings
                String[] result = responseFromServer.split("\\s", 2);
                System.out.print("[PARSER-" + connection.hashCode() + "]: " +
                    "Acknowledged command");
                for (String i : result) {
                    System.out.print(" [" + i + "]");
                }
                System.out.println(".");
                switch(result[0]) {
                    case "loginok":
                        onLoginResult(true,null);
                        System.out.println("[SWCASE-" + connection.hashCode() +
                            "]: Logon succeeded as");
                        break;
                    case "loginerr":
                        onLoginResult(false,result[1]);
                        System.out.println("[SWCASE-" + connection.hashCode() +
                            "]: Logon failed: " + result[1]);
                        break;
                }
            }
            
            // TODO Step 5: update this method, handle user-list response from the server
            // Hint: In Step 5 reuse onUserList() method
            
            // TODO Step 7: add support for incoming chat messages from other users (types: msg, privmsg)
            // TODO Step 7: add support for incoming message errors (type: msgerr)
            // TODO Step 7: add support for incoming command errors (type: cmderr)
            // Hint for Step 7: call corresponding onXXX() methods which will notify all the listeners
            
            // TODO Step 8: add support for incoming supported command list (type: supported)
            
        }
    }
    
    /**
     * Register a new listener for events (login result, incoming message, etc)
     *
     * @param listener
     */
    public void addListener(ChatListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Unregister an event listener
     *
     * @param listener
     */
    public void removeListener(ChatListener listener) {
        listeners.remove(listener);
    }
    
    
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // The following methods are all event-notificators - notify all the listeners about a specific event.
    // By "event" here we mean "information received from the chat server".
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * Notify listeners that login operation is complete (either with success or
     * failure)
     *
     * @param success When true, login successful. When false, it failed
     * @param errMsg  Error message if any
     */
    private void onLoginResult(boolean success, String errMsg) {
        for (ChatListener l : listeners) {
            l.onLoginResult(success, errMsg);
        }
    }
    
    /**
     * Notify listeners that socket was closed by the remote end (server or
     * Internet error)
     */
    private void onDisconnect() {
        // TODO Step 4: Implement this method
        // Hint: all the onXXX() methods will be similar to onLoginResult()
        for (ChatListener l : listeners) {
            // Notify all ChatListeners
            l.onDisconnect();
        }
    }
    
    /**
     * Notify listeners that server sent us a list of currently connected users
     *
     * @param users List with usernames
     */
    private void onUsersList(String[] users) {
        // TODO Step 5: Implement this method
    }
    
    /**
     * Notify listeners that a message is received from the server
     *
     * @param priv   When true, this is a private message
     * @param sender Username of the sender
     * @param text   Message text
     */
    private void onMsgReceived(boolean priv, String sender, String text) {
        // TODO Step 7: Implement this method
    }
    
    /**
     * Notify listeners that our message was not delivered
     *
     * @param errMsg Error description returned by the server
     */
    private void onMsgError(String errMsg) {
        // TODO Step 7: Implement this method
    }
    
    /**
     * Notify listeners that command was not understood by the server.
     *
     * @param errMsg Error message
     */
    private void onCmdError(String errMsg) {
        // TODO Step 7: Implement this method
    }
    
    /**
     * Notify listeners that a help response (supported commands) was received
     * from the server
     *
     * @param commands Commands supported by the server
     */
    private void onSupported(String[] commands) {
        // TODO Step 8: Implement this method
    }
}

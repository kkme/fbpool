// Server.java

package com.talient.socket;

import java.io.*;
import java.net.*;
import com.talient.socket.*;

// A Server class to handle input request
public abstract class Server
{
    // Socket on which to wait for new clients
    private ServerSocket serverSocket;

    // Method that opens a new output stream for a client.
    public abstract void newClientThread(Socket socket);

    // Default constructor
    public Server() { }

    // Creates a new server object to handle clients.
    public Server(ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    // Waits for a client ot connect and spawns a thread to handle it.
    public void handleClients()
    {
        try {
            for (;;) {
                Socket clientSocket = serverSocket.accept();
                newClientThread(clientSocket);
            }
        }
        catch (IOException e) {}
    }

}


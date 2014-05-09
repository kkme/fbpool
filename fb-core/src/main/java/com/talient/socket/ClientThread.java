package com.talient.socket;

import java.io.*;
import java.net.*;

// class that defines a client-handling thread
public class ClientThread extends Thread
{
    protected Socket clientSocket;

    // Creates a new thread object to handle the given client socket.
    public ClientThread(Socket clientSocket)
    {
        this.clientSocket = clientSocket;
        start();
    }

    // Main method for the thread
    public void run()
    {
        try {
            InputStream in = clientSocket.getInputStream();
            OutputStream out = clientSocket.getOutputStream();
            byte[] buf = new byte[1024];
            for (;;) {
                int bytesRead = in.read(buf);
                if (bytesRead < 0) {
                    break;
                }
                out.write(buf, 0, bytesRead);
            }
            in.close();
            out.close();
            clientSocket.close();
        }
        catch (IOException e) {}
    }
}


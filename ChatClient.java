package Project4Skeleton;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    /* ChatClient constructor
     * @param server - the ip address of the server as a string
     * @param port - the port number the server is hosted on
     * @param username - the username of the user connecting
     */
    private ChatClient(String username, int port, String server) {

        if (server == null) {
            this.server = "localhost";
        } else {
            this.server = server;
        }
        if (port == 0) {
            this.port = 1500;
        } else {
            this.port = port;
        }
        this.username = username;

    }
/*
    public ChatClient(String username, int port) {
        this.username = username;
        this.port = port;
        this.server = "localhost";
    }

    public ChatClient(String username) {
        this.username = username;
        this.port = 1500;
        this.server = "localhost";
    }
*/

    /**
     * Attempts to establish a connection with the server
     *
     * @return boolean - false if any errors occur in startup, true if successful
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Attempt to create output stream
        try {
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Attempt to create input stream
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Create client thread to listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    /*
     * Sends a string to the server
     * @param msg - the message to be sent
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {
        // Get proper arguments and override defaults


        String inp = "";

        // Create your client and start it
        ChatClient client = new ChatClient("localhost", 1500, "CS 180 Student");


        client.start();

        Scanner input = new Scanner(client.sInput);
        while (input.hasNextLine()) {
            inp = input.nextLine();
            /*
            if (inp.equalsIgnoreCase("/logout")) {
                try {
                    client.sInput.close();
                    client.sOutput.close();
                    client.socket.close();
                }catch (Exception e) {}
            }

        }*/
            // Send an empty message to the server
            client.sendMessage(new ChatMessage());
        }
    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {
        public void run() {
            while (true) {
                try {
                    String msg = (String) sInput.readObject();
                    System.out.print(msg);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
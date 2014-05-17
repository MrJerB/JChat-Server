package com.jeremyborg.jchat.server;

import java.net.*;
import java.util.Date;

public class Main {

    private static int PORT;
    private static int MAX_CLIENTS;

    public static void main(String[] args) {

        if (args.length == 2) {
            PORT = Integer.parseInt(args[0]);
            MAX_CLIENTS = Integer.parseInt(args[1]);
        }
        else {
            System.err.println("Arguments: PORT MAX_CLIENTS");
            System.exit(-1);
        }

        ClientManager cManager = new ClientManager(MAX_CLIENTS);

        try {

            log("JChat Server Initiated");
            ServerSocket listener = new ServerSocket(PORT);

            log("Now listening for incoming connections on port " + PORT);

            while(true) {
                Client newClient = new Client(listener.accept(), cManager);
               log("Incoming connection from " + newClient.getIP());
            }
        } catch(Exception e) {
            log(e.getMessage());
            System.exit(-1);
        }
    }

    public static void log(String message) {
        System.out.println("[" + new Date() + "] " + message);
    }
}

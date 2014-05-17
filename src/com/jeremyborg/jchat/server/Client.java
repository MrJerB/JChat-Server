package com.jeremyborg.jchat.server;

import java.net.Socket;
import java.util.Date;
import java.io.*;

public class Client extends Thread {

    private ClientManager cManager;

    private String nick;
    private Date connectedAt;
    private boolean isAdmin;

    private Socket uSocket;
    private BufferedReader iStream;
    private PrintStream oStream;

    public Client(Socket s, ClientManager cm) {
        this.nick = "Anonymous";
        this.connectedAt = new Date();
        this.isAdmin = false;

        this.cManager = cm;

        this.uSocket = s;
        this.setupStreams();
        this.start();
    }

    private void setupStreams() {
        try {
            this.iStream = new BufferedReader(new InputStreamReader(uSocket.getInputStream()));
            this.oStream = new PrintStream(uSocket.getOutputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void handshake() {

        if (this.cManager.getSize() < this.cManager.getLimit()) {

            this.send("$ HANDSHAKE WELCOME");
            this.send("$ HANDSHAKE GET NICK");

            try {
                String input[]= this.iStream.readLine().split(" ");
                if ((input[0].toUpperCase().equals("NICK")) && (input.length > 1)) {
                    Main.log("NICK OK");
                    if (this.setNick(input[1])) {
                        this.send("% Connected.");
                        for (Client c : this.cManager.getAllClients()) {
                            c.send("% " + this.getNick() + " has joined the chat!");
                        }
                        this.cManager.addClient(this);
                    }
                    else {
                        this.send("$ NICK INVALID OR TAKEN");
                        this.drop();
                    }
                }
                else {
                    this.send("$ NICK OPCODE EXPECTED");
                    this.drop();
                }
            } catch (IOException ex) {
                    this.drop();
            }
        }
        else {
            this.send("% SERVER FULL");
            this.drop();
        }
    }

    @Override
    public void run() {

        this.handshake();

        while(true) {
            try {
                this.cManager.message(this, this.iStream.readLine());
            } catch (Exception e) {
                this.drop();
                break;
            }
        }
    }

    public void send(String m) {
        this.oStream.println(m);
    }

    public void drop() {
        try {

            this.iStream.close();
            this.oStream.flush();
            this.oStream.close();
            this.uSocket.close();

            this.cManager.removeClient(this);

            this.interrupt();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getIP() {
        return this.uSocket.getInetAddress().toString();
    }

    public boolean setNick(String n) {

        String nSplit[] = n.split(" ");

        if ((nSplit[0].matches("\\w{4,16}")) && !(this.cManager.nickExists(n))) {
            this.nick = nSplit[0];
            return true;
        }
        else {
            return false;
        }
    }

    public String getNick() {
        return this.nick;
    }

    public long getConnectTime() {
        return this.connectedAt.getTime();
    }

    public void setAdmin(boolean b) {
        this.isAdmin = b;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }
}

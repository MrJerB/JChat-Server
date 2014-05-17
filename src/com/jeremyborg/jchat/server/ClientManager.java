package com.jeremyborg.jchat.server;

import java.util.ArrayList;

public class ClientManager {

    private int cLimit;
    private ArrayList<Client> cArray;

    public ClientManager(int cl) {
        this.cLimit = cl;
        this.cArray = new ArrayList();
    }

    public void message(Client c, String m) {
        System.out.println("<" + c.getNick() + "> " + m);
        new MessageHandler(this, c, m);
    }

    public void addClient(Client c) {
        this.cArray.add(c);
    }

    public void removeClient(Client c) {
        if (this.cArray.contains(c)) {
            this.cArray.remove(c);
            for (Client cl : this.cArray) {
                cl.send("% " + c.getNick() + " has disconnected.");
            }
        }
    }

    public Client getClient(int index) {
        return this.cArray.get(index);
    }

    public ArrayList<Client> getAllClients() {
        return this.cArray;
    }

    public int getLimit() {
        return this.cLimit;
    }

    public boolean nickExists(String n) {

        boolean result = false;

        for (Client c : this.cArray) {
            if(c.getNick().equals(n)) {
                result = true;
            }
        }

        return result;
    }

    public Client getClientByNick(String n) {

        Client result = null;

        for (Client c : this.cArray) {
            if(c.getNick().equals(n)) {
                result = c;
            }
        }

        return result;
    }

    public int getSize() {
        return this.cArray.size();
    }
}

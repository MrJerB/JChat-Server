package com.jeremyborg.jchat.server;

public class MessageHandler {

    private ClientManager cManager;
    private Client sender;
    private String[] message;
    private Opcode opcode;

    public MessageHandler(ClientManager cm, Client s, String m) {
        this.cManager = cm;
        this.sender = s;

        this.message = m.split(" ");
        try {
            this.opcode = Opcode.valueOf(this.message[0].toUpperCase());

            if (this.opcode.countArgs() > 0) {
                for(int i = this.opcode.countArgs(); i < this.message.length-1; i++) {
                    this.message[this.opcode.countArgs()] = this.message[this.opcode.countArgs()] + " " + this.message[i+1];
                }
            }

            this.handle();

        } catch (IllegalArgumentException iae) {
            this.sender.send("$ SERVER OPCODE SPECIFIED NOT FOUND");
        }
    }


    private void handle() {

        if (this.opcode.isAdminOnly() && !(this.sender.isAdmin())) {
            this.sender.send("% You are not an admin bro!");
        }
        else {
            switch (this.opcode) {

                case STATUS:
                    this.sender.send("% SERVER STATUS: " + this.cManager.getSize() + "/" + this.cManager.getLimit() + " clients connected.");
                    break;

                case NICK:

                    String oldnick = this.sender.getNick();

                    if (this.sender.setNick(this.message[1])) {
                        for (Client c : this.cManager.getAllClients()) {
                            c.send("% " + oldnick + " is now known as " + this.sender.getNick());
                        }
                    }
                    else {
                        this.sender.send("$ NICK INVALID OR TAKEN");
                    }
                    break;

                case SAY:
                    for (Client c : this.cManager.getAllClients()) {
                        c.send("# " + this.sender.getNick() + " " + this.message[1]);
                    }
                    break;

                case PM:
                    Client recepient = this.cManager.getClientByNick(this.message[1]);
                    if (recepient != null) {
                        String pmlayout = "@ " + this.sender.getNick() + " -> " + recepient.getNick() + ": " + this.message[2];
                        this.sender.send(pmlayout);
                        recepient.send(pmlayout);
                    }

                    break;

                case CLIENTS:
                    String clientList = "~ ";
                    for (Client c : this.cManager.getAllClients()) {
                        clientList = clientList + c.getNick() + ":";
                    }
                    clientList = clientList.substring(0, clientList.length()-1);
                    this.sender.send(clientList);
                    break;

                case ECHO:
                    System.out.println(this.message[1]);
                    this.sender.send("% " + this.message[1]);
                    break;

                case ADMIN:
                    if (this.message[1].equals("letmein")) {
                        this.sender.setAdmin(true);
                        this.sender.send("% Admin logged in!");

                        for (Client c: this.cManager.getAllClients()) {
                            c.send(this.sender.getNick() + " has been given magical admin powers.");
                        }
                    }
                    else {
                        this.sender.send("% Admin Incorrect password.");
                    }

                    break;

                case QUIT:
                    this.sender.send("% BYE " + this.sender.getNick() + "!");
                    this.sender.drop();
                    break;

                case HELP:
                    this.sender.send("% " + this.message[1] + ": " + Opcode.valueOf(this.message[1].toUpperCase()).getInfo());
                    break;

                case KICK:
                    Client target = cManager.getClientByNick(this.message[1]);
                    if (target != null) {
                        Main.log("Kicking " + target.getNick());
                        target.send("% You have been kicked.");

                        if (this.message.length > 3) {
                            target.send("Reason: " + this.message[2]);
                        }

                        target.drop();
                    }
                    else {
                        this.sender.send("% I cannot kick a non-existing client! Madness!");
                    }
                    break;

                case LIST:
                    this.sender.send("% LIST OF ALL OPCODES:");
                    for (Opcode o: Opcode.values()) {
                        this.sender.send(o.name() + ": " + o.getInfo());
                    }
                    this.sender.send("% END OF LIST");
            }
        }
    }
}

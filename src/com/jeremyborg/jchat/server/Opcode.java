package com.jeremyborg.jchat.server;

public enum Opcode {

    // OPCODE (String info, int args, boolean adminOnly);

    STATUS ("Returns information about the server to the user.", 0, false),
    NICK ("Used to change nickname.", 1, false),
    SAY ("Talk to whole room.", 1, false),
    PM ("Talk to one user.", 2, false),
    ADMIN ("Login to use admin features.", 1, false),
    CLIENTS ("Retrieves list of currently connected clients.", 0, false),
    QUIT ("Disconnects from the server.", 0, false),
    KICK ("Kicks a client out.", 2, true),
    BAN ("Bans a client from the server", 1, true),
    ECHO ("Server echoes back to client...", 1, false),
    HELP("Retrieves info about a command!", 1, false),
    LIST("Lists all available opcodes.", 0, false);

    private final String info;
    private final int args; // Amount of arguments the opcode takes.
    private final boolean adminOnly;


    Opcode(String info, int args, boolean adminOnly) {
        this.info = info;
        this.args = args;
        this.adminOnly = adminOnly;
    }

    public String getInfo() {
        return this.info;
    }

    public int countArgs() {
        return this.args;
    }

    public boolean isAdminOnly() {
        return this.adminOnly;
    }

}

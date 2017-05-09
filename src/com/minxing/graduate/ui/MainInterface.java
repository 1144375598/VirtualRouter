package com.minxing.graduate.ui;

/**
 * @author a.khettar
 * 
 */
public final class MainInterface {

    /**
     * Builds welcome screen.
     * 
     * @return
     */
    public static String buildWelcomeScreen() {
        String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
        StringBuilder builder = new StringBuilder();
        builder.append(cr);
        builder.append("==========================================================");
        builder.append(cr);
        builder.append(cr);
        builder.append("   Welcome to Virtual Router: Version 1.0                 ");
        builder.append(cr);
        builder.append(cr);
        builder.append("==========================================================");
        builder.append(cr);
        builder.append(cr);
        builder.append("List of possible commands:                                ");
        builder.append(cr);
        builder.append(cr);
        builder.append("config                                                    ");
        builder.append(cr);
        builder.append("include <file>                                            ");
        builder.append(cr);
        builder.append("port add <port number> <virtual IP/bits> <mtu>            ");
        builder.append(cr);
        builder.append("port del [<port number> | all]                            ");
        builder.append(cr);
        builder.append("connect add <local real port> <remote Real IP:port>       ");
        builder.append(cr);
        builder.append("connect del [<port number> | all]                         ");
        builder.append(cr);
        builder.append("route add [<network ID/bits> | default] <virtual IP>      ");
        builder.append(cr);
        builder.append("route del [<network ID/bits> <virtual IP> | all | default]");
        builder.append(cr);
        builder.append("send <SRC Virtual IP> <DST Virtual IP> <ID> <N bytes>     ");
        builder.append(cr);
        builder.append("usend <local port> <str>                                  ");
        builder.append(cr);
        builder.append("asend <str>                                               ");
        builder.append(cr);
        builder.append("troute <ip>                                               ");
        builder.append(cr);
        builder.append("exit                                                      ");
        builder.append(cr);
        return builder.toString();
    }

}

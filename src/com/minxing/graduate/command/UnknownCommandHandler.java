package com.minxing.graduate.command;

public class UnknownCommandHandler implements CommandHandler {

	private final String[] command;

	public UnknownCommandHandler(final String[] command) {
		this.command = command;
	}

	@Override
	public String handle() {

		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		builder.append("Unknown command [" + command[0] + "]");
		builder.append(cr);
		builder.append("Here are the list of commands you could run:");
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

package com.minxing.graduate.command;

import com.minxing.graduate.model.Router;

public class ConnectHandler implements CommandHandler {
	final private String[] command;

	public ConnectHandler(final String[] command) {
		this.command = command;
	}

	@Override
	public String handle() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		try {
			switch (command[1]) {
			case "add":
				builder.append(Router.getPortAdmin().connect(Integer.parseInt(command[2]), command[3]));
				break;
			case "del":
				if (command[2].trim().equals("all"))
					builder.append(Router.getPortAdmin().disconnectAll());
				else
					builder.append(Router.getPortAdmin().disconnect(Integer.parseInt(command[2])));
				break;
			default:
				throw new Exception();
			}
		} catch (Exception e) {
			builder.append(("usage: connect add <local port> <remote IP:port>"));
			builder.append(cr);
			builder.append(("usage: connect del [<local port> | all]"));
			builder.append(cr);
			// System.out.println(e.toString());
		}
		return builder.toString();
	}

}

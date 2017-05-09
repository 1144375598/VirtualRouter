package com.minxing.graduate.command;

import com.minxing.graduate.model.Router;

public class PortHandler implements CommandHandler {
	final private String[] command;

	public PortHandler(final String[] command) {
		this.command = command;
	}

	@Override
	public String handle() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();

		try {
			switch (command[1]) {
			case "add":
				builder.append(Router.getPortAdmin().addPort(Integer.parseInt(command[2]), command[3], Integer.parseInt(command[4])));
				break;
			case "del":
				if (command[2].trim().equals("all"))
					builder.append(Router.getPortAdmin().removeAll());
				else
					builder.append(Router.getPortAdmin().removePort(Integer.parseInt(command[2])));
				break;
			default:
				throw new Exception();
			}
		} catch (Exception e) {
			builder.append(("usage: port add <port number> <virtual IP/bits> <mtu>"));
			builder.append(cr);
			builder.append(("usage: port del [<port number> | all]"));
			builder.append(cr);
			e.printStackTrace();
		}
		return builder.toString();
	}

}

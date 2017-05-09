package com.minxing.graduate.command;

import com.minxing.graduate.model.IPv4;
import com.minxing.graduate.model.Router;

public class SendHandler implements CommandHandler {
	final private String[] command;

	public SendHandler(final String[] command) {
		this.command = command;
	}

	@Override
	public String handle() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		try {
			builder.append(("command: " + command[0] + " " + command[1] + " " + command[2] + " " + command[3] + " "
					+ command[4]));
			builder.append(cr);
			builder.append(Router.getPortAdmin().sendTestPacket(new IPv4(command[1],builder), new IPv4(command[2],builder),
					Short.parseShort(command[3]), Integer.parseInt(command[4])));
		} catch (Exception e) {
			builder.append(("usage: send <SRC Virtual IP> <DST Virtual IP> <ID> <N bytes>"));
			builder.append(cr);
			// System.out.println(e.toString());
		}
		return builder.toString();
	}

}

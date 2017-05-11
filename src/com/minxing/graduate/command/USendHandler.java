package com.minxing.graduate.command;

import com.minxing.graduate.model.Router;

public class USendHandler implements CommandHandler {
	final private String[] command;

	public USendHandler(final String[] command) {
		this.command = command;
	}

	@Override
	public String handle() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		try {
			builder.append(Router.getPortAdmin().usend(Integer.parseInt(command[1]), command[2].getBytes()));
		} catch (Exception e) {
			builder.append(("usage: usend <port> <data>"));
			builder.append(cr);
			// System.out.println(e.toString());
		}
		return builder.toString();

	}

}

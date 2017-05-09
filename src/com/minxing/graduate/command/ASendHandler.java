package com.minxing.graduate.command;

import com.minxing.graduate.model.Router;

public class ASendHandler implements CommandHandler {
	final private String[] command;

	public ASendHandler(final String[] command) {
		this.command = command;
	}

	@Override
	public String handle() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		try {
			builder.append(Router.getPortAdmin().asend(command[1].getBytes()));
		} catch (Exception e) {
			builder.append(("usage: asend <str>"));
			builder.append(cr);
			// System.out.println(e.toString());
		}
		return builder.toString();

	}

}

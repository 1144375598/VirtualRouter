package com.minxing.graduate.command;

import java.io.FileNotFoundException;

import com.minxing.graduate.util.FileReader;

public class IncludeHandler implements CommandHandler {
	final private String[] command;

	public IncludeHandler(final String[] command) {
		this.command = command;
	}

	@Override
	public String handle() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		String[] s = null;

		try {

			FileReader getCommand = new FileReader(command[1]);
			while (getCommand.hasNext()) {
				s = (getCommand.readLine().trim()).split(" ");
				doCommand(s, builder);
			}
		} catch (ArrayIndexOutOfBoundsException e) {

			builder.append(("usage: include <filename>"));
			builder.append(cr);
			// e.printStackTrace();
		} catch (FileNotFoundException e) {

			builder.append(("file not found: " + command[1]));
			builder.append(cr);
			// e.printStackTrace();
		}
		return builder.toString();

	}

	private void doCommand(String[] command, StringBuilder builder) {
		ASendHandler aSendHandler = null;
		ConfigHandler configHandler = null;
		ConnectHandler connectHandler = null;
		PortHandler portHandler = null;
		RouteHandler routeHandler = null;
		SendHandler sendHandler = null;
		TrouteHandler trouteHandler = null;
		USendHandler uSendHandler = null;
		switch (command[0]) {
		case "config":
			if (configHandler == null) {
				configHandler = new ConfigHandler();
			}
			builder.append(configHandler.handle());
			break;
		case "route":
			if (routeHandler == null) {
				routeHandler = new RouteHandler(command);
			}
			builder.append(routeHandler.handle());
			break;
		case "port":
			if (portHandler == null) {
				portHandler = new PortHandler(command);
			}
			builder.append(portHandler.handle());
			break;
		case "connect":
			if (connectHandler == null) {
				connectHandler = new ConnectHandler(command);
			}
			builder.append(connectHandler.handle());
			break;
		case "send":
			if (sendHandler == null) {
				sendHandler = new SendHandler(command);
			}
			builder.append(sendHandler.handle());
			break;
		case "usend":
			if (uSendHandler == null) {
				uSendHandler = new USendHandler(command);
			}
			builder.append(uSendHandler.handle());
			break;
		case "asend":
			if (aSendHandler == null) {
				aSendHandler = new ASendHandler(command);
			}
			builder.append(aSendHandler.handle());
			break;
		case "troute":
			if (trouteHandler == null) {
				trouteHandler = new TrouteHandler(command);
			}
			builder.append(trouteHandler.handle());
			break;

		}
	}
}

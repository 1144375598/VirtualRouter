package com.minxing.graduate.command;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.minxing.graduate.util.FileReader;

public class IncludeHandler implements CommandHandler {
	final private String[] command;
	private List<Integer> execuateFai;
	private String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";

	public IncludeHandler(final String[] command) {
		this.command = command;
		execuateFai = new ArrayList<Integer>();
	}

	@Override
	public String handle() {
		int count = 0;
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		String[] s = null;

		try {

			FileReader getCommand = new FileReader(command[1]);
			while (getCommand.hasNext()) {
				s = (getCommand.readLine().trim()).split(" ");
				count++;
				doCommand(s, builder, count);
			}
			builder.append("total " + count + " commands," + Integer.valueOf(count - execuateFai.size())
					+ " execuate successful");
			builder.append(cr);
			if (execuateFai.size() > 0) {
				for (Integer i : execuateFai) {
					builder.append(i + " ");
				}
				builder.append("commands execuate fail");
				builder.append(cr);
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

	private void doCommand(String[] command, StringBuilder builder, int count) {
		ASendHandler aSendHandler = null;
		ConfigHandler configHandler = null;
		ConnectHandler connectHandler = null;
		PortHandler portHandler = null;
		RouteHandler routeHandler = null;
		SendHandler sendHandler = null;
		TrouteHandler trouteHandler = null;
		USendHandler uSendHandler = null;
		String response = null;
		if (command[0].length() == 0) {
			// empty, do nothing
			return;
		} else if (command[0].startsWith("//")) {
			// comment, do nothing
			return;
		}
		switch (command[0]) {
		case "config":
			if (configHandler == null) {
				configHandler = new ConfigHandler();
			}
			response = configHandler.handle();
			if (response.startsWith(cr + "Successful")) {
				builder.append(response);
			} else {
				builder.append("command " + count + " execuate fail:");
				builder.append(response);
				execuateFai.add(count);
			}
			break;
		case "route":
			if (routeHandler == null) {
				routeHandler = new RouteHandler(command);
			}
			response = routeHandler.handle();
			if (response.startsWith("Successful")) {
				builder.append(response);
			} else {
				builder.append("command " + count + " execuate fail:");
				builder.append(response);
				execuateFai.add(count);
			}
			break;
		case "port":
			if (portHandler == null) {
				portHandler = new PortHandler(command);
			}
			response = portHandler.handle();
			if (response.startsWith("Successful")) {
				builder.append(response);
			} else {
				builder.append("command " + count + " execuate fail:");
				builder.append(response);
				execuateFai.add(count);
			}
			break;
		case "connect":
			if (connectHandler == null) {
				connectHandler = new ConnectHandler(command);
			}
			response = connectHandler.handle();
			if (response.startsWith("Successful")) {
				builder.append(response);
			} else {
				builder.append("command " + count + " execuate fail:");
				builder.append(response);
				execuateFai.add(count);
			}
			break;
		case "send":
			if (sendHandler == null) {
				sendHandler = new SendHandler(command);
			}
			response = sendHandler.handle();
			if (response.startsWith("Successful")) {
				builder.append(response);
			} else {
				builder.append("command " + count + " execuate fail:");
				builder.append(response);
				execuateFai.add(count);
			}
			break;
		case "usend":
			if (uSendHandler == null) {
				uSendHandler = new USendHandler(command);
			}
			response = uSendHandler.handle();
			if (response.startsWith("Successful")) {
				builder.append(response);
			} else {
				builder.append("command " + count + " execuate fail:");
				builder.append(response);
				execuateFai.add(count);
			}
			break;
		case "asend":
			if (aSendHandler == null) {
				aSendHandler = new ASendHandler(command);
			}
			response = aSendHandler.handle();
			if (response.startsWith("Successful")) {
				builder.append(response);
			} else {
				builder.append("command " + count + " execuate fail:");
				builder.append(response);
				execuateFai.add(count);
			}
			break;
		case "troute":
			if (trouteHandler == null) {
				trouteHandler = new TrouteHandler(command);
			}
			response = trouteHandler.handle();
			if (response.startsWith("Successful")) {
				builder.append(response);
			} else {
				builder.append("command " + count + " execuate fail:");
				builder.append(response);
				execuateFai.add(count);
			}
			break;
		default:
			builder.append("command " + count + " execuate fail:");
			builder.append("unknown command");
			builder.append(cr);
			execuateFai.add(count);
			break;
		}
	}
}

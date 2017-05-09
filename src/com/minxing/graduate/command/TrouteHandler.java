package com.minxing.graduate.command;

import com.minxing.graduate.model.IPv4;
import com.minxing.graduate.model.Router;

public class TrouteHandler implements CommandHandler {
	final private String[] command;

	public TrouteHandler(final String[] command) {
		this.command = command;
	}

	@Override
	public String handle() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		IPv4 testIP = Router.getRouteTable().nextRoute(new IPv4(command[1],builder),builder);
		builder.append(
				("port to route " + testIP.toString() + " is " + Router.getPortAdmin().getPort(testIP.getIPArray())));
		builder.append(cr);
		return builder.toString();
	}

}

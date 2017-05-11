package com.minxing.graduate.command;

import com.minxing.graduate.model.Router;

public class ConfigHandler implements CommandHandler {

	@Override
	public String handle() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		builder.append(cr);
		builder.append("Successful");
		builder.append(cr);
		builder.append("Port info");
		builder.append(cr);
		builder.append("-------------------------------------------------------------------------------");
		builder.append(cr);
		builder.append("MAC                   Port  Virtual IP          MTU  Remote IP       Port  Conn");
		builder.append(cr);
		builder.append("-------------------------------------------------------------------------------");
		builder.append(cr);
		builder.append(Router.getPortAdmin().getAllPortsConfig());
		builder.append(cr);
		builder.append(cr);
		builder.append(cr);
		builder.append(Router.getRouteTable().printTable());
		builder.append(cr);
		return builder.toString();
	}

}

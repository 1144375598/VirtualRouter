package com.minxing.graduate.command;

import com.minxing.graduate.model.Router;

public class RouteHandler implements CommandHandler {
	final private String[] command;

	public RouteHandler(final String[] command) {
		this.command = command;
	}

	@Override
	public String handle() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		try {

			// Add or delete new route to Routing Table
			switch (command[1]) {
			case "add":
				if (command[2].trim().equals("default"))
					builder.append(Router.getRouteTable().addDefaultRoute(command[3]));
				else
					builder.append(Router.getRouteTable().addRoute(command[2], command[3]));
				break;
			case "del":
				if (command[2].trim().equals("all"))
					builder.append(Router.getRouteTable().delAll());
				else if (command[2].trim().equals("default"))
					builder.append(Router.getRouteTable().delDefaultRoute());
				else
					builder.append(Router.getRouteTable().delRoute(command[2], command[3]));
				break;
			default:
				throw new Exception();
			}
		} catch (Exception e) {
			builder.append("usage: route add [<network ID/bits> | default] <virtual IP>");
			builder.append(cr);
			builder.append("usage: route del [<network ID/bits> <virtual IP> | all | default]");
			builder.append(cr);
			builder.append(e.toString());
			builder.append(cr);
		}
		return builder.toString();
	}

}

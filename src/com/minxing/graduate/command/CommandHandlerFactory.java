package com.minxing.graduate.command;

public class CommandHandlerFactory {

	private static CommandHandlerFactory factory;

	public static CommandHandlerFactory getInstance() {
		if (factory != null) {
			return factory;
		}
		return new CommandHandlerFactory();
	}

	public CommandHandler getHandler(final String[] command) {
		if (command[0].length() == 0) {
			// empty, do nothing
			return null;
		} else if (command[0].startsWith("//")) {
			// comment, do nothing
			return null;
		}
		switch (command[0]) {
		case "config":
			return new ConfigHandler();
		case "route":
			return new RouteHandler(command);
		case "port":
			return new PortHandler(command);
		case "connect":
			return new ConnectHandler(command);
		case "send":
			return new SendHandler(command);
		case "usend":
			return new USendHandler(command);
		case "asend":
			return new ASendHandler(command);
		case "include":
			return new IncludeHandler(command);
		case "troute":
			return new TrouteHandler(command);
		case "exit":
			return new ExitHandler();
		default:
			return new UnknownCommandHandler(command);
		}

	}

}

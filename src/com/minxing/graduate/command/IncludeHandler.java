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
				builder.append(CommandHandlerFactory.getInstance().getHandler(s).handle());
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

}

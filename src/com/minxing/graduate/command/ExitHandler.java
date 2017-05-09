package com.minxing.graduate.command;

public class ExitHandler implements CommandHandler {

	@Override
	public String handle() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		builder.append("releasing resources");
		builder.append(cr);
		builder.append("good bye");
		builder.append(cr);
		return builder.toString();
	}

}

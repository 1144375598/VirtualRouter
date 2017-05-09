package com.minxing.graduate.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.minxing.graduate.command.CommandHandler;
import com.minxing.graduate.command.CommandHandlerFactory;
import com.minxing.graduate.command.ExitHandler;
import com.minxing.graduate.ui.MainInterface;

public class ClientWorker implements Runnable {

	private final Socket socket;
	private final Logger logger = Logger.getLogger(ClientWorker.class.getName());

	/**
	 * @param socket
	 */
	public ClientWorker(final Socket socket) {
		this.socket = socket;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {

		try {
			final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			final PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

			// display welcome screen
			out.println(MainInterface.buildWelcomeScreen());

			boolean cancel = false;
			CommandHandlerFactory fac = CommandHandlerFactory.getInstance();
			while (!cancel) {

				final String[] command = getCommand(reader);
				if (command == null) {
					continue;
				}

				// handle the command
				final CommandHandler handler = fac.getHandler(command);
				if (handler == null) {
					continue;
				}
				String response = handler.handle();
				out.println(response);
				// command issuing an exit.
				if (handler instanceof ExitHandler) {
					cancel = true;
				}

			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Failed to close the socket", e);

			}
		}
	}

	private static String[] getCommand(final BufferedReader reader) {
		String inputString = "";
		String[] ret = new String[] { "" };

		System.out.print("\n" + System.getProperty("user.dir") + ":) ");

		try {
			inputString = reader.readLine();
			if (inputString == null) {
				return null;
			} else {

				inputString = inputString.trim();
				ret = inputString.split(" ");
			}
		} catch (Exception e) {

			System.out.println("router, getCommand: " + e.getMessage());
		}

		return ret;
	}
}

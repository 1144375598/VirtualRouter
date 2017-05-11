package com.minxing.graduate.model;

import java.nio.ByteBuffer;
import java.util.ArrayList;

// RoutingTable Class
// the class returns an IP of the next 'hop'
// should only have one instance of this class for all threads to search

public class RoutingTable {
	private ArrayList<IPv4[]> routeTable = new ArrayList<IPv4[]>();
	private IPv4 defaultRoute = new IPv4("0.0.0.0", new StringBuilder());
	private IPv4 badIP = new IPv4("0.0.0.0", new StringBuilder());

	// maximum number of routes not including default route
	private int maxRoutes;

	// constructor
	public RoutingTable(int maxRoutes) {
		this.maxRoutes = maxRoutes;

	}

	/*----------------------------------------------------------------------------------------*/
	public IPv4 nextRoute(IPv4 ip, StringBuilder builder) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		// System.out.println("RoutingTable: ... search table for gateway for
		// IP: " + ip.toString());
		IPv4 gateway = null;

		// Set IP Bytes
		byte[] testIP = ip.getIP();

		// Loop Through Routing Table Looking for Matching Network ID
		for (IPv4[] ipRecord : routeTable) {

			// -- NetworkIP w/ Subnet Mask
			IPv4 networkRec = ipRecord[0];
			IPv4 gatewayRec = ipRecord[1];

			// Set Subnet Mask byte array in some magical way
			ByteBuffer subnetMask = ByteBuffer.allocate(4)
					.putInt((int) (Long.reverse((long) Math.pow(2, networkRec.getIPSubBits()) - 1) >>> 32));
			byte[] subnet = new IPv4(subnetMask.array(), new StringBuilder()).getIP();

			// Set NetworkID byte array to Match
			// byte [] networkID = networkRec.getIP();

			// Outcome IP Address
			byte[] testIPOutcome = new byte[4];

			for (int x = 0; x < 4; x++) {
				testIPOutcome[x] = (byte) (testIP[x] & subnet[x]);
			}

			if (networkRec.equals(new IPv4(testIPOutcome, new StringBuilder()))) {
				gateway = gatewayRec;
			}

		}

		// -- Return Default if no ip found
		if (gateway == null) {
			// System.out.println("RoutingTable: ... no IP found, using
			// default");
			gateway = defaultRoute;
		}

		// -- Sanity Check that an IP address has been set
		if (gateway == null) {
			// TODO: Throw an exception up
			builder.append(("RoutingTable: errror no default route set"));
			builder.append(cr);
		}
		builder.append(("Successful RoutingTable: route the packet for " + ip.toString() + " to gateway: " + gateway.toString()));
		builder.append(cr);
		return gateway;
	}

	/*----------------------------------------------------------------------------------------*/
	// Add the default route to Routing Table
	public String addDefaultRoute(String route) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		IPv4 ipRoute = new IPv4(route, builder);
		if (!ipRoute.equals(badIP)) {
			defaultRoute = new IPv4(route, builder);
			builder.append("Successful new default route is: " + route);
			builder.append(cr);
		} else {
			builder.append("Bad IP, default route not set");
			builder.append(cr);
		}
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// Delete Default Route
	public String delDefaultRoute() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		defaultRoute = new IPv4("0.0.0.0", builder);
		builder.append(("Successful default route cleared"));
		builder.append(cr);
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	public String addRoute(String network, String gateway) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		IPv4 networkID = new IPv4(network, builder);
		IPv4 gatewayIP = new IPv4(gateway, builder);

		// check if table full
		if (routeTable.size() >= maxRoutes) {
			builder.append("Routing table max entries: " + maxRoutes);
			builder.append(cr);
			return builder.toString();
		}

		if (networkID.equals(badIP) || networkID.getIPSubBits() == 0 || gatewayIP.equals(badIP)) {
			builder.append("RT Error: Need a valid networkID with subnet mask and a valid gateway");
			builder.append(cr);
		} else {
			IPv4[] ipArr = new IPv4[2];

			ipArr[0] = networkID;
			ipArr[1] = gatewayIP;

			// -- Add record to table
			routeTable.add(ipArr);
			builder.append("Successful route " + network + " to " + gateway + " added");
			builder.append(cr);
		}
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// Remove route from the Routing Table
	public String delRoute(String network, String gateway) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		IPv4 networkID = new IPv4(network, builder);
		IPv4 gatewayIP = new IPv4(gateway, builder);
		int rtIndex = -1;

		if (networkID.equals(badIP) || networkID.getIPSubBits() == 0 || gatewayIP.equals(badIP)) {
			builder.append(("RT Error: Need a valid networkID with subnet mask and a valid gateway"));
		} else {

			// Define IP Record
			IPv4[] ipArr = new IPv4[2];
			ipArr[0] = networkID;
			ipArr[1] = gatewayIP;

			// Search for record in Array List
			for (IPv4[] rtIPArr : routeTable) {
				if (rtIPArr[0].equals(ipArr[0]) && rtIPArr[1].equals(ipArr[1])) {
					rtIndex = routeTable.indexOf(rtIPArr);
				}
			}

			// Remove record if match found
			if (rtIndex > -1) {
				routeTable.remove(rtIndex);
				builder.append(("Successful route " + network + " to " + gateway + " removed"));
				builder.append(cr);
			} else {
				// No match found
				builder.append(("No record found in routing table"));
				builder.append(cr);
			}

		}
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// remove all routes
	public String delAll() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		this.defaultRoute = new IPv4("0.0.0.0", builder);
		routeTable.clear();
		builder.append("Successful routing table cleared");
		builder.append(cr);
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// Display table in our console.
	public String printTable() {
		StringBuilder builder = new StringBuilder();
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		int size = routeTable.size();
		int def = 0;

		builder.append("Routing table");
		builder.append(cr);
		builder.append("-------------------------------------------------------------------------------");
		builder.append(cr);
		builder.append("NetworkID   \tSubnet Mask   \tGateway");
		builder.append(cr);
		builder.append("-------------------------------------------------------------------------------");
		builder.append(cr);
		for (IPv4[] ipArr : routeTable) {
			builder.append(String.format("%1$-" + 12 + "s", ipArr[0].toString()) + // networkID
																					// (padded)
			"\t  /" + ipArr[0].getIPSubBits() + // subnet mask
					"      \t" + ipArr[1].toString()); // gateway
			builder.append(cr);
		}

		if (!defaultRoute.equals(badIP)) {
			// Display default gateway if set
			builder.append("default       \t   --     \t" + defaultRoute.toString());
			builder.append(cr);
			def++;
		}
		builder.append("total routes: " + (size + def));
		builder.append(cr);
		builder.append("_______________________________________________________________________________");
		builder.append(cr);
		return builder.toString();
	}
	/*----------------------------------------------------------------------------------------*/

}

package com.minxing.graduate.model;


import com.minxing.graduate.manager.PortManager;

public class Router {

	// port admin class w/ 24 ports max
	private static PortManager portAdmin = new PortManager(48);

	// router table setup
	private static RoutingTable routeTable = new RoutingTable(1024);

	public static PortManager getPortAdmin() {
		return portAdmin;
	}

	public static void setPortAdmin(PortManager portAdmin) {
		Router.portAdmin = portAdmin;
	}

	public static RoutingTable getRouteTable() {
		return routeTable;
	}

	public static void setRouteTable(RoutingTable routeTable) {
		Router.routeTable = routeTable;
	}

}

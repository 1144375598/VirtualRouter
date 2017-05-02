import java.util.ArrayList;
import java.util.Arrays;
import java.nio.ByteBuffer;

// RoutingTable Class
// the class returns an IP of the next 'hop'
// should only have one instance of this class for all threads to search

public class RoutingTable {
	private ArrayList<IPv4[]> routeTable = new ArrayList<IPv4[]>();
    private IPv4 defaultRoute = new IPv4("0.0.0.0");
    private IPv4 badIP = new IPv4("0.0.0.0");
    
    // maximum number of routes not including default route
    private int maxRoutes;							
	
	// constructor 
	public RoutingTable(int maxRoutes) {
		
		this.maxRoutes = maxRoutes;
		
	/*
	 	RoutingTable rt = new RoutingTable();
		rt.addDefaultRoute(new IPv4("127.0.0.1"));
		IPv4 route2 = rt.nextRoute(new IPv4("127.1.1.1"));
		System.out.println(route2.toString());
	 */
		
	}
	/*----------------------------------------------------------------------------------------*/
	public IPv4 nextRoute(IPv4 ip) {
		//System.out.println("RoutingTable: ... search table for gateway for IP: " + ip.toString()); 
		IPv4 gateway = null; 
		
		// Set IP Bytes
		byte[] testIP = ip.getIP();
		
		
		// Loop Through Routing Table Looking for Matching Network ID
		for (IPv4[] ipRecord : routeTable) {
			
			//-- NetworkIP w/ Subnet Mask
			IPv4 networkRec = ipRecord[0];
			IPv4 gatewayRec = ipRecord[1];
			
			// Set Subnet Mask byte array in some magical way
			ByteBuffer subnetMask = ByteBuffer.allocate(4).putInt((int) (Long.reverse((long)Math.pow(2, networkRec.IPSubBits) - 1) >>> 32));
			byte []  subnet = new IPv4(subnetMask.array()).getIP();
			
			// Set NetworkID byte array to Match
			byte [] networkID = networkRec.getIP();
					
			// Outcome IP Address
			byte[]  testIPOutcome = new byte[4]; 
			
			for (int x = 0; x < 4; x ++){ 
				testIPOutcome[x] = (byte) (testIP[x] & subnet[x]); 
	        }
			
			/*
			System.out.println(Integer.toBinaryString(subnetMask.getInt(0)));
			System.out.println("subnetMaskBytes: " + new IPv4(subnetMaskBytes).toString());
			System.out.println("networkID: " + new IPv4(networkID).toString());
			System.out.println("testIP: " + ip.toString());
			System.out.println("testIPOutcome: " + new IPv4(testIPOutcome).toString());
			System.out.println("network: " + networkRec.toString());
			System.out.println(networkRec.equals(new IPv4(testIPOutcome))); */
						
			if (networkRec.equals(new IPv4(testIPOutcome))) {
				gateway = gatewayRec;
			}
			
		}

						
		//-- Return Default if no ip found
		if (gateway == null) {
			//System.out.println("RoutingTable: ... no IP found, using default"); 
			gateway = defaultRoute; 
		}
		
		//-- Sanity Check that an IP address has been set
		if (gateway == null) {
			//TODO: Throw an exception up
			System.out.println("RoutingTable: errror no default route set"); 
		}
		
		System.out.println("RoutingTable: route the packet for " + ip.toString() + " to gateay: " +gateway.toString());
		return gateway;
	}
	/*----------------------------------------------------------------------------------------*/
	// Add the default route to Routing Table
	public void addDefaultRoute(String route) {
		
		IPv4 ipRoute = new IPv4(route);
		if (!ipRoute.equals(badIP)) {
			
			defaultRoute = new IPv4(route);
			System.out.println("new default route is: " + route);
			
		} else {System.out.println("Bad IP, default route not set");}
		
	}
	/*----------------------------------------------------------------------------------------*/
	// Delete Default Route
	public void delDefaultRoute() {
		
		defaultRoute = new IPv4("0.0.0.0");	
		System.out.println("default route cleared");
	}
	/*----------------------------------------------------------------------------------------*/
	public void addRoute(String network, String gateway) {
		
		IPv4 networkID = new IPv4(network);
		IPv4 gatewayIP = new IPv4(gateway);
		
		// check if table full
		if(routeTable.size() >= maxRoutes) {
			System.out.println("Routing table max entries: " + maxRoutes);
			return;
		}
		
		if (networkID.equals(badIP) || networkID.IPSubBits == 0 || gatewayIP.equals(badIP)) {
			System.out.println("RT Error: Need a valid networkID with subnet mask and a valid gateway");
		} else {
			IPv4[] ipArr = new IPv4[2];
			 
			ipArr[0] = networkID;
			ipArr[1] = gatewayIP;
			 
			//-- Add record to table 
			routeTable.add(ipArr);
			System.out.println("route " + network + " to " + gateway + " added");
		}
	}
	/*----------------------------------------------------------------------------------------*/
	// Remove route from the Routing Table 
	public void delRoute(String network, String gateway) {
		
		IPv4 networkID = new IPv4(network);
		IPv4 gatewayIP = new IPv4(gateway);
		int rtIndex = -1;
		
		
		if (networkID.equals(badIP) || networkID.IPSubBits == 0 || gatewayIP.equals(badIP)) {
			System.out.println("RT Error: Need a valid networkID with subnet mask and a valid gateway");
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
			if(rtIndex > -1) {
				routeTable.remove(rtIndex);
				System.out.println("route " + network + " to " + gateway + " removed");
			} else { 
				// No match found
				System.out.println("No record found in routing table");
			}
			
		}		
	}
	/*----------------------------------------------------------------------------------------*/
	// remove all routes
	public void delAll() {
		
		this.defaultRoute = new IPv4("0.0.0.0");
		routeTable.clear();
		System.out.println("routing table cleared");
	}
	/*----------------------------------------------------------------------------------------*/
	// Display table in our console. 
	public void printTable() {
		  	
		int size = routeTable.size();
		int def = 0;
	   
		System.out.println("Routing table");
	    System.out.println("-------------------------------------------------------------------------------");
	    System.out.println("NetworkID   \tSubnet Mask   \tGateway");
	    System.out.println("-------------------------------------------------------------------------------");
	   	 
	    for (IPv4[] ipArr : routeTable) {
		    System.out.println(
		 		   String.format("%1$-" + 12 + "s", ipArr[0].toString()) + 	 // networkID (padded)
		 		   				 "\t  /" + ipArr[0].IPSubBits + 			 // subnet mask
		 		   				 "      \t" + ipArr[1].toString());			 // gateway 
	   }
	   
	   if(!defaultRoute.equals(badIP)) {
		   // Display default gateway if set
		   System.out.println("default       \t   --     \t" + defaultRoute.toString());
		   def++;
	   }	   
	   System.out.println("total routes: " + (size+def));
	   System.out.println("_______________________________________________________________________________");
		      
			
	}
	/*----------------------------------------------------------------------------------------*/
		
}

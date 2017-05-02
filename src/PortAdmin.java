import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

// maintain a list of ports

public class PortAdmin {

	// hash map 
	ConcurrentHashMap<Integer, Port> VRPorts;
	
	// max ports
	int maxPorts;
	
	/*----------------------------------------------------------------------------------------*/
	// constructor
	// create hash map w/ initial capacity 16 and load factor 0.75
	public PortAdmin(int maxPorts) {
		
		VRPorts = new  ConcurrentHashMap<Integer, Port>(16, (float) 0.75);
		this.maxPorts = maxPorts;
	}
	/*----------------------------------------------------------------------------------------*/
	// add port
	public void addPort(int portNo, String myIP, int mtu) {
		
		if(VRPorts.size() == maxPorts) {
			
			System.out.println("Maximum ports: " + maxPorts);
			return;
		}
		
		if(existsPort(portNo)) {
			System.out.println("port " + portNo + " exists");
		}
		else {
			try {
				
				Port p = new Port(portNo, myIP, mtu);
				VRPorts.put(portNo, p);
			
			} catch (SocketException e) {
				
				System.out.println("PortAdmin: add port " + portNo + " failed");
				e.printStackTrace();
				
			} catch (UnknownHostException e) {
				
				System.out.println("PortAdmin: add port " + portNo + " failed");
				e.printStackTrace();
			}
		}
			
	}
	/*----------------------------------------------------------------------------------------*/
	// delete port and call garbage collector
	public void removePort(int portNo) {
		
		if(existsPort(portNo)) {
			
			disconnect(portNo);
			VRPorts.get(portNo).datagramSocket.close();
			VRPorts.remove(portNo);
			System.out.println("port " + portNo + " deleted");
			try {
				
				finalize();
				
			} catch (Throwable e) {
				
				System.out.println("PortAdmin: errror finalizing"); 
				e.printStackTrace();
			}
		}
		else {
			System.out.println("port " + portNo + " does not exist");
		}
	}	
	/*----------------------------------------------------------------------------------------*/
	// remove all ports
	public void removeAll() {
		
		Enumeration<Integer> keys = VRPorts.keys();

		while(keys.hasMoreElements()) { 
			Object key = keys.nextElement();
			removePort((Integer)key);
		}	
		try { finalize(); } catch (Throwable e) { e.printStackTrace(); }
	}
	/*----------------------------------------------------------------------------------------*/
	// match ip and return port number
	public int getPort(byte[] ipArray) {
		
		Enumeration<Integer> keys = VRPorts.keys();
		byte[] ipNetworkID = new byte[4];

		
		while(keys.hasMoreElements()) { 
			Object key = keys.nextElement();
			
			byte[] subnet = VRPorts.get((Integer)key).getSubnet();		// get the ports subnet
			byte[] portNetID = VRPorts.get((Integer)key).getNetworkID();// get the ports network
			
			for(int i = 0; i < 4; i++) 
				ipNetworkID[i] = (byte) (ipArray[i] & subnet[i]);			// turn ipArray into network id
			
			
			if( Arrays.equals(portNetID, ipNetworkID))
				return(VRPorts.get((Integer)key).getPortNum());
		}
		
		return(-1);
		
	}/*----------------------------------------------------------------------------------------*/
	public void connect(int portNo, String connectStr) {
		
		if(existsPort(portNo)) {
			VRPorts.get(portNo).connect(connectStr);
		}
		else
			System.out.println("port " + portNo + " does not exists");
	}
	/*----------------------------------------------------------------------------------------*/
	public void disconnect(int portNo) {
				
		if(existsPort(portNo)) {
			VRPorts.get(portNo).disconnect();
		}
		else
			System.out.println("port " + portNo + " does not exists");
	}
	/*----------------------------------------------------------------------------------------*/
	// disconnect all ports
	public void disconnectAll() {
		
		Enumeration<Integer> keys = VRPorts.keys();
		
		while(keys.hasMoreElements()) { 
			Object key = keys.nextElement();
			VRPorts.get(key).disconnect();
		}	
	}
	/*----------------------------------------------------------------------------------------*/
	private boolean existsPort(int portNo) {
		
		return(VRPorts.containsKey(portNo));
	}
	/*----------------------------------------------------------------------------------------*/
	// send string in UDP 
	public <syncronized> void usend(int portNo, byte[] data) {
		
		if(existsPort(portNo)) {
			try {
				
				VRPorts.get(portNo).send(data);
				//System.out.println(data);
				
			} catch (IOException e) {
				
				System.out.println("PortAdmin: error sending data on port " + portNo);
				e.printStackTrace();
			}
		}
		else
			System.out.println("port " + portNo + " does not exists");	
	}
	/*----------------------------------------------------------------------------------------*/
	// send string in UDP 
	public void asend(byte[] data) {
		
		Enumeration<Integer> keys = VRPorts.keys();
		
		while(keys.hasMoreElements()) { 
			Object key = keys.nextElement();
			try {
				
				VRPorts.get(key).send(data);
				
			} catch (IOException e) {
				
				System.out.println("PortAdmin: error sending data on port " + key);
				e.printStackTrace();
			}
		}	
	}
	/*----------------------------------------------------------------------------------------*/
	// return port configuration as a string
	public String getAllPortsConfig() {
		
		String s = "";
		Enumeration<Integer> keys = VRPorts.keys();
		
		while(keys.hasMoreElements()) {
			Object key = keys.nextElement();
			s += VRPorts.get(key).getSettings();
		}
		
		return s;
	}
	/*----------------------------------------------------------------------------------------*/
	// route an Ethernet frame with an IP datagram test packed
	public <syncronized> void sendTestPacket(IPv4 srcIP, IPv4 dstIP, short id, int sampleLength) {
		
		// make an IPDatagram
		byte[] sampleData = VRMUtil.getSampleData(sampleLength).getBytes();
		IPDatagram samplePacket = new IPDatagram(sampleData, id, srcIP, dstIP);
		ByteBuffer macBytes = ByteBuffer.allocate(6);
		
		// get routing and port info
		IPv4 targetIP = Router.routeTable.nextRoute(dstIP);
		int port = getPort(targetIP.IPArray);
		
		if(port < 0) {
			
			System.out.println("no port found for " + targetIP.toString());
			return;
		}
		else {
		
			// make Ethernet frame
			MacAddress srcMAC = VRPorts.get(port).getMac();
			macBytes.put(dstIP.IPArray, 0, 4);						// M5:M4:M3:M2 = IP
			macBytes.putShort((short) port);						// M1:M0 = localPort
			MacAddress dstMAC = new MacAddress(macBytes.array());
			EthernetFrame tFrame = new EthernetFrame(dstMAC,srcMAC,(short) 0,samplePacket.toByteArray());
			
			// send frame
			usend(port,tFrame.toByteArray());
		}
		
	}
	/*----------------------------------------------------------------------------------------*/
	// route a received  Ethernet frame
	// this is the entire routing process right here
	public <syncronized> void route(byte[] frame) {
		
		
		EthernetFrame eframe = new EthernetFrame(frame);
		IPDatagram packet = new IPDatagram(eframe.getData());
		
		int ttl = packet.TTL.get(0);
		System.out.println("TTL: " + ttl);
		
		//drop the packet if TTL=0
		if(ttl > 0) {
			packet.TTL.put(0, (byte) (ttl-1));
		}
		else {
			System.out.println("TTL is 0, packet droped");
			return;
		}
		
		IPv4 dstIP = packet.getdstIP();
		ByteBuffer macBytes = ByteBuffer.allocate(6);
		
		// get routing and port info
		IPv4 targetIP = Router.routeTable.nextRoute(dstIP);
		int port = getPort(targetIP.IPArray);
		
		// if there is a port for the vitual IP, get it
		// other wise drop the packet
		if(port < 0) {
			
			System.out.println("no port found for " + targetIP.toString());
			return;
		}
		else {
			// make Ethernet frame
			MacAddress srcMAC = VRPorts.get(port).getMac();
			macBytes.put(dstIP.IPArray, 0, 4);						// M5:M4:M3:M2 = IP
			macBytes.putShort((short) port);						// M1:M0 = localPort
			MacAddress dstMAC = new MacAddress(macBytes.array());
			EthernetFrame tFrame = new EthernetFrame(dstMAC,srcMAC,(short) 0,packet.toByteArray());
			
			// send frame
			usend(port,tFrame.toByteArray());
		}
		
	}	
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/
}

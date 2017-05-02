// port class

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

// Port class
// 
public class Port {

	private MacAddress macAddress;			// hardware address for this port
	private int localPort;					// real port on this PC for UDP
	private IPv4 virtualIP;					// virtual IP for routing on this port
	private int MTU;						// this ports' MTU 
	private IPv4 remoteIP;					// physical remote IP for UDP packets
	private int remotePort;					// physical remote port for UDP packets
	private Listener listenPort;			// listener thread
	public DatagramSocket datagramSocket;	// used by listener and writer
	private boolean isConnected = false;	// true after connect, false after disconnect
	
	/*----------------------------------------------------------------------------------------*/
	// constructor 
	// setup a port, don't connect to anything yet
	// throws exception if port exists
	public Port(int localPort, String myIP, int mtu) throws SocketException, UnknownHostException {
		
		InetAddress inetAddress;
		this.datagramSocket = new DatagramSocket(localPort);
		this.localPort = localPort & 65535;							// port on this PC max 65535
		this.virtualIP = new IPv4(myIP);							// this ports' virtual IP
		this.MTU = mtu;												// this ports (segment) MTU
		
		// build the MAC address for this port
		ByteBuffer macBytes = ByteBuffer.allocate(6);
		inetAddress = InetAddress.getLocalHost(); 					// local host IP 
		macBytes.put(inetAddress.getAddress(), 0, 4);				// M5:M4:M3:M2 = IP
		macBytes.putShort((short) localPort);						// M1:M0 = localPort
		macAddress = new MacAddress(macBytes.array());				// set MAC address
		System.out.println("created port " + localPort + " " + macAddress.toDecString());

	}
	/*----------------------------------------------------------------------------------------*/
	// store remote IP and remote port number
	// start listener
	public void connect(String ipRemotePort)  {
		
		if(isConnected == true) {
			System.out.println(localPort + ": disconnect before connecting");
		}
		else {
			String[] t = ipRemotePort.split(":");						// no input checking
			remoteIP = new IPv4(t[0]);									// store remote IP
			remotePort = Integer.parseInt(t[1]);						// store remote port num
			
			System.out.println(localPort + " connected to " + remoteIP.toString() + ":" + remotePort);
			isConnected = true;	
			
			this.listenPort = new Listener(datagramSocket);				// new listener thread
			this.listenPort.start();									// start listening
			

		}

	}
	/*----------------------------------------------------------------------------------------*/
	// stop listening for packets
	public void disconnect() {
		
		if(isConnected == false) {
			System.out.println("port " + localPort + " already disconnected");
			return;
		}

		listenPort = null;											// destroy listener thread
		try {														// by forcing garbage collection
			
			finalize();												// collect garbage
			
		} catch (Throwable e) {
			
			System.out.println("port disconnect: finalize error " + localPort);
			e.printStackTrace();
		}	
		
		System.out.println("port " + localPort + " stopped listening");
		isConnected = false;	
		
	}
	/*----------------------------------------------------------------------------------------*/
	// package the data array in a UDP packed and send it to remoteIP:port
	public void send(byte[] data) throws IOException {
		
		
		if(isConnected == false) {
			System.out.println("can't send, port " + localPort + " is disconnected");
			return;
		}
		else if(data.length > 65508) {
			System.out.println("UDP data length too large (> 65508)");
			return;
		}
		
		InetAddress address = InetAddress.getByAddress(remoteIP.getIP());
		DatagramPacket packet = new DatagramPacket(data, data.length, address, remotePort);
		System.out.println("port " + localPort + " send: " + data.length +
				" bytes to " + remoteIP.toString() + ":"+ remotePort );
		String dataStr = new String(packet.getData());
		System.out.println(dataStr);
		datagramSocket.send(packet);

	}
	/*----------------------------------------------------------------------------------------*/
	public boolean isConnected() {
		
		return isConnected;
	}
	/*----------------------------------------------------------------------------------------*/
	public String getSettings() {
	
		String s = null;
		
		// MAC, local port, virtual IP,     MTU, remote IP, remote port, connect status
		s  = String.format("%-22s", macAddress.toDecString());
		s += String.format("%-6d", localPort);
		s += String.format("%s/", virtualIP.toString());
		s += String.format("%d\t", virtualIP.IPSubBits);
		
		s += String.format("%-5d", MTU);
		
		if(isConnected == true) {
			s += String.format("%-16s", remoteIP.toString());
			s += String.format("%-6d", remotePort);
		}
		else {
			s += String.format("%-22s","n/a             n/a");
		}
		s += String.format("%-4s\n", isConnected);
		
		return s;
	}
	/*----------------------------------------------------------------------------------------*/
	// return port number
	public int getPortNum() {
		
		return localPort;
	}
	/*----------------------------------------------------------------------------------------*/
	// return IP
	public byte[] getVirtualIP() {
		
		return virtualIP.IPArray;
	}
	/*----------------------------------------------------------------------------------------*/
	public MacAddress getMac() {
		
		return macAddress;
	}
	/*----------------------------------------------------------------------------------------*/
	// return this IP object
	public IPv4 getIPv4() {
		
		return virtualIP;
	}
	/*----------------------------------------------------------------------------------------*/
	public byte[] getNetworkID() {
		
		return virtualIP.networkID;
	}
	/*----------------------------------------------------------------------------------------*/
	public byte[] getSubnet() {
		
		return virtualIP.IPSubnet;
	}
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/

}

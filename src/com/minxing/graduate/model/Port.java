package com.minxing.graduate.model;
// port class

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import com.minxing.graduate.thread.Listener;

// Port class
// 
public class Port {

	private MacAddress macAddress; // hardware address for this port
	private int localPort; // real port on this PC for UDP
	private IPv4 virtualIP; // virtual IP for routing on this port
	private int MTU; // this ports' MTU
	private IPv4 remoteIP; // physical remote IP for UDP packets
	private int remotePort; // physical remote port for UDP packets
	private Listener listenPort; // listener thread
	public DatagramSocket datagramSocket; // used by listener and writer
	private boolean isConnected = false; // true after connect, false after
											// disconnect

	/*----------------------------------------------------------------------------------------*/
	// constructor
	// setup a port, don't connect to anything yet
	// throws exception if port exists
	public Port(int localPort, String myIP, int mtu, StringBuilder builder)
			throws SocketException, UnknownHostException {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		InetAddress inetAddress;
		this.datagramSocket = new DatagramSocket(localPort);
		this.localPort = localPort & 65535; // port on this PC max 65535
		this.virtualIP = new IPv4(myIP, builder); // this ports' virtual IP
		this.MTU = mtu; // this ports (segment) MTU

		// build the MAC address for this port
		ByteBuffer macBytes = ByteBuffer.allocate(6);
		inetAddress = InetAddress.getLocalHost(); // local host IP
		macBytes.put(inetAddress.getAddress(), 0, 4); // M5:M4:M3:M2 = IP
		macBytes.putShort((short) localPort); // M1:M0 = localPort
		macAddress = new MacAddress(macBytes.array(), builder); // set MAC
																// address
		builder.append(("Successful created port " + localPort + " " + macAddress.toDecString()));
		builder.append(cr);

	}

	/*----------------------------------------------------------------------------------------*/
	// store remote IP and remote port number
	// start listener
	public String connect(String ipRemotePort) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		if (isConnected == true) {
			builder.append((localPort + ": disconnect before connecting"));
			builder.append(cr);
		
		} else {
			String[] t = ipRemotePort.split(":"); // no input checking
			remoteIP = new IPv4(t[0], builder); // store remote IP
			remotePort = Integer.parseInt(t[1]); // store remote port num

			builder.append(("Successful "+localPort + " connected to " + remoteIP.toString() + ":" + remotePort));
			builder.append(cr);
			isConnected = true;

			this.listenPort = new Listener(datagramSocket); // new listener
															// thread
			this.listenPort.start(); // start listening

		}
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// stop listening for packets
	public String disconnect() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		if (isConnected == false) {
			builder.append(("port " + localPort + " already disconnected"));
			builder.append(cr);
			return builder.toString();
		}

		listenPort = null; // destroy listener thread
		try { // by forcing garbage collection

			finalize(); // collect garbage

		} catch (Throwable e) {

			builder.append(("port disconnect: finalize error " + localPort));
			builder.append(cr);
			e.printStackTrace();
		}

		builder.append(("Successful "+"port " + localPort + " stopped listening"));
		builder.append(cr);
		isConnected = false;
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// package the data array in a UDP packed and send it to remoteIP:port
	public String send(byte[] data) throws IOException {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		if (isConnected == false) {
			builder.append(("can't send, port " + localPort + " is disconnected"));
			builder.append(cr);
			return builder.toString();
		} else if (data.length > 65508) {
			builder.append(("UDP data length too large (> 65508)"));
			builder.append(cr);
			return builder.toString();
		}

		InetAddress address = InetAddress.getByAddress(remoteIP.getIP());
		DatagramPacket packet = new DatagramPacket(data, data.length, address, remotePort);
		builder.append(("Successful "+"port " + localPort + " send: " + data.length + " bytes to " + remoteIP.toString() + ":"
				+ remotePort));
		builder.append(cr);
		String dataStr = new String(packet.getData());
		builder.append((dataStr));
		builder.append(cr);
		datagramSocket.send(packet);
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	public boolean isConnected() {

		return isConnected;
	}

	/*----------------------------------------------------------------------------------------*/
	public String getSettings() {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		String s = null;

		// MAC, local port, virtual IP, MTU, remote IP, remote port, connect
		// status
		s = String.format("%-22s", macAddress.toDecString());
		s += String.format("%-6d", localPort);
		s += String.format("%s/", virtualIP.toString());
		s += String.format("%d\t", virtualIP.getIPSubBits());

		s += String.format("%-5d", MTU);

		if (isConnected == true) {
			s += String.format("%-16s", remoteIP.toString());
			s += String.format("%-6d", remotePort);
		} else {
			s += String.format("%-22s", "n/a             n/a");
		}
		s += String.format("%-4s", isConnected);
		s += cr;
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

		return virtualIP.getIPArray();
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

		return virtualIP.getNetworkID();
	}

	/*----------------------------------------------------------------------------------------*/
	public byte[] getSubnet() {

		return virtualIP.getIPSubnet();
	}
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/

}

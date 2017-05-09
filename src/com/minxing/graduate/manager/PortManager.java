package com.minxing.graduate.manager;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

import com.minxing.graduate.model.EthernetFrame;
import com.minxing.graduate.model.IPDatagram;
import com.minxing.graduate.model.IPv4;
import com.minxing.graduate.model.MacAddress;
import com.minxing.graduate.model.Port;
import com.minxing.graduate.model.Router;
import com.minxing.graduate.util.VRMUtil;

// maintain a list of ports

public class PortManager {

	// hash map
	ConcurrentHashMap<Integer, Port> VRPorts;

	// max ports
	int maxPorts;

	/*----------------------------------------------------------------------------------------*/
	// constructor
	// create hash map w/ initial capacity 16 and load factor 0.75
	public PortManager(int maxPorts) {

		VRPorts = new ConcurrentHashMap<Integer, Port>(16, (float) 0.75);
		this.maxPorts = maxPorts;
	}

	/*----------------------------------------------------------------------------------------*/
	// add port
	public String addPort(int portNo, String myIP, int mtu) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		if (VRPorts.size() == maxPorts) {

			builder.append(("Maximum ports: " + maxPorts));
			builder.append(cr);
			return builder.toString();
		}

		if (existsPort(portNo)) {
			builder.append(("port " + portNo + " exists"));
			builder.append(cr);
		} else {
			try {

				Port p = new Port(portNo, myIP, mtu, builder);
				VRPorts.put(portNo, p);

			} catch (SocketException e) {

				builder.append(("PortAdmin: add port " + portNo + " failed"));
				builder.append(cr);
				e.printStackTrace();

			} catch (UnknownHostException e) {

				builder.append(("PortAdmin: add port " + portNo + " failed"));
				builder.append(cr);
				e.printStackTrace();
			}
		}
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// delete port and call garbage collector
	public String removePort(int portNo) {

		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();

		if (existsPort(portNo)) {

			disconnect(portNo);
			VRPorts.get(portNo).datagramSocket.close();
			VRPorts.remove(portNo);
			builder.append(("port " + portNo + " deleted"));
			builder.append(cr);
			try {

				finalize();

			} catch (Throwable e) {

				builder.append(("PortAdmin: errror finalizing"));
				builder.append(cr);
				e.printStackTrace();
			}
		} else {
			builder.append(("port " + portNo + " does not exist"));
			builder.append(cr);
		}
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// remove all ports
	public String removeAll() {
		StringBuilder builder = new StringBuilder();

		Enumeration<Integer> keys = VRPorts.keys();

		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			builder.append(removePort((Integer) key));
		}
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// match ip and return port number
	public int getPort(byte[] ipArray) {

		Enumeration<Integer> keys = VRPorts.keys();
		byte[] ipNetworkID = new byte[4];

		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();

			byte[] subnet = VRPorts.get((Integer) key).getSubnet(); // get the
																	// ports
																	// subnet
			byte[] portNetID = VRPorts.get((Integer) key).getNetworkID();// get
																			// the
																			// ports
																			// network

			for (int i = 0; i < 4; i++)
				ipNetworkID[i] = (byte) (ipArray[i] & subnet[i]); // turn
																	// ipArray
																	// into
																	// network
																	// id

			if (Arrays.equals(portNetID, ipNetworkID))
				return (VRPorts.get((Integer) key).getPortNum());
		}

		return (-1);

	}/*----------------------------------------------------------------------------------------*/

	public String connect(int portNo, String connectStr) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();

		if (existsPort(portNo)) {
			return VRPorts.get(portNo).connect(connectStr);
		} else
			builder.append(("port " + portNo + " does not exists"));
		builder.append(cr);
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	public String disconnect(int portNo) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		if (existsPort(portNo)) {
			return VRPorts.get(portNo).disconnect();
		} else
			builder.append(("port " + portNo + " does not exists"));
		builder.append(cr);
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// disconnect all ports
	public String disconnectAll() {
		StringBuilder builder = new StringBuilder();
		Enumeration<Integer> keys = VRPorts.keys();

		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			builder.append(VRPorts.get(key).disconnect());
		}
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	private boolean existsPort(int portNo) {

		return (VRPorts.containsKey(portNo));
	}

	/*----------------------------------------------------------------------------------------*/
	// send string in UDP
	public <syncronized> String usend(int portNo, byte[] data) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		if (existsPort(portNo)) {
			try {

				builder.append(VRPorts.get(portNo).send(data));
				// System.out.println(data);

			} catch (IOException e) {

				builder.append(("PortAdmin: error sending data on port " + portNo));
				builder.append(cr);
				e.printStackTrace();
			}
		} else
			builder.append(("port " + portNo + " does not exists"));
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// send string in UDP
	public String asend(byte[] data) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		Enumeration<Integer> keys = VRPorts.keys();

		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			try {

				builder.append(VRPorts.get(key).send(data));

			} catch (IOException e) {

				builder.append(("PortAdmin: error sending data on port " + key));
				builder.append(cr);
				e.printStackTrace();
			}
		}
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// return port configuration as a string
	public String getAllPortsConfig() {

		String s = "";
		Enumeration<Integer> keys = VRPorts.keys();

		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			s += VRPorts.get(key).getSettings();
		}

		return s;
	}

	/*----------------------------------------------------------------------------------------*/
	// route an Ethernet frame with an IP datagram test packed
	public <syncronized> String sendTestPacket(IPv4 srcIP, IPv4 dstIP, short id, int sampleLength) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		// make an IPDatagram
		byte[] sampleData = VRMUtil.getSampleData(sampleLength).getBytes();
		IPDatagram samplePacket = new IPDatagram(sampleData, id, srcIP, dstIP, builder);
		ByteBuffer macBytes = ByteBuffer.allocate(6);

		// get routing and port info
		IPv4 targetIP = Router.getRouteTable().nextRoute(dstIP, builder);
		int port = getPort(targetIP.getIPArray());

		if (port < 0) {

			builder.append(("no port found for " + targetIP.toString()));
			builder.append(cr);
			return builder.toString();
		} else {

			// make Ethernet frame
			MacAddress srcMAC = VRPorts.get(port).getMac();
			macBytes.put(dstIP.getIPArray(), 0, 4); // M5:M4:M3:M2 = IP
			macBytes.putShort((short) port); // M1:M0 = localPort
			MacAddress dstMAC = new MacAddress(macBytes.array(), builder);
			EthernetFrame tFrame = new EthernetFrame(dstMAC, srcMAC, (short) 0, samplePacket.toByteArray(), builder);

			// send frame
			usend(port, tFrame.toByteArray());
		}
		return builder.toString();
	}

	/*----------------------------------------------------------------------------------------*/
	// route a received Ethernet frame
	// this is the entire routing process right here
	public <syncronized> String route(byte[] frame) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		StringBuilder builder = new StringBuilder();
		EthernetFrame eframe = new EthernetFrame(frame, builder);
		IPDatagram packet = new IPDatagram(eframe.getData(), builder);

		int ttl = packet.TTL.get(0);
		builder.append(("TTL: " + ttl));
		builder.append(cr);

		// drop the packet if TTL=0
		if (ttl > 0) {
			packet.TTL.put(0, (byte) (ttl - 1));
		} else {
			builder.append(("TTL is 0, packet droped"));
			builder.append(cr);
			return builder.toString();
		}

		IPv4 dstIP = packet.getdstIP();
		ByteBuffer macBytes = ByteBuffer.allocate(6);

		// get routing and port info
		IPv4 targetIP = Router.getRouteTable().nextRoute(dstIP, builder);
		int port = getPort(targetIP.getIPArray());

		// if there is a port for the vitual IP, get it
		// other wise drop the packet
		if (port < 0) {

			builder.append(("no port found for " + targetIP.toString()));
			builder.append(cr);
			return builder.toString();
		} else {
			// make Ethernet frame
			MacAddress srcMAC = VRPorts.get(port).getMac();
			macBytes.put(dstIP.getIPArray(), 0, 4); // M5:M4:M3:M2 = IP
			macBytes.putShort((short) port); // M1:M0 = localPort
			MacAddress dstMAC = new MacAddress(macBytes.array(), builder);
			EthernetFrame tFrame = new EthernetFrame(dstMAC, srcMAC, (short) 0, packet.toByteArray(), builder);

			// send frame
			builder.append(usend(port, tFrame.toByteArray()));
		}
		return builder.toString();
	}
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/
}

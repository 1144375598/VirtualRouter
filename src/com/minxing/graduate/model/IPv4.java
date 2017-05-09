package com.minxing.graduate.model;

import java.nio.ByteBuffer;
import java.util.Arrays;

// IPv4 class
// treat byte arrays as big endians

public class IPv4 {

	// class variables
	private int remotePort;

	// 4 byte IP array
	private byte[] IPArray = new byte[4];
	private byte[] networkID = new byte[4];
	private byte[] IPSubnet;
	private int IPSubBits;

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public byte[] getIPArray() {
		return IPArray;
	}

	public void setIPArray(byte[] iPArray) {
		IPArray = iPArray;
	}

	public byte[] getNetworkID() {
		return networkID;
	}

	public void setNetworkID(byte[] networkID) {
		this.networkID = networkID;
	}

	public byte[] getIPSubnet() {
		return IPSubnet;
	}

	public void setIPSubnet(byte[] iPSubnet) {
		IPSubnet = iPSubnet;
	}

	public int getIPSubBits() {
		return IPSubBits;
	}

	public void setIPSubBits(int iPSubBits) {
		IPSubBits = iPSubBits;
	}

	/*----------------------------------------------------------------------------------------*/
	// constructor takes a string in standard IP octet form "ddd.ddd.ddd.ddd"
	// decimals are parsed into bytes. decimals > 255 are truncated
	// IP v4 only
	public IPv4(String ipIn, StringBuilder builder) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		String[] srcStr = ipIn.split("/"); // for port
		String[] IPStr = srcStr[0].split("\\."); // split at literal '.'
													// (period)
		if (IPStr.length != 4) {
			builder.append(("IPv4 need: ddd.ddd.ddd.ddd")); // throw exception
															// here
			builder.append(cr);
		} else
			try {
				for (int i = 0; i < 4; i++)
					IPArray[i] = (byte) Integer.parseInt(IPStr[i]);
			} catch (Throwable e) {
				System.out.println(e.toString());
			}

		// if a subnet was given make the mask and network id
		if (srcStr.length == 2) {
			IPSubBits = Integer.parseInt(srcStr[1]) & 31; // subnet int max = 31
			IPSubnet = (ByteBuffer.allocate(4).putInt((int) (Long.reverse((long) Math.pow(2, IPSubBits) - 1) >>> 32)))
					.array();
			for (int i = 0; i < 4; i++)
				networkID[i] = (byte) (IPArray[i] & IPSubnet[i]);
		}
	}

	/*----------------------------------------------------------------------------------------*/
	// constructor takes 4 byte array[]
	// IP v4 only
	public IPv4(byte[] ipIn, StringBuilder builder) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		if (ipIn.length != 4)
			builder.append(("IPv4 class: bad IP array")); // throw exception
		builder.append(cr); // here

		IPArray = ipIn.clone(); // assign a copy of ipIn
	}

	/*----------------------------------------------------------------------------------------*/
	public void setIP(byte[] ipIn, StringBuilder builder) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		if (ipIn.length != 4)
			builder.append(("IPv4 class: bad IP array")); // throw exception
		builder.append(cr); // here

		IPArray = ipIn.clone(); // assign a copy of ipIn
	}

	/*----------------------------------------------------------------------------------------*/
	// compare IParray to input arg
	public boolean equals(byte[] ipIn) {

		return Arrays.equals(IPArray, ipIn);
	}

	/*----------------------------------------------------------------------------------------*/
	// compare any two primitives byte arrays
	public static boolean equals(byte[] ip1, byte[] ip2) {

		return Arrays.equals(ip1, ip2);
	}

	/*----------------------------------------------------------------------------------------*/
	// compare this IP with ipIn
	public boolean equals(IPv4 ipIn) {

		return Arrays.equals(IPArray, ipIn.IPArray);
	}

	/*----------------------------------------------------------------------------------------*/
	// return copy of IP byte array
	public byte[] getIP() {

		return IPArray.clone(); // return a copy of IPArray
	}

	/*----------------------------------------------------------------------------------------*/
	// return string representation in standard form ddd.ddd.ddd.ddd
	public String toString() {

		return String.format("%d.%d.%d.%d", IPArray[0] & 255, IPArray[1] & 255, IPArray[2] & 255, IPArray[3] & 255);
	}

	/*----------------------------------------------------------------------------------------*/
	// return binary string representation
	public String toBinString() {

		String s = "";

		for (int i = 0; i < 4; i++)
			s += String.format("%8s", Integer.toBinaryString(IPArray[i] & 255)).replace(' ', '0') + " ";

		return s;
	}
	/*----------------------------------------------------------------------------------------*/

}

package com.minxing.graduate.model;

import java.nio.ByteBuffer;
import java.util.Arrays;

// IP datagram class
// IPv4 only
// 6 bytes MAC
// 4 bytes IP

public class IPDatagram {

	// class variables
	private ByteBuffer versionAndHLength = ByteBuffer.allocate(1); // IPversion
																	// + header
																	// length
	private ByteBuffer typeOfService = ByteBuffer.allocate(1); // not used set
																// to 0
	private ByteBuffer totalLength = ByteBuffer.allocate(2); // total packet
																// length
	private ByteBuffer ID = ByteBuffer.allocate(2); // packed id
	private ByteBuffer FlagsFOffset = ByteBuffer.allocate(2); // flags and
																// fragment
																// offset
	public ByteBuffer TTL = ByteBuffer.allocate(1); // max hops
	private ByteBuffer protocol = ByteBuffer.allocate(1); // protocol = 4
	private ByteBuffer headerChecksum = ByteBuffer.allocate(2); // simple header
																// checksum
	private IPv4 srcIP; // source IP
	private IPv4 dstIP;// destination IP
	// private ByteBuffer options; // not used
	private byte[] data; // pay load

	/*----------------------------------------------------------------------------------------*/
	public IPDatagram(byte[] dataIn, short id, IPv4 srcIP, IPv4 dstIP, StringBuilder builder) {
		String cr = System.getProperty("os.name").matches("(W|w)indows.*") ? "\r\n" : "\n";
		if (dataIn.length > 65535) {
			builder.append(("IPDatagram: data truncated to 65,515 bytes"));
			builder.append(cr);
		}

		this.versionAndHLength.put((byte) 69); // IPv4=4 HLen=5
		this.typeOfService.put((byte) 123); // not used
		this.totalLength.putShort(0, (short) (20 + dataIn.length)); // total
																	// length of
																	// the
																	// packet
		this.ID.putShort(0, id); // packet ID
		this.FlagsFOffset.putShort(0, (short) 0); // flags and fragment offset
		this.TTL.put((byte) 5); // Time To Live
		this.protocol.put((byte) 4); // protocol = 4
		this.headerChecksum.putShort(0, (short) 0); // 0 for now
		this.srcIP = srcIP; // source IP
		this.dstIP = dstIP; // destination IP

		if (dataIn.length > 65515) { // max pay load = 65,535 bytes
			this.data = Arrays.copyOfRange(dataIn, 0, 65515); // truncate
			builder.append(("IPDatagram: data truncated to 65,515 bytes"));
			builder.append(cr);
		} else
			this.data = dataIn;

	}

	/*----------------------------------------------------------------------------------------*/
	public IPDatagram(byte[] packetIn, StringBuilder builder) {
		versionAndHLength.put(packetIn[0]);
		typeOfService.put(packetIn[1]);
		// System.arraycopy(packetIn, 2, totalLength, 0, 2);
		totalLength.put(packetIn, 2, 2);
		ID.put(packetIn, 4, 2);
		FlagsFOffset.put(packetIn, 6, 2);
		TTL.put(packetIn, 8, 1);
		protocol.put(packetIn, 9, 1);
		headerChecksum.put(packetIn, 10, 2);

		srcIP.setIP(Arrays.copyOfRange(packetIn, 12, 16), builder);
		dstIP.setIP(Arrays.copyOfRange(packetIn, 16, 20), builder);
		data = Arrays.copyOfRange(packetIn, 20, packetIn.length);

	}

	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/
	public byte[] toByteArray() {

		byte[] ipDatagram = new byte[totalLength.getShort(0)];

		System.arraycopy(versionAndHLength.array(), 0, ipDatagram, 0, 1); // copy
																			// version#
																			// and
																			// header
																			// length
																			// to
																			// datagram
		System.arraycopy(typeOfService.array(), 0, ipDatagram, 1, 1); // type of
																		// service
																		// byte
		System.arraycopy(totalLength.array(), 0, ipDatagram, 2, 2);
		System.arraycopy(ID.array(), 0, ipDatagram, 4, 2);
		System.arraycopy(FlagsFOffset.array(), 0, ipDatagram, 6, 2);
		System.arraycopy(TTL.array(), 0, ipDatagram, 8, 1);
		System.arraycopy(protocol.array(), 0, ipDatagram, 9, 1);
		System.arraycopy(headerChecksum.array(), 0, ipDatagram, 10, 2);
		System.arraycopy(srcIP.getIP(), 0, ipDatagram, 12, 4);
		System.arraycopy(dstIP.getIP(), 0, ipDatagram, 16, 4);
		System.arraycopy(data, 0, ipDatagram, 20, data.length);

		return ipDatagram;
	}

	/*----------------------------------------------------------------------------------------*/
	// return datagram as hex string
	// human consumption only
	public String toHexString(byte[] buffer) {

		String s = "";
		int i = 0;

		s += String.format("%02x ", buffer[0]) + "\n";
		s += String.format("%02x ", buffer[1]) + "\n";
		for (i = 2; i < 4; i++)
			s += String.format("%02x ", buffer[i]);
		s += "\n";
		for (i = 4; i < 6; i++)
			s += String.format("%02x ", buffer[i]);
		s += "\n";
		for (i = 6; i < 8; i++)
			s += String.format("%02x ", buffer[i]);
		s += "\n";

		s += String.format("%02x ", buffer[8]) + "\n";
		s += String.format("%02x ", buffer[9]) + "\n";

		for (i = 10; i < 12; i++)
			s += String.format("%02x ", buffer[i]);
		s += "\n";

		for (i = 12; i < 16; i++)
			s += String.format("%02x ", buffer[i]);
		s += "\n";

		for (i = 16; i < 20; i++)
			s += String.format("%02x ", buffer[i]);
		s += "\n";

		for (i = 20; i < data.length + 20; i++)
			s += String.format("%02x ", buffer[i]);
		s += "\n";
		return s;
	}

	/*----------------------------------------------------------------------------------------*/
	//
	public String toString() {

		String s, t = "";
		t = new String(data);
		// replace all non printable here

		s = "version and header length: " + versionAndHLength.get(0) + "\n" + "type of service:           "
				+ typeOfService.get(0) + "\n" + "total length:              " + totalLength.getShort(0) + "\n"
				+ "packet ID:                 " + ID.getShort(0) + "\n" + "Flags and fragment offset: "
				+ FlagsFOffset.getShort(0) + "\n" + "TTL:                       " + TTL.get(0) + "\n"
				+ "protocol:                  " + protocol.get(0) + "\n" + "header checksum:           "
				+ headerChecksum.getShort(0) + "\n" + "source IP:                 " + srcIP.toString() + "\n"
				+ "destinaton IP:             " + dstIP.toString() + "\n" + "data:                      " + t;

		return (s);
	}

	/*----------------------------------------------------------------------------------------*/
	public IPv4 getdstIP() {

		return dstIP;
	}
	/*----------------------------------------------------------------------------------------*/

}

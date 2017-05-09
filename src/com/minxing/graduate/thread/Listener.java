package com.minxing.graduate.thread;
// listener thread for DatagramSocket

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.minxing.graduate.model.Router;


public class Listener extends Thread {

	// class variables
	private byte[] buffer = new byte[4000];			// max Ethernet frame size
    private DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    private DatagramSocket datagramSocket;
	
	//constructor
	public Listener(DatagramSocket dgSocket) {
				
		this.datagramSocket = dgSocket;
	}
	/*----------------------------------------------------------------------------------------*/
	// start execution here
	// listen for packets and print data
    public void run() {
    	
		System.out.println(String.format("new thread %d listening on port %d", this.getId(), 
				datagramSocket.getLocalPort()));

		while(true) {
			
			try {
				
				if(datagramSocket.isClosed())
					break;				
				datagramSocket.receive(packet);
				String dataStr = new String(packet.getData(),0, packet.getLength());
				System.out.println(String.format("thread %d port %d received: %d bytes from %s:%d \n%s\n", 
						this.getId(),datagramSocket.getLocalPort(),
						packet.getLength(), packet.getAddress().toString(), packet.getPort(), dataStr ));
				Router.getPortAdmin().route(dataStr.getBytes());
				
			} catch (IOException e) {
				
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}
		System.out.println("thread " + this.getId() + " died");
    }
	/*----------------------------------------------------------------------------------------*/

}

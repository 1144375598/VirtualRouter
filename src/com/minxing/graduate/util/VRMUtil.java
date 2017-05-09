package com.minxing.graduate.util;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

// static class for miscellaneous functions
public class VRMUtil {
	
	// no variables

	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/
	// reverse bits in byteIn
	static public byte reverseByte(byte byteIn) {
		
		return (byte) (Integer.reverse((int) byteIn) >>> 24);
	}
	/*----------------------------------------------------------------------------------------*/
	// reverse bits in each byte byteIn
	// return new array
	static public void reverseArrayBits(byte[] bytesIn) {
		
		for(int i = 0; i < bytesIn.length; i++)
			bytesIn[i] = reverseByte(bytesIn[i]);
		
	}
	/*----------------------------------------------------------------------------------------*/
	// return an array with 6 random bytes
	public static byte[] getRandomdMac() {
		
		byte randomMac[] = new byte[6];
		Random random = new Random();
		
		for(int i = 0; i < 6; i++) 
			randomMac[i] = (byte) random.nextInt(256) ;
		
		return randomMac;
	}
	/*----------------------------------------------------------------------------------------*/
	// return CRC32 as int
	public static int getCRC(byte[] data) {
		
		CRC32 testCRC = new CRC32();
		int ret;
		
//		reverseArrayBits(data);
//		invertFirst32(data);

		testCRC.reset();
		testCRC.update(data, 0, data.length);
		//System.out.println("long value: " + Long.toHexString(testCRC.getValue()));	
		ret = (int)testCRC.getValue();
		
//		invertFirst32(data);
//		reverseArrayBits(data);

		return ret /*^ 0xffffffff*/;
		
	}
	/*----------------------------------------------------------------------------------------*/
	// return CRC32 as 4 byte hex string
	public static String getCRCStr(byte[] data) {
		
		int crc = getCRC(data);
		
		return String.format("%04x", crc);
		
	}
	/*----------------------------------------------------------------------------------------*/
	// invert first 32 bits of data[]
	public static void invertFirst32(byte[] data) {
		
		for(int i = 0; i < 4; i++)
			data[i] ^= 0xff;												// invert byte
		
	}
	/*----------------------------------------------------------------------------------------*/
	// check if frame is valid
	public static boolean frameValid(byte[] data) {
		
		byte[] frame;
		ByteBuffer crc = ByteBuffer.allocate(4);
		
		crc.put(Arrays.copyOfRange(data, data.length-4, data.length));		// extract CRC
		frame = Arrays.copyOfRange(data, 0, data.length-4);					// remove CRC from frame
		
		boolean ret = (crc.getInt(0) == getCRC(frame));						// compare CRCs

		return ret;
	}
	/*----------------------------------------------------------------------------------------*/
	// return some sample data a string of length dataLength
	// truncates to 1024 bytes
	public static String getSampleData(int dataLength) {
		
		int a,b;
		String data = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String ret = "";
		
		try {
			if(dataLength > 1024) {
				dataLength = 1024;
				System.out.println("VRMUtil: sample data length reduced to " + dataLength);
			}
			
			a = dataLength / 26;
			b = dataLength % 26;
			for(int i = 0; i < a; i++)
				ret += data;
			ret += new String(Arrays.copyOfRange(data.getBytes(), 0, b));
		} catch (Exception e) {
			// unspecified error
			System.out.println("VRMUtil: can't produce sample data");
			return null;
		}	
		
		System.out.println("created " + ret.length() + " bytes of sample data");
		return ret;
	}
	/*----------------------------------------------------------------------------------------*/
	/*----------------------------------------------------------------------------------------*/

}

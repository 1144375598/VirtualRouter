package com.minxing.graduate.util;
/**
 *
 * LoadFile is used to read and return the lines within a text file
 * 
 */
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReader {
	
	// variables
	Scanner input;
	java.io.File file;
	
    //constructor
    public FileReader(String fileName) throws FileNotFoundException
    {
		  file = new java.io.File(fileName);
		  input = new Scanner(file);
    }
    //Reads strings from the txt file
    public String readLine()
    {
	    if (input.hasNext())
		    return input.nextLine();
	    else
	    	return "";
 	 }
    // true if more lines to read
    public boolean hasNext()
    {
        return input.hasNext();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os.regv.os;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;
/**
 *
 * @author gabrius
 */
public class HardDisk {
    public HardDisk(){}

    private Scanner input = new Scanner(System.in);
    private static String header_format =  "STRT"; 
    private static String ending_format = "STOP";
    private static String prog_name;
    private static String buffer;
    private static boolean ending = false;

// Compare 2 arrays.
public static boolean compare_Commands(char[] array1, char[] array2, int length){
  for (int j = 0; j < length; j++) 
    if (array1[j] != array2[j]) 
      return false;
  
  
  return true;
}

public static int[] getFilenameAsInts(String filenameAsString) throws UnsupportedEncodingException, Exception {
        int[] filenameAsInts = new int[3];

        if (filenameAsString.length() <= 12) {
            byte[] bytesOfNameAscii = filenameAsString.getBytes("US-ASCII");
            byte[] bytesOfName = Arrays.copyOf(bytesOfNameAscii, 12);

            for (int i = 0; i < 3; i++) {
                filenameAsInts[i] = ByteBuffer.wrap(bytesOfName, i * 4, 4).getInt();
            }
        } else {
            throw new Exception("Failo pavadinimas per ilgas!");
        }

        return filenameAsInts;
    }
}

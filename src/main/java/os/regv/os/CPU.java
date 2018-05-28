/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os.regv.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import lombok.Data;

/**
 *
 * @author gabrius
 */
@Data
public class CPU {
    
    private ArrayList<Integer> page;
    private ArrayList<Memory> memoryList;
    //private ArrayList<Memory> memoryList2;
    private Memory mem;
    private Memory mem1;
    private Memory mem2;
    public static boolean jauBuvo;
    private int PTR = 0;
    //private int PI = 0;
    public static final int TIMER_CONST = 10;
    private static short IP = 0;
    private int block;
    private int word;
    /**
     * 1 - vartotojo režimas, 0 - supervizoriaus režimas
     */
    private static byte MODE = 1;
    /**
     * supervizoriaus pertraukimas
     */
    private static byte SI = 0;
    /**
     * programos pabaigos pertraukimas
     */
    private static byte END = 0;

    /**
     * programinis pertraukimas
     */
    private static byte PI = 0;
    /**
     * steko pertraukimas
     */
    private static byte STI = 0;
    /**
     * timeris
     */
    private static int TIMER = TIMER_CONST;
    /**
     * timerio pertraukimas
     */
    private static byte TI = 0;
    /**
     * 1 kanalo registras
     */
    private static byte CHST0 = 0;
    /**
     * 2 kanalo registras
     */
    private static byte CHST1 = 0;
    /**
     * 3 kanalo registras
     */
    private static byte CHST2 = 0;
    
    public CPU(){
        mem = new Memory();
        page = new ArrayList<Integer>();
        //String pg = Long.toString(mem.allocate_Memory());
        //page.add(Integer.parseInt(pg));
        page.add((int) mem.allocate_Memory());
        PTR = 30;
        block=0;
        word=0;
        memoryList = new ArrayList<Memory>();
        //memoryList2 = new ArrayList<Memory>();
        jauBuvo = false;
    }
    
    public void set_program_name(String name) throws Exception{
      int paging_adr = 30;
      while (mem.getMemory()[paging_adr][0] != page.get(0)) 
        if(paging_adr < 33)
          paging_adr++;
        else{
          throw new Exception("Can't set name\n");
        }

      for (int i = 0,j = 6; i < name.length(); i += 4){
       set_memory(paging_adr, j, name.substring(i));
       j++; 
      }
      
    }
    
    public void set_memory(int i, int j, String buffer){
        i = page.get(0) - 1 + i; 
      mem.getMemory()[i][j] =   (int) buffer.charAt(0) * 0x1000000
                     + (int) buffer.charAt(1) * 0x10000
                     + (int) buffer.charAt(2) * 0x100
                     + (int) buffer.charAt(3) * 0x1;
    }
    
    public void scanCommands(BufferedReader br) throws IOException{
      //int block = 0;
      //int word  = 0;
      String buffer;
      //Komandu skaitymas is failo
      while ((buffer = br.readLine()) != null){
            if(!buffer.equals("STOP")){
                //throw new IOException("STOP NOT FOUND");
            }  

            if(buffer.length() == 4) {
                
              this.set_memory(block, word, buffer);

              word ++;
              if((word %= 10) == 0) block++;

              if (block % 10 > 5){
                throw new IOException("Not enough memory for program\n");
                //System.exit(1);
              }

            }else{
              throw new IOException("Bad format\n");
              //System.exit(1);
            } 

        }
     if(jauBuvo == false){
        mem1 = mem;
        jauBuvo = true;
     }else{
         mem2 = mem;
     }
     
      //mem.show_Memory();
      block = block + 10;
      word = 0;
      
    }
    public static byte getEND() {
        return END;
    }

    public static byte getMODE() {
        return MODE;
    }

    public static byte getCHST0() {
        return CHST0;
    }

    public static byte getCHST1() {
        return CHST1;
    }
    
    public static void setMODE(byte value) {
        MODE = value;
    }

    public static byte getCHST2() {
        return CHST2;
    }


    public static short getIP() {
        return IP;
    }
    
    public static byte getSI() {
        return SI;
    }

    public static byte getTI() {
        return TI;
    }

    public static void setTIMER(int value) {
        TIMER = value;
    }
    
    public static void setSTI(byte value) {
        STI = value;
    }
    
    public static void setPI(byte value) {
        PI = value;
    }
    
    public static byte getPI() {
        return PI;
    }
    
    public static byte getSTI() {
        return STI;
    }

//     public static boolean getJauBuvo() {
//        return jauBuvo;
//    }
//     
//     public static void setJauBuvo(boolean t) {
//        jauBuvo = t;
//    }

}

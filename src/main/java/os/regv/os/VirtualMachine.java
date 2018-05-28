/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os.regv.os;

/**
 *
 * @author gabrius
 */
import java.util.ArrayList;
import java.util.Arrays;
import lombok.Data;
import java.util.Scanner;

/**
 *
 * @author gabrius
 */

enum Operations{ 
    LR, SR, AD, SU, MU, CR, JP, JM, JL, JE, PR, IN, DV, MD, HA, GO;
}

@Data
public class VirtualMachine {
    
    private final int NUMBER = 16;

    private char[][] commands = { 
     { 'L', 'R' },//Reiksmes issaugojimas registre R
     { 'S', 'R' },//Registro R reiksmes irasymas i atminti
     { 'A', 'D' },//Sudetis (registras + memory) 
     { 'S', 'U' },//Atimtis (registras - memory)
     { 'M', 'U' },//Daugyba (registras * memory)
     { 'C', 'R' },//Palygina registra R su atminties reiksme ir nustato SF
     { 'J', 'P' },//Valdymo perdavimas PC = 10x+y
     { 'J', 'M' },//Jei SF flag'as M=1 perduoda valdyma PC = 10x+y
     { 'J', 'L' },//Jei SF flag'as L=1 perduoda valdyma PC = 10x+y
     { 'J', 'E' },//Jei SF flag'as E=1 perduoda valdyma PC = 10x+y
     { 'P', 'R' },//Isveda duomenis
     { 'I', 'N' },//Skaito duomenis is isores
     { 'D', 'V' },//Dalyba
     { 'M', 'D' },//Moduline dalyba
     { 'H', 'A' }, //Halt - sustojimo komanda
     { 'G', 'O' } // GO - patalpina sekancius 4 baitus i nurodyta adresa
    };  
    
    private String output;
    //private int[] page;
    private ArrayList<Integer> page;
    private Memory mem;

    private int PI = 0;
    private int MODE = 0;
    private int PC = 0;              
    private long SF = 0x30303030;            
    private long R = 0x30303030; 
    private int PTR = 0;

    private int block_PC;
    private int field_PC;

    public VirtualMachine(Memory rmMem){
        mem = rmMem;
        page = new ArrayList<Integer>();
        output="";
        //String pg = Long.toString(mem.allocate_Memory());
        //page.add(Integer.parseInt(pg));
        page.add((int) mem.allocate_Memory());
        PTR = 30;
    }
    
    
    //executeCommand - komandos,nuskaitytos is atminties vykdymas
public void executeCommand(char[] cmd){
  int i = 0;
  boolean m = false;
  //Esamu ir nuskaitytu komandu palyginimas
  while ((i < NUMBER) && !m) { 
    if (HardDisk.compare_Commands(cmd, commands[i], 2)){ m = true; }
    else{i++;}
  }
  if (i > NUMBER-1) { 
    setPI(1);
    return;    
  }
  
  int block; 
  int field;
  
  if(cmd[2] == 'P' && cmd[3] == 'C'){
	block = block_PC;
	field = field_PC;
  }else{
	block = cmd[2] - '0' ;
	field = cmd[3] - '0' ;
  }
  Operations op[] = Operations.values();
  
  i++;
  //Komandu vykdymas
  switch (op[--i]){ 
    case LR: R = mem.getMemory()[block][field];
	           break;
    case AD: R = addition(mem.getMemory()[block][field]);
	           break;
    case SU: R = substraction(mem.getMemory()[block][field]);
	           break;
    case MU: R = multiplication(mem.getMemory()[block][field]);
	           break;
    case JP: PC = block*10 + field;
	           break;
    case IN: read_data(block, 0);
	           break;
    case CR: if (R > mem.getMemory()[block][field]){
                SF = SF + 0x00010000;
             } else if (R < mem.getMemory()[block][field]){
                SF = SF + 0x00000100;
             } else SF = SF + 0x00000001;
	           break;
    case JM: if ((SF & 0x00010000) == 0x00010000){
              PC = block*10 + field;
            }
             break;
    case JL: if ((SF & 0x00000100) == 0x00000100){
              PC = block*10 + field;
            }
             break;
    case JE: if ((SF & 0x00000001) == 0x00000001){
              PC = block*10 + field;
            }
             break;
    case PR: showData(block, field);
	           break;
    case SR: mem.getMemory()[block][field] = R;
	           break;
    case DV: R = division(mem.getMemory()[block][field]);
		   break;
    case MD: R = modular(mem.getMemory()[block][field]);
		   break;
    case GO: go(block, field); PC= PC + 1;break;
  }

  if(0>PC || PC > mem.getMemory().length){setPI(3);return;}
}
//Registru turiniu isvedimas i ekrana
public void show_Registers() {
  System.out.printf("\n*******************************************************\n");
  System.out.printf("Registers: \n");
  if (PC > 10) System.out.printf("   PC: %d\n", PC);
  else System.out.printf("   PC: 0%d\n", PC);
  System.out.printf("   R:  %c%c%c%c\n", (int)(R & 0xFF000000) / 0x1000000, (int)(R & 0xFF0000) / 0x10000, (int)(R & 0xFF00) / 0x100, (int)(R & 0xFF));
  System.out.printf("  SF:  %c%c%c%c\n", (int)(SF & 0xFF000000) / 0x1000000, (int)(SF & 0xFF0000) / 0x10000, (int)(SF & 0xFF00) / 0x100, (int)(SF & 0xFF));
  System.out.printf("MODE:  %d\n", MODE );
  System.out.printf("  PI:  %d\n", PI );
  System.out.printf(" PTR:  %d\n", PTR );
  System.out.printf("\n");
  System.out.printf("\n*******************************************************\n");
}

public void go(int block, int word){


      char[] data = { (char)((mem.getMemory()[PC/10][PC%10] & 0xFF000000) / 0x1000000), (char)((mem.getMemory()[PC/10][PC%10] & 0xFF0000) / 0x10000), 
                           (char)((mem.getMemory()[PC/10][PC%10] & 0xFF00) / 0x100), (char)((mem.getMemory()[PC/10][PC%10] & 0xFF)) };

      if ((block <= 9) && (block >= 0)){
	
        mem.getMemory()[block][word] = data[0]*0x1000000+data[1]*0x10000+data[2]*0x100+data[3];
      }
      else{
        setPI(2);
	return;
      }

}
//Duomenu apsikeitimas su isore (vyksta blokais)
    public void read_data(int a, int b){
      Scanner input = new Scanner(System.in);
      String s = input.nextLine();
      s = s.substring(0, 41);
      char character[] = new char[41];
      character = s.toCharArray();
      int i;
      for (i = 0; i < 40; i++) {
        //Naujos eilutes simboliu pasalinimas
        if (character[i] == '\n') {
          character[i] = 0;
          int j;
          //Tarpo uzpildymas simboliais
          for (j = 0; j < (i%4)-1; j++) character[i+j] = ' ';  
        }
      }
      //Nuskaitytu duomenu sudejimas i bloka
      for (i = 0; i < 10; i++){  
        mem.getMemory()[a][b+i] = character[0+(4*i)]*0x1000000+character[1+(4*i)]*0x10000+character[2+(4*i)]*0x100+character[3+(4*i)];
      } 

    }

    //Komandos nuskaitymas is atminties, skaitliuko didinimas
    public int nextCommand() 
    {
      char[] command = { (char)((mem.getMemory()[PC/10][PC%10] & 0xFF000000) / 0x1000000), (char)((mem.getMemory()[PC/10][PC%10] & 0xFF0000) / 0x10000), 
                           (char)((mem.getMemory()[PC/10][PC%10] & 0xFF00) / 0x100), (char)((mem.getMemory()[PC/10][PC%10] & 0xFF)) };
      PC++;

      //Halt komanda
      if (!(HardDisk.compare_Commands(commands[14], command, 2))){
        executeCommand(command);
        return 1;
      } 
      else {System.out.printf("Exiting Virtual Machine...\n");return 0;} 
    }

    //Duomenu isvedimas i ekrana
    public void showData(int a, int b){
       setPI(8);
      int i=0;
      char[] data = { (char)((mem.getMemory()[a][b+i] & 0xFF000000) / 0x1000000), (char)((mem.getMemory()[a][b+i] & 0xFF0000) / 0x10000), 
                           (char)((mem.getMemory()[a][b+i] & 0xFF00) / 0x100), (char)((mem.getMemory()[a][b+i] & 0xFF)) };

      output="";
      for (int j = 0; j < 4; j++) output = output + data[j];

       //return data;                     
      //printf("\n");
    }

    //Registras + atminties adresas
    public long addition(long adr){
      char[] value1 = { (char)((adr & 0xFF000000) / 0x1000000), (char)((adr & 0xFF0000) / 0x10000), 
                           (char)((adr & 0xFF00) / 0x100), (char)((adr & 0xFF)) };
      char[] value2 = { (char)((R & 0xFF000000) / 0x1000000), (char)((R & 0xFF0000) / 0x10000), 
                           (char)((R & 0xFF00) / 0x100), (char)((R & 0xFF)) }; 
  
      //Gaunami desimtainiai skaiciai
      int get_value_1 = Integer.parseInt(new String(value1));
      int get_value_2 = Integer.parseInt(new String(value2));

      int total_sum = get_value_1 + get_value_2;

      if (total_sum > 9999){
        setPI(4);
        return -1;
      }
      int sum1 = total_sum/1000;
      int sum2 = (total_sum%1000)/100;
      int sum3 = (total_sum%100)/10;
      int sum4 = total_sum%10;
  
      sum1 = sum1 + '0';
      sum2 = sum2 + '0'; 
      sum3 = sum3 + '0'; 
      sum4 = sum4 + '0';

      return (sum1 * 0x1000000 + sum2 * 0x10000 + sum3 * 0x100 + sum4);
    }

    //substraction: registras - atmintis su adresu
    public long substraction(long adr){ 

      char[] line1 = { (char)((adr & 0xFF000000) / 0x1000000), (char)((adr & 0xFF0000) / 0x10000),
                         (char)((adr & 0xFF00) / 0x100), (char)((adr & 0xFF)) };
      char[] line2 = { (char)((R & 0xFF000000) / 0x1000000), (char)((R & 0xFF0000) / 0x10000),
                          (char)((R & 0xFF00) / 0x100), (char)((R & 0xFF)) }; 

      int nr_1 = Integer.parseInt(new String(line1));
      int nr_2 = Integer.parseInt(new String(line2));
      
      int substract = nr_1 - nr_2;

      if (substract < 0){
            setPI(4);
            return -1;
      }

      int minus1 = substract/1000;
      int minus2 = (substract%1000)/100;
      int minus3 = (substract%100)/10;
      int minus4 = substract%10;

      minus1 = minus1 + '0';
      minus2 = minus2 + '0';
      minus3 = minus3 + '0';
      minus4 = minus4 + '0';

      long result = minus1 * 0x1000000 + minus2 * 0x10000 + minus3 * 0x100 + minus4;
      return result;
    }

    public long multiplication(long adr){ 

      char[] line1 = { (char)((adr & 0xFF000000) / 0x1000000), (char)((adr & 0xFF0000) / 0x10000),
                        (char)((adr & 0xFF00) / 0x100), (char)((adr & 0xFF)) };
      char[] line2 = { (char)((R & 0xFF000000) / 0x1000000), (char)((R & 0xFF0000) / 0x10000),
                        (char)((R & 0xFF00) / 0x100), (char)((R & 0xFF)) }; 

      int nr_1 = Integer.parseInt(new String(line1));
      int nr_2 = Integer.parseInt(new String(line2));

      int multiplication = nr_1 * nr_2;

      if ((nr_1 < 0) || (nr_1 < 0) || (multiplication > 9999)){
        setPI(4);
        return -1;
      }
      else{
        int mul1 = multiplication/1000;
        int mul2 = (multiplication%1000)/100;
        int mul3 = (multiplication%100)/10;
        int mul4 = multiplication%10;

        mul1 = mul1 + '0';
        mul2 = mul2 + '0';
        mul3 = mul3 + '0';
        mul4 = mul4 + '0';

        long result = mul1 * 0x1000000 + mul2 * 0x10000 + mul3 * 0x100 + mul4;
        return result;
      }
    }

    public long division(long adr){ 

      char[] line1 = { (char)((adr & 0xFF000000) / 0x1000000), (char)((adr & 0xFF0000) / 0x10000),
                         (char)((adr & 0xFF00) / 0x100), (char)((adr & 0xFF)) };
      char[] line2 = { (char)((R & 0xFF000000) / 0x1000000), (char)((R & 0xFF0000) / 0x10000),
                         (char)((R & 0xFF00) / 0x100), (char)((R & 0xFF)) }; 

      int nr_1 = Integer.parseInt(new String(line1));
      int nr_2 = Integer.parseInt(new String(line2));

      if(nr_2 == 0){setPI(7);return -1;}

      int division = nr_1 / nr_2;

      if ((nr_1 < 0) || (nr_1 < 0) || (division > 9999)){
        setPI(4);
        return -1;
      }
      else{
        int div1 = division/1000;
        int div2 = (division%1000)/100;
        int div3 = (division%100)/10;
        int div4 = division%10;

        div1 = div1 + '0';
        div2 = div2 + '0';
        div3 = div3 + '0';
        div4 = div4 + '0';

        long result = div1 * 0x1000000 + div2 * 0x10000 + div3 * 0x100 + div4;
        return result;
      }
    }

    public long modular(long adr){ 

      char[] line1 = { (char)((adr & 0xFF000000) / 0x1000000), (char)((adr & 0xFF0000) / 0x10000),
                         (char)((adr & 0xFF00) / 0x100), (char)((adr & 0xFF)) };
      char[] line2 = { (char)((R & 0xFF000000) / 0x1000000), (char)((R & 0xFF0000) / 0x10000),
                         (char)((R & 0xFF00) / 0x100), (char)((R & 0xFF)) }; 

      int nr_1 = Integer.parseInt(new String(line1));
      int nr_2 = Integer.parseInt(new String(line1));

      if(nr_2 == 0){setPI(7);return -1;}

      int module = nr_1 % nr_2;

      if ((nr_1 < 0) || (nr_1 < 0) || (module > 9999)){
        setPI(4);
        return -1;
      }
      else{
        int mod1 = module/1000;
        int mod2 = (module%1000)/100;
        int mod3 = (module%100)/10;
        int mod4 = module%10;

        mod1 = mod1 + '0';
        mod2 = mod2 + '0';
        mod3 = mod3 + '0';
        mod4 = mod4 + '0';

        long result = mod1 * 0x1000000 + mod2 * 0x10000 + mod3 * 0x100 + mod4;
        return result;
      }
    }

    public int testInterrupt(){
    /*	int test;*/
    /*	if(x==1){test=IOI;IOI=0;}*/
    /*	else if(x==2){test=PI;PI=0;}*/
    /*	else if(x==3){test=SI;SI=0;}*/
    /*	else if(x==4){test=TI;TI=0;}*/
    /*	else if((IOI + PI + SI + TI) > 0){test=1}else{test=0;}	*/
            if(PI>0){return PI;}else{return 0;}

    }

    // Puslapiu lentejei, iraso programos varda
    public void set_program_name(String name){
      int paging_adr = 30;
      while (mem.getMemory()[paging_adr][0] != page.get(0)) 
        if(paging_adr < 33)
          paging_adr++;
        else{
          System.out.printf("Can't set name\n");
          System.exit(1);
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

    public void free_memory(int PTR){
      for (int i = 0; i < 10; ++i)
      {
        mem.getMemory()[PTR][i] = 0;
      }

    }
}


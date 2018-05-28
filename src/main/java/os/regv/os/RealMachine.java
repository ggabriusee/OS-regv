/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os.regv.os;

import java.util.Scanner;
/**
 *
 * @author gabrius
 */
public class RealMachine {
    private static int interrupt;

    public static void intDetected(int x, VirtualMachine vm){
            if(x!=0){System.out.printf("Interrupt code %d\n", x);}
        if(x==2){
            System.out.printf("Program coused memory overflow\nQuitting Virtual Machine...\n");	
        }else if(x==1){
            System.out.printf("Undefined command detected\nQuitting Virtual Machine...\n"); 
        }else if(x==3){
            System.out.printf("bad memory adress in PC\nQuitting Virtual Machine...\n");
        }else if(x==4){
            System.out.printf("Bad input of numbers \nQuitting Virtual Machine...\n");
        }else if(x==5){
            System.out.printf("No STRT detected\nQuitting Virtual Machine...\n");
        }else if(x==6){
            System.out.printf("No STOP detected\nQuitting Virtual Machine...\n");
        }else if(x==7){
            System.out.printf("0 in division\nQuitting Virtual Machine...\n");
        }
        else if(x==8){
            System.out.println(vm.getOutput());

        }
        //exit(1);
        vm.setPI(0);
    }

  public static void vm(VirtualMachine vm){
      
	  //HardDisk.openFile(vm, "programa1.txt");       

	  System.out.printf("*****Starting Virtual Machine*****\n");                
	    boolean end = false;
	    vm.setMODE(1);
	    
	    while (!end){
              interrupt = vm.testInterrupt();
	      if(interrupt!=0) {System.out.printf("Quitting Virtual Machine...\n"); break;} 
	      vm.getMem().show_Memory();
	      vm.show_Registers();
	      System.out.printf("Choose menu number:\n");
	      System.out.printf("* 1 execute: ");
	      int PC = vm.getPC();
	      char[] character_pc = {  (char)((vm.getMem().getMemory()[PC/10][PC%10] & 0xFF0000000000L) / 0x10000000000L),
		                        (char)((vm.getMem().getMemory()[PC/10][PC%10] & 0xFF00000000L) / 0x100000000L),
		                        (char)((vm.getMem().getMemory()[PC/10][PC%10] & 0xFF000000) / 0x1000000),
		                        (char)((vm.getMem().getMemory()[PC/10][PC%10] & 0xFF0000) / 0x10000), 
		                        (char)((vm.getMem().getMemory()[PC/10][PC%10] & 0xFF00) / 0x100), 
		                        (char)((vm.getMem().getMemory()[PC/10][PC%10] & 0xFF)) };
	      System.out.printf("%c%c%c%c%c%c *\n",  character_pc[0], 
		                          character_pc[1], 
		                          character_pc[2], 
		                          character_pc[3],
		                          character_pc[4],
		                          character_pc[5]
		  );
	     System.out.printf("* 2 Exit *\n");
             Scanner input = new Scanner(System.in);
	      int in = input.nextInt();
	      switch (in){
		case 1: if(vm.nextCommand() == 0) end = true; 
			       break;
		case 2:  end = true;
			       break;
		default: System.out.printf("Bad command number\n");
	      }
             
	    }
	    //printf("Exiting Virtual Machine...\n");
	    
  }
    
    public void boot(){
        System.out.printf("Welcome\n");
        VirtualMachine vm = new VirtualMachine(os.regv.Main.cpu.getMem());
        Scanner input = new Scanner(System.in);
        vm.setPTR(30); 
  
        boolean eend=true;
        while (eend){
            vm.setMODE(0);
            vm.getMem().show_Memory();
            vm.show_Registers();
            intDetected(interrupt, vm);
                System.out.printf("Choose menu number:\n");
                System.out.printf("* 1 Change Mode *\n");
                System.out.printf("* 2 Shut down *\n");
                int inp = input.nextInt();
                switch (inp){
                    case 1:	vm(vm);
                                   break;
                    case 2:  eend = false;
                                   break;
                    default: System.out.printf("Bad command number\n");
                  }
		
	}
        System.out.printf("Shutting down\n");
        
    }
}

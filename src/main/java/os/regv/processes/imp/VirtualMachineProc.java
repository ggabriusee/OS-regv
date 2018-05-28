package os.regv.processes.imp;

import java.util.Scanner;
import os.regv.Main;
import os.regv.os.CPU;
//import os.regv.hardware.memory.VMMemory;
import os.regv.os.InterruptHandler;
import os.regv.os.VirtualMachine;
import os.regv.processes.Process;
import os.regv.resources.Resource;
import os.regv.resources.ResourceType;
import os.regv.resources.descriptors.InterruptDescriptor;
import os.regv.resources.descriptors.VirtualMemoryDescriptor;
//import os.regv.software.executor.InterruptChecker;
//import os.regv.software.executor.ProgramExecutor;

/**
 * Procesą „VirtualMachine“ kuria ir naikina procesas
 * „JobGorvernor“.„VirtualMachine“ paskirtis yra vykdyti vartotojo užduoties
 * programą. Procesų „VirtualMachine“ yra tiek pat, kiek ir procesų
 * „JobGorvernor“.
 *
 * @author gabrius
 *
 */
public class VirtualMachineProc extends Process {
    
    //private VMMemory vmm = null;
    //private ProgramExecutor exec = null;
    VirtualMachine vmm;
    private Process parentOfVM = null;
    
    public VirtualMachineProc(VirtualMachine vmm, Process parent) {
        this.vmm = vmm;
        this.parentOfVM = parent;
    }
    
    public void setStep(int step){
        this.changeStep(step);
    }
    
    public int vm(os.regv.os.VirtualMachine vm){
            

	  System.out.printf("*****Starting Virtual Machine*****\n");                
	    boolean end = false;
	    vm.setMODE(1);
	    
	    while (!end){
              int interrupt = vm.testInterrupt();
	      if(interrupt!=0) {System.out.printf("Quitting Virtual Machine...\n");return interrupt;} 
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
	    return vm.testInterrupt();
  }
    
    @Override
    public void nextStep() {
        
        switch (this.step) {
            case (0):
                if (vmm != null) {
                    CPU.setMODE((byte) 0);
                    //exec = new ProgramExecutor(vmm, parentOfVM);
                    this.changeStep(1);
                } else {
                    this.changeStep(0);
                }
                
                break;
            case (1):
                //>>>>>>>>>>>>>>>>>>vmm.loadCPUState();
                CPU.setTIMER(CPU.TIMER_CONST);
                //while (!exec.executeNext()){}
                int i = vm(vmm);
                Resource interrupt = new Resource(ResourceType.PERTR);
                interrupt.setParent(parentOfVM);
                //interrupt.setDescriptor(InterruptHandler.getInt());
                interrupt.setDescriptor(InterruptHandler.intDetected(i, vmm));
                //interrupt.setDescriptor(i);
                Main.resourceList.addResource(interrupt);
                //>>>>>>>>>>>>>>>>>>>>>>vmm.saveCPUState();
                this.changeStep(2);
                break;
            case (2):
                this.changeStep(2);
                
                break;
        }
    }
}

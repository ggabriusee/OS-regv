package os.regv.os;

import os.regv.Main;
import os.regv.os.CPU;
//>>>import os2.main.hardware.memory.RMMemory;
//>>>import os2.main.hardware.memory.VMMemory;
import os.regv.resources.descriptors.InterruptDescriptor;
import os.regv.processes.Process;
import os.regv.resources.Resource;
import os.regv.resources.ResourceType;
import os.regv.resources.descriptors.InterruptDescriptor.Type;
import os.regv.resources.descriptors.LineToPrintDescriptor;
import os.regv.resources.descriptors.PrintDescriptor;

/**
 *
 * @author Arturas
 */
public class InterruptHandler {

    private InterruptDescriptor intDes;
    private Process parentOfVM;
    //>>>>private VMMemory vmm;
    private VirtualMachine vmm;

    public InterruptHandler(InterruptDescriptor intDes, Process parentOfVM, VirtualMachine vmm) {
        this.intDes = intDes;
        this.parentOfVM = parentOfVM;
        this.vmm = vmm;
    }
    /*
    public boolean fix() {
        if (intDes.getType() == Type.END) {
            return false;
            
        } else if (intDes.getType() == Type.PI) {
            return false;

        } else if (intDes.getType() == Type.SI) {
            Resource liRes = Main.resourceList.searchChildResource(parentOfVM, ResourceType.LI_TO_PR);
            LineToPrintDescriptor liDes = (LineToPrintDescriptor) liRes.getDescriptor();
            Resource r = new Resource(ResourceType.LI_IN_MEM);
            r.setDescriptor(new PrintDescriptor(Integer.toString(liDes.getLine())));
            Main.resourceList.addResource(r);
            return true;

        } else if (intDes.getType() == Type.STI) {
            return false;

        } else if (intDes.getType() == Type.TI) {
            //vmm.set(17, CPU.TIMER_CONST);
            return true;

        }
        return false;
    }
*/
    public boolean fix() {
        if (intDes.getType() == Type.END) {
            Resource r = new Resource(ResourceType.END);
            r.setDescriptor(new PrintDescriptor(intDes.getError()));
            Main.resourceList.addResource(r);
            return false;
            
        } else if (intDes.getType() == Type.PI) {
            Resource r = new Resource(ResourceType.LI_IN_MEM);
            r.setDescriptor(new PrintDescriptor(intDes.getError()));
            Main.resourceList.addResource(r);
            return false;

        } else if (intDes.getType() == Type.SI) {
            Resource r = new Resource(ResourceType.LI_IN_MEM);
            r.setDescriptor(new PrintDescriptor(intDes.getError()));
            Main.resourceList.addResource(r);
            return true;

        }
        return false;
    }
    /*
    public static InterruptDescriptor getInt() {
        InterruptDescriptor des = null;

        if (os.regv.Main.cpu.getEND() != 0) {
            des = new InterruptDescriptor();
            des.setType(InterruptDescriptor.Type.END);
            des.setValue(os.regv.Main.cpu.getEND());
            des.setFixed(false);
        } else if (os.regv.Main.cpu.getPI() != 0) {
            des = new InterruptDescriptor();
            des.setType(InterruptDescriptor.Type.PI);
            des.setValue(os.regv.Main.cpu.getPI());
            des.setFixed(false);
        } else if (os.regv.Main.cpu.getSI() != 0) {
            des = new InterruptDescriptor();
            des.setType(InterruptDescriptor.Type.SI);
            des.setValue(os.regv.Main.cpu.getSI());
            des.setFixed(false);
        } else if (os.regv.Main.cpu.getTI() != 0) {
            des = new InterruptDescriptor();
            des.setType(InterruptDescriptor.Type.TI);
            des.setValue(os.regv.Main.cpu.getTI());
            des.setFixed(false);
        } else if (os.regv.Main.cpu.getSTI() != 0) {
            des = new InterruptDescriptor();
            des.setType(InterruptDescriptor.Type.STI);
            des.setValue(os.regv.Main.cpu.getSTI());
            des.setFixed(false);
        }
        return des;
    }
    */
    public static InterruptDescriptor intDetected(int x, VirtualMachine vm){
        InterruptDescriptor des = null;
        if(x==2){ 
            des = new InterruptDescriptor();
            des.setError("Interrupt code " + String.valueOf(x) + "\n" +"Program coused memory overflow\n");
            des.setType(InterruptDescriptor.Type.PI);
            //System.out.printf("Program coused memory overflow\nQuitting Virtual Machine...\n");	
        }else if(x==1){
            des = new InterruptDescriptor();
            des.setError("Interrupt code " + String.valueOf(x) + "\n" +"Undefined command detected\n");
            des.setType(InterruptDescriptor.Type.PI);
            //System.out.printf("Undefined command detected\nQuitting Virtual Machine...\n"); 
        }else if(x==3){
            des = new InterruptDescriptor();
            des.setError("Interrupt code " + String.valueOf(x) + "\n" +"bad memory adress in PC\n");
            des.setType(InterruptDescriptor.Type.PI);
            //System.out.printf("bad memory adress in PC\nQuitting Virtual Machine...\n");
        }else if(x==4){
            des = new InterruptDescriptor();
            des.setError("Interrupt code " + String.valueOf(x) + "\n" +"Bad input of numbers\n");
            des.setType(InterruptDescriptor.Type.PI);
            //System.out.printf("Bad input of numbers \nQuitting Virtual Machine...\n");
        }else if(x==5){
            des = new InterruptDescriptor();
            des.setError("Interrupt code " + String.valueOf(x) + "\n" +"No STRT detected\n");
            des.setType(InterruptDescriptor.Type.PI);
            //System.out.printf("No STRT detected\nQuitting Virtual Machine...\n");
        }else if(x==6){
            des = new InterruptDescriptor();
            des.setError("Interrupt code " + String.valueOf(x) + "\n" +"No STOP detected\n");
            des.setType(InterruptDescriptor.Type.PI);
            //System.out.printf("No STOP detected\nQuitting Virtual Machine...\n");
        }else if(x==7){
            des = new InterruptDescriptor();
            des.setError("Interrupt code " + String.valueOf(x) + "\n" +"0 in division\n");
            des.setType(InterruptDescriptor.Type.PI);
            //System.out.printf("0 in division\nQuitting Virtual Machine...\n");
        }
        else if(x==8){
            des = new InterruptDescriptor();
            des.setError("Interrupt code " + String.valueOf(x) + "\n" + vm.getOutput() + "\n");
            des.setType(InterruptDescriptor.Type.SI);
            //System.out.println(vm.getOutput());

        }else if(x==0){
            des = new InterruptDescriptor();
            des.setError("Interrupt code " + String.valueOf(x) + "\n" + "Shuting down\n");
            des.setType(InterruptDescriptor.Type.END);
        }
        //exit(1);
        vm.setPI(0);
        return des;
    }
}

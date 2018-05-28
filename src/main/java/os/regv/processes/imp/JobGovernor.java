package os.regv.processes.imp;

import os.regv.Main;
import os.regv.os.CPU;
//import os.regv.hardware.memory.RMMemory;
//import os.regv.hardware.memory.VMMemory;
import os.regv.os.VirtualMachine;
import os.regv.processes.Process;
import os.regv.resources.Resource;
import os.regv.resources.ResourceType;
import os.regv.resources.descriptors.InterruptDescriptor;
import os.regv.resources.descriptors.LoaderPacketDescriptor;
import os.regv.resources.descriptors.ProgramInHDDDescriptor;
import os.regv.os.InterruptHandler;
import os.regv.processes.ProcessType;

/**
 * Proceso „JobGorvernor“ paskirtis – kurti, naikinti ir padėti procesui
 * „VirtualMachine“ atlikti savo darbą, t. y. atlikti veiksmus, kurių
 * „VirtualMachine“, procesoriui dirbant vartotojo režimu, nesugeba atlikti.
 * Vienas „JobGorvernor“ aptarnauja vieną virtualią mašiną.
 *
 * @author gabrius
 *
 */
public class JobGovernor extends Process {

    private Process vmProc = null;
    private os.regv.os.VirtualMachine vmm = null;
    private Resource progInHddRes = null;
    private Resource memRes = null;
    private Resource intRes = null;
    private Resource fromLoader = null;

    public JobGovernor(Resource progInHdd) {
        this.progInHddRes = progInHdd;
        this.type = ProcessType.USER;
    }

    @Override
    public void nextStep() {

        switch (this.step) {
            case (0):
                try {
                    //>>vmm = RMMemory.createVMMemory();
                    
                    //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    //System.out.println(os.regv.Main.cpu.getMemoryList().size());
                    //os.regv.Main.cpu.getMemoryList().get(0).show_Memory();
//                    if(CPU.jauBuvo == true){
//                        vmm = new VirtualMachine(os.regv.Main.cpu.getMemoryList().get(0));
//                        CPU.jauBuvo = false;
//                    }else{
//                        vmm = new VirtualMachine(os.regv.Main.cpu.getMemoryList2().get(0));
//                    }
                        if(CPU.jauBuvo == false){
                            vmm = new VirtualMachine(os.regv.Main.cpu.getMem1());
                            CPU.jauBuvo = false;
                        }else{
                            vmm = new VirtualMachine(os.regv.Main.cpu.getMem2());
                        }
                } catch (RuntimeException e) {
                    this.changeStep(0);
                    break;
                }
                this.changeStep(1);
                
                break;
            case (1):
                ProgramInHDDDescriptor progInHddDes = (ProgramInHDDDescriptor) progInHddRes.getDescriptor();
                int[] filename = progInHddDes.getProgramName();

                Resource loaderPacket = new Resource(ResourceType.LOAD_PACK);
                loaderPacket.setParent(this);
                LoaderPacketDescriptor loadDes = new LoaderPacketDescriptor();
                loadDes.setMemory(vmm.getMem());
                loadDes.setFilename(filename);
                loaderPacket.setDescriptor(loadDes);
               
                Main.resourceList.addResource(loaderPacket);
                this.changeStep(2);
                
                break;
            case (2):
                fromLoader = Main.resourceList.searchChildResource(this, ResourceType.PACK_FROM_LOAD);
                if (fromLoader != null) {
                    this.changeStep(3);
                } else {
                    this.changeStep(2);
                }
                
                break;
            case (3):
            	//>>>vmm.set(14, 0);
            	//>>>vmm.set(10, 0);
                vmProc = new VirtualMachineProc(vmm, this);
                Main.processQueue.add(vmProc);
                this.changeStep(4);
                
                break;
            case (4):
                intRes = Main.resourceList.searchChildResource(this, ResourceType.FROM_INT);
                if (intRes != null) {
                    this.changeStep(5);
                } else {
                    this.changeStep(4);
                }
                
                break;
            case (5):
                InterruptDescriptor intDes = (InterruptDescriptor) intRes.getDescriptor();
                InterruptHandler handler = new InterruptHandler(intDes, this, vmm);
                if (handler.fix()) {
                    intDes.setFixed(true);
                    VirtualMachineProc vmProcAsVM = (VirtualMachineProc) vmProc;
                    vmProcAsVM.setStep(1);
                    Main.resourceList.deleteByInstance(intRes);
                    this.changeStep(4);
                    break;
                } else {
                    Main.processQueue.delete(vmProc.getPid());
                    ProgramInHDDDescriptor des = (ProgramInHDDDescriptor) progInHddRes.getDescriptor();
                    des.setFromSaver(false);
                    Main.resourceList.deleteByInstance(memRes);
                    Main.resourceList.deleteByInstance(fromLoader);
                    this.changeStep(6);
                }
                
                Main.resourceList.deleteByInstance(intRes);
                break;
            case (6):
                //blokavimasis laukiant "neegzistuojantis" resuro
                this.changeStep(6);
                break;
        }
    }
}

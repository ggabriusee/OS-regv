package os.regv.resources.descriptors;

//>>>import os.regv.hardware.memory.VMMemory;
import os.regv.os.Memory;
import os.regv.os.VirtualMachine;
import os.regv.resources.descriptors.interfaces.ResourceDescriptorInterface;

/**
 *
 * @author gabrius
 */
public class VirtualMemoryDescriptor implements ResourceDescriptorInterface {
    private Memory memory;
    
    public Memory getMemory(){
        return this.memory;
    }
    
    public void setMemory(Memory memory){
        this.memory = memory;
    }
    
}

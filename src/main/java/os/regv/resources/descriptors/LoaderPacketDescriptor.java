package os.regv.resources.descriptors;

import java.util.ArrayList;

//>>>import os.regv.hardware.memory.VMMemory;
import os.regv.os.Memory;
import os.regv.os.VirtualMachine;
import os.regv.resources.descriptors.interfaces.ResourceDescriptorInterface;

/**
 *
 * @author Arturas
 */
public class LoaderPacketDescriptor implements ResourceDescriptorInterface {

    //>>>private VMMemory memory;
    private Memory memory;
    private int[] filename;
    private ArrayList vars;
    
    public Memory getMemory(){
        return this.memory;
    }
    
    public void setMemory(Memory memory){
        this.memory = memory;
    }
    
    public int[] getFilename(){
        return this.filename;
    }
    
    public void setFilename(int[] filename){
        this.filename = filename;
    }

	public ArrayList getVars() {
		return this.vars;
	}
	
	public void setVars(ArrayList vars) {
		this.vars = vars;
	}
}

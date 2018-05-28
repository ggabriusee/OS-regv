package os.regv.resources.descriptors;

import os.regv.resources.descriptors.interfaces.ResourceDescriptorInterface;
import lombok.Data;

/**
 *
 * @author gabrius
 */
@Data
public class InterruptDescriptor implements ResourceDescriptorInterface {

    public enum Type {
        END, PI, SI, TI, STI
    }

    private Type type;
    private byte value;
    private boolean fixed;
    private String error;
    private int interNum;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    public byte getValue() {
        return this.value;
    }
    
     public void setValue(byte value ) {
        this.value = value;
    }
     
     public void setFixed(boolean fixed){
         this.fixed = fixed;
     }
     
     public boolean getFixed(){
         return this.fixed;
     }

}

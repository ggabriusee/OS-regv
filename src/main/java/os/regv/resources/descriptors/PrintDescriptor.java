package os.regv.resources.descriptors;

import os.regv.resources.descriptors.interfaces.ResourceDescriptorInterface;
import lombok.Data;

@Data
public class PrintDescriptor implements ResourceDescriptorInterface {

	private String message;

        public PrintDescriptor(String s){
            this.message = s;
        }
	
}

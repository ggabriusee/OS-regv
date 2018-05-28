package os.regv.resources.descriptors;

import os.regv.resources.descriptors.interfaces.ResourceDescriptorInterface;

public class FromLoaderDescriptor implements ResourceDescriptorInterface {
	
	private String message;
	
	public String getMessage() {
		return this.message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}

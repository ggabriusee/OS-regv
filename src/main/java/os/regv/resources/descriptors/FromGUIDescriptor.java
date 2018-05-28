package os.regv.resources.descriptors;

import os.regv.resources.descriptors.interfaces.ResourceDescriptorInterface;

public class FromGUIDescriptor implements ResourceDescriptorInterface {

	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}

package os.regv.resources.descriptors;

import os.regv.resources.descriptors.interfaces.ResourceDescriptorInterface;

public class ProgramInHDDDescriptor implements ResourceDescriptorInterface {

	private int[] programName;
	private boolean fromSaver = true;
	
	public int[] getProgramName() {
		return this.programName;
	}

	public void setProgramName(int[] programName) {
		this.programName = programName;
	}

	public boolean isFromSaver() {
		return fromSaver;
	}

	public void setFromSaver(boolean fromJobToSwap) {
		this.fromSaver = fromJobToSwap;
	}

}

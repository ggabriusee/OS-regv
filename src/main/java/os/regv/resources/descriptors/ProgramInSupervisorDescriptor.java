package os.regv.resources.descriptors;

import lombok.Data;
import java.io.BufferedReader;
import os.regv.resources.descriptors.interfaces.ResourceDescriptorInterface;

@Data
public class ProgramInSupervisorDescriptor implements
		ResourceDescriptorInterface {

	private BufferedReader br;
	private String programName;

}

package os.regv.processes.imp;

import os.regv.Main;
import os.regv.processes.Process;
import os.regv.processes.ProcessType;
import os.regv.resources.Resource;
import os.regv.resources.ResourceType;
import os.regv.resources.descriptors.ProgramInHDDDescriptor;

/**
 * Šio proceso paskirtis – kurti ir naikinti procesus „JobGorvernor“.
 * 
 * @author domas
 * 
 */
public class MainProc extends Process {

	private Resource resourceToDestroy;
	private int pidToDestroy;

        public MainProc() {
		this.type = ProcessType.SYSTEM;
        }
	@Override
	public void nextStep() {
		Resource res;
		ProgramInHDDDescriptor des;
		switch (this.step) {
		case (0):
			// Blokuojam, laukiam resurso "Užduotis būge" resurso
			res = Main.resourceList.searchChildResource(null, ResourceType.PROGRAM_IN_HDD);
			if (res != null) {
				this.changeStep(1);
			} else {
				this.changeStep(0);
			}
			break;
		case (1):
			// Tikrinam vygdymo laiką
			// step = time > 0 ? 2 : 3
			res = Main.resourceList.searchChildResource(null, ResourceType.PROGRAM_IN_HDD);
			des = (ProgramInHDDDescriptor) res.getDescriptor();
			if (des.isFromSaver()) {
				this.changeStep(2);
				res.setParent(this);
			} else {
				this.resourceToDestroy = res;
				this.pidToDestroy = resourceToDestroy.getParent().getPid();
				this.changeStep(3);
			}
			break;
		case (2):
			// Kuriamas procesas JobGovernor ir
			// resursas "Užduotis bugne" perduodamas jam
			res = Main.resourceList.searchChildResource(this,
					ResourceType.PROGRAM_IN_HDD);
			JobGovernor jg = new JobGovernor(res);
			res.setParent(jg);
			Main.processQueue.add(jg);
			this.changeStep(0);
			break;
		case (3):
			// Naikinamas procesas "JobGovernor" sukūręs gautajį resursą
			Main.resourceList.deleteByInstance(this.resourceToDestroy);
			Main.processQueue.delete(this.pidToDestroy);
			this.changeStep(0);
			break;

		}
	}

}

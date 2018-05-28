package os.regv.processes.imp;

import os.regv.Main;
import os.regv.os.VirtualMachine;
//>>>>import os.regv.hardware.ChannelsDevice.ChannelsDevice;
//>>>>import os.regv.hardware.memory.VMMemory;
import os.regv.processes.Process;
import os.regv.processes.ProcessType;
import os.regv.resources.Resource;
import os.regv.resources.ResourceType;
import os.regv.resources.descriptors.LoaderPacketDescriptor;

/**
 * Šio proceso paskirtis – išorinėje atmintyje esančius blokus 
 * perkelti į vartotojo atmintį.
 * @author gabrius
 *
 */

public class Loader extends Process {
	
	//>>>>private VMMemory vmm;
        VirtualMachine vm;
	private int[] programName;
	private JobGovernor jg;

        public Loader() {
		this.type = ProcessType.SYSTEM;
        }
        
	@Override
	public void nextStep() {
		Resource res;
		switch (this.step) {
		case (0):
			// Blokuotas, laukiam resurso "Pakrovimo paketas"
			res = Main.resourceList.searchResource(ResourceType.LOAD_PACK);
			if (res != null) {
				LoaderPacketDescriptor descriptor = (LoaderPacketDescriptor) res.getDescriptor();
				this.jg = (JobGovernor) res.getParent();
				res.setParent(this);
				this.programName = descriptor.getFilename();
				//>>>>this.vmm = descriptor.getMemory();
				this.changeStep(1);
			} else {
				this.changeStep(0);
			}
			break;
		case (1):
			// Blokuotas, laukiam resurso "Kanalų įrenginys"
			res = Main.resourceList.searchResource(ResourceType.CH_DEV);
			if (res != null) {
				if (res.getParent() == null) {
					res.setParent(this);
					this.changeStep(2);
				} else {
					this.changeStep(1);
				}
			} else {
				this.changeStep(1);
			}
			break;
		case (2):
			// Nustatom kanalų įrenginio registrus ir vykdom komandą XCHG
			//>>>>ChannelsDevice.ST = 3; // Šaltinis: išorinė atmintis
			//>>>>ChannelsDevice.DT = 1; // Tikslas: vartotojo atmintis
			//>>>>ChannelsDevice.programName = this.programName;
			//>>>>ChannelsDevice.vmm = this.vmm;
			//>>>>ChannelsDevice.XCHG();
			this.changeStep(3);
			break;
		case (3):
			// Atlaisvinam "Kanalų įrenginys" resursą
			res = Main.resourceList.searchResource(ResourceType.CH_DEV);
			res.removeParent();
			this.changeStep(4);
			break;
		case (4):
			// Sukuriamas "Iš loader" resursas, skirtas JobGovernor procesui
			// sukūrusiam gautąjį "Pakrovimo paketo" resursą
			res = new Resource(ResourceType.PACK_FROM_LOAD);
			res.setParent(this.jg);
			Main.resourceList.addResource(res);
			Main.resourceList.deleteChildResource(this, ResourceType.LOAD_PACK);
			this.changeStep(0);
			break;
		}
	}

}

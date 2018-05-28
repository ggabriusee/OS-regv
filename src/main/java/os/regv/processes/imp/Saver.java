package os.regv.processes.imp;

import os.regv.Main;
import os.regv.os.HardDisk;
//import os.regv.hardware.ChannelsDevice.ChannelsDevice;
//import os.regv.hardware.HDD.Utilities;
import os.regv.processes.Process;
import os.regv.processes.ProcessType;
import os.regv.resources.Resource;
import os.regv.resources.ResourceType;
import os.regv.resources.descriptors.ExecParamsDescriptor;
import os.regv.resources.descriptors.ProgramInHDDDescriptor;

/**
 * Šio proceso paskirtis – perkelti užduoties programos blokus iš supervizorinės atminties į
 * išorinę.
 * @author gabrius
 *
 */

public class Saver extends Process {
	
	private int[] programName;
	private int startAddress;
	private int endAddress;

        public Saver(){
            this.type = ProcessType.SYSTEM;
        }
	@Override
	public void nextStep() {
		Resource res;
		switch (this.step) {
		case (0):
			// Blokuotas, laukiam resurso "Užduoties vykdymo parametrai
			// supervizorinėje atmintyje" resurso
			res = Main.resourceList.searchResource(ResourceType.VYKD_PAR);
			if (res != null) {
				ExecParamsDescriptor descriptor = (ExecParamsDescriptor) res.getDescriptor();
				res.setParent(this);
                                /*hhhhhhhhhhhmmmmmmmmmmmmmmmmm
				this.startAddress = descriptor.getStartAddress();
				this.endAddress = descriptor.getEndAddress();
                                  */
				try {
					this.programName = HardDisk.getFilenameAsInts(descriptor.getProgramName());
				} catch (Exception e) {
					System.out.println("Nepavyko paversti programos pavadinimo į integerių masyvą!");
					e.printStackTrace();
				}
				this.changeStep(1);
			}
			else {
				this.changeStep(0);
			}
			break;
		case (1):
			// Blokuotas laukiant "Išorinė atmintis" resurso
			res = Main.resourceList.searchResource(ResourceType.ISOR_ATM);
			if (res != null) {
				if (res.getParent() == null || res.getParent() == this) {
					res.setParent(this);
					this.changeStep(2);
				}
				else {
					this.changeStep(1);
				}
			}
			else {
				this.changeStep(1);
			}
			break;
		case (2):
			// Blokuotas laukiant "Kanalų įrenginys" resurso
			res = Main.resourceList.searchResource(ResourceType.KANALU_IRENG);
			if (res != null) {
				if (res.getParent() == null || res.getParent() == this) {
					res.setParent(this);
					this.changeStep(3);
				}
				else {
					this.changeStep(2);
				}
			}
			else {
				this.changeStep(2);
			}
			break;
		case (3):
			// Nustatinėjami kanalų įrenginio registra ir vygdoma komanda "XCHG"
			res = Main.resourceList.searchChildResource(this, ResourceType.VYKD_PAR);
			if (res != null) {
                            /* hmmmmmmmmmmmm
				ChannelsDevice.ST = 2; // Šaltinis: supervizorinė atmintis
				ChannelsDevice.DT = 3; // Tikslas: išorinė atmintis
				ChannelsDevice.startAddress = this.startAddress;
				ChannelsDevice.endAddress = this.endAddress;
				ChannelsDevice.programName = this.programName;
				if (ChannelsDevice.XCHG()) { // įrašoma programą iš supervizorinės atminties į išorinę
					this.changeStep(4);
				}
				else {
					res.setParent(null);
					this.changeStep(3);
				}
hmmmmmmmmmmmmmmm*/
                            this.changeStep(4);
			}
			break;
		case (4):
			// Atlaisvinamas "Kanalo įrenginys" resursas
			res = Main.resourceList.searchResource(ResourceType.KANALU_IRENG);
			res.removeParent();
			this.changeStep(5);
			break;
		case (5):
			// Sukuriamas "Užduotis bugne" resursas
			ProgramInHDDDescriptor descriptor = new ProgramInHDDDescriptor();
			descriptor.setProgramName(this.programName);
			res = new Resource(ResourceType.HARD_PROG);
			res.setDescriptor(descriptor);
			Main.resourceList.addResource(res);
			Main.resourceList.deleteChildResource(this, ResourceType.VYKD_PAR);
			this.changeStep(0);
			break;
		}
	}

}

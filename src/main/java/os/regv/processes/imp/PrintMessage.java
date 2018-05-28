package os.regv.processes.imp;

import os.regv.Main;
//>>>>import os.regv.hardware.ChannelsDevice.ChannelsDevice;
//>>>>import os.regv.hardware.memory.RMMemory;
import os.regv.processes.Process;
import os.regv.resources.Resource;
import os.regv.resources.ResourceType;
import os.regv.resources.descriptors.PrintDescriptor;

/**
 * Šio proceso paskirtis – į išvedimo srautą pasiųsti 
 * kokioje nors atmintyje esantį pranešimą.
 * @author gabrius
 *
 */
public class PrintMessage extends Process {
	
	private int startAddress;
	private int endAddress;
        private String mes;

	@Override
	public void nextStep() {
		Resource res;
		switch (this.step) {
		case (0):
			// Blokuotas, laukiam "Eilutė atmintyje" resurso
			res = Main.resourceList.searchResource(ResourceType.EILUTE_ATM);
			if (res != null) {
				PrintDescriptor descriptor = (PrintDescriptor) res.getDescriptor();
				res.setParent(this);
                                mes = descriptor.getMessage();
				//>>>>this.startAddress = descriptor.getStartAddress();
				//>>>>this.endAddress = descriptor.getEndAddress();
				this.changeStep(1);
			}
			else {
				this.changeStep(0);
			}
			break;
		case (1):
			// Blokuotas, laukiam "Kanalų įrenginio" resurso
			res = Main.resourceList.searchResource(ResourceType.KANALU_IRENG);
			if (res != null) {
				if (res.getParent() == null || res.getParent() == this) {
					res.setParent(this);
					this.changeStep(this.step + 1);
				}
				else {
					this.changeStep(2);
				}
			}
			else {
				this.changeStep(2);
			}
			break;
		case (2):
                        System.out.println("*************ISVEDIMO SRAUTAS***************");
                        System.out.printf(mes + "\n");
                        System.out.println("********************************************");
			// Nustatinėjami įrenginio registra ir įvygdoma komanda XCHG
			//>>>>ChannelsDevice.ST = 2;
			//>>>>ChannelsDevice.DT = 4;
			//>>>>ChannelsDevice.startAddress = this.startAddress;
			//>>>>ChannelsDevice.endAddress = this.endAddress;
			//>>>>if (ChannelsDevice.XCHG()) {
				this.changeStep(3);
			//>>>>} else {
				//>>>>}this.changeStep(2);
			//>>>>}
                                
			break;
		case (3):
			// Atlaisvinamas "Kanalų įrenginys" resursas
			res = Main.resourceList.searchResource(ResourceType.KANALU_IRENG);
			res.removeParent();
			Main.resourceList.deleteChildResource(this, ResourceType.EILUTE_ATM);
			this.changeStep(0);
			break;
		}
	}

}

package os.regv.processes.imp;

import os.regv.Main;
import os.regv.processes.Process;
import os.regv.processes.ProcessStatus;
import os.regv.processes.ProcessType;
import os.regv.resources.Resource;
import os.regv.resources.ResourceType;

/**
 * Šis procesas atsakingas už sistemos darbo pradžią ir pabaigą. Įjungus kompiuterį šis procesas
 * pasileidžia automatiškai. Šio proceso paskirtis – sisteminių procesų ir resursų kūrimas.
 * @author gabrius
 */
public class StartStop extends Process {

	public StartStop() {
		this.type = ProcessType.SYSTEM;
	}

	@Override
	public void nextStep() {
		switch (step) {
		case 0:
			// Sisteminiu resursų inicializacija
			Main.resourceList.addResource(new Resource(ResourceType.ISOR_ATM));
			Main.resourceList.addResource(new Resource(ResourceType.VARTOT_ATM));
			Main.resourceList.addResource(new Resource(ResourceType.CPU));
			Main.resourceList.addResource(new Resource(ResourceType.SUPERVIZ_ATM));
			Main.resourceList.addResource(new Resource(ResourceType.KANALU_IRENG));
			this.changeStep(this.step + 1);
			break;
		case 1:
			// Sisteminių permanentinių procesų inicializacija
			Main.processQueue.add(new GetProgramData());
			Main.processQueue.add(new CreateBlocks());
			Main.processQueue.add(new Saver());
			Main.processQueue.add(new Loader());
			Main.processQueue.add(new MainProc());
			Main.processQueue.add(new Interrupt());
			Main.processQueue.add(new PrintMessage());
			this.changeStep(this.step + 1);
			break;
		case 2:
			// Blokavimas laukiant "OS pabaiga" resurso
			if (Main.resourceList.searchResource(ResourceType.END) != null) {
				this.changeStep(this.step + 1);
			} else {
				this.changeStep(2);
			}			
			break;
		case 3:
			// Sisteminių procesų naikinimas
			Main.processQueue.deleteAll();
			this.changeStep(this.step + 1);
			break;
		case 4:
			// Sisteminių resursų naikinimas
			Main.resourceList.deleteAll();
			Main.running = false;
			break;
		}
	}

}

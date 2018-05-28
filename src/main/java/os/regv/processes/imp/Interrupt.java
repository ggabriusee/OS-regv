package os.regv.processes.imp;

import os.regv.Main;
import os.regv.processes.Process;
import os.regv.resources.Resource;
import os.regv.resources.ResourceType;
import os.regv.resources.descriptors.InterruptDescriptor;
//import os.regv.software.executor.InterruptChecker;

/**
 * Šio proceso paskirtis – reaguoti į pertraukimus, kilusius virtualios mašinos
 * darbo metu.
 *
 * @author domas
 *
 */
public class Interrupt extends Process {

    private Resource interrupt;
    private Process jobGovernor;
    private InterruptDescriptor intDes;

    @Override
    public void nextStep() {
        switch (this.step) {
            case (0):
                interrupt = Main.resourceList.searchResource(ResourceType.INT);
                if (interrupt != null) {
                    this.changeStep(1);
                } else {
                    this.changeStep(0);
                }
                break;
            case (1):
            	intDes = (InterruptDescriptor) interrupt.getDescriptor();
            	//intDes = InterruptChecker.getInt();
                this.changeStep(2);
                break;
            case (2):
                jobGovernor = interrupt.getParent();
                Main.resourceList.deleteByInstance(interrupt);
                this.changeStep(3);
                break;
            case (3):
                Resource fromInt = new Resource(ResourceType.FROM_INT);
                fromInt.setDescriptor(intDes);
                fromInt.setParent(jobGovernor);
                Main.resourceList.addResource(fromInt);
                this.changeStep(0);
                break;
        }
    }
}

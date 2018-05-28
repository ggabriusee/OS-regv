package os.regv;

import os.regv.processes.ProcessQueue;
import os.regv.processes.imp.StartStop;
import os.regv.resources.Resource;
import os.regv.resources.ResourceList;
import os.regv.resources.ResourceType;
import os.regv.resources.descriptors.FromGUIDescriptor;
import os.regv.os.CPU;

public class Main {

	public static ResourceList resourceList = new ResourceList();
	public static ProcessQueue processQueue = new ProcessQueue();
        public static CPU cpu;

	public static boolean running = false;

	public static void init() {
                cpu = new CPU();
		processQueue.add(new StartStop());
	}

	public static void main(String args[]) {
                init();
		Resource r = new Resource(ResourceType.PROG_VYKD);
		FromGUIDescriptor descriptor = new FromGUIDescriptor();
		descriptor.setFileName("programa1.txt");
		r.setDescriptor(descriptor);
		resourceList.addResource(r);
		
//		r = new Resource(ResourceType.PROG_VYKD);
//		descriptor = new FromGUIDescriptor();
//		descriptor.setFileName("Fibo_2.ltu");
//		r.setDescriptor(descriptor);
//		resourceList.addResource(r);

		running = true;
		while (running) {
			os.regv.processes.Process process = processQueue.get();
			if (process != null) {
//				 System.out.println(process + " žingsnis: " +
//				 process.getStep() + " prioritetas: " +
//				 process.getPriority());
				process.nextStep();
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}

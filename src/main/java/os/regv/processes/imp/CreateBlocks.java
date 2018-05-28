package os.regv.processes.imp;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Iterator;

import os.regv.Main;
//import os.regv.hardware.memory.RMMemory;
import os.regv.processes.Process;
import os.regv.processes.ProcessType;
import os.regv.resources.Resource;
import os.regv.resources.ResourceType;
import os.regv.resources.descriptors.ExecParamsDescriptor;
import os.regv.resources.descriptors.PrintDescriptor;
import os.regv.resources.descriptors.ProgramInSupervisorDescriptor;
//import os.regv.software.commandsConverter.CommandsConverter;
//import os.regv.software.commandsConverter.Variable;
//import os.regv.software.executor.Compiler;

/**
 * Proceso „CreateBlocks“ paskirtis – gautus blokus iš „ReadFromInterface“ suskirstyti į
 * antraštės blokus ir programos blokus, ir atidavus juos procesui „Saver“,
 * vėl blokuotis laukiant pranešimo iš „GetProgramData“.
 * 
 * @author gabrius
 * 
 */
public class CreateBlocks extends Process {

	// private Resource programResource;

	String st;
	//private int[] content;
        BufferedReader br;
	private String programName;

        public CreateBlocks() {
		this.type = ProcessType.SYSTEM;
        }
        
	@Override
	public void nextStep() {
		switch (this.step) {
		case 0:
			if (Main.resourceList.searchResource(ResourceType.PROG_SUPER) != null) {
				this.changeStep(1);
			} else {
				this.changeStep(0);
			}
			break;
		case 1:
			// Tikrinamas programos validumas
			// Jei programa nekorektiška, gražiname atitinkamą klaidos pranešimą
			Resource r = Main.resourceList.searchResource(ResourceType.PROG_SUPER);
			r.setParent(this);
			ProgramInSupervisorDescriptor descriptor = (ProgramInSupervisorDescriptor) r.getDescriptor();
			this.programName = descriptor.getProgramName();
                        this.br = descriptor.getBr();
			Main.resourceList.deleteChildResource(this, ResourceType.PROG_SUPER);

			try {
                            if(programName.length() >= 16){
                                throw new Exception("\"Program name is too long.\\nExpected max length: 16");
                            }
                            os.regv.Main.cpu.set_program_name(programName);
                            os.regv.Main.cpu.scanCommands(br);
                            //os.regv.Main.cpu.getMemoryList().remove(0).show_Memory();
                            this.changeStep(2);
			} catch (Exception e) {
                            e.printStackTrace();
				Resource resource = new Resource(ResourceType.EILUTE_ATM);
                                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>" + programName.length());
                                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>" + programName);
                                //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>" + e.getMessage());
				resource.setDescriptor(new PrintDescriptor(e.getMessage()));
				Main.resourceList.addResource(resource);
				this.changeStep(0);
			}
			break;
		case 2:
			// Sukuriamas resursas
			// "Užduoties vygdymo parametrai supervizorinėje atmintyje"
			// hmmmm int byteCodeStart = RMMemory.loadProgramToMemory(this.byteCode);
			ExecParamsDescriptor execDescriptor = new ExecParamsDescriptor();
			execDescriptor.setProgramName(this.programName);
                        /*hmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm
			execDescriptor.setStartAddress(byteCodeStart);
			execDescriptor.setEndAddress(byteCodeStart + this.byteCode.length
					+ 1);
                         */
			Resource res = new Resource(ResourceType.VYKD_PAR);
			res.setDescriptor(execDescriptor);
			Main.resourceList.addResource(res);
			this.changeStep(0);
			break;
		}
	}

	private int[] concat(int[] A, int[] B) {
		int aLen = A.length;
		int bLen = B.length;
		int[] C = new int[aLen + bLen];
		System.arraycopy(A, 0, C, 0, aLen);
		System.arraycopy(B, 0, C, aLen, bLen);
		return C;
	}
}

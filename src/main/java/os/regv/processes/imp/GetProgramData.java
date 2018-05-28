package os.regv.processes.imp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import os.regv.Main;
import os.regv.os.HardDisk;
import os.regv.processes.Process;
import os.regv.processes.ProcessType;
import os.regv.resources.Resource;
import os.regv.resources.ResourceType;
import os.regv.resources.descriptors.FromGUIDescriptor;
import os.regv.resources.descriptors.PrintDescriptor;
import os.regv.resources.descriptors.ProgramInSupervisorDescriptor;
import os.regv.resources.descriptors.PrintDescriptor;

/**
 * Šį procesą kuria ir naikina procesas „StartStop“. Proceso paskirtis – gavus
 * informaciją iš įvedimo srauto ir atlikus pirminį jos apdorojimą, atiduoti
 * informaciją tolesniam apdorojimui, kurį atliks procesas „CreateBlocks“.
 * 
 * @author gabrius
 */
public class GetProgramData extends Process {

	private String fileName;
        String st;
        String programName;
	//private int[] content;
        BufferedReader br;

        public GetProgramData() {
		this.type = ProcessType.SYSTEM;
	}
	@Override
	public void nextStep() {
		Resource res;
		switch (this.step) {
		case (0):
			// Blokuojam kol sulaukiam resurso "Iš vartojo sąsajos
			res = Main.resourceList.searchResource(ResourceType.PROG_VYKD);
			if (res != null) {
				FromGUIDescriptor descriptor = (FromGUIDescriptor) res.getDescriptor();
				this.fileName = descriptor.getFileName();
				Main.resourceList.delete(ResourceType.PROG_VYKD);
				this.changeStep(this.step + 1);
			} else {
				this.changeStep(0);
			}
			break;
		case (1):
			// Nuskaitom failą
                        File file = new File(this.fileName);
			try {
				br = new BufferedReader(new FileReader(file));
				this.changeStep(this.step + 1);
			} catch (IOException e) {
				e.printStackTrace();
				Resource r = new Resource(ResourceType.EILUTE_ATM);
				r.setDescriptor(new PrintDescriptor("Failed to open  "
						+ this.fileName + " file!"));
				Main.resourceList.addResource(r);
				this.changeStep(0);
			}
			break;
		case (2):
			res = Main.resourceList.searchResource(ResourceType.SUPERVIZ_ATM);
			if (res != null) {
				if (res.getParent() == null) {
					res.setParent(this);
					this.changeStep(this.step + 1);
				} else {
					this.changeStep(2);
				}
			} else {
				this.changeStep(2);
			}
			break;
		case (3):
			// Kopijuojam blokus į supervizorinę atmintį
			res = Main.resourceList.searchResource(ResourceType.SUPERVIZ_ATM);
			if (res != null) {
				if (res.getParent() == this) {
					res.removeParent();
					// Read program name.
                                        try{
                                            st = br.readLine();
                                            if(!st.equals("STRT")){
                                                throw new Exception();
                                            }
                                            programName = br.readLine();
                                           this.changeStep(this.step + 1);
                                        }catch(Exception e){
                                                    Resource r = new Resource(ResourceType.EILUTE_ATM);
                                                    r.setDescriptor(new PrintDescriptor("No STRT detected"));
                                                    Main.resourceList.addResource(r);
                                                    this.changeStep(0);
                                        }
                                        
				} else {
					this.changeStep(2);
				}
			} else {
				this.changeStep(2);
			}
			break;
		case (4):
			// Sukuriamas resursas "Užduotis supervizorinėje atmintyje"
			Resource r = new Resource(ResourceType.PROG_SUPER);
			ProgramInSupervisorDescriptor descriptor = new ProgramInSupervisorDescriptor();
			descriptor.setBr(this.br);
			descriptor.setProgramName(this.programName);
			r.setDescriptor(descriptor);
			Main.resourceList.addResource(r);
			this.changeStep(0);
			break;
		}

	}

}

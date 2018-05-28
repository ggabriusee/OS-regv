package os.regv.resources;

import java.util.ArrayList;
import java.util.Iterator;

public class ResourceList {

	private ArrayList<Resource> list = new ArrayList<Resource>();
	
	public ResourceList() {
		
	}
	
	public void addResource(Resource resource) {
		System.out.println("resource " + resource.getName() + " created");
		this.list.add(resource);
	}
	
	public Resource searchResource(String name) {
		Iterator<Resource> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			Resource resource = iterator.next();
			if (resource.getName().equalsIgnoreCase(name)) {
				return resource;
			}
		}
		return null;
	}
	
	public Resource searchChildResource(os.regv.processes.Process process, String name) {
		Iterator<Resource> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			Resource resource = iterator.next();
			if (resource.getName().equalsIgnoreCase(name) && resource.getParent() == process) {
				return resource;
			}
		}
		return null;
	}

	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	public void delete(String name) {
		Iterator<Resource> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			Resource resource = iterator.next();
			if (resource.getName().equalsIgnoreCase(name)) {
				iterator.remove();
				return;
			}
		}	
	}
	
	public void deleteChildResource(os.regv.processes.Process process, String name) {
		Iterator<Resource> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			Resource resource = iterator.next();
			if (resource.getName().equalsIgnoreCase(name) && resource.getParent() == process) {
				iterator.remove();
				return;
			}
		}
	}
	
	public void deleteByInstance(Resource res) {
		Iterator<Resource> iterator = this.list.iterator();
		while (iterator.hasNext()) {
			Resource resource = iterator.next();
			if (resource == res) {
				iterator.remove();
				return;
			}
		}
	}

	
}

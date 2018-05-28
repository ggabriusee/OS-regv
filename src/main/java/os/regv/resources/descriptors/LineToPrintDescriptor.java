package os.regv.resources.descriptors;

import os.regv.resources.descriptors.interfaces.ResourceDescriptorInterface;

/**
 *
 * @author Arturas
 */
public class LineToPrintDescriptor implements ResourceDescriptorInterface{

    private int line;

    public int getLine() {
        return this.line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}

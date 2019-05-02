package gui;
import java.io.File;

import javax.swing.filechooser.FileFilter;

public class PersonFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		String name = f.getName();
		
		String extension = Utils.getFileExtension(name);
		
		
		if (f.isDirectory()) {
			return true;
		}
		
		if (extension == null) {
			return false;
		}
		
		if (extension.equals("per")) {
			return true;
		}
		
		return false;
	}

	@Override
	public String getDescription() {
		return "Person database files (*.per)";
	}

}

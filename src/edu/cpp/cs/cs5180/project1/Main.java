package edu.cpp.cs.cs5180.project1;

import java.io.File;
import java.io.IOException;

import edu.cpp.cs.cs5180.project1.NoiseRemoval;

public class Main {

	public static void main(String[] args) throws IOException { 
		
		File folder = new File("repository");
		File[] listOfFiles = folder.listFiles();
		String filePath = "";
		String result = "";
		
		for (File file : listOfFiles) {
			if (file.isFile()) {
				// Get the file path for each file
				filePath = folder + "/" + file.getName();
				System.out.println("File path: " + filePath);
				
				// Initialize the noise removal
				NoiseRemoval nr = new NoiseRemoval();
				nr.setDoc(filePath);
				nr.basicFilter(true);
				
				// If there is no body section after applying the basic filtering
				// reload the doc and apply the basic filtering without removing empty tags.
				// Do not perform tokenToRatio to extract the main content if no body section
				// after applying the basic filtering.
				if (nr.getBodySize() == 0) {
					nr.setDoc(folder + file.getName());
					nr.basicFilter(false);
					result = nr.getHTML();	
				} else {
					nr.tokenize(nr.getBody());
					nr.optimize();
					result = nr.getCombined();
				}
				
				// Write the result to a file
				nr.writeToFile(result, filePath);
			}
		}
		
	} 

}


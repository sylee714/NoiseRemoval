package edu.cpp.cs.cs5180.project1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

/**
 * @author SYL
 *
 */
public class NoiseRemoval {
	private String[] basicTags = {"audio","button", "img", "input", "nav", "video", "script", "style", "a", "link"};
	private File inputFile;
	private Document doc; 
	
	public NoiseRemoval() {
	}

	
	/**
	 * Set the document with the given file
	 * @param filePath
	 */ 
	public void setDoc(String filePath) {
		inputFile = new File(filePath);
		try {
			doc = Jsoup.parse(inputFile, "UTF-8", "");
		} catch (IOException e) {
			System.out.println("Error occured while loading the file");
		}
	}
	
	/**
	 * Apply basic filters on the original HTML file to filter out obvious noises. 
	 * @return Filtered HTML content as String
	 */
	public String basicFilter() {
		String result = "";
		removeTags();
		Elements filteredContent = doc.select("html");
		if (!filteredContent.isEmpty()) {
			result = filteredContent.toString();
		}
		return result;
	}
	
	public void removeTags() {
		for (String tag: basicTags) {
			doc.select(tag).remove();
		}
	}
	
	public void writeToFile(String str, String filePath) {
		try {
			FileWriter myWriter = new FileWriter(filePath);
			myWriter.write(str);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");			
		} catch (IOException e) {
			System.out.println("An error occurted.");
			e.printStackTrace();
		}
	}
	
}

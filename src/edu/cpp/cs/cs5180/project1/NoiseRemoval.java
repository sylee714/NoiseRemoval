package edu.cpp.cs.cs5180.project1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

/**
 * @author SYL
 *
 */
public class NoiseRemoval {
	private String[] basicTags = {"audio","button", "img", "input", "nav", "video", "script", "style", "a", "link", "footer"};
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
	public void basicFilter() {
		removeTags();
		removeEmptyTags();
		removeNav();
	}
	
	/**
	 * Remove obvious noise tags
	 */
	public void removeTags() {
		for (String tag: basicTags) {
			doc.select(tag).remove();
		}
	}
	
	//removes all html aspects
	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
	
	/**
	 * Traverse the tree
	 */
	public void traverse() {
//		int maxDepth = 0;
		doc.traverse(new NodeVisitor() {
			int maxDepth = 0;
			public void head(Node node, int depth) {
//				System.out.println("Node start: " + node.nodeName());
//				System.out.println("Node start depth: " + depth);
				if (maxDepth < depth) {
					maxDepth = depth;
					System.out.println("Node start: " + node.nodeName());
					System.out.println("Max depth: " + maxDepth);
					System.out.println("Attributes: " + node.attributes());
//					System.out.println("Child node size: " + node.childNodeSize());
//					System.out.println("Child node: " + node.childNodes().toString());
					
				}
			}
			
			public void tail(Node node, int depth) {
//				System.out.println("Node end: " + node.nodeName());
//				System.out.println("Node end depth: " + depth);
				if (maxDepth < depth) {
					maxDepth = depth;
					System.out.println("Max depth: " + maxDepth);
				}
			}
			
		});
//		System.out.println("Max Depth: " + maxDepth);
	}
	
	/**
	 * Remove empty tags after performing first removal
	 */
	public void removeEmptyTags() {
		Elements elements = doc.body().select("*");
		
		for (Element element : elements) {
			if (!element.hasText()) {
//				System.out.println("Tag: "+ element.tagName());
//				System.out.println("Attributes: " + element.attributes());
//				System.out.println("Content: " + element.text());
				element.remove();
		 	}
		}
	}
	
	/**
	 * Remove tags with navigation purpose
	 */
	public void removeNav() {
		Elements elements = doc.select("nav");
		String[] navs = {"div[class*=nav]", "div[role*=nav]", "div[id*=nav]"};
		for (String nav : navs) {
//			if (!elements.containsAll(doc.select(nav))) {
//				elements.addAll(doc.select(nav));
//			}
			doc.select(nav).remove();
		}
		
//		for (Element element : elements) {
//			System.out.println("Tag: " + element.tag());
//			System.out.println("ID: " + element.id());
//			System.out.println("Attr: " + element.attributes());
//		}
	}
	
	public void writeToFile(String str, String filePath) {
		try {
			// Second parameter = false means overwrite; true means otherwise.
			FileWriter myWriter = new FileWriter(filePath, false);
			myWriter.write(str);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");			
		} catch (IOException e) {
			System.out.println("An error occurted.");
			e.printStackTrace();
		}
	}
	
	public String getHTML() {
		String result = "";
		Elements elements = doc.select("html");
		System.out.println(elements.size());
		for (Element element : elements) {
			result += element.toString();
		}
		return result;
	}
	
}

package edu.cpp.cs.cs5180.project1;

import java.io.IOException;

import edu.cpp.cs.cs5180.project1.NoiseRemoval;

public class Main {

	public static void main(String[] args) throws IOException { 
		NoiseRemoval nr = new NoiseRemoval();
//		String num = "1";
//		String temp = "sample" + num + ".html";
//		System.out.println(temp);
		String src = "sample.html";
		
		// Load the file and apply basic filter
		nr.setDoc(src);
		int size = nr.getHTML().length();
//		System.out.println("Initial size");
//		System.out.println("Size: " + size);
		nr.basicFilter();
		
		
		// Check after applying the basic filter
		String result = nr.getHTML();
//		System.out.println("Before applying tagToRatio");
//		System.out.println("Size: " + result.length());
		
//		System.out.println(nr.getBody());
		
		// Check tagToRatio method
//		nr.setStringFile(result);
//		nr.tagToTextRatio(result, 5.0);
//		System.out.println("After applying tagToRatio");
//		System.out.println("Size: " + nr.getStringFileSize());
//		System.out.println(nr.getStringFile());
		
		// Write the result to a file
		String dst = "filtered.html";
		nr.writeToFile(result, dst);
//		
//		
		System.out.println("-------------------------------------");
//		System.out.println(nr.getBody());
		nr.tokenize(nr.getBody());
		nr.optimize();
//		nr.printPairs();
		nr.printMainContent();
	} 

}


package edu.cpp.cs.cs5180.project1;

import java.io.IOException;

import edu.cpp.cs.cs5180.project1.NoiseRemoval;

public class Main {

	public static void main(String[] args) throws IOException { 
		NoiseRemoval nr = new NoiseRemoval();
		String src = "sample.html";
		
		nr.setDoc(src);
		nr.basicFilter();
		
		String result = nr.getHTML();
		
		String dst = "filtered.html";
		nr.writeToFile(result, dst);
		
		System.out.println("Done");
	} 

}


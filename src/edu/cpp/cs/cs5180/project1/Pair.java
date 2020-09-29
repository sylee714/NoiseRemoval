package edu.cpp.cs.cs5180.project1;

public class Pair {
	private int i;
	private int j;
	private int weight;
	
	public Pair(int start, int end, int value) {
		i = start;
		j = end;
		weight = value;
	}
	
	public int getI() {
		return i;
	}
	
	public int getJ() {
		return j;
	}
	
	public int getWeight() {
		return weight;
	}
}

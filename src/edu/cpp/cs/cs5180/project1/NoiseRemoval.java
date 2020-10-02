package edu.cpp.cs.cs5180.project1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
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
	private int initialSize = 0;
	private int finalSize = 0;
	private String[] basicTags = {"audio","button", "img", "input", "nav", "video", "script", 
									"style", "a", "link", "footer", "object", "figure", "track",
									"noscript", "form"};
	private File inputFile;
	private Document doc;
	private String stringfile;
	private ArrayList<Token> tokens = new ArrayList<Token>(); 
	private ArrayList<Pair> pairs = new ArrayList<Pair>();
	private Pair mainContentPair = new Pair(0,0,0);
	private String mainContent = "";
	
	public NoiseRemoval() {
	}
	
	public void getTokens() {
//		System.out.println("Converting to bits");
//		for (Token t : tokens) {
//			System.out.print(t.getBit());
//		}
//		System.out.println("");
		for (Token t : tokens) {
			System.out.println("-------------------------");
			if (t.getContent().contains("\n")) {
				System.out.println("Content: newLine" );
				System.out.println("Bit: " + t.getBit());
			} else {
				System.out.println("Content: " + t.getContent());
				System.out.println("Bit: " + t.getBit());
			}
		}
	}

	public void setStringFile(String str) {
		stringfile = str;
	}
	
	public int getStringFileSize() {
		return stringfile.length();
	}
	
	public String getStringFile() {
		return stringfile;
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
	public void basicFilter(boolean noEmptyTags) {
		removeTags();
		removeNav();
		removeFooter();
		removeComments();
		if (noEmptyTags) {
			removeEmptyTags();
		}
	}
	
	public int getBodySize() {
		Elements elements = doc.select("body");
//		System.out.println("Size of body: " + elements.size());
		return elements.size();
	}
	
	/**
	 * Remove obvious noise tags
	 */
	public void removeTags() {
		for (String tag: basicTags) {
			doc.select(tag).remove();
		}
	}
	
	//gets tag to text ratio (of leaf nodes) and removes them if it is above a certain threshold
	public void tagToTextRatio(String s,double ratio) {
		//defining variables
		String openTag;
		String closeTag;
		if(s.indexOf("<")==-1|s.indexOf(">")==-1)return;
		openTag = s.substring(s.indexOf("<"),s.indexOf(">")+1);
		if(openTag.indexOf(" ") == -1) {
			closeTag = "</"+openTag.substring(1);
		}
		else {
			closeTag = "</"+openTag.substring(1,openTag.indexOf(" "))+">";
		}
		//if there is no close tag, it is stand alone, and should be skipped
		if(s.indexOf(closeTag)==-1) {
//			System.out.println("couldnt find "+closeTag);
			tagToTextRatio(s.substring(s.indexOf(openTag)+openTag.length()),ratio);
		}
		else {
			String mid = s.substring(s.indexOf(openTag)+openTag.length(),s.indexOf(closeTag));
			//traverse 1 level deeper
			if(mid.contains("<")) {
				tagToTextRatio(mid.trim(),ratio);
			}
			else {
				//case: leaf node
				float f = (float)mid.length()/(float)(openTag.length()+closeTag.length());
				if(f<ratio) {
					stringfile = stringfile.substring(0,stringfile.indexOf(mid))+stringfile.substring(stringfile.indexOf(mid)+mid.length());
				}
			}
		}
		//traverse across
		if(s.indexOf(closeTag)!=-1&&s.substring(s.indexOf(closeTag)+closeTag.length()).contains("<")) {
			tagToTextRatio(s.substring(s.indexOf(closeTag)+closeTag.length()),ratio);
		}
	}
	
	//removes all HTML aspects
	public static String html2text(String html) {
	    return Jsoup.parse(html).text();
	}
	
	
	/**
	 * Converts the doc to String
	 * @return String
	 */
	public String getHTML() {
		String result = "";
		Elements elements = doc.select("html");
		for (Element element : elements) {
			result += element.toString();
		}
		return result;
	}
	
	/**
	 * Get the head of the doc and return it as String
	 * @return
	 */
	public String getHead() {
		String result = "";
		Elements elements = doc.select("head");
		for (Element element : elements) {
			result += element.toString();
		}
		return result;
	}
	
	/**
	 * Get the body of the doc and return it as String.
	 * @return String
	 */
	public String getBody() {
		String result = "";
		Elements elements = doc.select("body");
		for (Element element : elements) {
			result += element.toString();
		}
		return result;
	}
	
	/**
	 * Remove comments.
	 */
	public void removeComments() {
		List<Node> toRemove = new LinkedList<>();
		doc.traverse(new NodeVisitor() {
			@Override
			public void head(Node node, int depth) {
				if (node.nodeName().equals("#comment")) {
					toRemove.add(node);
				}
			}
			@Override
			public void tail(Node arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		for (Node node : toRemove) {
			node.remove();
		}
	}
	
	/**
	 * Remove empty tags (element with no texts) after performing first removal.
	 */
	public void removeEmptyTags() {
		Elements elements = doc.body().select("*");
		
		for (Element element : elements) {
			if (!element.hasText()) {
				element.remove();
		 	}
		}
	}
	
	/**
	 * Remove tags with a navigation purpose.
	 */
	public void removeNav() {
		String[] navs = {"div[class*=nav]", "div[role*=nav]", "div[id*=nav]"};
		for (String nav : navs) {
			doc.select(nav).remove();
		}
	}
	
	/**
	 * Remove tags with a footer purpose.
	 */
	public void removeFooter() {
		String[] footers = {"div[class*=footer]", "div[role*=footer]", "div[id*=footer]"};
		for (String footer : footers) {
			doc.select(footer).remove();
		}
	}
	
	
	/**
	 * Write the given String to a file with the given path.
	 * @param str
	 * @param filePath
	 */
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
	
	/**
	 * Tokenize the whole web-page. Valid tags = 1 and other tags/texts = 0.
	 * @param str
	 */
	public void tokenize(String str) {
//		ArrayList<Token> result = new ArrayList<Token>();
		int index = 0;
		int skip = 0;
		int invalidTagIndex = -1;
		int validTagIndex = -1;
		String tempSubStr = "";
		String subStr = "";
		String token = "";
		String temp = "";
		while (index < str.length()) {
			skip = 0;
			// when you see the opening of a tag
			if (str.charAt(index) == '<') {
				subStr = str.substring(index);
				tempSubStr = subStr.substring(1);
				// Whenever there is an opening tag, then its corresponding closing tag
				// has to come before another opening tag to make a valid tag.
				// ex. "<p> < </p>" invalid
				// ex. "<p>   </p>" valid
				invalidTagIndex = tempSubStr.indexOf('<');
				validTagIndex = tempSubStr.indexOf('>');
				// for the last tag
				if (invalidTagIndex == -1) {
					token = subStr.substring(0, subStr.indexOf('>')+1);
					Token newToken = new Token(token);
					tokens.add(newToken);
					skip += token.length();
				// when there is no invalid opening tag
				} else if (validTagIndex < invalidTagIndex) {
					token = subStr.substring(0, subStr.indexOf('>')+1);
					Token newToken = new Token(token);
					tokens.add(newToken);
					skip += token.length();
				// when there is an invalid opening tag, turn itself as a token
				} else {
					token = subStr.substring(0,1);
					Token newToken = new Token(token);
					tokens.add(newToken);
					skip += token.length();
				}
				
			// when you see the first char of a word
			} else if (str.charAt(index) != ' ' && str.charAt(index) != '\n') {
				subStr = str.substring(index);
//				System.out.println("*********");
//				System.out.println(subStr);
				// texts in a tag(s) can either end before 
				// an empty space or the opening of a closed tag
				// Get two and compare the size and the one with 
				// the smaller size will be the token.
				if (subStr.indexOf(' ') != -1) {
					token = subStr.substring(0, subStr.indexOf(' '));
					temp = subStr.substring(0, subStr.indexOf('<'));
					if (token.length() > temp.length()) {
						token = temp;
					}
				// when it's the last word that does not end with an empty space
				} else {
					token = subStr.substring(0, subStr.indexOf('<'));
				}
				
				Token newToken = new Token(token);
				tokens.add(newToken);
				skip += token.length();
			
			// skip new lines or empty spaces
			} else {
//				System.out.println("empty space");
				skip++;
			}
			index += skip;
		}
	}
	
	/**
	 * Calculate the weight for a given interval, i - j.
	 * @param i
	 * @param j
	 * @return
	 */
	public int calculateWeight(int i, int j) {
		int sum1 = 0;
		for (int k = 0; k < i; ++k) {
			sum1 += tokens.get(k).getBit();
		}
		
		int sum2 = 0;
		for (int k = i; k < j; ++k) {
			sum2 += (1 - tokens.get(k).getBit());		
		}
		
		int sum3 = 0;
		for (int k = j; k < tokens.size(); ++k) {
			sum3 += tokens.get(k).getBit();
		}
		
		int total = sum1 + sum2 + sum3;
		
		return total;
	}
	
	/**
	 * Loop thru the main content pairs and find the one with the highest weight.
	 */
	public void optimize() {
		int weight = 0;
		for (int i = 0; i < tokens.size(); ++i) {
			for (int j = i+1; j < tokens.size(); ++j) {
				weight = calculateWeight(i, j);
				if (mainContentPair.getWeight() < weight) {
					mainContentPair.setI(i);
					mainContentPair.setJ(j);
					mainContentPair.setWeight(weight);
				}
//				Pair pair = new Pair(i, j, weight);
//				pairs.add(pair);
			}
		}
//		Collections.sort(pairs, new CustomComparator());
	}
	
	/**
	 * Print top 20 pairs of indices after calculating their weights
	 */
//	public void printPairs() {
//		System.out.println("Size of Tokens: " + tokens.size());
//		System.out.println("Size of Pairs: " + pairs.size());
//		for (int i = 0; i < 20; ++i) {
//			System.out.println("i: " + pairs.get(i).getI());
//			System.out.println("j: " + pairs.get(i).getJ());
//			System.out.println("weight: " + pairs.get(i).getWeight());
//			System.out.println("---------------");
//		}
//	}
//	
//	public void printMainContentInfo() {
//		System.out.println("i: " + mainContentPair.getI());
//		System.out.println("j: " + mainContentPair.getJ());
//		System.out.println("Weight: " + mainContentPair.getWeight());
//	}
//	
//	public class CustomComparator implements Comparator<Pair> {
//		@Override
//	    public int compare(Pair o1, Pair o2) {
//			// -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
//	        return o1.getWeight() > o2.getWeight() ? -1 : o1.getWeight() < o2.getWeight() ? 1: 0;
//	    }
//	}
	
	/**
	 * Get the main content of the web-page after performing tokenToRatio.
	 * @return
	 */
	public String getMainContent() {
		String result = "";
		addOpenTag();
		addCloseTag();
		boolean hasOpenBodyTag = hasOpenBodyTag();
		boolean hasCloseBodyTag = hasCloseBodyTag();
		// If the first element is not the open body tag, add it.
		if (!hasOpenBodyTag) {
			result = result + "<body>" + "\n";
		}
		for (int i = mainContentPair.getI(); i < mainContentPair.getJ() + 1; ++i) {
			if (tokens.get(i).getBit() == 0) {
				result = result + tokens.get(i).getContent() + " ";
			} else { 
				if  (i > mainContentPair.getI() && tokens.get(i-1).getBit() == 0) {
					result = result + "\n";
				}
				result = result + tokens.get(i).getContent() + "\n";
			}
		}
		// If the last element is not the close body tag, add it.
		if (!hasCloseBodyTag) {
			result = result + "</body>";
		}
		return result;
	}
	
	/**
	 * Check if first token is the open body tag.
	 * @return True, if it is; otherwise, false.
	 */
	public boolean hasOpenBodyTag() {
		String firstElement = tokens.get(mainContentPair.getI()).getContent();
		if (firstElement.contains("<body")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Check if first token is the close body tag.
	 * @return True, if it is; otherwise, false.
	 */
	public boolean hasCloseBodyTag() {
		String lastElement = tokens.get(mainContentPair.getJ()).getContent();
		if (lastElement.contains("</body>")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Check if the first element of the main content is an open tag.
	 * If it's not, loop back until find an open tag.
	 */
	public void addOpenTag() {
		int index = mainContentPair.getI();
		while (index > 0 && tokens.get(index).getBit() != 1) {
			index--;
		}
		mainContentPair.setI(index);
	}
	
	/**
	 * Check if the last element of the main content is a close tag.
	 * If it's not, loop until find a close tag.
	 */
	public void addCloseTag() {
		int index = mainContentPair.getJ();
		while (index < tokens.size() && tokens.get(index).getBit() != 1) {
			index++;
		}
		mainContentPair.setJ(index);
	}
	
	/**
	 * Get the opening of the HTML tag.
	 * @return opening HTML tag as String
	 */
	public String getHTMLTag() {
		String htmlTag = "";
		String[] lines = getHTML().split("\n");
		htmlTag = lines[0];
		return htmlTag;
	}
	
	/**
	 * Combine the head section and the body section.
	 * @return String of head and body combined.
	 */
	public String getCombined() {
		String htmlTag = getHTMLTag();
		String combinded = htmlTag;
		String head = getHead();
		String body = getMainContent();
		combinded = combinded + "\n" + head + "\n" + body + "\n" + "</html>";
		return combinded;
	}
}

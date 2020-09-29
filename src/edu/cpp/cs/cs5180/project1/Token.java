package edu.cpp.cs.cs5180.project1;

public class Token {
	private String content;
	private int bit;
	private String[] formattingTags = {"b", "strong", "i", "em", "mark", "small", "del", "ins", "sub","sup"};
	private String[] headingTags = {"h1", "h2", "h3", "h4", "h5", "h6"};
	private String[] tableTags = {"table", "tr", "th", "td", "caption"};
	private String[] emptyTags = {"area", "base", "basefont", "br", "col", "frame", 
									"hr", "img", "input", "isindex", "link", "meta", "param"};
	private String[] validTags = {"p", "div", "span", "section", "article", "body", "main"};

	public Token(String str) {
		content = str;
		setBit();
	}

	public String getContent() {
		return content;
		
	}
	
	public int getBit() {
		return bit;
	}
	
	public void setBit() {
		boolean valid = false;
		if (content.charAt(0) == '<' && content.charAt(content.length()-1) == '>') {
			for (int i = 0; i < validTags.length; ++i) {
				if (content.contains(validTags[i])) {
					valid = true;
					break;
				}
			}
			if (valid) {
				bit = 1;				
			} else {
				bit = 0;
			}
		} else {
			bit = 0;
		}
	}
}

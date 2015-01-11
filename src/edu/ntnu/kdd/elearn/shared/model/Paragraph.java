package edu.ntnu.kdd.elearn.shared.model;

import java.io.Serializable;
import java.util.List;

public class Paragraph implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int start, end;
	private List<Sentence> sentenceList;
	private String content;
	
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public List<Sentence> getSentenceList() {
		return sentenceList;
	}
	public void setSentenceList(List<Sentence> sentenceList) {
		this.sentenceList = sentenceList;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
}

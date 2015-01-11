package edu.ntnu.kdd.elearn.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Sentence implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String content;
	
	private List<Question> questionList = new ArrayList<Question>();
	
	public List<Question> getQuestionList() {
		return questionList;
	}

	public void addQuestion(Question question) {
		questionList.add(question);
	}
	
	private List<String> changeList = new ArrayList<String>();

	private boolean filter;
	private int start, chCount;
	
	private ArrayList<Word> wordList = new ArrayList<Word>();
	
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ArrayList<Word> getWordList() {
		return wordList;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getChCount() {
		return chCount;
	}

	public void setChCount(int chCount) {
		this.chCount = chCount;
	}

	public int getQuestionCount() {
	
		
		return questionList.size();
	}
	
	public int getQuestionCount(Type type){
		int count = 0;
		for (Question q : questionList){
			if (q.getType() == type){
				count ++;
			}
		}
		
		return count;
	}

	public void addChange(String changeString) {
		changeList.add(changeString);
		
	}

	public List<String> getChangeList() {
		return changeList;
	}

	
	public void out(){//test output content
		System.out.print(content);
	}
	/*
	public void setDialogue(boolean dialogue) {
		this.dialogue = dialogue;
	}
	public boolean getDialogue() {
		return dialogue;
	}
	public void setSubsentence(boolean subsentence) {
		this.subsentence = subsentence;
	}
	public boolean getSubsentence() {
		return subsentence;
	}
	*/
	public void setFilter(boolean filter) {
		this.filter = filter;
	}
	public boolean getFilter() {
		return filter;
	}
	
}

package edu.ntnu.kdd.elearn.shared.model;

import java.io.Serializable;
import java.util.ArrayList;

import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.trees.GrammaticalRelation;

public class Word implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String original;
	private String pos;
	private String ner = null;
	private int beginPosition;
	private int endPosition;
	private ArrayList<IndexedWord> ReWd ;
	private ArrayList<GrammaticalRelation> Reln;
	
	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String tag) {
		this.pos = tag;
	}

	public String getNer() {
		return ner;
	}

	public void setNer(String ner) {
		this.ner = ner;
	}

	public ArrayList<GrammaticalRelation> getReln() {
		return Reln;
	}

	public ArrayList<IndexedWord> getReWd() {
		return ReWd;
	}
	
	public void addReln(GrammaticalRelation relation){
		if(Reln == null)
			Reln = new ArrayList<GrammaticalRelation>();
		Reln.add(relation);
	}
	
	public void addReWd(IndexedWord w){
		if(ReWd == null)
			ReWd = new ArrayList<IndexedWord>();
		ReWd.add(w);
	}

	public int getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	public int getBeginPosition() {
		return beginPosition;
	}

	public void setBeginPosition(int beginPosition) {
		this.beginPosition = beginPosition;
	}

	
	
}

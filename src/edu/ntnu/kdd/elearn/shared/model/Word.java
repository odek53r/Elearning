package edu.ntnu.kdd.elearn.shared.model;

import java.io.Serializable;

public class Word implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String original;
	private String pos;
	private String ner = null;
	
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

	
	
}

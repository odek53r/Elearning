package edu.ntnu.kdd.elearn.shared.model;

import java.io.Serializable;

public class Question implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String content;
	private Type type;
	private boolean isUsed;
	private long id;
	private long articleID;
	
	
	public boolean getIsUsed() {
		return isUsed;
	}
	public void setIsUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}
	
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getArticleID() {
		return articleID;
	}
	public void setArticleID(long articleID) {
		this.articleID = articleID;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	
	public void set_sinorpul(boolean check){ //檢查問句的單複數問題
		int doindex = -1 ;
		int writer = -1 ;
		if(check){
			if(((doindex = this.content.indexOf("Do ")) >= 0) && ((this.content.indexOf("the writer ")) >= 0)) {
				this.content = this.content.subSequence(0, doindex) + "Does " + this.content.subSequence(doindex+3,content.length()) ;
			}
		}
		
		
	}
	
}

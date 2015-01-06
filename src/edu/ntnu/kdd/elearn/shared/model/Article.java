package edu.ntnu.kdd.elearn.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Article implements Serializable{
	/**
	 * 
	 */
	
	public enum TYPE {STANDARD, DIALOG};
	
	private static final long serialVersionUID = 1L;
	private long id;
	private String Title;
	private String content;
	private Date createTime;
	private Date updateTime;
	private String author;
	private String uploader;
	private String coreferenceResult[]=null;
	
	private ArrayList<Sentence> sentenceList = new ArrayList<Sentence>();
	
	private boolean isFirstPerson = false;
	
	private String type;
	
	
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public ArrayList<Sentence> getSentenceList() {
		return sentenceList;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getUploader() {
		return uploader;
	}
	public void setUploader(String uploader) {
		this.uploader = uploader;
	}
	public boolean isFirstPerson() {
		return isFirstPerson;
	}
	public void setFirstPerson(boolean isFirstPerson) {
		this.isFirstPerson = isFirstPerson;
	}
	public String[] getCoreferenceResult() {
		return coreferenceResult;
	}
	public void setCoreferenceResult(String[] coreferenceResult) {
		this.coreferenceResult = coreferenceResult;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	public void out(){ //test code
		System.out.print(content);
	}
	
	
}

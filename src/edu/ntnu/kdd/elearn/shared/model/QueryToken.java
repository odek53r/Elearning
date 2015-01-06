package edu.ntnu.kdd.elearn.shared.model;

public class QueryToken {
	public enum Type{WORD, VERBCOUNT, POS};
	
	private Type type;
	private String stringContent;
	private int intContent;
	
	public QueryToken(){
		
	}
	
	public QueryToken(String token){
		
	
	}
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getStringContent() {
		return stringContent;
	}
	public void setStringContent(String stringContent) {
		this.stringContent = stringContent;
	}
	public int getIntContent() {
		return intContent;
	}
	public void setIntContent(int intContent) {
		this.intContent = intContent;
	}
	
	
	
}

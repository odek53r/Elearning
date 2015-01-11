package edu.ntnu.kdd.elearn.shared.model;

import java.util.List;

public class Query {
	private List<QueryToken> tokenList;

	public List<QueryToken> getTokenList() {
		return tokenList;
	}

	public void setTokenList(List<QueryToken> tokenList) {
		this.tokenList = tokenList;
	}
	
	
}

package edu.ntnu.kdd.elearn.server.nlp;

import java.util.ArrayList;

import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Sentence;
import edu.ntnu.kdd.elearn.shared.model.Type;
import edu.ntnu.kdd.elearn.shared.model.Word;

public class WhenQG extends ConnectiveQG{

	public WhenQG(NLPUtil nlpUtil) {
		super(nlpUtil);
		this.connective = "when";
		this.type = Type.WHEN;
		this.prefix = "When";
	}
	
	public WhenQG(NLPUtil nlpUtil, String connective) {
	
		this(nlpUtil);
		this.connective = connective;
		
	}
}

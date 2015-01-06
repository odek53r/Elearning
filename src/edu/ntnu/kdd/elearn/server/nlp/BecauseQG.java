package edu.ntnu.kdd.elearn.server.nlp;

import edu.ntnu.kdd.elearn.shared.model.Type;

public class BecauseQG extends ConnectiveQG {

	public BecauseQG(NLPUtil nlpUtil) {
		super(nlpUtil);
		this.connective = "because";
		this.type = Type.BECAUSE;
		this.prefix = "Why";
	}
	
}

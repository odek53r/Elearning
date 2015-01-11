package edu.ntnu.kdd.elearn.server.nlp;

import java.util.ArrayList;
import java.util.List;

public class QGBuilder {
	private static List<QuestionGeneratorInterface> qgList = null;

	public static List<QuestionGeneratorInterface> listQG(NLPUtil nlpUtil) {

		if (qgList == null) {
			
			qgList = new ArrayList<QuestionGeneratorInterface>();
			qgList.add(new YNQuestionGenerator(nlpUtil));
			qgList.add(new BecauseQG(nlpUtil));
			qgList.add(new WhenQG(nlpUtil));
			qgList.add(new TimeQG(nlpUtil));
			qgList.add(new WhereQG(nlpUtil));
			qgList.add(new TestWhoQG(nlpUtil));
		}
		return qgList;

	}

}

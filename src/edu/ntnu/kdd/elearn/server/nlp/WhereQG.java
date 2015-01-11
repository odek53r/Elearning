package edu.ntnu.kdd.elearn.server.nlp;

import java.util.ArrayList;

import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Question;
import edu.ntnu.kdd.elearn.shared.model.Sentence;
import edu.ntnu.kdd.elearn.shared.model.Type;
import edu.ntnu.kdd.elearn.shared.model.Word;

public class WhereQG extends ConnectiveQG {

	private NLPUtil nlpUtil = null;
	
	public WhereQG(NLPUtil nlpUtil) {
		super(nlpUtil);
		this.connective = "where"; //原來是when改作where
		this.type = Type.WHERE;
		this.prefix = "Where";
	}

	@Override
	public boolean isAcceptable(Sentence sentence, Article article) {
		boolean filter = sentence.getFilter();
		if(filter){
			return false;
		}
		boolean result = false;
		String connective = getConnective(sentence);
		if (connective != null) {
			result =  true;
		} else {
			result = false;
		}

		if (result) {
			this.connective = connective;
		}
		result = super.isAcceptable(sentence,article) & result;
		return result;
	}

	public String getConnective(Sentence sentence) {

		String connective = null;

		for (int i = 0; i < sentence.getWordList().size(); i++) {
			ArrayList<Word> wordList = sentence.getWordList();

			if (i > 0 && wordList.get(i - 1).getPos() != null
					&& wordList.get(i - 1).getPos().equals("IN")
					&& wordList.get(i).getNer()!=null
					&& wordList.get(i).getNer().equals("LOCATION")) {
				connective = wordList.get(i - 1).getOriginal();
			}
		}

		return connective;
	}

}

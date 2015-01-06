package edu.ntnu.kdd.elearn.server.nlp;

import java.util.ArrayList;
import java.util.List;

import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Question;
import edu.ntnu.kdd.elearn.shared.model.Sentence;
import edu.ntnu.kdd.elearn.shared.model.Type;
import edu.ntnu.kdd.elearn.shared.model.Word;

public class YNQuestionGenerator implements QuestionGeneratorInterface {

	private NLPUtil nlpUtil = null;

	private YNQuestionGenerator() {

	}

	public YNQuestionGenerator(NLPUtil nlpUtil) {
		this.nlpUtil = nlpUtil;
	}

	@Override
	public boolean isAcceptable(Sentence sentence, Article article) {
		ArrayList<Word> temp = sentence.getWordList();
		nlpUtil.setSentence(article);
		boolean filter = sentence.getFilter();
		if(filter){
			return false;
		}
		else if (nlpUtil.countVerb(temp)!=1 || sentence.getContent().contains(",")){
			return false;
		}
		
		return true;
	}

	@Override
	public List<Question> getQuestion(Sentence sentence, Article article) {
		StandQuestions standQuestions = nlpUtil.getStandQuestions();

		Question question = null;

		if (sentence != null) {
			standQuestions.setStent(sentence.getContent(),sentence);
			standQuestions.setFirstPerson(article.isFirstPerson());
			
			question = new Question();
			question.setContent(standQuestions.getQuest());
			question.setType(Type.YN);
		}
		
		List<Question> qList = new ArrayList<Question>();
		qList.add(question);
		return qList;
	
	}
}

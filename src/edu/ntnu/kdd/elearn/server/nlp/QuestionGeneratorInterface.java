package edu.ntnu.kdd.elearn.server.nlp;

import java.util.List;

import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Question;
import edu.ntnu.kdd.elearn.shared.model.Sentence;

public interface QuestionGeneratorInterface{
	
	public boolean isAcceptable(Sentence sentence, Article article);
	public List<Question> getQuestion(Sentence sentence, Article article);

}

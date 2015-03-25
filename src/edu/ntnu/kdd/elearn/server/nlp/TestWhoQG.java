package edu.ntnu.kdd.elearn.server.nlp;
import java.util.ArrayList;
import java.util.List;

import edu.ntnu.kdd.elearn.server.nlp.QuestionGeneratorInterface;
import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Question;
import edu.ntnu.kdd.elearn.shared.model.Sentence;
import edu.ntnu.kdd.elearn.shared.model.Type;
import edu.ntnu.kdd.elearn.shared.model.Word;


public class TestWhoQG implements QuestionGeneratorInterface{
	 private ArrayList<String> whoQuestions ;
	 private NLPUtil nlpUtil = null;
	 private whoQuestion who;

	public TestWhoQG(NLPUtil nlpUtil) {
		this.nlpUtil = nlpUtil;
	}
	@Override
	public boolean isAcceptable(Sentence sentence,Article article) {
		ArrayList<Word> temp = sentence.getWordList();
		boolean filter = sentence.getFilter();
		if(filter){
			return false;
		}
		int count=nlpUtil.countVerb(temp);
		// TODO Auto-generated method stub
		who = new whoQuestion();	
		whoQuestions = new ArrayList<String>();
		if(count==0){
			return false;
		}
		else if((whoQuestions=who.WhoQG(sentence,article))==null){
			return false;
		}
		//whoQuestions.addAll(who.WhoQG(sentence,article));
		else if(whoQuestions.isEmpty()){
			return false;
		}
		return true;
	}
	public String getChangedSubject()
	{
		return who.getChangedSubject();
	}
	@Override
	public List<Question> getQuestion(Sentence sentence, Article article)  {
		// TODO Auto-generated method stub
		List<Question> whoqList = new ArrayList<Question>();
		
		for(String x:whoQuestions){
			Question whoq = new Question();
			whoq.setContent(x);
			whoq.setType(Type.WHO);	
			whoqList.add(whoq);
		}

		
		return whoqList;
	}

	
}

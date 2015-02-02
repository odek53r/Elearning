package edu.ntnu.kdd.elearn.server.nlp;
/*
銝餉���摮銝血��摮���� because => ���hy��
				       ��� when�����閰� =>���hen��
				       ��� where����� =>���here��
*/
import java.util.ArrayList;
import java.util.List;

import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Question;
import edu.ntnu.kdd.elearn.shared.model.Sentence;
import edu.ntnu.kdd.elearn.shared.model.Type;
import edu.ntnu.kdd.elearn.shared.model.Word;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.trees.GrammaticalRelation;

public abstract class ConnectiveQG implements QuestionGeneratorInterface {

	private List<Word> becauseArg = new ArrayList<Word>();
	private List<Word> anotherArg = new ArrayList<Word>();
	private NLPUtil nlpUtil = null;

	private boolean checkComma = true;

	protected String connective;
	protected Type type;
	protected String prefix;

	public ConnectiveQG(NLPUtil nlpUtil) {
		this.nlpUtil = nlpUtil;
	}

	@Override
	public boolean isAcceptable(Sentence sentence, Article article) { //蝭拚�摮�
		becauseArg.clear();
		anotherArg.clear();
		boolean result = false;
		int connectiveIndex = -1;
		boolean filter = sentence.getFilter();
		if(filter){
			return false;
		}
		//System.out.println("D: " +trace);
		if (sentence != null && filter==false) {
			System.out.println("C Sentence: " + sentence.getContent());
			for (int i = 0; i < sentence.getWordList().size(); i++) {
				Word word = sentence.getWordList().get(i);
				if (word.getOriginal().toLowerCase().equals(connective)) {
					if (!sentence.getWordList().get(i + 1).getOriginal()
							.equals("of")) { //憒��瘝�of
						
						result = true;
						connectiveIndex = i;
						break;
					}

				}
			}
		}

		if (result) {//憿撮One of��摮�,because of

			if (connectiveIndex == 0) {
				boolean isMeetComma = false;
				for (Word word : sentence.getWordList()) {

					if (!isMeetComma) {

						if (word.getOriginal().equals(",")) { //憒���,隞�銵典����
							isMeetComma = true;

						} else {
							becauseArg.add(word);//because of 摮(of~,)
						}
					} else {
						anotherArg.add(word);//�隞洵鈭��of...X* of 敺惇摮(,~end)
					}

				}
			} else {//of�������蔭
				for (int i = 0; i < sentence.getWordList().size(); i++) {
					Word w = sentence.getWordList().get(i);
					if (i < connectiveIndex) {
						anotherArg.add(w); //��of銋��ord
					} else {
						becauseArg.add(w); //��of銋��ord
					}
				}
			}

			if (nlpUtil.countVerb(anotherArg) > 2||nlpUtil.countVerb(anotherArg) < 1) {
				result = false; //鋆⊿銝�����1~2�� 隞�銵典摮葉憭芸���� ��pos tagging��
			}
			
		}

		/* this is for check comma */
		
		if (checkComma) { //隞�銵典摮ㄐ�����泵��� ���摮����店

			for (Word word : anotherArg) {
				if (word.getOriginal().equals(",")) {
					result = false;
				}
			}

			for (Word word : becauseArg) {
				if (word.getOriginal().equals(",")) {
					result = false;
				}
			}

			if (result) {
				if (sentence.getContent().matches(".*[��\"|\'].*")) { //�銝剖"�����
					result = false; //�銝剖銝���
				}
			}

		}

		return result;

	}

	@Override
	public List<Question> getQuestion(Sentence sentence, Article article) {
		Question result = null;
		YNQuestionGenerator ynQuestionGenerator = new YNQuestionGenerator(
				nlpUtil); //��y/n��
		
		if (isAcceptable(sentence,article)) {
			StringBuilder stringBuilder = new StringBuilder();
			Word w = new Word();
			w.setOriginal(".");
			w.setPos(".");
			anotherArg.add(w);
			for (Word word : anotherArg) {
				if (word.getPos().matches("[A-Z]+")) { //憒��Pos憭折 隞�銵典�閰�� 雿��璅��
					stringBuilder.append(word.getOriginal() + " ");
				}
			}
			

			String tempSentence = stringBuilder.toString().toLowerCase();
			Sentence s = new Sentence();
			for (String changeSuggestion : sentence.getChangeList()){
				s.addChange(changeSuggestion); //��string
				
//				String change[] = changeSuggestion.split("=>");
//				tempSentence = tempSentence.replace(change[0].toLowerCase(), change[1]);
			}
			s.setContent(tempSentence);
			s.getWordList().addAll(anotherArg); //�������
			
			
			if(type.equals(Type.WHERE))//如果是產生where問句，則刪除句子中的location片語
			{    
				List<Word> words = cutLocationPhrase(s);
		        s.getWordList().clear();
		        stringBuilder = new StringBuilder();
		        for(int i = 0 ; i < words.size() ; i++)
		        {
		        	Word w1 = words.get(i);
		        	s.getWordList().add(w1);
		        	if(i == words.size()-2)
		        	{
		        		stringBuilder.append(w1.getOriginal());
		        	}
		        	else
		        		stringBuilder.append(w1.getOriginal()+" ");
		        }
		        s.setContent(stringBuilder.toString());
			}
			
			result = new Question();
			result.setType(type);
			
			String ynQuestion = ynQuestionGenerator.getQuestion(s,article).get(0).getContent();
			System.err.println(ynQuestion);
			
			result.setContent(prefix + " " //���hen/who/when
					+ ynQuestion.substring(0, 1).toLowerCase()
					+ ynQuestion.substring(1, ynQuestion.length()));
			
		}
		List<Question> qList = new ArrayList<Question>();
		qList.add(result);
		return qList;
	}

	public boolean isCheckComma() {
		return checkComma;
	}

	public void setCheckComma(boolean checkComma) {
		this.checkComma = checkComma;
	}
	public List<Word> cutLocationPhrase(Sentence s)//刪除句子中的location片語
	{
		
			ArrayList<Word> removedWords = new ArrayList<Word>();
			List<Word> words = new ArrayList<Word>(s.getWordList());//避免更改到原始sentence內容
			for(int i = 0 ; i < words.size() ; i++)
	        {
	        	Word w1 = words.get(i);
	        	if(w1.getNer()!=null && w1.getNer().equals("LOCATION") )
	        	{
	        		removedWords.add(w1);
	        	}else
	        	{
	        		if(w1.getReWd() != null)
	        		{
		        		ArrayList<IndexedWord> reWd = w1.getReWd();
		    			ArrayList<GrammaticalRelation> reln = w1.getReln();
		    			for(int j = 0 ; j < reWd.size() ; j++)
		    			{
		    				IndexedWord iw = reWd.get(j);
		    				if(iw.ner().equals("LOCATION"))
		    				{
		    					removedWords.add(w1);
		    				}
		    			}
	        		}
	        	}
	        }
	        deleteWord(words, removedWords);
		return words;
	}
	public void deleteWord(List<Word> words,ArrayList<Word> removedWords)
	{
		if(removedWords.isEmpty())return;
		for(Word removedWord : removedWords)
		{
			words.remove(removedWord);
			ArrayList<IndexedWord> reWd = removedWord.getReWd();
			ArrayList<GrammaticalRelation> reln = removedWord.getReln();
			if(reWd != null)
			{
				ArrayList<Word> removedWordRec = new ArrayList<Word>();
				for(int i = 0 ; i < reWd.size() ; i++)
				{
					IndexedWord iw = reWd.get(i);
					GrammaticalRelation relation = reln.get(i);		
					if(relation.getShortName().equals("pobj")||relation.getShortName().equals("prep")||relation.getShortName().equals("amod")
							||relation.getShortName().equals("det")||relation.getShortName().equals("tmod")||relation.getShortName().equals("pcomp")
							||relation.getShortName().equals("npadvmod"))
					{
						if(iw.backingLabel().get(PartOfSpeechAnnotation.class).equals("IN"))
						{
							for(Word w:words)
							{
								if(w.getBeginPosition() == iw.beginPosition())
								{
									removedWordRec.add(w);
								}
							}
						}
					}
				}
				deleteWord(words,removedWordRec);//遞迴刪除連接到介詞的word
			}
			
		}
	}
	
}

package edu.ntnu.kdd.elearn.server.nlp;

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

public class TimeQG implements QuestionGeneratorInterface {

	private NLPUtil nlpUtil;

	public TimeQG(NLPUtil nlpUtil) {
		this.nlpUtil = nlpUtil;
	}

	@Override
	public boolean isAcceptable(Sentence sentence, Article article) {
		ArrayList<Word> temp = sentence.getWordList();
		boolean filter = sentence.getFilter();
		if(filter){
			return false;
		}
		return checkHasTime(sentence); //��迂���hen��
	}

	public boolean checkHasTime(Sentence sentence) { //憒�摮ㄐ�����
		boolean result = false;
		Word preWord = null ;
		for (Word word : sentence.getWordList()) {
			if (word.getNer() != null
					&& (word.getNer().equals("TIME") || word.getNer().equals(
							"DATE"))) {
				System.err.println(sentence.getContent()+"|"+word.getNer()+"|"+word.getOriginal());
				result = true;
			}
		}
		return result;

	}

	public boolean checkWeekDay(Sentence sentence) {
		boolean result = false;
		for (int i = 0; i < sentence.getWordList().size(); i++) {
			Word word = sentence.getWordList().get(i);
			if (word.getOriginal().equals("from")) {
				if (i + 1 < sentence.getWordList().size()
						&& TimeWord.isMatch(sentence.getWordList().get(i + 1))) { //瘥�銝����1~���
					if (i + 2 < sentence.getWordList().size()
							&& sentence.getWordList().get(i + 2).getOriginal()
									.equals("to")) {
						if (i + 3 < sentence.getWordList().size()
								&& TimeWord.isMatch(sentence.getWordList().get(
										i + 3))) {
							result = true;
							break;
						}
					}
				}
			}
		}
		return result;
	}

	public boolean checkTime(Sentence sentence) {
		boolean result = false;
		String targetPOSArray[] = { "CD", "NN", "TO", "CD", "NN" };

		for (int i = 0; i < sentence.getWordList().size(); i++) {
			Word word = sentence.getWordList().get(i);
			// boolean findFrom = false;
			if (word.getOriginal().equals("from")
					&& sentence.getWordList().size() >= i + 6) {
				String posArray[] = new String[5];
				for (int j = 1; j <= 5; j++) {
					String temp = sentence.getWordList().get(i + j).getPos();
					posArray[j - 1] = temp;
				}
				result = true;
				for (int j = 0; j < 5; j++) {
					result = result & targetPOSArray[j].equals(posArray[j]);
				}

				if (result == true) {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public List<Question> getQuestion(Sentence sentence, Article article) {
		// String connective = "from";
		//
		// WhenQG whenQG = new WhenQG(nlpUtil, connective);
		// whenQG.setCheckComma(false);
		// return whenQG.getQuestion(sentence, article);

		List<Question> questionList = new ArrayList<Question>();
		StringBuilder builder = new StringBuilder();
		Sentence tempSentence = new Sentence();
		boolean ner_time_error = false ;
		boolean inbetween_error = false ;
		int between_error_word = 0 ;// 銝剝��,
		int between_in = 0 ;
		int length = sentence.getWordList().size() ;
		Word preword = null ;//������
		
		
//		for (Word word : sentence.getWordList()) { //��摮�
//			if (word.getNer() != null //憒�er = Date or Time �脖��
//					&& (word.getNer().equals("TIME") || word.getNer().equals(
//							"DATE"))) {
//				if(!ner_time_error && between_error_word == 0){
//					ner_time_error = true ;
//					tempSentence.getWordList().add(word);
//				}
//				if(preword!=null){
//					if(preword.getNer()!=null&&(preword.getNer().equals("Time")||preword.getNer().equals("DATE"))){
//						tempSentence.getWordList().remove(preword) ;
//					}
//					else{
//						System.out.println("nothing") ;
//					}
//				}
//			
//				
//			} 
//			else if (word.getPos().equals("IN")) { //隞��� 敺��� = nextIndex
//				boolean ignore = false ;
//				int instart = sentence.getWordList().indexOf(word) ;
//				int nextIndex = sentence.getWordList().indexOf(word) + 1;
//				Word nextWord = null;
//
//				if (sentence.getWordList().size() - 1 > nextIndex) { //�����滯雿�
//					nextWord = sentence.getWordList().get(nextIndex); //敺銝���ord
//				}
//
//				if (nextWord != null //銝�����Time && Date
//						&& (nextWord.getNer() != null
//								&& (nextWord.getNer().equals("TIME") || nextWord
//								.getNer().equals("DATE")))) {
//					ignore = true; //撠梁��������
//					
//				}//in 敺������ �� time
//				else if(nextWord != null && nextWord.getPos().equals("DT")){
//						between_in = instart ; //instart
//						while(between_in < length){
//							nextWord = sentence.getWordList().get(between_in);
//							String ner =  nextWord.getNer() ;
//							System.out.println(nextWord.getOriginal()+":"+ner) ;
//							if(ner=="TIME" || ner=="DATE"){
//								ignore = true ;
//								break ;
//							}
//							between_in ++ ;
//						}
//						if((between_in > nextIndex) && (ignore == true) ){
//							inbetween_error = true ; //in the afternoon���隤�
//							between_in = between_in - instart - 1 ; //敺�頂閰Time/Date���
//							System.err.println("When:"+sentence.getWordList().get(instart)+"->"+sentence.getWordList().get(between_in));
//						}
//						
//				}
//
//				if (!ignore) { //�脖���銝���� +摮�
//						builder.append(word.getOriginal() + " ");
//						tempSentence.getWordList().add(word) ;
//				}
//
//				between_error_word ++ ;
//			} 
//			else {
//				if(inbetween_error && (between_in > 0)){
//					between_in -- ;
//					if(between_in == 0){
//						inbetween_error = false ;
//					}
//				}
//				else{
//					tempSentence.getWordList().add(word);
//					builder.append(word.getOriginal() + " ");
//					between_error_word ++ ;
//				}
//			}
//			preword = word ; //蝯虫������
//			//tempSentence.allword() ;
//		}
		
		ArrayList<Word> removedWords = new ArrayList<Word>();
		List<Word> words = new ArrayList<Word>(sentence.getWordList());//避免更改到原始sentence內容
		for(int i = 0 ; i < words.size() ; i++)//刪除句子中的時間片語
        {
        	Word w = words.get(i);
        	System.err.println(w.getOriginal()+"|"+w.getNer());
        	if(w.getNer()!=null && (w.getNer().equals("DATE")||w.getNer().equals("TIME")||w.getNer().equals("DURATION")) )
        	{
        		removedWords.add(w);
        	}else
        	{
        		if(w.getReWd() != null)
        		{
	        		ArrayList<IndexedWord> reWd = w.getReWd();
	    			ArrayList<GrammaticalRelation> reln = w.getReln();
	    			for(int j = 0 ; j < reWd.size() ; j++)
	    			{
	    				IndexedWord iw = reWd.get(j);
	    				if(iw.ner().equals("DATE")||iw.ner().equals("TIME")||iw.ner().equals("DURATION"))
	    				{
	    					removedWords.add(w);
	    				}
	    			}
        		}
        	}
        }
        deleteWord(words, removedWords);
        for(int i = 0 ; i < words.size() ; i++)
        {
        	Word w = words.get(i);
        	tempSentence.getWordList().add(w);
        	if(i == words.size()-2)
        	{
        		builder.append(w.getOriginal());
        	}
        	else
        		builder.append(w.getOriginal()+" ");
        }
        
		tempSentence.setContent(builder.toString());
		System.out.println("cut time phase:"+builder.toString());
		
		for (String change : sentence.getChangeList()) {
			tempSentence.addChange(change);
		}
		List<Question> result = new YNQuestionGenerator(this.nlpUtil)
				.getQuestion(tempSentence, article);
		for (Question question : result) {
			String tempContent = question.getContent() ;
			
			int q = tempContent.indexOf(",") ; //test
			System.out.println(q+"tmpContent with question:!!"+tempContent) ;//testcode
			
			tempContent = "When " + tempContent.substring(0, 1).toLowerCase() //頧�神 憒�������
					+ tempContent.substring(1, tempContent.length());
			question.setContent(tempContent);
			question.setType(Type.WHEN);
		}

		return result;

	}

	private enum WeekDay {
		Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
	};

	static class TimeWord {

		private Word word;

		private TimeWord(Word word) {
			this.word = word;
		}

		TimeWord get(Word word) {
			TimeWord timeWord = null;
			if (isMatch(word)) {
				timeWord = new TimeWord(word);

			}
			return timeWord;

		}

		static boolean isMatch(Word word) {
			boolean result = false;
			for (WeekDay weekDay : WeekDay.values()) {
				if (word.getOriginal().equals(weekDay.name())) {
					result = true;
				}
			}

			return result;
		}
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
						if(iw.backingLabel().get(PartOfSpeechAnnotation.class).equals("IN")||iw.backingLabel().get(PartOfSpeechAnnotation.class).equals("CD")
								||iw.backingLabel().get(PartOfSpeechAnnotation.class).equals("DT")||iw.backingLabel().get(PartOfSpeechAnnotation.class).equals("JJ")
								||iw.backingLabel().get(PartOfSpeechAnnotation.class).equals("RB"))
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
				deleteWord(words,removedWordRec);
			}
			
		}
	}
}

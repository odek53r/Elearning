package edu.ntnu.kdd.elearn.server.nlp;

import java.util.ArrayList;
import java.util.List;

import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Question;
import edu.ntnu.kdd.elearn.shared.model.Sentence;
import edu.ntnu.kdd.elearn.shared.model.Type;
import edu.ntnu.kdd.elearn.shared.model.Word;

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
		return checkHasTime(sentence); //允許產生when問句
	}

	public boolean checkHasTime(Sentence sentence) { //如果句子裡有時間
		boolean result = false;
		Word preWord = null ;
		for (Word word : sentence.getWordList()) {
			if (word.getNer() != null
					&& (word.getNer().equals("TIME") || word.getNer().equals(
							"DATE"))) {
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
						&& TimeWord.isMatch(sentence.getWordList().get(i + 1))) { //比對是不是星期1~星期日
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
		int between_error_word = 0 ;// 中間出現,
		int between_in = 0 ;
		int length = sentence.getWordList().size() ;
		Word preword = null ;//前一個字
		for (Word word : sentence.getWordList()) { //原句子
			if (word.getNer() != null //如果Ner = Date or Time 進來
					&& (word.getNer().equals("TIME") || word.getNer().equals(
							"DATE"))) {
				if(!ner_time_error && between_error_word == 0){
					ner_time_error = true ;
					tempSentence.getWordList().add(word);
				}
				if(preword!=null){
					if(preword.getNer()!=null&&(preword.getNer().equals("Time")||preword.getNer().equals("DATE"))){
						tempSentence.getWordList().remove(preword) ;
					}
					else{
						System.out.println("nothing") ;
					}
				}
			
				
			} 
			else if (word.getPos().equals("IN")) { //介係詞 後一個 = nextIndex
				boolean ignore = false ;
				int instart = sentence.getWordList().indexOf(word) ;
				int nextIndex = sentence.getWordList().indexOf(word) + 1;
				Word nextWord = null;

				if (sentence.getWordList().size() - 1 > nextIndex) { //看有沒有溢位
					nextWord = sentence.getWordList().get(nextIndex); //得到下一個字word
				}

				if (nextWord != null //下一個字如果是Time && Date
						&& (nextWord.getNer() != null
								&& (nextWord.getNer().equals("TIME") || nextWord
								.getNer().equals("DATE")))) {
					ignore = true; //就省略這個字的意思
					
				}//in 後面所有字 直到 time
				else if(nextWord != null && nextWord.getPos().equals("DT")){
						between_in = instart ; //instart
						while(between_in < length){
							nextWord = sentence.getWordList().get(between_in);
							String ner =  nextWord.getNer() ;
							System.out.println(ner) ;
							if(ner=="TIME" || ner=="DATE"){
								ignore = true ;
								break ;
							}
							between_in ++ ;
						}
						if((between_in > nextIndex) && (ignore == true) ){
							inbetween_error = true ; //in the afternoon省略錯誤
							between_in = between_in - instart - 1 ; //從介系詞到Time/Date的距離
						}
				}

				if (!ignore) { //進來的都是不省略的 +字
						builder.append(word.getOriginal() + " ");
						tempSentence.getWordList().add(word) ;
				}

				between_error_word ++ ;
			} 
			else {
				if(inbetween_error && (between_in > 0)){
					between_in -- ;
					if(between_in == 0){
						inbetween_error = false ;
					}
				}
				else{
					tempSentence.getWordList().add(word);
					builder.append(word.getOriginal() + " ");
					between_error_word ++ ;
				}
			}
			preword = word ; //給予前一個字
			//tempSentence.allword() ;
		}
		tempSentence.setContent(builder.toString());
		for (String change : sentence.getChangeList()) {
			tempSentence.addChange(change);
		}
		List<Question> result = new YNQuestionGenerator(this.nlpUtil)
				.getQuestion(tempSentence, article);
		for (Question question : result) {
			String tempContent = question.getContent() ;
			
			int q = tempContent.indexOf(",") ; //test
			System.out.println(q+"tmpContent with question:!!"+tempContent) ;//testcode
			
			tempContent = "When " + tempContent.substring(0, 1).toLowerCase() //轉小寫 如果專有名詞會出錯
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

}

package edu.ntnu.kdd.elearn.server.nlp;
/*
主要是分割子句並判斷句子是否有 because => 產生why問句
				       有 when的連接詞子句 =>產生when問句
				       有 where的關係子句 =>產生where問句
*/
import java.util.ArrayList;
import java.util.List;

import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Question;
import edu.ntnu.kdd.elearn.shared.model.Sentence;
import edu.ntnu.kdd.elearn.shared.model.Type;
import edu.ntnu.kdd.elearn.shared.model.Word;

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
	public boolean isAcceptable(Sentence sentence, Article article) { //篩選句子
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
							.equals("of")) { //如果後面沒有接of
						result = true;
						connectiveIndex = i;
						break;
					}

				}
			}
		}

		if (result) {//類似One of的句子,because of

			if (connectiveIndex == 0) {
				boolean isMeetComma = false;
				for (Word word : sentence.getWordList()) {

					if (!isMeetComma) {

						if (word.getOriginal().equals(",")) { //如果有,代表分成兩個子句
							isMeetComma = true;

						} else {
							becauseArg.add(word);//because of 子句(of~,)
						}
					} else {
						anotherArg.add(word);//其他第二個字為of...X* of 從屬子句(,~end)
					}

				}
			} else {//of出現在別的位置
				for (int i = 0; i < sentence.getWordList().size(); i++) {
					Word w = sentence.getWordList().get(i);
					if (i < connectiveIndex) {
						anotherArg.add(w); //加入of之前的word
					} else {
						becauseArg.add(w); //加入of之後的word
					}
				}
			}

			if (nlpUtil.countVerb(anotherArg) > 2||nlpUtil.countVerb(anotherArg) < 1) {
				result = false; //裡面不能有動詞1~2個 代表句子中太多動詞 可能pos tagging出錯
			}

		}

		/* this is for check comma */

		if (checkComma) { //代表句子裡面有標點符號 可能是子句或者對話

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
				if (sentence.getContent().matches(".*[“|”|\"|\'].*")) { //句中句"的意思
					result = false; //句中句不產生問句
				}
			}

		}

		return result;

	}

	@Override
	public List<Question> getQuestion(Sentence sentence, Article article) {
		Question result = null;
		YNQuestionGenerator ynQuestionGenerator = new YNQuestionGenerator(
				nlpUtil); //生產y/n問句
		
		if (isAcceptable(sentence,article)) {
			StringBuilder stringBuilder = new StringBuilder();
			Word w = new Word();
			w.setOriginal(".");
			w.setPos(".");
			anotherArg.add(w);
			for (Word word : anotherArg) {
				if (word.getPos().matches("[A-Z]+")) { //如果找到Pos大雪 代表全部詞性 但不包含標點
					stringBuilder.append(word.getOriginal() + " ");
				}
			}
			

			String tempSentence = stringBuilder.toString().toLowerCase();
			Sentence s = new Sentence();
			for (String changeSuggestion : sentence.getChangeList()){
				s.addChange(changeSuggestion); //加入string
				
//				String change[] = changeSuggestion.split("=>");
//				tempSentence = tempSentence.replace(change[0].toLowerCase(), change[1]);
			}
			
			
			
			s.setContent(tempSentence);
			s.getWordList().addAll(anotherArg); //把所有字加入
			result = new Question();
			result.setType(type);
			
			String ynQuestion = ynQuestionGenerator.getQuestion(s,article).get(0).getContent();

			result.setContent(prefix + " " //加上when/who/when
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
	
	
}

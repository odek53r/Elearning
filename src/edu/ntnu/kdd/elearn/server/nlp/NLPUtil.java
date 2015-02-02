package edu.ntnu.kdd.elearn.server.nlp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Question;
import edu.ntnu.kdd.elearn.shared.model.Sentence;
import edu.ntnu.kdd.elearn.shared.model.Word;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.CoreMap;

public class NLPUtil implements Serializable{
	private static NLPUtil instance = new NLPUtil();// singleton pattern

	private List<Word> wordList = new ArrayList<Word>();
	private Sentence sentence = null;
	private StandQuestions standQuestion = new StandQuestions();

	private QGBuilder qgBuiler = new QGBuilder();

	private NLPUtil() {

	}

	public Sentence getSentence() {
		return sentence;
	}

	public static NLPUtil getInstance() {
		if (instance == null) {
			instance = new NLPUtil();
		}

		return instance;
	}

	public void processStandardArticle(Article article) { //load article 
		StringBuilder stringBuilder = new StringBuilder();
		String temp = preprossing(article);//撠y|my name is ���� I am
	
		StanfordCoreNLP pipeline = new StanfordCoreNLP();
		Annotation annotation = new Annotation(temp);
		pipeline.annotate(annotation);

		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		//List<Sentence> subSentences = new ArrayList<Sentence>();
		
		Map<Sentence, Sentence> sentenceRelation = new HashMap<Sentence, Sentence>();
		
		
		//蝝�������entence����ord閰�扼�er�os
		for (CoreMap sentence : sentences) {
			Sentence s = new Sentence(); //瘥�銝�銵停摰����甈�
			stringBuilder.setLength(0);
			
			int length = 0;
			// post.tagLabel(coreLabelList);
			for (int i = 0; i < sentence.get(TokensAnnotation.class).size(); i++) {
				// this is the text of the token
				CoreLabel token = sentence.get(TokensAnnotation.class).get(i);
				
				String original = token.get(TextAnnotation.class);
				// this is the POS tag of the token 摮ord of sentence
				String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the speech annotation 摮��閰�扯酉���
				String ner = token.get(NamedEntityTagAnnotation.class);
				// this is the NER label of the token撠������(�鈭粹������隞�)
				int beginPosition = token.beginPosition();
				
				int endPosition = token.endPosition();
				
				SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
				
		        List<SemanticGraphEdge> edgeList = graph.edgeListSorted();
		        
				if (i == 0) {
					s.setStart(token.beginPosition()); 
				}

				Word w = new Word(); //摨���ava�隞嗉������
				w.setOriginal(original);
				w.setPos(pos);
				w.setBeginPosition(beginPosition);
				w.setEndPosition(endPosition);
				
				for (int j = 0 ; j < edgeList.size() ; j++ ) 
		        {
		        	SemanticGraphEdge edge = edgeList.get(j);
			        IndexedWord iw1 = edge.getGovernor();
			        IndexedWord iw2 = edge.getDependent();
			        GrammaticalRelation  relation = edge.getRelation();
			        if(iw2.beginPosition() == w.getBeginPosition())
			        {
			        	w.addReln(relation);
			        	w.addReWd(iw1);
			        }
				}
				if (!ner.equals("O")) { //隞�銵典��撠���停set嚗���et
					w.setNer(ner);
				}

				s.getWordList().add(w); //�����隞嗆�����s.list

				if (i == sentence.get(TokensAnnotation.class).size() - 1) {
					length = token.endPosition() - s.getStart(); //�摮�摨�
				}

			}

			s.setChCount(length);
			s.setContent(temp.substring(s.getStart(), s.getStart() + length)); //蝝���摮�

			
			article.getSentenceList().add(s); //��摮葡�����??
			List<Integer> startList = new ArrayList<Integer>();
			List<Integer> endList = new ArrayList<Integer>();
			/*
			Tree tree = sentence.get(TreeAnnotation.class);
			tree.pennPrint(); //銵函內璅寧����ata��撘扯”蝷�
			
			for (Tree subTree : tree.getChildrenAsList()) { //return ���ode��hildnode
				
				if (subTree.label().value().equals("S")) {
					boolean checkInFirstLabel = false;
					boolean checkInSecondLabel = false;
					List<Tree> subTreeList2 = subTree.getChildrenAsList();
					int start = 0;
					int end =0;
					for (int i = 0; i< subTreeList2.size();i++) {
						Tree lab = subTreeList2.get(i); 
						System.out.print(lab.label().value()+"\t");

						if(i==0&&lab.label().value().equals("PP")){
							checkInFirstLabel = true;
						}else if (i==1&&lab.label().value().equals(",")){
							checkInSecondLabel = true;
						}else if (checkInFirstLabel&&checkInSecondLabel) {
							
							List<Label> labels = lab.yield(); 
							for (int j=0;j<labels.size();j++){
							
								CoreLabel label = ((CoreLabel) labels.get(j));
								int tempStart = label.beginPosition();
								int tempEnd = label.endPosition();
								if (start==0){
									start = tempStart;
								}else if (tempEnd>end){
									end = tempEnd;
								}
							}
							
							
						}
						
					}
					System.out.println();

					if (start>0&&end>0){
						List<Integer> removeList = new ArrayList<Integer>();
						
						for(int i=0;i<startList.size();i++){
							if (startList.get(i)<start&&endList.get(i)>end){
								removeList.add(i);
							}
						}
						
						for(Integer i : removeList){
							int index = i;
							startList.remove(index);
							endList.remove(index);
						}
						
						startList.add(start);
						endList.add(end);
					}
					
				}

			}
			
			for (int i = 0; i < startList.size(); i++) {
				int start = startList.get(i);
				int end = endList.get(i);

				Sentence splittedSentence = new Sentence();
				splittedSentence.setStart(start);
				splittedSentence.setChCount(end-start);

				
				String tempContent = temp.substring(start, end);//end+1
				
				tempContent = tempContent.toUpperCase().substring(0,1)+tempContent.substring(1,tempContent.length());
				//擐��之撖�
				boolean hasDot = true; //����暺�lag
				if (!tempContent.contains(".")){ //憒������ 閬�+����
					tempContent = tempContent+".";
					hasDot = false;
				}
				splittedSentence.setContent(tempContent);
				//���������策隞�
				for (int j = 0; j < sentence.get(TokensAnnotation.class).size(); j++) {
					// this is the text of the token
					CoreLabel token = sentence.get(TokensAnnotation.class).get(j);
					if (token.beginPosition()>=start&&token.endPosition() <= end){
						String original = token.get(TextAnnotation.class);
						// this is the POS tag of the token
						String pos = token.get(PartOfSpeechAnnotation.class);
						// this is the NER label of the token
						String ner = token.get(NamedEntityTagAnnotation.class);
						
						Word w = new Word();
						w.setOriginal(original);

						w.setPos(pos);
						if (!ner.equals("O")) {
							w.setNer(ner);
						}

						splittedSentence.getWordList().add(w);
					}
				}
				if (!hasDot) {
					Word w = new Word();
					w.setOriginal(".");
					w.setPos(".");
					splittedSentence.getWordList().add(w);
				}
				subSentences.add(splittedSentence);
				
				sentenceRelation.put(splittedSentence, s);
			}
			*/

		}
		
		for (Sentence sentence1 : article.getSentenceList()) {
			sentence1.setFilter(false);
		}
		setSentence(article);
		
		
		changeProcessing(article, temp, sentenceRelation);
		for (Sentence sentence : article.getSentenceList()) {
			List<QuestionGeneratorInterface> qgList = QGBuilder.listQG(this);
			for (QuestionGeneratorInterface qg : qgList) {
				if (qg.isAcceptable(sentence, article)) {
					for (Question question : qg.getQuestion(sentence, article)) {
						if (question != null) {
							String q = changeWriter(sentence.getContent(), question.getContent());
							question.setContent(changeTheWriter(q));	
							question.set_sinorpul(thewriter_sinpul(q)); //��the writer���銴
							sentence.addQuestion(question);
						}
					}

				}				
				
			}

		}
		/*
		for (Sentence sentence : subSentences) {
			List<QuestionGeneratorInterface> qgList = QGBuilder.listQG(this);

			for (QuestionGeneratorInterface qg : qgList) {
				if (qg.isAcceptable(sentence, article)) {
					for (Question question : qg.getQuestion(sentence, article)) {
						if (question != null) {
							question.setContent(changeTheWriter(question.getContent()));
							sentence.addQuestion(question);
						}
					}

				}				
				
			}

		}
		*/
		/*
		for (Sentence splittedSentence : subSentences){
			Sentence originalSentence = sentenceRelation.get(splittedSentence);
			if (originalSentence!=null&&splittedSentence.getQuestionCount()>0){
				for (Question q : splittedSentence.getQuestionList()){
					originalSentence.addQuestion(q);
				}
			}
			
		}
		*/
		
	}

	public int countVerb(List<Word> wordList) { //閮�������

		int verbCount = 0;
		for (int i = 0; i < wordList.size(); i++) {
			Word w = wordList.get(i);

			if (isVerb(w)) {
				                                 //銝�摰,�甇Ｘ�����                                                                            
				if ((i + 1) < wordList.size() && !isDoNot(w, wordList.get(i + 1))) { 
					if (i==0){
						verbCount++;
					}
					else if(!isToV(wordList.get(i - 1))){	//銝To + V
						verbCount++;
					}
				}

			}
		}
		return verbCount;
	}

	public boolean isDoNot(Word w1, Word w2) { //��w1��2�銝�摰
		boolean result = false;
		String word1 = w1.getOriginal();
		String word2 = w2.getOriginal();

		if (word1.equals("do") || word1.equals("does") || word1.equals("did")) {
			if (word2.equals("n't")|| word2.equals("n�") || word2.equals("not")) {
				result = true;
			}
		}
		return result;
	}

	public boolean isToV(Word w1) { //���銝to V
		boolean result = false;

		if (w1.getPos().equals("TO")) {
			result = true;
		}

		return result;
	}

	public boolean isVerb(Word w) { //check �������
		boolean result = false;

		if (w.getPos().matches("VB(Z|D|P)*")) {

			result = true;
		}

		return result;

	}

	public void setSentence(Article article) {
		boolean dialogue=false;
		for (Sentence sentence : article.getSentenceList()) {
			ArrayList<Word> sTemp = sentence.getWordList();
			System.out.println(sentence.getContent()+" size: "+sTemp.size());
			//撠店 " "
			if (!dialogue){
				if ((isQuotes(sTemp)%2) == 1 ){
					dialogue=true;
					sentence.setFilter(true);
				}
				else if ((isQuotes(sTemp)%2) == 0 && isQuotes(sTemp)!=0){
					sentence.setFilter(true);
				}
			}
			else{
				sentence.setFilter(dialogue);
				if (isQuotes(sTemp) == 1){
					dialogue=false;
				}
			}
			//撠店 :
			if (isColon(sTemp)!=0){
				
				sentence.setFilter(true);
			}
			// ���r���摮
			if (isRelationSubSentence(sTemp))
			{
				sentence.setFilter(true);
			}
			//蟡�:To+V��敺瘝����)
			if (isImperatives(sTemp))
			{
				sentence.setFilter(true);
			}
			//��撠3������ 
			if (sTemp.size()<=3){
				sentence.setFilter(true);
				System.out.println("less than 3 words.");
			}
			
		}
	}
	
	public int isQuotes(List<Word> wordList) {//�摮葉���" "���撠店
	
		int countQuotes = 0; //" "
		for (int i = 0; i < wordList.size(); i++) {
			String w = wordList.get(i).getPos();
			if (w.equals("��") || w.equals("��") ||  w.equals('"') || w.equals("��")|| w.equals("��") || w.equals("``")|| w.equals("''") ){
				countQuotes++;
			}
		}
		return countQuotes;
	}
	
	public int isColon(List<Word> wordList) { //�摮葉���:���撠店
		
		int countColon= 0;	 // :
		for (int i = 0; i < wordList.size(); i++) {
			String w = wordList.get(i).getPos();
			if (w.equals(":")){
				countColon++;
			}
		}
		//System.out.println("Colon: " + countColon);
		return countColon;
	}

	public boolean isRelationSubSentence(List<Word> wordList) { //瞈暹���誨�������閰�
		boolean countAnd=false;
		boolean countP=false;
		for (int i = 0; i < wordList.size(); i++) {
			String w = wordList.get(i).getOriginal();
			if (w.equals("what")||w.equals("What")||w.equals("who")||w.equals("Who")||w.equals("when")||w.equals("When")||w.equals("where")||w.equals("Where")||w.equals("how")||w.equals("How")||w.equals("that")||w.equals("That")||w.equals("because")||w.equals("Because")){
				//System.out.println("Is RelationSubSentence");
				return true;
			}
			//憒�� and, but敺��� 銝餉��+���� =>瞈暹��
			if (w.equals("and")||w.equals("And")||w.equals("but")||w.equals("But")||w.equals("then")||w.equals("Then")){
				countAnd=true;
			}
			if(countAnd)
			{
				if (wordList.get(i).getPos().matches("P(OS|RP|RP$)*")||wordList.get(i).getPos().matches("NN(S|P|PS)*")) {
					countP = true;
				}
			}
			if(countP)
			{
				if (wordList.get(i).getPos().matches("VB(Z|D|P|G|N)*")) {
					return true;
				}
			}
			
		}
		return false;
	}
	//蟡蝙�
	public boolean isImperatives(List<Word> wordList){
		Word w1 = wordList.get(0);
		//To + V
		if(isToV(w1)){
			return true;
		}
		//V��
		if (w1.getPos().matches("VB(Z|D|P|N)*")) {
			return true;
		}
		boolean verb = false;
		boolean subject_term = false;
		
		for (int i = 0; i < wordList.size(); i++) {
			Word w = wordList.get(i);
			if (w.getPos().matches("NN(S|P|PS)*")||w.getPos().matches("P(OS|RP|RP$)*")){
				subject_term = true;
			}
			if (w.getPos().matches("VB(Z|D|P|G|N)*")){
				verb = true;
			}
			//N敺瘝����
			if(i==wordList.size()-1){
				//N敺瘝����
				if (subject_term && !verb){
					return true;
				}
				//瘝�蜓閰�
				if (!subject_term){
					return true;
				}
			}
			
		}
		return false;
	}
	public List<Word> getWordList() {

		return wordList;
	}

	public StandQuestions getStandQuestions() {
		return standQuestion;
	}
/*
	public Map<Article, List<Integer>> searchArticle(String query) {

		Map<Article, List<Integer>> result = new TreeMap<Article, List<Integer>>();

		QueryHelper queryHelper = new QueryHelper();
		Query q = queryHelper.buildQuery(query);

		ArticleDao articleDao = new ArticleDao();
		List<Article> allArticle = articleDao.listArticle();

		for (Article article : allArticle) {
			boolean isMatch = false;
			List<Sentence> sentenceList = article.getSentenceList();
			for (int i = 0; i < sentenceList.size(); i++) {
				if (queryHelper.isMatch(sentence, q)) {
					if (isMatch) {
						result.get(article).add(i);
					} else {
						List<Integer> sentenceIndexList = new ArrayList<Integer>();
						sentenceIndexList.add(i);
						result.put(article, sentenceIndexList);
					}
				}
			}

		}
		return result;
	}
	
*/
	//��誨瘨圾
	public void changeProcessing(Article article, String articleAfterPreprocessing, Map<Sentence, Sentence> sentenceRelation) {
		boolean hasAPersonName = false;
		for (Sentence s : article.getSentenceList()) {
			for (Word w : s.getWordList()) {
				if (w.getNer() != null && w.getNer().contains("PERSON")) {
					hasAPersonName = true;
					break;
				}
			}
		}

		if (hasAPersonName) { //憒��犖��������
			String[] result = Corenlp.main(articleAfterPreprocessing);
			//result = newReplaceRules(result);
			article.setCoreferenceResult(result);  

			for (String perChange : result) {
				System.out.println("perchange............."+perChange) ;
				String[] temp = perChange.split(",");
				int sentenceIndex = Integer.parseInt(temp[0]); //�摮楊���
				int wordStart = Integer.parseInt(temp[1]);//韏瑕��
				int wordEnd = Integer.parseInt(temp[2]);//蝯��

				int targetSentence = Integer.parseInt(temp[3]);
				int targetWordStart = Integer.parseInt(temp[4]);
				int targetWordEnd = Integer.parseInt(temp[5]);

				Sentence source = article.getSentenceList().get(sentenceIndex);
				String changeString = null;
				boolean canAdd = false;
				boolean isPRPs = false;
				boolean isPRP = false ;
				boolean isPoss = false;
				Word changed =null;
				for (int i = wordStart; i <= wordEnd; i++) {
					if (changeString == null) {
						changeString = "";
					}
					changed = source.getWordList().get(i);
					
					if(changed.getReln()!=null)//檢查是否為所有格
					{
						for(GrammaticalRelation relation : changed.getReln())
						{
							if(relation.getShortName().equals("poss"))
								isPoss = true;
						}
					}
					
					changeString = changeString
							+ changed.getOriginal() + " ";
					if(changed.getPos().equals("PRP")||changed.getPos().equals("PRP$")){
						if(changed.getPos().equals("PRP$")){
							isPRPs = true;
						}
						canAdd = true;
					}
					if(changed.getPos().equals("PRP")){
						isPRP = true ;
						canAdd = true;
					}
				}

				changeString += "=>";
				Sentence target = article.getSentenceList().get(targetSentence);
				if(canAdd){							
					int counter=0;
					
					for (int i = targetWordStart; i <= targetWordEnd; i++){
						changed = target.getWordList().get(i);	
						if (changeString == null) {
							changeString = "";
						}
						
						boolean isRecWordFound = false;
						if(changed.getPos().equals("PRP")||changed.getPos().equals("PRP$")){//代換後如果還是PRP or PRP$則繼續代換，只遞迴一次
							int curWordStart = i;
							int curWordEnd = i+1;
							
							for(String perChange2 : result)
							{
								String[] temp2 = perChange2.split(",");
								int sourceRecSentence = Integer.parseInt(temp2[0]);
								int sourceRecWordStart = Integer.parseInt(temp2[1]);
								int sourceRecWordEnd = Integer.parseInt(temp2[2]);
								if(targetSentence == sourceRecSentence && curWordStart == sourceRecWordStart && sourceRecWordEnd <= curWordEnd)
								{
									int targetRecSentence = Integer.parseInt(temp2[3]);
									int targetRecWordStart = Integer.parseInt(temp2[4]);
									int targetRecWordEnd = Integer.parseInt(temp2[5]);
									for(int j = targetRecWordStart ; j <= targetRecWordEnd ; j++)
									{
										Word targetRecWord = article.getSentenceList().get(targetRecSentence).getWordList().get(j);
										Word targetRecNxtWord = article.getSentenceList().get(targetRecSentence).getWordList().get(j+1);
										String targetWord = targetRecWord.getOriginal();
										
										if(changed.getPos().equals("PRP$")&&targetRecWord.getNer()!=null && targetRecWord.getNer().equals("PERSON") && targetRecNxtWord != null &&//避免專有名詞已經有加's,而再加一次
												!targetRecNxtWord.getOriginal().contains("'s") && !targetRecNxtWord.getOriginal().contains("’s"))
										{
											targetWord += "'s";
										}
										
										if(changed.getPos().equals("PRP$"))
										{
											if(targetWord.toLowerCase().equals("i"))
												changeString = changeString+"my"+ " ";
											else if(targetWord.toLowerCase().equals("you"))
												changeString = changeString+"your"+ " ";
											else if(targetWord.toLowerCase().equals("she"))
												changeString = changeString+"her"+ " ";
											else if(targetWord.toLowerCase().equals("he"))//避免his代換成he
												changeString = changeString+"his"+ " ";
											else if(targetWord.toLowerCase().equals("we"))
												changeString = changeString+"our"+ " ";
											else if(targetWord.toLowerCase().equals("they"))
												changeString = changeString+"their"+ " ";
											else if(targetWord.toLowerCase().equals("it"))
												changeString = changeString+"its"+ " ";
											else
												changeString = changeString
														+ targetWord + " ";
										}
										else
											changeString = changeString
													+ targetWord + " ";
										
										
										
										counter++;//代換字變長
									}
									counter--;//扣掉原始字重複加
									isRecWordFound = true;
									
									break;
								}
							}
							canAdd = true;
						} 
						if (counter>3) { //大於3個字則不取代
							canAdd=false;
						}
						if (sentenceIndex==targetSentence) { //產生規則的那句不取代
							canAdd=false;
						}
						
						
						if(changed.getPos().equals("POS")){//避免PRP => 's錯誤
							if(isPRP && (!isPRPs)){
								changeString = changeString
										+ changed.getOriginal() + " ";
							}
							else{
								changeString = changeString
										+ changed.getOriginal() + " ";
							}
						}
						else if(changed.getPos().equals("PRP")||changed.getPos().equals("PRP$"))
						{
							if(isRecWordFound == false)
							{
								if(isPoss)
								{
									if(changed.getOriginal().toLowerCase().equals("i"))
										changeString = changeString+"my"+ " ";
									else if(changed.getOriginal().toLowerCase().equals("you"))
										changeString = changeString+"your"+ " ";
									else if(changed.getOriginal().toLowerCase().equals("she"))
										changeString = changeString+"her"+ " ";
									else if(changed.getOriginal().toLowerCase().equals("he"))
										changeString = changeString+"his"+ " ";
									else if(changed.getOriginal().toLowerCase().equals("we"))
										changeString = changeString+"our"+ " ";
									else if(changed.getOriginal().toLowerCase().equals("they"))
										changeString = changeString+"their"+ " ";
									else if(changed.getOriginal().toLowerCase().equals("it"))
										changeString = changeString+"its"+ " ";
									else
										changeString = changeString
											+ changed.getOriginal() + " ";
								}else
								{
									changeString = changeString
											+ changed.getOriginal() + " ";
								}
							}
						}
						else{
							if(changed.getNer()!=null && changed.getNer().equals("PERSON"))//避免[She => A new student],Is A new student tall and pretty
							{
								changeString = changeString
										+ changed.getOriginal() + " ";
							}
							else
							{
								changeString = changeString
										+ changed.getOriginal().toLowerCase() + " ";
							}
						}
						
						counter++;
						
					}
				}
													
				if(canAdd){
						if(isPoss){// 避免PRPs => non-PRPs
							String revers = "";
							String temp1[] = changeString.split("=>");
							for(int i = changeString.length()-1; i>=0; i--)
							{
								revers = revers + changeString.charAt(i);
							}							
							revers = revers.substring(1,3);
							//除了person's和PRP$之外的名詞所有格如果沒加's，在這邊加
							if(!((revers.equals("s'")||revers.equals("s’"))) && !temp1[0].contains("our") && !temp1[0].contains("your") && !temp1[0].contains("their")
									&& !temp1[0].contains("Our") && !temp1[0].contains("Your") && !temp1[0].contains("Their")&& !temp1[0].contains("my") && !temp1[0].contains("My")
									&& !temp1[0].contains("Her") && !temp1[0].contains("her")&&!temp1[0].contains("His") && !temp1[0].contains("his")
									&& !temp1[0].contains("My") && !temp1[0].contains("my")&& !temp1[0].contains("Its") && !temp1[0].contains("its")){
								changeString = changeString + "'s";
							}
						}					
					System.err.println(changeString);
					source.addChange(changeString);
				}
				
			}

		}
		/*
		for (Sentence splittedSentence : sentenceRelation.keySet()){
			Sentence originalSentence = sentenceRelation.get(splittedSentence);
			for (String change : originalSentence.getChangeList()){
				splittedSentence.addChange(change);
			}
			
		}
		*/
	}
//	private boolean RecChange(Article article,String []result,String changeString,int curWordStart,int curWordEnd,int targetSentence)
//	{
////		int curWordStart = i;
////		int curWordEnd = i+1;
//		boolean isRecWordFound = false;
//		for(String perChange2 : result)
//		{
//			String[] temp2 = perChange2.split(",");
//			int sourceRecSentence = Integer.parseInt(temp2[0]);
//			int sourceRecWordStart = Integer.parseInt(temp2[1]);
//			int sourceRecWordEnd = Integer.parseInt(temp2[2]);
//			if(targetSentence == sourceRecSentence && curWordStart == sourceRecWordStart && sourceRecWordEnd <= curWordEnd)
//			{
//				int targetRecSentence = Integer.parseInt(temp2[3]);
//				int targetRecWordStart = Integer.parseInt(temp2[4]);
//				int targetRecWordEnd = Integer.parseInt(temp2[5]);
//				for(int j = targetRecWordStart ; j <= targetRecWordEnd ; j++)
//				{
//					String targetWord = article.getSentenceList().get(targetRecSentence).getWordList().get(j).getOriginal();
//					changeString = changeString
//							+ targetWord + " ";
//				}
//				
//				isRecWordFound = true;
//			}
//		}
//		return isRecWordFound;
//	}
	private String[] newReplaceRules(String[] result){
		ArrayList<String> back = new ArrayList<String>();
		for(int i=0;i<result.length;i++){
			back.add(result[i]);
			System.out.println("Replacement rule: " + result[i]);
			String temp = result[i].split(",", 4)[3];
			for(int j=i;j>=0;j--){
				
				if(result[j].indexOf(temp)==0){
					back.add(result[i].replace(temp, "")+result[j].replace(temp, ""));
					System.out.println("New Replacement :" + result[i].replace(temp, "")+result[j].replace(temp, ""));
				}
			}
		}
		
		return back.toArray(new String[back.size()]);
	}

	public String preprossing(Article article) {
		String result = article.getContent();

		if (result.matches(".*(\\W+(I|we|I'm|My|our|my|Our|We)+\\W+)+.*")) {// include
			result = result.replaceAll("((My|my) name is)", "I am");																// I,we,My...etc			
			article.setFirstPerson(true);
		} else {
			article.setFirstPerson(false);
		}
		return result;
	}
	public String changeWriter(String origin,String sentence)
	{
		String result = new String(sentence);
		if(origin.matches(".*(\\s?(I|i)\\s(\\s|\\w)+)\\smyself\\s.*"))//I....myself -> I....himself
		{
			result = result.replaceAll("myself","himself");
		}
		if(origin.matches(".*(\\s?(I|i)\\s(\\s|\\w)+)\\smy\\s.*"))//I....myself -> I....himself
		{
			result = result.replaceAll("my","his");
		}
		return result;
	}
	public String changeTheWriter(String sentence) {
		String result = new String(sentence);
		result = result.replaceAll("Now ","");
		System.out.println("test-------------"+result);
		
		if (result.matches(".*(\\W+(I|we|I'm|My|our|my|Our|We|Us|us|i)+\\W+)+.*")) {// include
			System.out.println("I am");
			// I,we,My...etc
			result = result.replaceAll("(\\s?(I|i)\\s(am)\\s)|\\s?(I�)\\s",
					" the writer is ");
			result = result.replaceAll("(\\s?(am|Am)\\s(i|I)\\s)|\\s?(I�)\\s",
					" is the writer ");
			result = result.replaceAll(
					"(\\s(I|we|us|i|me)(\\W)\\s)|(\\s(I|we|us|i|me)\\W)|((I|We|Us)\\s)",
					" the writer ");
			result = result.replaceAll(
					"(\\s(my|our)\\s)|(\\s(my|our)\\W)|((My|Our)\\s)",
					" the writer's ");

		}
		result = result.replaceAll(" mr. "," Mr. ");
		result = result.replaceAll(" mrs. "," Mrs. ");
		result = result.replaceAll(", too","");
		//result = result.replaceAll(", but",""); //�芣��
		//result = result.replaceAll(", then","");
		return result;
	}
	
	public boolean thewriter_sinpul(String sentence){
		String result = new String(sentence);
		boolean flag = false ;
		if (result.matches(".*(\\W+(the writer)+\\W+)+.*")) {// 憒��he writer
			flag = true ;
		}
		return flag ;
	}
	
}

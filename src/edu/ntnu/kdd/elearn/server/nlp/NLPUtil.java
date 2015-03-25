package edu.ntnu.kdd.elearn.server.nlp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	private boolean isPRP$_NN_POS = false;
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
		for (Sentence sentence : article.getSentenceList()) 
		{
			if(isPRP$_NN_POS(sentence))//有PRP$ NN POS句型不產生問句
				continue;
			if(isGreetStartWith(sentence))
				continue;
			Paramater para = new Paramater();
			checkPRP(para,sentence);
			boolean isFound = para.isFirstPRPFound;//若第一個字為代名詞(非第一人稱)且找不到取代, 則不產生問句.
//			if(!isFound)
//				continue;
			List<QuestionGeneratorInterface> qgList = QGBuilder.listQG(this);
			for (QuestionGeneratorInterface qg : qgList) {
				if (qg.isAcceptable(sentence, article)) 
				{
					for (Question question : qg.getQuestion(sentence, article)) 
					{
						if(!isFound&&!startWithWord(question.getContent()).equals("Who")&&!startWithWord(question.getContent()).equals("What"))//若第一個字為代名詞(非第一人稱)且找不到取代, 則不產生問句.
						{
							continue;
						}
						if(!para.isPRP_POS_ACCFound && !startWithWord(question.getContent()).equals("Whose"))//若代名詞所有格 或 代名詞受格 找不到取代(非第一人稱), 則不產生問句.
							continue;
						
						if (question != null) {
							System.out.println("before changeWriter: "+question.getContent());
							String q = changeWriter(sentence.getContent(), question.getContent());//before changeTheWriter
							isPRP$_NN_POS = false;
							
							if(startWithWord(q).equals("Who")||startWithWord(q).equals("Whose"))
							{
								String subject = ((TestWhoQG)qg).getChangedSubject();
								if(subject.toLowerCase().equals("i")||subject.toLowerCase().equals("we")||subject.toLowerCase().equals("my")||subject.toLowerCase().equals("our"))
								{
									q = q.replaceAll("\\W(i|I)\\W"," he/she ");
									q = q.replaceAll("\\W(my)\\W"," his/her ");
									q = q.replaceAll("\\W(me)\\W"," him/her ");
									q = q.replaceAll("\\W(we)\\W"," they ");
									q = q.replaceAll("\\W(our)\\W"," their ");
									q = q.replaceAll("\\W(us)\\W"," them ");
								}
							}
							System.out.println("before changeTheWriter: "+q);
							q = changeTheWriter(q);
							System.out.println("after changeTheWriter: "+q);
							q=  changeDT_PosNames(q);
							System.out.println("after changeDT_PosNames: "+q);
							q = q.replaceAll(" ,",",");
							q = q.replaceAll(" 's","'s");
							q = q.replaceAll("\\b(here|Here)\\s","there ");
//							q = q.replaceAll("\\s+$","?");//避免沒有問號
//							q = q.replaceAll("(\\s+\\?+)\\s*$", "?");//避免問號前有空格
							q = q.replaceAll("\\s*\\?*\\s*$", "");//去掉句子後的問號和空格
							q = q+"?";//重新補上問號
							if(isPRP$_NN_POS)
								continue;
//							if(q.toLowerCase().matches(".*(\\W+(i|my|me|you|your|he|his|him|she|her|they|their|them|we|our|us|it|its)+(\\W+|\\?))+.*"))//有代名詞不產生問句
//							{
//								continue;
//							}
							question.setContent(q);	
							question.set_sinorpul(thewriter_sinpul(q)); //��the writer���銴
//							if(!isFound)
//							{
//								question.setContent("NotFound: "+question.getContent());
//							}
//							if(!para.isPRP_POS_ACCFound)
//								question.setContent("PRPNotFound: "+question.getContent());
							
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
	
	class Paramater
	{
		public boolean isFirstPRPFound;//若第一個字為代名詞(非第一人稱)且找不到取代
		public boolean isPRP_POS_ACCFound;//若代名詞所有格 或 代名詞受格 找不到取代
	}
	private void checkPRP(Paramater para,Sentence sentence)//檢查代名詞可否找出取代
	{
		boolean isFound = true;
		para.isPRP_POS_ACCFound = true;
		List<String> ch = sentence.getChangeList();
		ArrayList<String> pos_acc_list = new ArrayList<String>();
		Word w = sentence.getWordList().get(0);
		for(Word word:sentence.getWordList())//紀錄所有格代名詞跟代名詞受格的詞
		{
			if(word.getPos().equals("PRP")||word.getPos().equals("PRP$"))
			{
				String ori = word.getOriginal().toLowerCase();
				if(ori.equals("your")||ori.equals("you")||ori.equals("his")||ori.equals("him")
				 ||ori.equals("her")||ori.equals("their")||ori.equals("them")||ori.equals("its")
				 ||ori.equals("it"))
				{
					pos_acc_list.add(ori);
				}
			}
		}
		
		for(String PRP: pos_acc_list)//檢查代名詞所有格或代名詞受格可否找出取代
		{
			for(String s:ch)
			{
				String split[] = s.split("=>");
				String prior = split[0].replaceAll(" ","").toLowerCase();
				String posterior = split[1].replaceAll(" ","").toLowerCase();
				
				if(PRP.equals(prior) && (posterior.equals("i")|| posterior.equals("my")|| posterior.equals("me")
					|| posterior.equals("you")|| posterior.equals("your")|| posterior.equals("you")|| posterior.equals("he")
					|| posterior.equals("his")|| posterior.equals("him")|| posterior.equals("she")|| posterior.equals("her")
					|| posterior.equals("they")|| posterior.equals("their")|| posterior.equals("them")|| posterior.equals("it")
					|| posterior.equals("its")|| posterior.equals("we")|| posterior.equals("our")|| posterior.equals("us")
					) 
				  )
				{
					if(para.isPRP_POS_ACCFound == true)
					{
						para.isPRP_POS_ACCFound = false;
						System.err.println("TEST3:"+sentence.getContent()+"|"+PRP+"|"+prior+"|"+posterior);
					}
				}
			}
			if(para.isPRP_POS_ACCFound == false)//縮短計算時間用
				break;
		}
		if(ch.isEmpty())//取代陣列為空，原句第一個代名詞找不到取代
		{
			if(w.getPos().equals("PRP")&&!w.getOriginal().equals("I")&&!w.getOriginal().equals("We")&&!w.getOriginal().equals("Our")&&!w.getOriginal().equals("My"))
			{
				isFound = false;
				
			}
			if(!pos_acc_list.isEmpty())
				para.isPRP_POS_ACCFound = false;
		}
		for(String s:ch)//檢查原句第一個word代名詞主格是否能找出取代
		{
			String split[] = s.split("=>");
			if(w.getPos().equals("PRP")&&!w.getOriginal().equals("I")&&!w.getOriginal().equals("We")&&!w.getOriginal().equals("Our")&&!w.getOriginal().equals("My"))
			{
				if(!split[0].equals(w.getOriginal()+" "))//陣列不為空，可是第一個代名詞不在取代陣列裡
				{
					isFound = false;
				}
				else
				{
					if(//split[0].equals(w.getOriginal()+" ")&&
							(split[1].toLowerCase().equals("i ")||split[1].toLowerCase().equals("my ")||split[1].toLowerCase().equals("me ")
							||split[1].toLowerCase().equals("you ")||split[1].toLowerCase().equals("your ")||split[1].toLowerCase().equals("he ")
							||split[1].toLowerCase().equals("his ")||split[1].toLowerCase().equals("him ")||split[1].toLowerCase().equals("she ")
							||split[1].toLowerCase().equals("her ")||split[1].toLowerCase().equals("they ")||split[1].toLowerCase().equals("their ")
							||split[1].toLowerCase().equals("them ")||split[1].toLowerCase().equals("we ")||split[1].toLowerCase().equals("our ")
							||split[1].toLowerCase().equals("us ")||split[1].toLowerCase().equals("it ")||split[1].toLowerCase().equals("its ")) )
					{
						isFound = false;
					}
					else
					{
						isFound = true;
						break;
					}
				}
				
			}
		}
		
		para.isFirstPRPFound = isFound;
		
	}
//	private boolean checkFirstPRP(Sentence sentence)//若第一個字為代名詞(非第一人稱)且找不到取代, 則不產生問句.
//	{
//		boolean isFound = true;
//		List<String> ch = sentence.getChangeList();
//		Word w = sentence.getWordList().get(0);
//		if(w.getPos().equals("PRP")&&!w.getOriginal().equals("I")&&!w.getOriginal().equals("We")&&!w.getOriginal().equals("Our")&&!w.getOriginal().equals("My"))
//		{
//			if(ch.isEmpty())//取代陣列為空，原句第一個代名詞找不到取代
//				isFound = false;
//			for(String s:ch)
//			{
//				String split[] = s.split("=>");
//				if(!split[0].equals(w.getOriginal()+" "))//陣列不為空，可是第一個代名詞不在取代陣列裡
//					isFound = false;
//				else 
//					isFound = true;
//				if(split[0].equals(w.getOriginal()+" ")&&
//						(split[1].toLowerCase().equals("i ")||split[1].toLowerCase().equals("my ")||split[1].toLowerCase().equals("me ")
//						||split[1].toLowerCase().equals("you ")||split[1].toLowerCase().equals("your ")||split[1].toLowerCase().equals("he ")
//						||split[1].toLowerCase().equals("his ")||split[1].toLowerCase().equals("him ")||split[1].toLowerCase().equals("she ")||split[1].toLowerCase().equals("her ")||split[1].toLowerCase().equals("they ")
//						||split[1].toLowerCase().equals("their ")||split[1].toLowerCase().equals("them ")||split[1].toLowerCase().equals("we ")
//						||split[1].toLowerCase().equals("our ")||split[1].toLowerCase().equals("us ")||split[1].toLowerCase().equals("it ")
//						||split[1].toLowerCase().equals("its ")) )
//				{
//					isFound = false;
//					System.err.println("TEST!!!!!!!1");
//				}
//				
//			}
//		}
//		return isFound;
//	}
	public String startWithWord(String s)
	{
		return s.substring(0, s.indexOf(" "));
	}
	public boolean isGreetStartWith(Sentence sentence)
	{
		String inputStr = sentence.getContent();
	    String patternStr = "^(Dear|Hey|Hi|Howdy|Yo|Hello)\\s?\\w*,\\s";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher matcher = pattern.matcher(inputStr);
	    boolean matchFound = matcher.find();
	    
		return matchFound;
	}
	public boolean isPRP$_NN_POS(Sentence sentence)
	{
//		System.err.println("PRP$:"+sentence.getContent());
		boolean PRP$_NN_POS = false;
		for(int i = 0; i < sentence.getWordList().size()-2 ; i++)
		{
			Word word = sentence.getWordList().get(i);
			if(word.getPos().equals("PRP$")&&sentence.getWordList().get(i+1)!=null&&sentence.getWordList().get(i+2)!=null)
			{
				if(sentence.getWordList().get(i+1).getPos().equals("NN")&&sentence.getWordList().get(i+2).getPos().equals("POS"))
				{
					PRP$_NN_POS = true;
					
				}
			}
//			System.err.print(word.getPos());
		}
		
		return PRP$_NN_POS;
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
					
					if(changed.getPos().equals("PRP$")||changed.getPos().equals("POS"))//判斷是否所有格
						isPoss = true;
					else
						isPoss = false;
					
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
						if(changed.getPos().equals("PRP")||changed.getPos().equals("PRP$"))//代換後如果還是PRP or PRP$則繼續代換，只遞迴一次
						{
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
//							canAdd = true;
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
						
					}//end for
					if (counter>3) { //大於3個字則不取代
						canAdd=false;
					}
					System.err.println(changeString+"|"+counter+"|"+canAdd);
				}//end canAdd
													
				if(canAdd){
						if(isPoss)
						{// 避免PRPs => non-PRPs//避免[his =>Josh ] or [his =>him ] or [his =>he ] or [his =>Mary's brother ] or [his =>Their brother ] or[his =>his ]
							String revers = "";
							String temp1[] = changeString.split("=>");
							for(int i = changeString.length()-1; i>=0; i--)
							{
								revers = revers + changeString.charAt(i);
							}							
							revers = revers.substring(1,3);
							//除了person's和PRP$之外的名詞所有格如果沒加's，在這邊加//[his =>him ] or [his =>he ]
							if(temp1[1].toLowerCase().equals("i ")||temp1[1].toLowerCase().equals("me ")
									||temp1[1].toLowerCase().equals("you ")||temp1[1].toLowerCase().equals("he ")
									||temp1[1].toLowerCase().equals("him ")||temp1[1].toLowerCase().equals("she ")
									||temp1[1].toLowerCase().equals("her ")||temp1[1].toLowerCase().equals("they ")
									||temp1[1].toLowerCase().equals("them ")||temp1[1].toLowerCase().equals("we ")
									||temp1[1].toLowerCase().equals("us ")||temp1[1].toLowerCase().equals("it "))
							{
								changeString = changeString.replace("=>"+temp1[1],"=>");
								changeString = changeString+temp1[0].toLowerCase();
							}else if//[his =>Josh ] or[his =>Mary's brother ] or [his =>Their brother ] or[his =>his ]
							(!((revers.equals("s'")||revers.equals("s’"))) && !temp1[1].equals("our ") && !temp1[1].equals("your ") && !temp1[1].equals("their ")
									&& !temp1[1].equals("Our ") && !temp1[1].equals("Your ") && !temp1[1].equals("Their ")&& !temp1[1].equals("my ") && !temp1[1].equals("My ")
									&& !temp1[1].equals("Her ") && !temp1[1].equals("her ")&&!temp1[1].equals("His ") && !temp1[1].equals("his ")
									&& !temp1[1].equals("My ") && !temp1[1].equals("my ")&& !temp1[1].equals("Its ") && !temp1[1].equals("its "))
							{
								changeString = changeString + "'s";
							}
						}else//避免[he =>Josh's ] or [(he|him) =>(he|his|him) ] or [he =>Mary's brother's ] or [he =>Their brother's ] or[he =>Josh ]， 箭頭前面不會出現人名，只有指代才會進來
						{
							String revers = "";//避免人名+'s
							String t[] = changeString.split("=>");
							for(int i = changeString.length()-1; i>=0; i--)
							{
								revers = revers + changeString.charAt(i);
							}							
							revers = revers.substring(1,3);
							if(revers.equals("s'")||revers.equals("s’"))//[he =>Josh's ] or[he =>Mary's brother's ] or [he =>Their brother's ]
							{
								changeString = changeString.replace("=>"+t[1],"=>");
								t[1] = t[1].substring(0, t[1].length()-3);
								changeString = changeString+t[1];
							}//[he =>his ] or [him =>his ]
							else if(t[1].toLowerCase().equals("my ")||t[1].toLowerCase().equals("your ")||t[1].toLowerCase().equals("her ")
									||t[1].toLowerCase().equals("his ")||t[1].toLowerCase().equals("our ")||t[1].toLowerCase().equals("their ")
									||t[1].toLowerCase().equals("its ")||t[1].toLowerCase().equals("i ")||t[1].toLowerCase().equals("you ")||t[1].toLowerCase().equals("she ")
									||t[1].toLowerCase().equals("he ")||t[1].toLowerCase().equals("we ")||t[1].toLowerCase().equals("they ")
									||t[1].toLowerCase().equals("it ")||t[1].toLowerCase().equals("me ")||t[1].toLowerCase().equals("him ")
									||t[1].toLowerCase().equals("us ")||t[1].toLowerCase().equals("them "))
									
							{
								changeString = changeString.replace("=>"+t[1],"=>");
								changeString = changeString+t[0].toLowerCase();
							}
							
						}
					
					if(changeString.split("=>")[0].matches("I\\s"))
					{
						changeString = "I =>I ";
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
	
	
	public String changeDT_PosNames(String s)//冠詞替換 EX: a/an/this/that/these/those -> the //對已產生的問句檢查, 若所有格的名詞在前面出現過, 則換成his/her
	{
		StringBuilder builder = new StringBuilder();
		StanfordCoreNLP pipeline = new StanfordCoreNLP();
		Annotation annotation = new Annotation(s);
		pipeline.annotate(annotation);
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		SemanticGraph graph = sentences.get(0).get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
        List<SemanticGraphEdge> edgeList = graph.edgeListSorted();
        boolean flag;
        int tokenSize = sentences.get(0).get(TokensAnnotation.class).size();
        HashMap<String, String> nameMap = new HashMap<String, String>();
        String tokenFirstOri = "";
        Sentence sentence = new Sentence();
        for (int i = 0; i < tokenSize; i++) 
        {
        	flag = true;
        	CoreLabel token = sentences.get(0).get(TokensAnnotation.class).get(i);
        	Word word = new Word();
        	word.setPos(token.get(PartOfSpeechAnnotation.class));
        	sentence.getWordList().add(word);
        	sentence.setContent(sentence.getContent()+token.originalText()+" ");
//        	if(token.originalText().toLowerCase().equals("a")||token.originalText().toLowerCase().equals("this")||token.originalText().toLowerCase().equals("that")
//        			||token.originalText().toLowerCase().equals("these")||token.originalText().toLowerCase().equals("those")||token.originalText().toLowerCase().equals("an"))
//        	{
//		        for (int j = 0 ; j < edgeList.size() ; j++ ) 
//		        {
//		        	SemanticGraphEdge edge = edgeList.get(j);
//			        IndexedWord iw1 = edge.getGovernor();
//			        IndexedWord iw2 = edge.getDependent();
//			        GrammaticalRelation  relation = edge.getRelation();
//			        if(iw2.beginPosition() == token.beginPosition() && relation.getShortName().equals("det"))
//			        {
//			        	builder.append("the ");
//			        	flag = false;
//			        }
//				}
//        	}	
        	
        	String tokenNer = token.ner();
        	if((i+1)<tokenSize && sentences.get(0).get(TokensAnnotation.class).get(i+1).get(PartOfSpeechAnnotation.class).equals("POS"))
        	{
        		if(nameMap.get(token.originalText())!=null)
        		{
        			String temp  = builder.toString();
        			temp = replaceAll(temp,"(Miss|Mr.|Mrs.|Ms.)\\s$","");
//        			if(i > 0)
//        			{
//        				String prevTokenOri = sentences.get(0).get(TokensAnnotation.class).get(i-1).originalText();
//        				String prevTokenNer = sentences.get(0).get(TokensAnnotation.class).get(i-1).ner();
//        				if(prevTokenNer.equals("PERSON")&&nameMap.get(prevTokenOri)!=null)
//        				{
//        					temp = replaceAll(temp,prevTokenOri+"\\s$","");
//        				}
//        			}
        			builder = new StringBuilder(temp);
        			
        			token.setOriginalText("his/her");
        			i++;
        		}
        	}
        	
        	if(flag)
        		builder.append(token.originalText()+" ");
        	if(tokenNer.equals("PERSON"))
        		nameMap.put(token.originalText(), token.originalText());
        }
        isPRP$_NN_POS = isPRP$_NN_POS(sentence);//check isPRP$_NN_POS
		return builder.toString();
	}
	
	public String changeWriter(String origin,String sentence)
	{
		String result = new String(sentence);
		if(origin.matches(".*(\\s?(I|i)\\s(\\s|\\w)+)\\smyself\\s.*"))//I....myself -> I....himself
		{
			result = result.replaceAll("myself","himself/herself");
		}
		if(origin.matches(".*(\\s?(I|i)\\s(\\s|\\w)+)\\smy\\s.*"))//I....my -> I....his
		{
			result = result.replaceAll("my","his/her");
		}
		return result;
	}
	public String changeTheWriter(String sentence) {
		String result = new String(sentence);
		result = result.replaceAll("Now ","");
		System.out.println("test-------------"+result);
		
		if (result.matches(".*(\\W+(I|we|I'm|My|our|my|Our|We|Us|us|i|Me|me)+\\W*)+.*")) {// include
			System.out.println("I am");
			// I,we,My...etc
			result = result.replaceAll("(\\s?(I|i)\\s(am)\\s)|\\s?(I�)\\s",
					" the author is ");
			result = result.replaceAll("(\\s?(am|Am)\\s(i|I)\\s)|\\s?(I�)\\s",
					" is the author ");
			result = result.replaceAll(
					"(\\s(I|we|us|i|me)(\\W)\\s)|(\\s(I|we|us|i|me)\\W)|((I|Me|We|Us)\\s)",
					" the author ");
			result = result.replaceAll(
					"(\\s(my|our)\\s)|(\\s(my|our)\\W)|((My|Our)\\s)",
					" the author's ");

		}
		result = result.replaceAll(" mr. "," Mr. ");
		result = result.replaceAll(" mrs. "," Mrs. ");
		result = result.replaceAll(", too","");
		//result = result.replaceAll(", but",""); //�芣��
		//result = result.replaceAll(", then","");
		StringBuilder builder = new StringBuilder();
		String temp[] = result.split(" ");
		
		
		boolean isFirstFind = false;
		for(int i = 0 ; i <temp.length ; i++)//問句中已出現the writer, 後面的the writer’s換成”her/his”
		{
			
			if( temp[i].matches("the")&&(i+1<temp.length)&&temp[i+1].matches("author\\s*('s)?"))
			{
				if(isFirstFind == false)
				{
					isFirstFind = true;
					builder.append(temp[i]+" "+temp[i+1]+" ");
					
				}else
				{
					if(temp[i].matches("the")&&(i+1<temp.length)&&temp[i+1].matches("author\\s*('s)"))
					{
						builder.append(" his ");
						
					}
				}
				i++;
			}else
				builder.append(temp[i]+" ");
		}
		return builder.toString();
	}
	
	public boolean thewriter_sinpul(String sentence){
		String result = new String(sentence);
		boolean flag = false ;
		if (result.matches(".*(\\W+(the writer)+\\W+)+.*")) {// 憒��he writer
			flag = true ;
		}
		return flag ;
	}
	private String replaceAll(String s,String patternStr,String to)
	{
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher matcher = pattern.matcher(s);
	    boolean matchFound = matcher.find();
	    return matcher.replaceAll(to);
	}
}

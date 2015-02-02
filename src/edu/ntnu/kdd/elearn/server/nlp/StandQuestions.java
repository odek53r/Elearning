package edu.ntnu.kdd.elearn.server.nlp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ntnu.kdd.elearn.shared.model.Sentence;
import edu.ntnu.kdd.elearn.shared.model.Word;


public class StandQuestions {
	
	private Stemmer stem;	
	private String stem_verb ;
	

	private String input;
	private String question;
	
	private Sentence sentence;
	private Sentence changedSentence;
	private boolean isFirstPerson ;
	private boolean isQCreated = false;
	//----------------------------------------------------//

		

	public StandQuestions()
	{
		stem = new Stemmer();
			
	}
	
	//�交�亙�嚗蒂����
	public void setStent(String in,Sentence sentence)
	{
		//�寞�the writer
		this.sentence = sentence;
		
		//�OS
		//pos.posting(input);
	}
	
	public void setFirstPerson(boolean firstOrNot){
		isFirstPerson = firstOrNot;
	}
	
	//�question
	public String getQuest()
	{
		quest();
		return question;
	}
	
	
	
	//�Ｙ��
	private void quest()
	{	    
	    question = "";
	    ArrayList<Word> temp = sentence.getWordList(); //get each word in the sentence
	    boolean isIamPerson =false;
	    String sent = sentence.getContent();
	    if(sent.startsWith("I am")||sent.startsWith("I'm")||sent.startsWith("I’m")){ //看句子是不是以I am 開頭
	    	String Ner = temp.get(2).getNer() ; //得到名字(人名專有名詞)
	    	if(Ner!=null){
	    		if(Ner.equals("PERSON")){
	    			isIamPerson = true;
	    		}  	
	    	} 		  	
	    }
	    if(isIamPerson){ //建立問名字的問句後面照抄
	    	question = "Is the writer's name ";
	    	for(int a =2;a<temp.size()-1;a++){
	    		question = question + temp.get(a).getOriginal()+" ";	  
	    	}
	    	question = question +"?";
	    }
	    else{   
	    //process 
	    boolean use_v = false ;
	    String NPos =null;
	    String RBPos_end = null;
	    for(int i = 0;i< temp.size();i++)
	    {
	    	String OriWord = temp.get(i).getOriginal();  //original word
	    	String WordPos =  temp.get(i).getPos();   //result of POS tagging 
	    	String WordNer =  temp.get(i).getNer();  //result of NER
	    	String OriNxtWord = null;
	    	String NxtPos = null;
	    	
	    	if(WordPos.equals("NN")||WordPos.equals("NNS")||WordPos.equals("NNP")||WordPos.equals("NNPS"))//判斷temp[i]的word是否為名詞(單複數)，從code tag1移到這  2015/01/21
	    	{
	    		NPos=WordPos;
	    	}
	    	if(i==0 && OriWord.equals(",")){//檢查sentence的第一個word是否為","，如果為","，則跳過逗點 2015/01/21
	    		continue;
	    		/*i++ ;
	    		OriWord = temp.get(i).getOriginal();  //original word
	 	        WordPos =  temp.get(i).getPos();   //result of POS tagging 
	 	    	WordNer =  temp.get(i).getNer();  //result of NER
	 	    	*/
	    		//2015/01/21 註解掉
	    	}
	    	if(i==0 && OriWord.equals("But")){//檢查sentence的第一個word是否為"But"，如果為"But"，則跳過But 2015/01/21
	    		continue;
	    		/*i++ ;
	    		OriWord = temp.get(i).getOriginal();  //original word
	 	        WordPos =  temp.get(i).getPos();   //result of POS tagging 
	 	    	WordNer =  temp.get(i).getNer();  //result of NER
	 	    	*/
	    		//2015/01/21 註解掉
	    	}
	    	
	    	if(i+1<temp.size()){ //得到下一個word的詞性跟word
	    		OriNxtWord = temp.get(i+1).getOriginal();
	    		NxtPos = temp.get(i+1).getPos();
	    	}
	    	String PreWord = "";
	    	String PrePos = "";
	    	if(i>1){ //得到前一個字的內容跟詞性 //if(i>0)才對? 2015/01/21
	    		PreWord =  temp.get(i-1).getOriginal(); //original front word
	    		PrePos = temp.get(i-1).getPos();
	    	}
	    	
	    	//VBZ:is,am,has   VBP:are,have   VBD:was,were,had
	    	//問當前i的字是否為be動詞(單複數，過去)，動詞
	    	
	    	if(WordPos.equals("VBZ")||WordPos.equals("VBP")||WordPos.equals("VBD")||WordPos.equals("VB")||WordPos.equals("VBG"))//新增WordPos.equals("VBG") 2015/01/21
	    	{
	    		if(NPos!=null && (NPos.equals("NNS")||NPos.equals("NNPS")))//判斷是否單數
	    		{
	    			setFirstPerson(false);
	    		}
	    		//is,are 判斷動詞為哪一種動詞
	    		if(OriWord.equals("is")||OriWord.equals("are")||OriWord.equals("am")||OriWord.equals("was")||
	    				OriWord.equals("were")||OriWord.equals("'m")||OriWord.equals("’m"))
	    		{
	    			
	    			if(OriNxtWord!=null&&(OriNxtWord.equals("n’t")||OriNxtWord.equals("n't"))){
	    				if(!use_v){ //判斷問句是否已經有動詞 防止dododoes雙動詞問題
	    					if(OriWord.equals("'m")||OriWord.equals("’m"))
	    					{
	    						question = OriWord + OriNxtWord + question;
	    					}else
	    					{
	    						question = OriWord + OriNxtWord + question;
	    					}
	    					
	    					use_v = true ;
	    				}
	    			}
	    			else{
	    				if(!use_v){
	    					if(OriWord.equals("'m")||OriWord.equals("’m"))
	    					{
	    						question = "is" + question;
	    					}else
	    					{
	    						question = OriWord + question;
	    					}
	    					
	    				}
	    			}		
		    			
	    			
	    		}
	    		else if(OriWord.equals("’s")||OriWord.equals("'s")) //be V 問句
	    		{
	    			if(!use_v){
	    				question = "Is" + question;
	    				use_v = true ;
	    			}
	    		}
	    		else if((OriWord.equals("have")||OriWord.equals("has")||OriWord.equals("had")||OriWord.equals("'ve")))
	    		{
	    			if(OriWord.equals("'ve") && isFirstPerson != true)//have、has單複數校正
	    				OriWord = "have";
	    			if(OriWord.equals("'ve") && isFirstPerson != false)
	    				OriWord = "has";
	    			//have�
	    			//VBN:���
	    			if(NxtPos.contains("VBN")){ //have.has,had + p.p
	    				if(!use_v){
	    					question = OriWord + question;
	    					use_v = true ;
	    				}
	    			}
	    			
	    			//have�箏�閰�
	    			else{
	    				if(PrePos.equals("TO")) //如果have前一個to表示"動詞"(Do you have...)
			    		{
			    			question = question + " " + OriWord;
			    		}
	    				else
	    				{
	    					if(!use_v){
	    						question = getDoDid(WordPos) + question;  //Do/Does/Did問句產生
	    						System.out.println("have has "+WordPos);
	    						use_v = true ;
	    					}
		    				try {
								stem.writeVerb(OriWord); //不知道幹嘛
								stem_verb = stem.wordmain("verb.txt"); //加入問句的主要動詞
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							question = question + " " + stem_verb;
	    				}
	    				
	    			}
	    			
	    			
	    		}
	    		else if(OriWord.equals("does")||OriWord.equals("do")||OriWord.equals("did")) //這邊當作一般V
	    		{
	    			if(!use_v){
	    				question = OriWord + question  ; 
	    				use_v = true ;
	    			}
	    			if(!NxtPos.equals("RB")){//
		    			if((!NxtPos.equals("VB"))){
		    				 if(!NxtPos.equals("RB")){
		    					question = question + " do" ; 
		    				 }
		    				 else {
		    					 question = question ;
		    				 }
		    			}
	    			}
	    		}
	    		//V+ing
	    		else if(WordPos.equals("VBG")) //動名詞
	    		{
	    			if(temp.get(i).getNer()!=null && temp.get(i).getNer().equals("PERSON"))
					{
						question = question + " " + OriWord;
					}
					else
					{
						question = question + " " + OriWord.toLowerCase();
					}
	    			
	    		}
	    		else if(PrePos.equals("MD")){ //感官動詞 looks , sound , feel
	    			question = question + " " + OriWord; 
	    		}
	    		else //銝���
	    		{
	    			if(PreWord.equals("n’t")||PreWord.equals("not")||PreWord.equals("n't"))
	    			{
	    				question = question + " " + OriWord ;
	    			}
	    			//to+V
		    		else if(PrePos.equals("TO"))
		    		{
		    			question = question + " " + OriWord;
		    		}
	    			else
	    			{
	    				if(!use_v){
	    					question = getDoDid(WordPos) + question; //真正的Do/did問句
	    					System.out.println("else "+WordPos);
	    					use_v = true ;
	    				}
	    				try {
							stem.writeVerb(OriWord);
							stem_verb = stem.wordmain("verb.txt");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						question = question + " " + stem_verb;
	    			}
	    		}
	    	}
	    	else if(WordPos.equals(""))
	    	{
	    		
	    	}
	    	else if(OriWord.equals("n’t")||OriWord.equals("not")||OriWord.equals("n't"))
	    	{
	    		question = question;
	    	}
	    	//MD:can,could,may,would,shall,should,will
	    	else if(WordPos.equals("MD")){
	    		if(OriWord.equals("'d"))//縮寫校正
	    			OriWord = "could";
	    		if(!use_v){
	    			question = OriWord + question; 	 
	    			use_v = true ;
	    		}
	    	}
	    	
	    	else if(i == temp.size()-1) //到最後一個字了
	    	{
	    		
	    		if(OriWord.matches("(\\w+\\W)+")){ //判斷是不是英文字
	    			
	    			question = question +" " + toQuestionMark(OriWord); 
	    		}
	    		else{ //如果不是代表end + "?"
	    			question = question +  "?";
	    		}
	    		if(RBPos_end != null)
	    		{
    				
	    			StringBuilder str = new StringBuilder(question);
	    			question = str.replace(str.length()-1, str.length()," "+RBPos_end+"?").toString();
	    		}
	    		
	    	}
	    	else if(i == temp.size()-2){ //重複標點符號的問題
	    		if(OriWord.equals(".")||OriWord.equals(",")){

	    		}
	    		else{
	    			question = question + " " + OriWord ;
	    		}
	    	}
	    	else
	    	{	
	    		//change the first word of the original sentence into lowercase  
	    		if(i==0)
	    		{ 
	    			
	    			if(WordNer==null)//not NNP,location,organization
	    			{
	    				if(WordPos.equals("RB")&&(OriWord.equals("Now")||OriWord.equals("Today")||OriWord.equals("now")||OriWord.equals("today"))||
	    						OriWord.equals("Recently")||OriWord.equals("recently")||OriWord.equals("Soon")||OriWord.equals("soon")||
	    						OriWord.equals("Yesterday")||OriWord.equals("yesterday")||OriWord.equals("Already")||OriWord.equals("already")||
	    						OriWord.equals("Yet")||OriWord.equals("yet")||OriWord.equals("Finally")||OriWord.equals("finally"))//句首時間副詞暫存
	    				{
	    					RBPos_end = OriWord.toLowerCase();
	    					if(OriNxtWord.equals(","))//避免","出現在人名或代名詞前 ,ex: When did , Andy come back finally
	    					{
	    						i=i+1;
	    					}
	    				}
	    				else 
	    				{
	    						question = question + " " + OriWord.toLowerCase();
	    				}
	    			 				
	    			}
	    			else{//NNP,location,organization
		    			question = question + " " + OriWord;
		    		
	    			}
	    		}
	    		else{ //其他字都加在後面
	    			if(!OriWord.equals("n’t")|| !OriWord.equals("n't"))
	    			{
	    				if(temp.get(i).getNer()!=null && temp.get(i).getNer().equals("PERSON"))
    					{
    						question = question + " " + OriWord;
    					}
    					else
    					{
    						question = question + " " + OriWord.toLowerCase();
    					}

	    			}
	    			    			
	    		}
	    		
	    	 }

	     }
	    System.out.println("primal standQuestion:"+question);
	    //process end
	    question = toUpperCase(question); //首字的第一個字變大寫
	    if(isFirstPerson){ //看是不是第一人稱的文章
	    	question = toChangeBeginning(question); //如果開頭的問句是Am....,am  ,'m  ...
	    }
	    System.out.println("YES/NO original Q :"+question) ; //印出問句
	    question = PRPreplacement(sentence,question);   //文法代換規則
	    question.replaceAll("( {1,}\\?)$", "?");//避免問號前有空格
	    }	   	    	    	   
	}
	
	private String PRPreplacement(Sentence sent,String oldQ){
		List<String> changelist = sent.getChangeList();		
		System.out.println("YES/NO :"+changelist);
		for(String x : changelist){
			String[] temp = x.split("=>");
//			System.out.println("replacement :"+temp[0]+"--->"+temp[1]);
			String replaceArg0 = addSpace(temp[0].toLowerCase());
			String replaceArg1 = addSpace(temp[1]);
			System.out.println("temp[0] addSpace:"+replaceArg0+", temp[1] addSpace:"+replaceArg1);
			String temp1 = new String(oldQ);
			oldQ = oldQ.replace(replaceArg0,replaceArg1);
			if(temp1.equals(oldQ))
				oldQ = oldQ.replace(replaceArg0.substring(0,replaceArg0.length()-1),replaceArg1);
			if(temp1.equals(oldQ))
				oldQ = oldQ.replace(replaceArg0.substring(1,replaceArg0.length()),replaceArg1);
		}
		System.out.println("Yes/No Q with PRP replacement : "+oldQ);
		return oldQ;
	}
	
	private String toChangeBeginning(String in){
		if(in.startsWith("Am")){
			in = in.replace("Am", "Is");
		}
		else if(in.startsWith("'m")){
			in = in.replace("'m", "Is");
		}
		else if(in.startsWith("’m")){
			in = in.replace("’m", "Is");
		}
		return in;
	}
	
/*
	//蝯�
	private String combineStent(String[] in){
		String out = "";
		for(String x : in){
		if(x.endsWith("\\.")){
		out = out + x;
		}
		else{
		out = out + x +" ";
		}

		}
		System.out.println("input:"+out);
		return out;
	}
	*/	
	
	//���銝��摮��?
	private String toQuestionMark(String ori){
		StringBuilder sb = new StringBuilder(ori);
		return sb.reverse().replace(0, 1, "?").reverse().toString();	//最後的標點符號替換成"?"	
	}
	
	private String addSpace(String in){
		if(in.startsWith(" ")){
			if(in.endsWith(" ")){
				return in;
			}
			else{
				in = in+" ";
			}
		}
		else if(in.endsWith(" ")){
			in = " "+in;
		}
		else{
			in = " "+in+" ";
		}
		return in;
	}

	private String toUpperCase(String ori){
		char[] chars = ori.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return String.valueOf(chars);
	}
	
	
	private String getDoDid(String h){
		String ret="";
		if(h.equals("VBD")){
			ret = "Did";
		}
		else if(isFirstPerson){
			System.out.println("isFirst");
			ret = "Does";
		}
		else if(h.equals("VBZ")){
			System.out.println("VBZ");
			ret = "Does";
		}
		else{
			ret = "Do";
		}
		return ret;
	}

}

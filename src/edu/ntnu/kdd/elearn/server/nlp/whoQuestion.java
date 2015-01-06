package edu.ntnu.kdd.elearn.server.nlp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Sentence;
import edu.ntnu.kdd.elearn.shared.model.Word;

public class whoQuestion {
	private String whoQ = "";
	Boolean RB = false;
	private static String path = Stemmer.class.getResource("/").getPath();    
	private static String extra_verb = path + "SpecialCase.txt" ;
	
	public ArrayList<String> WhoQG(Sentence sentence, Article article) {
		String[] corenlpResult = article.getCoreferenceResult();// if empty then
																// length =0 ,if
																// null then
																// means no
																// PERSON of NER

		int index = getSentindex(article, sentence); // index of document
		ArrayList<String> ChangedMapping;
		ArrayList<String> question = new ArrayList<String>();
		if (corenlpResult == null) {// the article doesn't have any PERSON of
									// NER
			return null;
		} else if ((ChangedMapping = Changedlist(corenlpResult, index))
				.isEmpty()) {// the article doesn't have any replacement
		} else {
			for (String a : ChangedMapping) {
				String[] splitRes = a.split(",");
				if (splitRes[0].equals("0")) {// process the PRP at head of
												// sentence
					ArrayList<Word> WordChangedlist = sentence.getWordList();
					int WordchangedStart = Integer.parseInt(splitRes[0]);
					int WordchangedEnd = Integer.parseInt(splitRes[1]);
					for (int i = WordchangedStart; i <= WordchangedEnd; i++) {
						String WordChangedPostag = WordChangedlist.get(i)
								.getPos();
						if (WordChangedPostag.equals("POS")
								|| WordChangedPostag.equals("PRP")
								|| WordChangedPostag.equals("PRP$")) {
							question.add(ProduceWhoQuestion(sentence, i));
						}
					}
				} else { // process the PRP in the middle of sentence
					System.out.println("No First:" + sentence.getContent());
				}
			}
		}

		return question;
	}

	private String ProduceWhoQuestion(Sentence sen, int WordEnd) {
		boolean fixverb = false ;
		ArrayList<Word> words = sen.getWordList();

		Word endwordStr = words.get(WordEnd);
		String output = "";
		if (endwordStr.getPos().equals("PRP$")) {
			if (!endwordStr.getOriginal().equals("Its")) {
				output = "Whose";
			} else {
				output = "What";
			}
		}

		else if (endwordStr.getPos().equals("PRP")) {
			if (!endwordStr.getOriginal().equals("It")) {
				output = "Who";
				String ori = endwordStr.getOriginal() ; //判斷原句的主詞是否為I/We
				if(ori.equals("I")||ori.equals("i")||ori.equals("We")||ori.equals("we")||ori.equals("They")||ori.equals("they")){
					fixverb = true ;
				}
				
			} else {
				output = "What";
			}
		} else if (endwordStr.getPos().equals("POS")) {
			if (!endwordStr.getOriginal().equals("Its")) {
				output = "Whose";
			} else {
				output = "What";
			}
		} else {
			
		}

		for (int i = WordEnd + 1; i < words.size() - 1; i++) {
			String wQri = words.get(i).getOriginal();
			String wPos = words.get(i).getPos();
			String wNer = words.get(i).getNer();
			
			
			if (wQri.equals("n't") || wQri.equals("n’t")) {
				wQri = "not";
			} else if (wQri.equals("am") || wQri.equals("'m")
					|| wQri.equals("’m")) {
				wQri = "is";
			} else {

			}
			
			if(wPos.equals("VBP")){
				if(fixverb){
					if(!(wQri.equals("is")||wQri.equals("was")||wQri.equals("are")||wQri.equals("were"))){ //不等於be.V
						wQri = Chang(wQri) ; //代換動詞單複數問題
					}
				}
			}
			output = output + " " + wQri;
			
		}
		if (words.get(WordEnd + 1).getPos().equals("RB")) {
			RB = true;
		}
		output = output + " ?";
		System.out.println("WHO Q : " + output);
		whoQ = output;

		List<String> changed = sen.getChangeList();
		System.out.println("WHO :" + changed);
		String tempword=endwordStr.getOriginal();
		String secondword=" ";
		boolean second=false;
		for (String x : changed) {
			System.out.println(x);
			String[] temp = x.split(" =>");
			System.out.println("temp0: "+temp[0]+" temp1: "+temp[1]+" endword: "+tempword);
			if (temp[0].matches(tempword)){
				System.out.println(temp[0]+" first");
				second=true;
				secondword=temp[1];
			}
			if (second && temp[1].contains(secondword))
			{
				System.out.println(temp[0]+" second");
			}
			else
			{
				whoQ = whoQ.replace(addSpace(temp[0].toLowerCase()),
						addSpace(temp[1]));
			}
		}
		//whoQ = whoQ.replaceAll("Who have", "Who has");

		/*if (whoQ.contains("have") && RB) {
			String tempend = whoQ.substring(whoQ.indexOf("have"));
			tempend = tempend.replaceFirst("have", "has");
			String tempbegin = whoQ.substring(0, whoQ.indexOf("have") - 1);
			whoQ = tempbegin + " " + tempend;
			RB = false;
		}*/
		System.out.println("WHO RPR replace :" + whoQ);
		return whoQ;
	}
	private static String Chang(String aa) {
		String Original =aa;
		
		try {
			FileReader fr = new FileReader(extra_verb);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while((line = br.readLine())!=null){
//				System.out.println(line);
				String[] arr = line.split("\\t");
				String[] temp = null;
				
				for(int i=0;i<arr.length;i++){
					if(arr[i].startsWith(Original)){
						temp=line.split(",");
						Original=temp[1];
						return Original;}
				}
			}
			//e+s
			//�hy+ies
			//����+s
			if( Original.endsWith("s")||Original.endsWith("x")||Original.endsWith("o")||Original.endsWith("ch")||Original.endsWith("sh")){
				
				 Original= Original+"es";
			}else if(Original.endsWith("y")){
				Original=Original.substring(0,Original.length()-1);
				Original=Original+"ies";
				
			}else{
				Original=Original+"s";
				
				
			}		
			
			
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return Original;
	}
	private String addSpace(String in) {
		if (in.startsWith(" ")) {
			if (in.endsWith(" ")) {
				return in;
			} else {
				in = in + " ";
			}
		} else if (in.endsWith(" ")) {
			in = " " + in;
		} else {
			in = " " + in + " ";
		}
		return in;
	}

	private int getSentindex(Article article, Sentence sent) {
		ArrayList<Sentence> sentences = article.getSentenceList();
		return sentences.indexOf(sent);
	}

	private ArrayList<String> Changedlist(String[] corenlpResult, int sentNum) {
		ArrayList<String> indexOfChanged = new ArrayList<String>();
		if (corenlpResult.length == 0) {
			return indexOfChanged;
		} else {
			for (int i = 0; i < corenlpResult.length; i++) {
				int index = corenlpResult[i].indexOf(",");
				String indexofstartsent = corenlpResult[i].substring(0, index);

				if (indexofstartsent.equals("" + sentNum)) {
					System.out.println("index : " + corenlpResult[i] + " "
							+ index);
					String sub = corenlpResult[i].substring(index + 1);

					indexOfChanged.add(sub);

				}
			}
		}

		return indexOfChanged;
	}

}
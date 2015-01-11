package edu.ntnu.kdd.elearn.server.nlp;


import java.io.*;
import java.util.*;

import edu.stanford.nlp.dcoref.*;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

public class Corenlp {
	
	static StanfordCoreNLP pipeline = new StanfordCoreNLP();
    static Annotation annotation;
    //static PrintWriter out;

	public static String[] getCoreference(List<CoreMap> sentences)
	{
		String[] cofer = new String[100];
		String[] cofer_head = new String[100];
		String[] cofer_tail = new String[100];
		int num = 0;
		Map<Integer, CorefChain> corefChains = 
				annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
		if (corefChains != null && sentences != null)
		{
			List<List<CoreLabel>> sents = new ArrayList<List<CoreLabel>>();

			for (CoreMap sentence : sentences) 
			{
				List<CoreLabel> tokens =
						sentence.get(CoreAnnotations.TokensAnnotation.class);
				sents.add(tokens);
			}
		    	 
			for (CorefChain chain : corefChains.values())
			{
				CorefChain.CorefMention representative =
						chain.getRepresentativeMention();
				boolean outputHeading = false;
				for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder() ) 
				{
					boolean check = true;//�L����
					if (mention == representative)
						continue;

					//setNum:�b�ĴX�ӥy�l
					//startIndex�Gword�}�l��m
					//endIndex�Gword�̫��m����@�Ӧr
					//mentionSpan�G���word		
					String temp_head = (mention.sentNum-1) + "," +
							(mention.startIndex-1);
					String temp_tail = (mention.sentNum-1) + "," +
							(mention.endIndex-2);
					if( num > 0 )
					{
						for( int i = 0; i < num; i++ )
						{
							if( temp_head.equals(cofer_head[i]) == true || temp_tail.equals(cofer_tail[i])== true)
							{
								check = false;
							}
						}
					}
					
					if( check == true )
					{
						cofer_head[num] = temp_head;
						cofer_tail[num] = temp_tail;
						cofer[num] = (mention.sentNum-1) + "," +
								(mention.startIndex-1) + "," +
								(mention.endIndex-2) + "," +
								(representative.sentNum-1) + "," +
								(representative.startIndex-1) + "," +
								(representative.endIndex-2);;
						System.out.println(mention.sentNum+","+mention.startIndex+","+mention.endIndex+","+representative.sentNum+","+representative.startIndex+","+representative.endIndex);
						num++;
					}
				}
			}
		}
		
		String[] coference = new String[num];
		for(int i = 0; i < coference.length; i++)
		{
			coference[i] = cofer[i];
		}
		
		return coference;
		//return cofer;
	}

	/**
	 * @param args
	 */
	public static String[] main(String args){
		// TODO Auto-generated method stub

		String[] result = new String[100];	
		if( args.length() > 0)
		{
			//System.out.println(args);
			annotation = new Annotation(args);
		}	
		else
		{
		    //annotation = new Annotation("Stanford University is located in California. It is a great university, founded in 1891.");
			annotation = new Annotation("Stanford University is located in California. It is a great university, founded in 1891.");
		}

		pipeline.annotate(annotation);		    
		// An Annotation is a Map and you can get and use the various analyses individually.   
		// For instance, this gets the parse tree of the first sentence in the text.
		    
		List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

		// display the new-style coreference graph
		String[] temp = getCoreference(sentences);
		
		//writeFile(result, "file"+".txt");
		return temp;
	}
}

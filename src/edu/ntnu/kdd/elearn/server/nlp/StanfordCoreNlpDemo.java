package edu.ntnu.kdd.elearn.server.nlp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.ntnu.kdd.elearn.shared.model.Word;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.CoreMap;

public class StanfordCoreNlpDemo {

	public static String[] getBasicDepRelations (CoreMap sentence) {
        List<CoreLabel> labels = sentence.get(CoreAnnotations.TokensAnnotation.class);
        List<Word> words = new ArrayList<Word>();
        SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
        graph.prettyPrint();
        List<SemanticGraphEdge> edgeList = graph.edgeListSorted();
        for (int i = 0 ; i < labels.size() ; i++ ) 
        {
        	CoreLabel token = labels.get(i);

			String original = token.get(TextAnnotation.class);

			String pos = token.get(PartOfSpeechAnnotation.class);
		
			String ner = token.get(NamedEntityTagAnnotation.class);
			
			int beginPosition = token.beginPosition();
			
			int endPosition = token.endPosition();
			
			Word w = new Word();
			w.setOriginal(original);
			w.setPos(pos);
			w.setNer(ner);
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
			words.add(w);
        }
        
        ArrayList<Word> removedWords = new ArrayList<Word>();
        
        for(int i = 0 ; i < words.size() ; i++)
        {
        	Word w = words.get(i);
        	System.err.println(w.getOriginal()+"|"+w.getNer());
        	if(w.getNer().equals("DATE")||w.getNer().equals("TIME")||w.getNer().equals("DURATION"))
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
        
        for(Word w : words)
        {
        	System.out.print(w.getOriginal()+" ");
        }
        
		return null;
}
	public static void deleteWord(List<Word> words,ArrayList<Word> removedWords)
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
  public static void main(String[] args) throws IOException {
    PrintWriter out;
    if (args.length > 1) {
      out = new PrintWriter(args[1]);
    } else {
      out = new PrintWriter(System.out);
    }
    PrintWriter xmlOut = null;
    if (args.length > 2) {
      xmlOut = new PrintWriter(args[2]);
    }

    StanfordCoreNLP pipeline = new StanfordCoreNLP();
    Annotation annotation;
    if (args.length > 0) {
      annotation = new Annotation(IOUtils.slurpFileNoExceptions(args[0]));
    } else {
      annotation = new Annotation("They play with her at the playground in the afternoon.");
    }
    Scanner scanner = new Scanner(System.in);
    while(scanner.hasNextLine())
	{
    	annotation = new Annotation(scanner.nextLine());
    	pipeline.annotate(annotation);
	    pipeline.prettyPrint(annotation, out);
	    if (xmlOut != null) {
	      pipeline.xmlPrint(annotation, xmlOut);
	    }
    
	    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
	    if (sentences != null && sentences.size() > 0) {
	      CoreMap sentence = sentences.get(0);
	      getBasicDepRelations(sentence);
	    }
    }
  }

}

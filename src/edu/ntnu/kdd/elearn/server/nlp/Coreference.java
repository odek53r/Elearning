package edu.ntnu.kdd.elearn.server.nlp;
import java.io.IOException;

public class Coreference {

	/**
	 * @param args
	 * @throws IOException 
	 */
	
//	public static void main(String[] args) throws IOException {
//		// TODO Auto-generated method stub
//		Corenlp coreNlp = new Corenlp();
//		
//		//---------�Цbarticle��峹���e�r��-----------------
//		String article = new ArticleDao().listArticle().get(6).getContent();
//		String[] result = coreNlp.main(article);
//		for(int i = 0; i < result.length; i++)
//		{
//			System.out.println(result[i]);
//		}
//	}
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Corenlp coreNlp = new Corenlp();
		
		//---------�Цbarticle��峹���e�r��-----------------
	
		String[] result = coreNlp.main("Christmas is only a day away and Della wants to buy Jim a watch chain for his gold watch");
		for(int i = 0; i < result.length; i++)
		{
			System.out.println(result[i]);
		}
	}
}

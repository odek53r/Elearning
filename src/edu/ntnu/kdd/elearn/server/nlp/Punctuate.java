package edu.ntnu.kdd.elearn.server.nlp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Punctuate {
	
	
	/*
	 * readFile() is to read the input English file.
	 */
	public static String readFile() throws IOException{
		
		FileReader reader = new FileReader( "Sara Went Shopping.txt" );
		BufferedReader bufRead = new BufferedReader( reader );
		String line = bufRead.readLine();
		
		int i = 0;
		String content ="";
		while( line != null )
		{
			content = content + line + " " ;
			line = bufRead.readLine();
				
		}
			
		reader.close();
		bufRead.close();
		
		return content;
	}
	
	
	/*
	 * writeFile() is to write the English file which is punctuated.
	 * writeintFile() is to write the punctuated string position in all file.
	 */
	
	public static void writeFile(String[] document, String filename) throws IOException{
	    	
	    File file = new File(filename);
	    PrintWriter out = new PrintWriter(new FileWriter(file));

	    for( String s : document)
	    {
	    	out.print(s);
	    }
	    out.close();
	  }
	
	public static void writeintFile(int[] document, String filename) throws IOException{
    	
	    File file = new File(filename);
	    PrintWriter out = new PrintWriter(new FileWriter(file));

	    for( int s : document)
	    {
	    	out.println(s);
	    }
	    out.close();
	  }

	/*
	 * punctuate() is to split the English file.
	 * Use "." or "!" or "?" to decide the string.
	 */
	public static String[] punctuate(String content) {
	
	
		String sp = " ";
	
		String[] token_space = content.split(sp);
		String temp = "";
		String[] document = new String[100];
		int count = 0;

		for( int i = 0; i < token_space.length; i++ )
		{
			int a = token_space[i].length();
			boolean Equal;
			if(  Equal = token_space[i].equals("Emily") == true )
			{
				temp = temp + token_space[i] + " ";
			}
			else if(  Equal = token_space[i].equals("Mrs.") == true )
			{
				temp = temp + temp + " ";
			}
			else
			{
				char c = token_space[i].charAt(a-1);
				//System.out.println(c);
				if( c == '.' || c == '!' || c == '?' )
				{
					temp = temp + token_space[i] + "\n";
					//System.out.println("!!!!!");
					document[count] = temp;
					count++;
					temp = "";
				}
				else
				{
					temp = temp + token_space[i] + " ";
				}
			}
		}
	
		String[] sentence = new String[count];
		for( int i = 0; i < count; i++ )
		{
			sentence[i] = document[i];
			//System.out.print(sentence[i] );
		}
	
		return sentence;
	
	}
	
	/*
	 * firstword() is to count every string the first word position in all file.
	 */
	public static int[] firstword(String[] sentence){
		int count = sentence.length;
		int [] first = new int[count];
		for( int i = 0; i < first.length; i++ )
		{
			first[i] = 0;
		}
		
		int total_length = 0;
		
		for( int i = 0; i < sentence.length; i++ )
		{
			first[i] = total_length;
			total_length = total_length + sentence[i].length();
		}
		
		return first;
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String file = readFile();//Ū��
		String[] sentence = punctuate(file);//�_�y
		int [] first = firstword(sentence);//��m
		writeFile(sentence, "sentence.txt");//�g��
		writeintFile(first, "position.txt");//�g��
		
		for( int i = 0; i < sentence.length; i++ )
		{
			System.out.print(first[i]+ " ");
			System.out.print(sentence[i]);
		}
	}
}

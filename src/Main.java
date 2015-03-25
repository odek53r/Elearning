import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.ntnu.kdd.elearn.server.nlp.NLPUtil;
import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Question;
import edu.ntnu.kdd.elearn.shared.model.Sentence;


public class Main {


	Gson gson ;
	private int questionNum = 0;
	
	public Main(String[] filepaths) throws Exception{
		
		GsonBuilder builder = new GsonBuilder();
		builder.disableHtmlEscaping();
	    gson = builder.create();
	    
		
	    for(int i = 0 ; i < filepaths.length ; i++)
	    {
	    	
			String content = readFile(filepaths[i]);
			if(content == null)
				throw new Exception("no content");
			
			Article article = createArticle(content);	
			Questions_with_sentence questions = nlpProcess(article);
			questionNum += questions.output.size();
//			StringBuilder builder1 = new StringBuilder();
//			for(Main.Questions_with_sentence.Question q : questions.output)
//			{
//				builder1.append("S:"+q.sentence+"\n"+"Q:"+q.question+"\n");
//			}
//			System.err.print(builder1.toString());
//		
			writeFile(gson.toJson(questions),filepaths[i]);
			System.out.print(gson.toJson(questions));
	    }
	    System.out.println(questionNum);
	
	}
	
	
	public Questions_with_sentence nlpProcess(Article article) throws IOException{
	
		NLPUtil nlpUtil = NLPUtil.getInstance();
		
		nlpUtil.processStandardArticle(article);
		
		ArrayList<Sentence> list = article.getSentenceList();
//		Questions questions = new Questions();
		Questions_with_sentence questions = new Questions_with_sentence();
		for(int i = 0 ; i < list.size() ; i++)
		{
			Sentence s = list.get(i);
			System.out.println("S:"+s.getContent());
			for(Question q : s.getQuestionList())
			{
				System.out.println("Q:"+q.getContent());
				questions.addQuestion(q.getContent(),s.getContent());
			}
		}
		return questions;
		
	}
	public Article createArticle(String content){
		
		Article article = new Article();
		article.setContent(content);
		article.setTitle("Reading 502");
		article.setType("STANDARD");
		Date now = new Date();
		article.setCreateTime(now);
		article.setUpdateTime(now);
		
		return article;
		
	}
	public void writeFile(String s,String filepath) throws IOException{
		Path p = Paths.get(filepath);
		String fileRoot = p.getParent().toString();
		String fileName = p.getFileName().toString().replace(".txt", "");
		String outputPath = Paths.get(fileRoot).resolve(fileName+"_output.txt").toString();
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
		writer.write(s);
		writer.flush();
		writer.close();
	}
	public static  String readFile(String filepath) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		StringBuilder builder = new StringBuilder();
		String line = "";
		while((line = reader.readLine()) != null){
			builder.append(line);
		}
		reader.close();
		return builder.toString();
		
	}
	
	private class Questions{
		ArrayList<Question> output = new ArrayList<Question>(); 
		private class Question{
			String question = "";
		}
		public void addQuestion(String q){
			Question question = new Question();
			question.question = q;
			output.add(question);
		}
	}
	
	private class Questions_with_sentence{
		ArrayList<Question> output = new ArrayList<Question>(); 
		private class Question{
			String sentence = "";
			String question = "";
		}
		public void addQuestion(String q,String s){
			Question question = new Question();
			question.question = q;
			question.sentence = s;
			output.add(question);
		}
	}
	public static void main(String[] args) throws Exception {
			
//			System.setErr(new PrintStream(new OutputStream(){
//			@Override
//			public void write(int arg0) throws IOException{}
//			}));
//			System.setOut(new PrintStream(new OutputStream() {
//			
//			@Override
//			public void write(int arg0) throws IOException {
//				// TODO Auto-generated method stub
//				
//			}
//		}));
			if(args == null)
				throw new Exception("no argument");
			String pathEntry = readFile(args[0]);
			String []filepaths = pathEntry.split(",");
			
				new Main(filepaths);
	
//			String s = "Do Mrs. Richardson love ruby like Mrs. ?";
//			String inputStr = s;
//		    String patternStr = "(Miss|Mr.|Mrs.|Ms.)\\s?$";
//		    Pattern pattern = Pattern.compile(patternStr);
//		    Matcher matcher = pattern.matcher(inputStr);
//		    boolean matchFound = matcher.find();
//		    
//			System.out.print(matchFound+"|"+matcher.replaceAll(""));
	}
	
	
}

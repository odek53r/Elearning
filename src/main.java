import java.util.ArrayList;
import java.util.Date;

import edu.ntnu.kdd.elearn.server.nlp.NLPUtil;
import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Question;
import edu.ntnu.kdd.elearn.shared.model.Sentence;


public class main {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Article article = new Article();
		article.setContent("It¡¦s lunch break time. Jimmy and Kelly are having their lunch in the classroom. ¡§Kelly, do you know our school is going to have several contests?¡¨ said Jimmy. ¡§What contests?¡¨ asked Kelly. ¡§It¡¦s for our school library,¡¨ explained Jimmy. ¡§Our school library will reopen next month. Now our school is planning different activities for this event, such as poster contest, writing contest, and on-line reading contest. All these contests are about reading.¡¨ ¡§Hmmm¡K I think I can join both the poster contest and the writing contest,¡¨ said Kelly. ¡§But we can only enter one contest at a time,¡¨ replied Jimmy. ¡§The winner can get a 500-dollar gift certificate to Brain Enrich Bookstore and Panda stationary store. You can buy many things with the certificate in the two stores.¡¨ ¡§I am much more interested now. Wait! ¡¨Kelly stopped. ¡§Which contest are you going to join? Your drawing is much better than mine. If you join the poster contest, I will have a small chance of winning.¡¨ Jimmy smiled and said,¡¨ And you are a very excellent writer. Why don¡¦t you join the writing contest and I join the poster contest?¡¨");
		article.setTitle("Reading XXX");
		article.setType("STANDARD");
		
		Date now = new Date();
		article.setCreateTime(now);
		article.setUpdateTime(now);		
		
		NLPUtil nlpUtil = NLPUtil.getInstance();
		nlpUtil.processStandardArticle(article);
		
		ArrayList<Sentence> list = article.getSentenceList();
		for(int i = 0 ; i < list.size() ; i++)
		{
			Sentence s = list.get(i);
			System.out.println("S:"+s.getContent());
			for(Question q : s.getQuestionList())
			{
				System.out.println("Q:"+q.getContent());
			}
			System.out.println();
			System.out.println();
			System.out.println();
			System.out.println();
		}

	}

}

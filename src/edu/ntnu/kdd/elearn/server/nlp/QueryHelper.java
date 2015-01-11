package edu.ntnu.kdd.elearn.server.nlp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.ntnu.kdd.elearn.shared.model.Article;
import edu.ntnu.kdd.elearn.shared.model.Query;
import edu.ntnu.kdd.elearn.shared.model.QueryToken;
import edu.ntnu.kdd.elearn.shared.model.Sentence;
import edu.ntnu.kdd.elearn.shared.model.Word;
import edu.ntnu.kdd.elearn.shared.model.QueryToken.Type;

public class QueryHelper {

	public Query buildQuery(String queryString) {

		String temp[] = queryString.split(" ");
		List<QueryToken> tokenList = new ArrayList<QueryToken>();

		for (String token : temp) {

			QueryToken queryToken = createQueryToken(token);
			tokenList.add(queryToken);

		}
		Query query = new Query();
		query.setTokenList(tokenList);
		return query;

	}

	public QueryToken createQueryToken(String tokenString) {

		String temp[] = tokenString.split(":");
		QueryToken result = null;

		if (temp.length == 2 && !temp[0].equals("") && !temp[1].equals("")) {

			if (temp[0].toLowerCase().equals("vb")) {
				try {
					result = new QueryToken();
					result.setType(Type.VERBCOUNT);
					result.setIntContent(Integer.parseInt(temp[1]));

				} catch (NumberFormatException e) {
					int length = tokenString.length();
					if (tokenString.matches("[A-Za-z]{" + length + "}")) {

						result = new QueryToken();
						result.setType(Type.WORD);
						result.setStringContent(tokenString.toLowerCase());
					}
				}
			} else if (temp[0].toLowerCase().equals("pos")) {

				if (temp[1].matches("[A-Z]+")) {

					result = new QueryToken();
					result.setType(Type.POS);
					result.setStringContent(temp[1]);
				}

			}

		} else {

			int length = tokenString.length();

			if (tokenString.matches("[A-Za-z,]{" + length + "}")) {

				result = new QueryToken();
				result.setType(Type.WORD);
				result.setStringContent(tokenString.toLowerCase());
			}

		}

		return result;
	}

	public boolean isMatch(Sentence sentence, Query q) {

		StringBuilder tokenStringBuilder = new StringBuilder();
		StringBuilder targetStringBuilder = new StringBuilder();
		List<QueryToken> tokenList = q.getTokenList();

		for (int j = 0; j < tokenList.size(); j++) {
			QueryToken token = tokenList.get(j);

			if (token.getType() == QueryToken.Type.VERBCOUNT) {
				int verbCount = token.getIntContent();
				tokenStringBuilder.append("(.*<pos:VB.>){" + verbCount+","+verbCount + "} ");

			} else if (token.getType() == QueryToken.Type.WORD) {
				tokenStringBuilder.append(".*" + token.getStringContent()
						+ ".*");

			}
		}

		for (Word word : sentence.getWordList()) {
			targetStringBuilder.append(word.getOriginal().toLowerCase());
			targetStringBuilder.append("<pos:"+word.getPos()+"> ");
		}
		
		String target = targetStringBuilder.toString();
		String rule = tokenStringBuilder.toString();
		
		

		return target.matches(rule);
	}

}

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class PageScraper {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		List<Game> games = new ArrayList<Game>();
		
		try {
			
//			for (int a = 1; a < 4720; a++) {
			for (int a = 4685; a < 4686; a++) {
				
				//System.out.println("Scanning file: " + "game" + a + ".html");
				
				//Load the document
				String docString = FileUtils.readFileToString(new File("game" + a + ".html"));
				Document doc = Jsoup.parse(docString);
				
				//***********************
				//Getting game data
				
				//Get the title element
				Element title = doc.getElementById("game_title");
				title = title.child(0);
				String titleText = title.text();
				
				//Get the show number
				Matcher showNumM = Pattern.compile("\\d+").matcher(titleText);
				showNumM.find();
				String showNum = showNumM.group().replaceAll("<i>", "").replaceAll("</i>", "");
				
				//Get the date
				Matcher dateMatcher = Pattern.compile(" - (.+$)").matcher(titleText);
				dateMatcher.find();
				String stringDate = dateMatcher.group(1).replaceAll("<i>", "").replaceAll("</i>", "");
				SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
				Date d = 	sdf.parse(stringDate);
				
				//Create the game
				Game g = new Game(showNum);
				g.airDate = d;
				
				
				//***********************
				//Getting Contestant data
				
				Elements cE = doc.getElementsByClass("contestants");
				for (Element e : cE) {
					
					//Get the name
					String name = e.child(0).text().replaceAll("<i>", "").replaceAll("</i>", "");
					
					//TODO get the job
					//TODO get the location
					
					Contestant c = new Contestant();
					c.name = name;
					
					g.contestants.add(c);
					
				}
				
	
				//***********************
				//Getting the jeopardy round data
				
				Round jr = g.round1;
				
				//Find the right section
				Element jre = doc.getElementById("jeopardy_round");
				
				//Check that there is any data at all
				if (jre == null) {
					System.out.println("FIRST\tSkipping file: game" + a + ".html which holds show number: " + showNum);
					continue;
				}
				
				//Get the category names
				Elements catEs = jre.getElementsByClass("category_name");
				for (Element catE : catEs) {
					
					String catName = catE.text().replaceAll("<i>", "").replaceAll("</i>", "");
					
					Category cat = new Category();
					cat.name = catName;
					
					jr.categories.add(cat);
					
				}
				
				//******************
				//Get the questions for round 1
				
				
				//First we get the right table
				Elements rowElements = jre.getElementsByClass("round").first().children().first().children();
				
				//We go by rows here. ignoring the first row (the title elements)
				for (int i = 1; i < 5; i++) {
					
					//Get the current row, and associated elements
					Element rowElement = rowElements.get(i);
					Elements clueElements = rowElement.getElementsByClass("clue");
					
					//Now we go through each clue element
					for (int j = 0; j < jr.categories.size(); j++) {
						
						//Check if this question never was shown (i.e. it's a blank spot)
						if (clueElements.get(j).getElementsByClass("clue_text").size() == 0) {
							continue;
						}
						
						
						Element clueTextElement = clueElements.get(j).getElementsByClass("clue_text").first();
						Element clueValueElement = clueElements.get(j).select("[class^=clue_value]").first();
						Element clueAnswerElement = clueElements.get(j).select("[onmouseover]").first();
						
						Question q = new Question();
						
						//Get the clue
						q.clue = clueTextElement.text().replaceAll("<i>", "").replaceAll("</i>", "");
						
						//Get who answered correctly
	//					Matcher winnerMatcher = Pattern.compile("right\">(.+)</td").matcher(clueAnswerElements.get(j).attr("onmouseover"));
	//					winnerMatcher.find();  //This will find the name of the correct answerer
						
						//Get the correct answer
						Matcher answerMatcher = Pattern.compile("correct_response\">(.+)</em>").matcher(clueAnswerElement.attr("onmouseover"));
						answerMatcher.find();
						q.correctAnswer = answerMatcher.group(1).replaceAll("<i>", "").replaceAll("</i>", "");
						
						
						//Get the value
						//Handle daily doubles
						if (clueValueElement.text().replaceAll("<i>", "").replaceAll("</i>", "").startsWith("DD")) {
							
							q.dailyDouble = true;
							q.value = Integer.parseInt(clueValueElement.text().replaceAll("<i>", "").replaceAll("</i>", "").substring(5).replaceAll(",", ""));
							
						}
						else {
							
							q.value = Integer.parseInt(clueValueElement.text().replaceAll("<i>", "").replaceAll("</i>", "").substring(1).replaceAll(",", ""));
							
						}
						
						//Get the right category and add to that
						Category cat = g.round1.categories.get(j);
						cat.questions.add(q);
						
					}
					
					
					
				}
				
				
				//Now we get the scores for the round
				Elements jrscores = jre.select("h3:containsOwn(Scores at the end of the Jeopardy! Round:)").first().nextElementSibling().select("tr").first().nextElementSibling().select("[class^=score]");
				
				//Scores appear in reverse order from the names listed at beginning of the document
				for (int l = 0; l < 3; l++) {
					
					int score = Integer.parseInt(jrscores.get(2 - l).text().replaceAll(",|\\$", ""));
					g.round1.scoresAtEnd.put(g.contestants.get(l), score);
					
//					System.out.println(g.contestants.get(l).name + " " + score);
					
				}
				
//				for (Contestant myp : g.contestants) {
//					System.out.println(myp.name + " " + g.round1.scoresAtEnd.get(myp));
//				}
				
	
				//***********************
				//Getting the double jeopardy round data
				
				Round djr = g.round2;
				
				//Find the right section
				Element djre = doc.getElementById("double_jeopardy_round");
				
				//Check that there is any data at all
				if (djre == null) {
					System.out.println("DOUBLE\tSkipping file: game" + a + ".html which holds show number: " + showNum);
					continue;
				}
				
				//Get the category names
				Elements dcatEs = djre.getElementsByClass("category_name");
				for (Element catE : dcatEs) {
					
					String catName = catE.text().replaceAll("<i>", "").replaceAll("</i>", "");
					
					Category cat = new Category();
					cat.name = catName;
					
					djr.categories.add(cat);
					
				}
				
				//******************
				//Get the questions for round 2
				
				
				//First we get the right table
				Elements drowElements = djre.getElementsByClass("round").first().children().first().children();
				
				//We go by rows here. ignoring the first row (the title elements)
				for (int i = 1; i < 5; i++) {
					
					//Get the current row, and associated elements
					Element rowElement = drowElements.get(i);
					Elements clueElements = rowElement.getElementsByClass("clue");
					
					//Now we go through each clue element
					for (int j = 0; j < djr.categories.size(); j++) {
						
						//Check if this question never was shown (i.e. it's a blank spot)
						if (clueElements.get(j).getElementsByClass("clue_text").size() == 0) {
							continue;
						}
						
						
						Element clueTextElement = clueElements.get(j).getElementsByClass("clue_text").first();
						Element clueValueElement = clueElements.get(j).select("[class^=clue_value]").first();
						Element clueAnswerElement = clueElements.get(j).select("[onmouseover]").first();
						
						
						Question q = new Question();
						
						//Get the clue
						q.clue = clueTextElement.text().replaceAll("<i>", "").replaceAll("</i>", "");
						
						//Get who answered correctly
	//					Matcher winnerMatcher = Pattern.compile("right\">(.+)</td").matcher(clueAnswerElements.get(j).attr("onmouseover"));
	//					winnerMatcher.find();  //This will find the name of the correct answerer
						
						//Get the correct answer
						Matcher answerMatcher = Pattern.compile("correct_response\">(.+)</em>").matcher(clueAnswerElement.attr("onmouseover"));
						answerMatcher.find();
						q.correctAnswer = answerMatcher.group(1).replaceAll("<i>", "").replaceAll("</i>", "");
						
						
						//Get the value
						//Handle daily doubles
						if (clueValueElement.text().replaceAll("<i>", "").replaceAll("</i>", "").startsWith("DD")) {
							
							q.dailyDouble = true;
							q.value = Integer.parseInt(clueValueElement.text().replaceAll("<i>", "").replaceAll("</i>", "").substring(5).replaceAll(",", ""));
							
						}
						else {
							
							q.value = Integer.parseInt(clueValueElement.text().replaceAll("<i>", "").replaceAll("</i>", "").substring(1).replaceAll(",", ""));
							
						}
						
						//Get the right category and add to that
						Category cat = g.round2.categories.get(j);
						cat.questions.add(q);
						
					}
					
					
					
				}
				
				
				//*****************************
				// Getting the final jeopardy round
				
				Round fin = g.round3;
				
				//Find the right section
				Element finE = doc.getElementById("final_jeopardy_round");
				
				//Check that there is any data at all
				if (finE == null) {
					System.out.println("FINAL\tSkipping file: game" + a + ".html which holds show number: " + showNum);
					continue;
				}
				
				//Get the category
				String finalCatName = finE.getElementsByClass("category_name").first().text().replaceAll("<i>", "").replaceAll("</i>", "");
				Category finCat = new Category();
				finCat.name = finalCatName;
				fin.categories.add(finCat);
				
				//Get the question info
				String finClue = doc.getElementById("clue_FJ").text().replaceAll("<i>", "").replaceAll("</i>", "");
				
				String attrString = finE.select("[onmouseover]").first().attr("onmouseover");
				Matcher finAnswerMatcher = Pattern.compile("correct_response\\\\\">(.+)</em>").matcher(attrString);
				finAnswerMatcher.find();
				String finCorrectAnswer = finAnswerMatcher.group(1).replaceAll("<i>", "").replaceAll("</i>", "");
				
				Question finQ = new Question();
				finQ.clue = finClue;
				finQ.correctAnswer = finCorrectAnswer;
				finQ.value = 0;
				
				//Add it to the category
				finCat.questions.add(finQ);
				
				//Add the game to the list
				games.add(g);
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Print this to a file.
		//makeSimpleCSV(games);
		

	}
	
	public static void makeSimpleCSV(List<Game> games) throws IOException {
		
		for(Game g : games) {
			
			String gameOutput = "";
			
			String line = "";
			
			line += g.showNumber;
			
			SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
			line += "," + sdf.format(g.airDate);
			
			//Do the first round
			
			String lineR1 = line;
			lineR1 += "," + g.round1.roundName;
			
			for (Category c : g.round1.categories) {
				
				String cLine = lineR1;
				cLine += "," + c.name.replaceAll(",|<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "");
				
				for (Question q : c.questions) {
					
					String qLine = cLine;
					
					qLine += ",$" + q.value;
					qLine += "," + q.clue.replaceAll(",|<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "");
					qLine += "," + q.correctAnswer.replaceAll(",|<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "");
					
					gameOutput += qLine + "\n";
				}
				
			}
			
			//Do the second round
			
			String lineR2 = line;
			lineR2 += "," + g.round2.roundName;
			
			for (Category c : g.round2.categories) {
				
				String cLine = lineR2;
				cLine += "," + c.name.replaceAll(",|<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "");
				
				for (Question q : c.questions) {
					
					String qLine = cLine;
					
					qLine += ",$" + q.value;
					qLine += "," + q.clue.replaceAll(",|<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "");
					qLine += "," + q.correctAnswer.replaceAll(",|<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "");
					
					gameOutput += qLine + "\n";
				}
				
			}
			
			//Do the final round
			
			String lineR3 = line;
			lineR3 += "," + g.round3.roundName;
			
			Category fCat = g.round3.categories.get(0);
			lineR3 += "," + fCat.name.replaceAll(",|<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "");
			
			lineR3 += ",$" + fCat.questions.get(0).value;
			lineR3 += "," + fCat.questions.get(0).clue.replaceAll(",|<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "");
			lineR3 += "," + fCat.questions.get(0).correctAnswer.replaceAll(",|<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "");
			
			gameOutput += lineR3;
			
			
			//Finally, store the data
			FileUtils.writeStringToFile(new File("BasicData.csv"), gameOutput + "\n", true);
			
			//System.out.println("Saved data for game number " + g.showNumber);
			
		}
		
	}

}

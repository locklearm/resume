import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class PageScraper2 {

	public static void main(String[] args) throws IOException {
		
//		int gameID = 2345;
		
		List<Game> games = new ArrayList<Game>();
		
		for (int gameID = 1; gameID < 4720; gameID++) {
//		for (int gameID = 2345; gameID < 2346; gameID++) {
			String docString = FileUtils.readFileToString(new File("game" + gameID + ".html"));
			Document gameDoc = Jsoup.parse(docString);
			PageScraper2 scraper = new PageScraper2();
			Game g = scraper.getGame(gameDoc, gameID);
			games.add(g);
		}
		
		FileUtils.writeStringToFile(new File("HeatMaps.txt"), DataFormatter.buildHeatMaps(games) + "\n", true);
		
//		FileUtils.writeStringToFile(new File("DDAverages.csv"), DataFormatter.calcDDLocationAverages(games) + "\n", true);
		
		FileUtils.writeStringToFile(new File("ScoreData.csv"), DataFormatter.buildRoundScoreCSV(games) + "\n", true);
		
		FileUtils.writeStringToFile(new File("RowColumnNumStringsWithFrequency.txt"), DataFormatter.generateRowAndColumnNumStrings(games) + "\n", true);
		
		for (Entry<String, BufferedImage> e : DataFormatter.buildImages(games).entrySet()) {
			System.out.println("Saving image: " + e.getKey());
			File f = new File(e.getKey() + ".png");
			ImageIO.write(e.getValue(), "PNG", f);
			
		}
		
//		System.out.println("You are not saving anything to file right now.");
		
	}
	
	public Game getGame(Document gamePage, int gameID) {
		
		
		//Instantiate a game
		Game g = new Game();
		g.gameID = gameID;
		
		//Get the show number and air date
		this.collectBasicGameData(g, gamePage);
		
		//Get the contestant data
		this.collectPlayerData(g, gamePage);
		
		//Get the jeopardy round data
		this.collectJeopardyRoundData(g, gamePage);
		
		//Get the double jeopardy round data
		this.collectDoubleJeopardyRoundData(g, gamePage);
		
		//Get the final jeopardy round data
		this.collectFinalJeopardyRoundData(g, gamePage);
		
		return g;
		
	}

	//Show Number, Air Date
	public void collectBasicGameData(Game g, Document gamePage) {
		
		//Get the text we need to parse
		String t = gamePage.getElementById("game_title").children().first().text();
		
		//Get the show number
		Matcher showNumMatcher = Pattern.compile(" #(\\d+) ").matcher(t);
		showNumMatcher.find();
		g.showNumber = Integer.parseInt(showNumMatcher.group(1));
		
		//Get the date
		Matcher dateMatcher = Pattern.compile(" - (.+$)").matcher(t);
		dateMatcher.find();
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
		try {
			g.airDate = sdf.parse(removeHTMLTags(dateMatcher.group(1)));
		} catch (ParseException e) {
			g.airDate = null;
		}
		
	}

	//Player name
	public void collectPlayerData(Game g, Document gamePage) {
		
		//Get the player elements
		Elements playerEls = gamePage.getElementsByClass("contestants");
		
		//Check that the data was there
		if (playerEls == null || playerEls.size() == 0) {
			System.out.println("Could get player data for gameID " + g.gameID);
			return;
		}
		
		//For each elements
		for (Element playerEl : playerEls) {
			
			//Get the name
			String name = playerEl.children().first().text();
			name = removeHTMLTags(name);
			
			//Create the contestant
			Contestant c = new Contestant();
			c.name = name;
			
			g.players.add(c);
			
		}
		
	}
	
	public void collectJeopardyRoundData(Game g, Document gamePage) {
		
		//Get the correct section of the document
		Element jrEl = gamePage.getElementById("jeopardy_round");
		
		//Check that the data is there
		if (jrEl == null) {
			System.out.println("No round1 data for gameID " + g.gameID);
			return;
		}
		
		//Get the category names
		this.collectCategorieNames(g.jRound, jrEl);
		
		//Collect the tiles and insert them into this round
		List<Tile> tiles = this.collectTiles(jrEl);
		this.insertTilesInCategories(tiles, g.jRound);
		
		//Collect the round scores
		this.collectRoundScores(g.jRound, jrEl);
		
	}
	
	public void collectDoubleJeopardyRoundData(Game g, Document gamePage) {
		
		//Get the correct section of the document
		Element djrEl = gamePage.getElementById("double_jeopardy_round");
		
		//Check that the data is there
		if (djrEl == null) {
			System.out.println("No round2 data for gameID " + g.gameID);
			return;
		}
		
		//Get the category names
		this.collectCategorieNames(g.djRound, djrEl);
		
		//Collect the tiles and insert them into this round
		List<Tile> tiles = this.collectTiles(djrEl);
		this.insertTilesInCategories(tiles, g.djRound);
		
		//Collect the round scores
		this.collectRoundScores(g.djRound, djrEl);
		
	}
	
	public void collectFinalJeopardyRoundData(Game g, Document gamePage) {
		
		//Get the correct section of the document
		Element fjrEl = gamePage.getElementById("final_jeopardy_round");
		
		//Check that the data is there
		if (fjrEl == null) {
			System.out.println("No round3 data for gameID " + g.gameID);
			return;
		}
		
		//Get the category name
		
		//Get the clue and answers
		
		//Collect the round scores
		this.collectFinalScores(g, fjrEl);
		
		
		
	}
	
	public void collectCategorieNames(Round r, Element roundEl) {
		
		//Get the correct element
		Elements categoryEls = roundEl.getElementsByClass("category_name");
		
		for (int i = 0; i < categoryEls.size(); i++) {
			
			Element catE = categoryEls.get(i);
			
			String categoryName = catE.text();
			categoryName = removeHTMLTags(categoryName);
			
			Category cat = new Category();
			cat.name = categoryName;
			
			r.categories.put(i, cat);
		}
		
	}
	
	public List<Tile> collectTiles(Element roundEl) {
		
		//Get all the clue elements in this round
		Elements clueEls = roundEl.getElementsByClass("clue");
		
		//Now, for each clue, we create a tile
		List<Tile> tiles = new ArrayList<Tile>();
		for (Element clueEl : clueEls) {
			
			Tile t = new Tile();

			//First we get the clue position
			Element cluePosEl = clueEl.getElementsByClass("clue_unstuck").first();
			if (cluePosEl == null) {
				continue;
			}
			String positionInfo = cluePosEl.id();
			Matcher positionMatcher = Pattern.compile("\\d{1}").matcher(positionInfo);
			if (positionMatcher.find()) {
				t.col = Integer.parseInt(positionMatcher.group()) - 1;
				if (positionMatcher.find()) {
					t.row = Integer.parseInt(positionMatcher.group()) - 1;
				}
				else {
					continue;
				}
			}
			else {
				//If we can't get the position, we can't use this tile
				continue;
			}
			
			//Now we get the clue text
			t.clue = clueEl.getElementsByClass("clue_text").first().text();
			t.clue = removeHTMLTags(t.clue);
			
			//Get the clue value
			Element clueValueEl = clueEl.select("[class^=clue_value]").first();
			String valueText = clueValueEl.text();
			Matcher valueMatcher = Pattern.compile("(\\d+)").matcher(valueText);
			valueMatcher.find();
			t.value = Integer.parseInt(valueMatcher.group(1));
			
			//Handle daily doubles
			if (valueText.startsWith("DD")) {
				t.isDailyDouble = true;
			}
			
			//Get the correct answer
			Element clueAnswerEl = clueEl.select("[onmouseover]").first();
			String answerText = clueAnswerEl.attr("onmouseover");
			Matcher answerMatcher = Pattern.compile("correct_response\">(.+)</em>").matcher(answerText);
			if (answerMatcher.find()) {
				t.correctAnswer = answerMatcher.group(1);
				t.correctAnswer = removeHTMLTags(t.correctAnswer);
			}
			
			//And get the person who answered correctly (if any)
			Matcher winnerMatcher = Pattern.compile("right\">(.+?)</td>").matcher(answerText);
			if (winnerMatcher.find()) {
				t.winner = winnerMatcher.group(1);
				t.winner = removeHTMLTags(t.winner);
			}
			else {
				t.winner = "NO_WINNER";
			}
			
			//Get incorrect answers, if any
			Matcher wrongAnswerMatcher = Pattern.compile("wrong\">(.+?)</td>").matcher(answerText);
			while (wrongAnswerMatcher.find()) {
				
				String person = wrongAnswerMatcher.group(1);
				person = person.split(" ")[0];
				
				if (person.equals("Triple Stumper")) {
					continue;
				}
				
				Matcher wrongAnswerTextMatcher = Pattern.compile("\\(" + person +":(.+?)\\)").matcher(answerText);
				if (wrongAnswerTextMatcher.find()) {
					String personAnswer = wrongAnswerTextMatcher.group(1);
					
					t.wrongAnswers.put(person, personAnswer);
					
				}
				
			}
			
			//Get the order of the clue
//			Element clueOrderEl = clueEl.getElementsByClass("clue_order_number").first().children().first();
//			t.questionOrder = Integer.parseInt(clueOrderEl.text());
			String clueOrderS = removeHTMLTags(clueEl.getElementsByClass("clue_order_number").text());
			t.questionOrder = Integer.parseInt(clueOrderS);
			
			
			//TODO continue
			
			
			
			//Add the tile to the list we are returning
			tiles.add(t);
			
		}
		
		
		return tiles;
	}
	
	public void insertTilesInCategories(List<Tile> tiles, Round r) {
		
		//Go through each tile
		for (Tile t : tiles) {
			
			//The column is also the category index
			r.categories.get(t.col).tiles.put(t.row, t);
			
		}
		
	}
	
	public void collectRoundScores(Round r, Element roundEl) {
		
		//Find the header element for end of round scores
		Element headerEl = roundEl.select("h3:containsOwn(Scores at the end)").first();
		
		//If it's there
		if (headerEl != null) {
			
			
			//We get the list of names
			Elements playerNickEls = headerEl.nextElementSibling().getElementsByClass("score_player_nickname");
			
			if (playerNickEls != null && playerNickEls.size() != 0) {
				
				
				//Get the score elements
				Elements scoreEls = playerNickEls.first().parent().nextElementSibling().select("[class^=score_]");
				
				
				for (int i = 0; i < playerNickEls.size(); i++) {
					
					Element playerNickEl = playerNickEls.get(i);
					
					String playerNick = playerNickEl.text();
					playerNick = removeHTMLTags(playerNick);
					
					String playerScore = scoreEls.get(i).text().replaceAll(",|\\$", "");
					playerScore = removeHTMLTags(playerScore);
					
					r.scoresAtEnd.put(playerNick, Integer.parseInt(playerScore));
					
				}
				
				
			}
			
			
		}
		
		
	}
	
	public void collectFinalScores(Game g, Element roundEl) {
		
		//Find the header element for end of round scores
		Element headerEl = roundEl.select("h3:containsOwn(Final scores)").first();
		
		//If it's there
		if (headerEl != null) {
			
			
			//We get the list of names
			Elements playerNickEls = headerEl.nextElementSibling().getElementsByClass("score_player_nickname");
			
			if (playerNickEls != null && playerNickEls.size() != 0) {
				
				
				//Get the score elements
				Elements scoreEls = playerNickEls.first().parent().nextElementSibling().select("[class^=score_]");
				
				
				for (int i = 0; i < playerNickEls.size(); i++) {
					
					Element playerNickEl = playerNickEls.get(i);
					
					String playerNick = playerNickEl.text();
					playerNick = removeHTMLTags(playerNick);
					
					String playerScore = scoreEls.get(i).text().replaceAll(",|\\$", "");
					playerScore = removeHTMLTags(playerScore);
					
					g.finalScores.put(playerNick, Integer.parseInt(playerScore));
					
					
				}
				
				
			}
			
			
		}
		
	}
	
	public static String removeHTMLTags(String s) {
		
		return s.replaceAll("<(\"[^\"]*\"|'[^']*'|[^'\">])*>", "");
		
	}

	public class Game {
		
		public int gameID = -1;
		
		public int showNumber = -1;
		public Date airDate = new Date(0l);
		
		public ArrayList<Contestant> players = new ArrayList<Contestant>();
		
		public Round jRound = new Round("Jeopardy!");
		public Round djRound = new Round("Double Jeopardy!");
		
		public HashMap<String, Integer> finalScores = new HashMap<String, Integer>();
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Game [\n\tshowNumber=" + showNumber + ", \n\tairDate=" + airDate + ", \n\tplayers=" + players
					+ ", \n\tjRound=" + jRound + ", \n\tdjRound=" + djRound + "\n\tfinalScores=" + finalScores + "]";
		}
		
		
		
	}
	
	public class Contestant {
		
		public String name = "UNKNOWN";
		
		public String getFirstName() {
			return this.name.split(" ")[0];
		}

		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Contestant [name=" + name + "]";
		}
		
		
		
	}
	
	public class Round {
		
		public String roundName;
		
		public HashMap<Integer, Category> categories = new HashMap<Integer, Category>();
		
		public HashMap<String, Integer> scoresAtEnd = new HashMap<String, Integer>();
		
		public Round(String name) {
			this.roundName = name;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String s = "Round [\n\t\troundName=" + roundName + ", \n\t\tcategories=";
			
			//s += categories;
			for (Entry<Integer, Category> e : categories.entrySet()) {
				
				s+= "\n\t\t\t" + e;
				
			}
			
			s+= ", \n\t\tscoresAtEnd="
					+ scoresAtEnd + "]";
			
			return s;
		}
		
		
		
	}
	
	
	
	public class Category {
		
		public String name = "UNKNOWN";
		
		public HashMap<Integer, Tile> tiles = new HashMap<Integer, Tile>();

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			String s = "Category [name=" + name + ", tiles=";
//			+ tiles;
			for (Tile t : tiles.values()) {
				s += "\n\t\t\t\t" + t;
			}
			s += "]";
			return s;
		}
		
		
		
	}
	
	public class Tile {
		
		public int value = -1;
		public String clue = "UNKNOWN";
		public String correctAnswer = "UNKNOWN";
		
		public boolean isDailyDouble = false;
		
		public int col = -1;
		public int row = -1;
		public int questionOrder = -1;
		
		public String winner = null;
		public Map<String, String> wrongAnswers = new HashMap<String, String>(); //Key = playername, Value = their answer
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Tile [\tclue=" + clue 
					+ ", \n\t\t\t\t\tcorrectAnswer=" + correctAnswer 
					+ "\n\t\t\t\t\torderNum=" + questionOrder
					+ "]";
		}
		
		
		
	}
	
	
	public static class DataFormatter {
		
		public static String buildHeatMaps(List<Game> games) {
			
			int[][] round1DDMap = {
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0}
					};
			
			int[][] round2DDMap = {
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0}
					}; 
			
			int[][] round1FirstQMap = {
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0}
					};
			
			int[][] round2FirstQMap = {
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0}
					};
			
			int[][] round1LastQMap = {
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0}
					};
			
			int[][] round2LastQMap = {
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0}
					};
			
			for (Game g : games) {
				
				// Handle the first round
				
				for (Category c : g.jRound.categories.values()) {
					for (Tile t : c.tiles.values()) {
						if (t.isDailyDouble) {
							round1DDMap[t.row][t.col]++;
						}
						if (t.questionOrder == 1) {
							round1FirstQMap[t.row][t.col]++;
						}
						if (t.questionOrder == 30) {
							round1LastQMap[t.row][t.col]++;
						}
					}
				}
				
				// Handle the second round
				
				for (Category c : g.djRound.categories.values()) {
					for (Tile t : c.tiles.values()) {
						if (t.isDailyDouble) {
							round2DDMap[t.row][t.col]++;
						}
						if (t.questionOrder == 1) {
							round2FirstQMap[t.row][t.col]++;
						}
						if (t.questionOrder == 30) {
							round2LastQMap[t.row][t.col]++;
						}
					}
				}
				
			}
			
			String result = "";
			
			//Print result
			result += "Jeopardy! Round Daily Double Locations\n";
			for (int i = 0; i < round1DDMap.length; i++) {
				int[] row = round1DDMap[i];
				for (int j = 0; j < row.length; j++) {
					result += row[j] + "\t";
				}
				result += "\n";
			}
			result += "\nJeopardy! Round First Question Locations\n";
			for (int i = 0; i < round1FirstQMap.length; i++) {
				int[] row = round1FirstQMap[i];
				for (int j = 0; j < row.length; j++) {
					result += row[j] + "\t";
				}
				result += "\n";
			}
			result += "\nJeopardy! Round Last Question Locations (When all questions asked)\n";
			for (int i = 0; i < round1LastQMap.length; i++) {
				int[] row = round1LastQMap[i];
				for (int j = 0; j < row.length; j++) {
					result += row[j] + "\t";
				}
				result += "\n";
			}
			

			result += "\n\nDouble Jeopardy! Round Daily Double Locations\n";
			for (int i = 0; i < round2DDMap.length; i++) {
				int[] row = round2DDMap[i];
				for (int j = 0; j < row.length; j++) {
					result += row[j] + "\t";
				}
				result += "\n";
			}
			result += "\nDouble Jeopardy! Round First Question Locations\n";
			for (int i = 0; i < round1FirstQMap.length; i++) {
				int[] row = round2FirstQMap[i];
				for (int j = 0; j < row.length; j++) {
					result += row[j] + "\t";
				}
				result += "\n";
			}
			result += "\nDouble Jeopardy! Round Last Question Locations (When all questions asked)\n";
			for (int i = 0; i < round1LastQMap.length; i++) {
				int[] row = round2LastQMap[i];
				for (int j = 0; j < row.length; j++) {
					result += row[j] + "\t";
				}
				result += "\n";
			}
			
			return result;
		}
		
		public static String calcDDLocationAverages(List<Game> games) {
			
			String result = "";
			
			int[][] DDMap = {
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0},
					{0,0,0,0,0,0}
					}; 
			
			
			int totalDDs = 0;
			
			//Create a sorted list of the games
			TreeMap<Date, Game> sortedGames = new TreeMap<Date, Game>();
			for(Game g : games) {
				
				System.out.println("sorting game " + g.airDate.toString());
				
				sortedGames.put(g.airDate, g);
				
			}
			
			
			for (Game g : sortedGames.values()) {
				
				System.out.println("Calcing averages for game " + g.airDate.toString());
				
				//Handle the first round
				
				for (Category c : g.jRound.categories.values()) {
					for (Tile t : c.tiles.values()) {
						if (t.isDailyDouble) {
							DDMap[t.row][t.col]++;
							totalDDs++;
						}
					}
				}
				
				//Handle the second round
				
				for (Category c : g.djRound.categories.values()) {
					for (Tile t : c.tiles.values()) {
						if (t.isDailyDouble) {
							DDMap[t.row][t.col]++;
							totalDDs++;
						}
					}
				}
			
				//Store the current ratios
				for (int i = 0; i < DDMap.length; i++) {
					int[] row = DDMap[i];
					for (int j = 0; j < row.length; j++) {
						result += ((double)row[j] / (double)totalDDs) + ",";
					}
				}
				result += "\n";
				
			}
			
			
			return result;
			
		}
		
		public static String buildRoundScoreCSV(List<Game> games) {
			
			String result = "";
			
			
			//Add the header
			result += "Show #, Air Date,  Round, Name1, Round Score1, Name2, Round Score2, Name3, Round Score3, Name4, Round Score4\n";
			
			
			for (Game g : games) {
				
				String gameResult = "";
				
				gameResult += g.showNumber + ",";
				gameResult += g.airDate + ",";
				
				//Do round 1
				String round1Result = gameResult;
				
				round1Result += g.jRound.roundName;
				
				for (Entry<String, Integer> e : g.jRound.scoresAtEnd.entrySet()) {
					
					round1Result += "," + e.getKey().replaceAll(",", "") + ",";
					round1Result += e.getValue();
					
				}
				
				result += round1Result + "\n";
				
				//Do round 2
				String round2Result = gameResult;
				
				round2Result += g.djRound.roundName;
				
				for (Entry<String, Integer> e : g.djRound.scoresAtEnd.entrySet()) {
					
					round2Result += "," + e.getKey().replaceAll(",", "") + ",";
					round2Result += e.getValue();
					
				}
				
				result += round2Result + "\n";
				
				//Do final round
				String finalRoundResult = gameResult;
				
				finalRoundResult += "Final Jeopardy!";
				
				for (Entry<String, Integer> e : g.finalScores.entrySet()) {
					
					finalRoundResult += "," + e.getKey().replaceAll(",", "") + ",";
					finalRoundResult += e.getValue();
					
				}
				
				result += finalRoundResult + "\n";
				
			}
			
			
			
			
			
			return result;
			
		}
		
		public static Map<String, BufferedImage> buildImages(List<Game> games) {
			
			Map<String, BufferedImage> results = new HashMap<String, BufferedImage>();
			
			String[] round1Rows = {"", "", "", "", ""};
			String[] round1Cols = {"", "", "", "", "", ""};
			
			String[] round2DD1Rows = {"", "", "", "", ""};
			String[] round2DD2Rows = {"", "", "", "", ""};
			String[] round2DD1Cols = {"", "", "", "", "", ""};
			String[] round2DD2Cols = {"", "", "", "", "", ""};
			
			
			
			for (Game g : games) {
				
				
				Round r1 = g.jRound;
				Round r2 = g.djRound;
				
				
				Tile r1DD = null;
				int r1FinalTileNum = 0;
				Tile r2DD1 = null;
				Tile r2DD2 = null;
				int r2FinalTileNum = 0;
				
				
				for (Category c : r1.categories.values()) {
					for (Tile t : c.tiles.values()) {
						if (t.isDailyDouble) {
							r1DD = t;
						}
						if (t.questionOrder > r1FinalTileNum) {
							r1FinalTileNum = t.questionOrder;
						}
					}
				}
				for (Category c : r2.categories.values()) {
					for (Tile t : c.tiles.values()) {
						if (t.isDailyDouble) {
							if (r2DD1 == null) {
								r2DD1 = t;
							}
							else {
								
								if ( r2DD1.questionOrder < t.questionOrder) {
									r2DD2 = t;
								}
								else {
									r2DD2 = r2DD1;
									r2DD1 = t;
								}
							}
						}
						if (t.questionOrder > r2FinalTileNum) {
							r2FinalTileNum = t.questionOrder;
						}
					}
				}
				
				if (r1FinalTileNum == 30 && r1DD != null) {
					
					round1Rows[r1DD.row] += r1DD.col;
					round1Cols[r1DD.col] += r1DD.row;
					
				}
				
				if (r2FinalTileNum == 30 && r2DD1 != null && r2DD2 != null) {
					
					round2DD1Rows[r2DD1.row] += r2DD1.col;
					round2DD1Cols[r2DD1.col] += r2DD1.row;

					round2DD2Rows[r2DD2.row] += r2DD2.col;
					round2DD2Cols[r2DD2.col] += r2DD2.row;
					
					
				}
				
			}
			
//			int[] color = {
//					(255 << 16) | (0 << 8) | 0,
//					(0 << 16) | (255 << 8) | 0,
//					(0 << 16) | (0 << 8) | 255,
//					(0 << 16) | (0 << 8) | 0,
//					(255 << 16) | (255 << 8) | 255,
//					(255 << 16) | (255 << 8) | 0
//			};
			
			
			//Grayscale
			int[] color = {
					(255 << 16) | (255 << 8) | 255,
					(204 << 16) | (204 << 8) | 204,
					(153 << 16) | (153 << 8) | 153,
					(102 << 16) | (102 << 8) | 102,
					(51 << 16) | (51 << 8) | 51,
					(0 << 16) | (0 << 8) | 0
			};
			
			int black = (0 << 16) | (0 << 8) | 0;
			int white = (255 << 16) | (255 << 8) | 255;
			
			int zoom = 5;
			int zoom2 = 2;
			
			for (int i = 0; i < round1Rows.length; i++) {
				
				int pixCount = round1Rows[i].length();
				
				if (pixCount >= 4) {
					
					int imgDim = (int) Math.sqrt(pixCount);
					int count = 0;

					
//					int zoom = 4;
					
					BufferedImage img = new BufferedImage(imgDim * zoom, imgDim * zoom, BufferedImage.TYPE_INT_RGB);
					
					for (int x = 0; x < imgDim * zoom; x += zoom) {
						for (int y = 0; y < imgDim * zoom; y += zoom) {
							for (int zx = 0; zx < zoom; zx++ ) {
								for (int zy = 0; zy < zoom; zy++ ) {
									img.setRGB(x + zx, y + zy, color[Integer.parseInt("" + round1Rows[i].charAt(count))]);
								}
							}
							count++;
						}
					}
					
					results.put("Round1Row" + i, img);
					
					
					
					BufferedImage longImg = new BufferedImage(6, round1Rows[i].length(), BufferedImage.TYPE_INT_RGB);
					
					for (int t = 0; t < round1Rows[i].length(); t++) {
						
						int[] line = {black, black, black, black, black, black};
						
						line[Integer.parseInt(round1Rows[i].charAt(t) + "")] = white;
						
						for (int w = 0; w < line.length; w++) {
							longImg.setRGB(w, t, line[w]);
						}
						
					}
					
					results.put("Round1LongRow" + i, longImg);
					
					
				}
				
			}
			
			for (int i = 0; i < round1Cols.length; i++) {
				
				int pixCount = round1Cols[i].length();
				
				if (pixCount >= 4) {
					
					int imgDim = (int) Math.sqrt(pixCount);
					int count = 0;

					
//					int zoom = 4;
					
					BufferedImage img = new BufferedImage(imgDim * zoom, imgDim * zoom, BufferedImage.TYPE_INT_RGB);
					
					for (int x = 0; x < imgDim * zoom; x += zoom) {
						for (int y = 0; y < imgDim * zoom; y += zoom) {
							for (int zx = 0; zx < zoom; zx++ ) {
								for (int zy = 0; zy < zoom; zy++ ) {
									img.setRGB(x + zx, y + zy, color[Integer.parseInt("" + round1Cols[i].charAt(count))]);
								}
							}
							count++;
						}
					}
					
					results.put("Round1Col" + i, img);
					
					
					
					BufferedImage longImg = new BufferedImage(5, round1Cols[i].length(), BufferedImage.TYPE_INT_RGB);
					
					for (int t = 0; t < round1Cols[i].length(); t++) {
						
						int[] line = {black, black, black, black, black};
						
						line[Integer.parseInt(round1Cols[i].charAt(t) + "")] = white;
						
						for (int w = 0; w < line.length; w++) {
							longImg.setRGB(w, t, line[w]);
						}
						
					}
					
					results.put("Round1LongCol" + i, longImg);
					
				}
				
			}
			
			for (int i = 0; i < round2DD1Rows.length; i++) {
				
				int pixCount = round2DD1Rows[i].length();
				
				if (pixCount >= 4) {
					
					int imgDim = (int) Math.sqrt(pixCount);
					int count = 0;

					
//					int zoom = 4;
					
					BufferedImage img = new BufferedImage(imgDim * zoom, imgDim * zoom, BufferedImage.TYPE_INT_RGB);
					
					for (int x = 0; x < imgDim * zoom; x += zoom) {
						for (int y = 0; y < imgDim * zoom; y += zoom) {
							for (int zx = 0; zx < zoom; zx++ ) {
								for (int zy = 0; zy < zoom; zy++ ) {
									img.setRGB(x + zx, y + zy, color[Integer.parseInt("" + round2DD1Rows[i].charAt(count))]);
								}
							}
							count++;
						}
					}
					
					results.put("Round2DD1Row" + i, img);
					
					
					
					BufferedImage longImg = new BufferedImage(6, round2DD1Rows[i].length(), BufferedImage.TYPE_INT_RGB);
					
					for (int t = 0; t < round2DD1Rows[i].length(); t++) {
						
						int[] line = {black, black, black, black, black, black};
						
						line[Integer.parseInt(round2DD1Rows[i].charAt(t) + "")] = white;
						
						for (int w = 0; w < line.length; w++) {
							longImg.setRGB(w, t, line[w]);
						}
						
					}
					
					results.put("Round2DD1LongRow" + i, longImg);
					
				}
				
			}
			
			for (int i = 0; i < round2DD1Cols.length; i++) {
				
				int pixCount = round2DD1Cols[i].length();
				
				if (pixCount >= 4) {
					
					int imgDim = (int) Math.sqrt(pixCount);
					int count = 0;

					
//					int zoom = 4;
					
					BufferedImage img = new BufferedImage(imgDim * zoom, imgDim * zoom, BufferedImage.TYPE_INT_RGB);
					
					for (int x = 0; x < imgDim * zoom; x += zoom) {
						for (int y = 0; y < imgDim * zoom; y += zoom) {
							for (int zx = 0; zx < zoom; zx++ ) {
								for (int zy = 0; zy < zoom; zy++ ) {
									img.setRGB(x + zx, y + zy, color[Integer.parseInt("" + round2DD1Cols[i].charAt(count))]);
								}
							}
							count++;
						}
					}
					
					results.put("Round2DD1Col" + i, img);
					
					
					
					BufferedImage longImg = new BufferedImage(5, round2DD1Cols[i].length(), BufferedImage.TYPE_INT_RGB);
					
					for (int t = 0; t < round2DD1Cols[i].length(); t++) {
						
						int[] line = {black, black, black, black, black};
						
						line[Integer.parseInt(round2DD1Cols[i].charAt(t) + "")] = white;
						
						for (int w = 0; w < line.length; w++) {
							longImg.setRGB(w, t, line[w]);
						}
						
					}
					
					results.put("Round2DD1LongCol" + i, longImg);
					
				}
				
			}
			
			for (int i = 0; i < round2DD2Rows.length; i++) {
				
				int pixCount = round2DD2Rows[i].length();
				
				if (pixCount >= 4) {
					
					int imgDim = (int) Math.sqrt(pixCount);
					int count = 0;

					
//					int zoom = 4;
					
					BufferedImage img = new BufferedImage(imgDim * zoom, imgDim * zoom, BufferedImage.TYPE_INT_RGB);
					
					for (int x = 0; x < imgDim * zoom; x += zoom) {
						for (int y = 0; y < imgDim * zoom; y += zoom) {
							for (int zx = 0; zx < zoom; zx++ ) {
								for (int zy = 0; zy < zoom; zy++ ) {
									img.setRGB(x + zx, y + zy, color[Integer.parseInt("" + round2DD2Rows[i].charAt(count))]);
								}
							}
							count++;
						}
					}
					
					results.put("Round2DD2Row" + i, img);
					
				}
				
				
				
				BufferedImage longImg = new BufferedImage(6, round2DD2Rows[i].length(), BufferedImage.TYPE_INT_RGB);
				
				for (int t = 0; t < round2DD2Rows[i].length(); t++) {
					
					int[] line = {black, black, black, black, black, black};
					
					line[Integer.parseInt(round2DD2Rows[i].charAt(t) + "")] = white;
					
					for (int w = 0; w < line.length; w++) {
						longImg.setRGB(w, t, line[w]);
					}
					
				}
				
				results.put("Round2DD2LongRow" + i, longImg);
				
			}
			
			for (int i = 0; i < round2DD2Cols.length; i++) {
				
				int pixCount = round2DD2Cols[i].length();
				
				if (pixCount >= 4) {
					
					int imgDim = (int) Math.sqrt(pixCount);
					int count = 0;

					
//					int zoom = 4;
					
					BufferedImage img = new BufferedImage(imgDim * zoom, imgDim * zoom, BufferedImage.TYPE_INT_RGB);
					
					for (int x = 0; x < imgDim * zoom; x += zoom) {
						for (int y = 0; y < imgDim * zoom; y += zoom) {
							for (int zx = 0; zx < zoom; zx++ ) {
								for (int zy = 0; zy < zoom; zy++ ) {
									img.setRGB(x + zx, y + zy, color[Integer.parseInt("" + round2DD2Cols[i].charAt(count))]);
								}
							}
							count++;
						}
					}
					
					results.put("Round2DD2Col" + i, img);
					
					
					
					BufferedImage longImg = new BufferedImage(5, round2DD2Cols[i].length(), BufferedImage.TYPE_INT_RGB);
					
					for (int t = 0; t < round2DD2Cols[i].length(); t++) {
						
						int[] line = {black, black, black, black, black};
						
						line[Integer.parseInt(round2DD2Cols[i].charAt(t) + "")] = white;
						
						for (int w = 0; w < line.length; w++) {
							longImg.setRGB(w, t, line[w]);
						}
						
					}
					
					results.put("Round2DD2LongCol" + i, longImg);
					
				}
				
			}
			
//			for (int i = 0; i < round1Cols.length; i++) {
//				
//				int pixCount = round1Cols[i].length();
//				
//				if (pixCount >= 4) {
//					
//					int imgDim = (int) Math.sqrt(pixCount);
//					int count = 0;
//					
//					BufferedImage img = new BufferedImage(imgDim, imgDim, BufferedImage.TYPE_INT_RGB);
//					
//					for (int x = 0; x < imgDim; x++) {
//						for (int y = 0; y < imgDim; y++) {
//							img.setRGB(x, y, color[Integer.parseInt("" + round1Cols[i].charAt(count++))]);
//						}
//					}
//					
//					results.put("Round1Col" + i, img);
//					
//				}
//				
//			}
//			
//			for (int i = 0; i < round2DD1Rows.length; i++) {
//				
//				int pixCount = round2DD1Rows[i].length();
//				
//				if (pixCount >= 4) {
//					
//					int imgDim = (int) Math.sqrt(pixCount);
//					int count = 0;
//					
//					BufferedImage img = new BufferedImage(imgDim, imgDim, BufferedImage.TYPE_INT_RGB);
//					
//					for (int x = 0; x < imgDim; x++) {
//						for (int y = 0; y < imgDim; y++) {
//							img.setRGB(x, y, color[Integer.parseInt("" + round2DD1Rows[i].charAt(count++))]);
//						}
//					}
//					
//					results.put("Round2DD1Row" + i, img);
//					
//				}
//				
//			}
//			
//			for (int i = 0; i < round2DD1Cols.length; i++) {
//				
//				int pixCount = round2DD1Cols[i].length();
//				
//				if (pixCount >= 4) {
//					
//					int imgDim = (int) Math.sqrt(pixCount);
//					int count = 0;
//					
//					BufferedImage img = new BufferedImage(imgDim, imgDim, BufferedImage.TYPE_INT_RGB);
//					
//					for (int x = 0; x < imgDim; x++) {
//						for (int y = 0; y < imgDim; y++) {
//							img.setRGB(x, y, color[Integer.parseInt("" + round2DD1Cols[i].charAt(count++))]);
//						}
//					}
//					
//					results.put("Round2DD1Col" + i, img);
//					
//				}
//				
//			}
//			
//			for (int i = 0; i < round2DD2Rows.length; i++) {
//				
//				int pixCount = round2DD2Rows[i].length();
//				
//				if (pixCount >= 4) {
//					
//					int imgDim = (int) Math.sqrt(pixCount);
//					int count = 0;
//					
//					BufferedImage img = new BufferedImage(imgDim, imgDim, BufferedImage.TYPE_INT_RGB);
//					
//					for (int x = 0; x < imgDim; x++) {
//						for (int y = 0; y < imgDim; y++) {
//							img.setRGB(x, y, color[Integer.parseInt("" + round2DD2Rows[i].charAt(count++))]);
//						}
//					}
//					
//					results.put("Round2DD2Row" + i, img);
//					
//				}
//				
//			}
//			
//			for (int i = 0; i < round2DD2Cols.length; i++) {
//				
//				int pixCount = round2DD2Cols[i].length();
//				
//				if (pixCount >= 4) {
//					
//					int imgDim = (int) Math.sqrt(pixCount);
//					int count = 0;
//					
//					BufferedImage img = new BufferedImage(imgDim, imgDim, BufferedImage.TYPE_INT_RGB);
//					
//					for (int x = 0; x < imgDim; x++) {
//						for (int y = 0; y < imgDim; y++) {
//							img.setRGB(x, y, color[Integer.parseInt("" + round2DD2Cols[i].charAt(count++))]);
//						}
//					}
//					
//					results.put("Round2DD2Col" + i, img);
//					
//				}
//				
//			}
			
			return results;
			
		}
		
		public static String generateRowAndColumnNumStrings(List<Game> games) {
			
			String result = "";
			
			//We have
			
			String[] round1Rows = {"", "", "", "", ""};
			String[] round1Cols = {"", "", "", "", "", ""};
			
			String[] round2DD1Rows = {"", "", "", "", ""};
			String[] round2DD2Rows = {"", "", "", "", ""};
			String[] round2DD1Cols = {"", "", "", "", "", ""};
			String[] round2DD2Cols = {"", "", "", "", "", ""};
			
			
			
			for (Game g : games) {
				
				
				Round r1 = g.jRound;
				Round r2 = g.djRound;
				
				
				Tile r1DD = null;
				int r1FinalTileNum = 0;
				Tile r2DD1 = null;
				Tile r2DD2 = null;
				int r2FinalTileNum = 0;
				
				
				for (Category c : r1.categories.values()) {
					for (Tile t : c.tiles.values()) {
						if (t.isDailyDouble) {
							r1DD = t;
						}
						if (t.questionOrder > r1FinalTileNum) {
							r1FinalTileNum = t.questionOrder;
						}
					}
				}
				for (Category c : r2.categories.values()) {
					for (Tile t : c.tiles.values()) {
						if (t.isDailyDouble) {
							if (r2DD1 == null) {
								r2DD1 = t;
							}
							else {
								
								if ( r2DD1.questionOrder < t.questionOrder) {
									r2DD2 = t;
								}
								else {
									r2DD2 = r2DD1;
									r2DD1 = t;
								}
							}
						}
						if (t.questionOrder > r2FinalTileNum) {
							r2FinalTileNum = t.questionOrder;
						}
					}
				}
				
				if (r1FinalTileNum == 30 && r1DD != null) {
					
					round1Rows[r1DD.row] += r1DD.col;
					round1Cols[r1DD.col] += r1DD.row;
					
				}
				
				if (r2FinalTileNum == 30 && r2DD1 != null && r2DD2 != null) {
					
					round2DD1Rows[r2DD1.row] += r2DD1.col;
					round2DD1Cols[r2DD1.col] += r2DD1.row;

					round2DD2Rows[r2DD2.row] += r2DD2.col;
					round2DD2Cols[r2DD2.col] += r2DD2.row;
					
					
				}
				
			}
			
			
			
			
			
			
			
			
			
			
			result += "We have ignored rounds where all tiles haven't been uncovered, so that the data isn't skewed based on player tile preference.  (DD tiles may be preferentially uncovered)\n";
			
			result += "\nRound 1 Daily Double Location Row Number Series\n";
			for (int i = 0; i < round1Rows.length; i++) {
				result += round1Rows[i] + "\n";

				result += "0 count: " + charCounter(round1Rows[i], '0') + "\n";
				result += "1 count: " + charCounter(round1Rows[i], '1') + "\n";
				result += "2 count: " + charCounter(round1Rows[i], '2') + "\n";
				result += "3 count: " + charCounter(round1Rows[i], '3') + "\n";
				result += "4 count: " + charCounter(round1Rows[i], '4') + "\n";
				result += "5 count: " + charCounter(round1Rows[i], '5') + "\n";
				
				result += "\n";
			}
			result += "\nRound 1 Daily Double Location Col Number Series\n";
			for (int i = 0; i < round1Cols.length; i++) {
				result += round1Cols[i] + "\n";

				result += "0 count: " + charCounter(round1Cols[i], '0') + "\n";
				result += "1 count: " + charCounter(round1Cols[i], '1') + "\n";
				result += "2 count: " + charCounter(round1Cols[i], '2') + "\n";
				result += "3 count: " + charCounter(round1Cols[i], '3') + "\n";
				result += "4 count: " + charCounter(round1Cols[i], '4') + "\n";
				result += "5 count: " + charCounter(round1Cols[i], '5') + "\n";
				
				result += "\n";
			}
			result += "\nRound 2 First Daily Double Location Row Number Series\n";
			for (int i = 0; i < round2DD1Rows.length; i++) {
				result += round2DD1Rows[i] + "\n";

				result += "0 count: " + charCounter(round2DD1Rows[i], '0') + "\n";
				result += "1 count: " + charCounter(round2DD1Rows[i], '1') + "\n";
				result += "2 count: " + charCounter(round2DD1Rows[i], '2') + "\n";
				result += "3 count: " + charCounter(round2DD1Rows[i], '3') + "\n";
				result += "4 count: " + charCounter(round2DD1Rows[i], '4') + "\n";
				result += "5 count: " + charCounter(round2DD1Rows[i], '5') + "\n";
				
				result += "\n";
			}
			result += "\nRound 2 First Daily Double Location Col Number Series\n";
			for (int i = 0; i < round2DD1Cols.length; i++) {
				result += round2DD1Cols[i] + "\n";

				result += "0 count: " + charCounter(round2DD1Cols[i], '0') + "\n";
				result += "1 count: " + charCounter(round2DD1Cols[i], '1') + "\n";
				result += "2 count: " + charCounter(round2DD1Cols[i], '2') + "\n";
				result += "3 count: " + charCounter(round2DD1Cols[i], '3') + "\n";
				result += "4 count: " + charCounter(round2DD1Cols[i], '4') + "\n";
				result += "5 count: " + charCounter(round2DD1Cols[i], '5') + "\n";
				
				result += "\n";
			}
			result += "\nRound 2 Second Daily Double Location Row Number Series\n";
			for (int i = 0; i < round2DD2Rows.length; i++) {
				result += round2DD2Rows[i] + "\n";

				result += "0 count: " + charCounter(round2DD2Rows[i], '0') + "\n";
				result += "1 count: " + charCounter(round2DD2Rows[i], '1') + "\n";
				result += "2 count: " + charCounter(round2DD2Rows[i], '2') + "\n";
				result += "3 count: " + charCounter(round2DD2Rows[i], '3') + "\n";
				result += "4 count: " + charCounter(round2DD2Rows[i], '4') + "\n";
				result += "5 count: " + charCounter(round2DD2Rows[i], '5') + "\n";
				
				result += "\n";
			}
			result += "\nRound 2 Second Daily Double Location Col Number Series\n";
			for (int i = 0; i < round2DD2Cols.length; i++) {
				result += round2DD2Cols[i] + "\n";

				result += "0 count: " + charCounter(round2DD2Cols[i], '0') + "\n";
				result += "1 count: " + charCounter(round2DD2Cols[i], '1') + "\n";
				result += "2 count: " + charCounter(round2DD2Cols[i], '2') + "\n";
				result += "3 count: " + charCounter(round2DD2Cols[i], '3') + "\n";
				result += "4 count: " + charCounter(round2DD2Cols[i], '4') + "\n";
				result += "5 count: " + charCounter(round2DD2Cols[i], '5') + "\n";
				
				result += "\n";
			}
			
			
			
			
			
			
			
			
			
			
			
			
			return result;
			
		}
		
		private static int charCounter(String s, char c) {
			
			int count = 0;
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == c) {
					count++;
				}
			}
			return count;
		}
		
	}
	
	
}

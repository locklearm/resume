import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class Game {
	
	public Round round1;
	public Round round2;
	public Round round3;
	public Round round4;
	
	public String showNumber;
	
	public Date airDate;
	
	public List<Contestant> contestants = new ArrayList<Contestant>();
	
	public Game(String showNumber) {
		this.round1 = new Round("Jeopardy!");
		this.round2 = new Round("Double Jeopardy!");
		this.round3 = new Round("Final Jeopard!");
		this.round4 = new Round("Tiebreaker");
		
		this.showNumber = showNumber;
	}
	
}

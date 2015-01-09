import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Round {
	
	public String roundName;
	
	public List<Category> categories = new ArrayList<Category>();
	
	public Map<Contestant, Integer> scoresAtBreak = new HashMap<Contestant, Integer>();
	public Map<Contestant, Integer> scoresAtEnd = new HashMap<Contestant, Integer>();
	
	public Round(String roundName) {
		this.roundName = roundName;
	}
	
}
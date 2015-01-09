import java.util.HashMap;
import java.util.Map;


public class Question {
	
	public int value;
	public String clue;
	public String correctAnswer;
	public Contestant winner;
	public Map<Contestant, String> answers = new HashMap<Contestant, String>();
	public boolean dailyDouble = false;
	public int questionOrder;
}
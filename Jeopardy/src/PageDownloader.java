import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * http://stackoverflow.com/questions/13176405/java-program-to-read-a-html-page-and-save-its-html-code-in-a-text-file
 * @author novusfolium
 *
 */
public class PageDownloader {

	public static void main(String[] args) {

		for (int i = 4720; i < 4321; i++) {
			try {
				URL oracle = new URL("http://www.j-archive.com/showgame.php?game_id=" + i);
				BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
				BufferedWriter writer = new BufferedWriter(new FileWriter("game" + i + ".html"));

				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					try {
						writer.write(inputLine);
					} catch (IOException e) {
						e.printStackTrace();
						return;
					}
				}
				in.close();
				writer.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

import java.io.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Scraper {
	String urlBase = "https://www.airfleets.net/flottecie/";
	static Map<String, Map<String, Integer>> airlineFleets = new HashMap<String, Map<String, Integer>>();
	
	public static void main(String[] args) {
		File airlineFile = new File("airlines.txt");
		BufferedReader br = null;
		List<String> airlines = new ArrayList<String>();
		
		try {
			br = new BufferedReader(new FileReader(airlineFile));
			String airline = null;
			
			while ((airline = br.readLine()) != null) {
				airlines.add(airline);
			}
			
			for (String al : airlines) {
				LinkedList<String> planes = new LinkedList<String>();
				LinkedList<Integer> numPlanes = new LinkedList<Integer>();
				HashMap<String, Integer> fleet = new HashMap<String, Integer>();
				
				String urlAl = al.replaceAll(" ", "%20");
				String url = "https://www.airfleets.net/flottecie/" + urlAl + ".htm";
				System.out.println(url);
				Document doc = Jsoup.connect(url).get();
				Elements tables = doc.select("table");
				
				if (tables.size() < 14) {
					continue;
				}
				
				Element fleetTable = tables.get(14);
				Elements rows = fleetTable.select("tr");
				
				for (Element row : rows) {
					Elements columns = row.select("td");
					String planeLine = columns.get(0).toString();
					String numPlaneLine = columns.get(1).toString();
					
					String[] planesSplit = planeLine.split("&nbsp; &nbsp; ");
					for (int i = 1; i < planesSplit.length; i++) {
						String plane = planesSplit[i].split("<")[0];
						planes.addLast(plane);
					}
					
					String[] numPlaneSplit = numPlaneLine.split("<img src=\"../images/pix.gif\" border=\"0\" width=\"1\" height=\"12\">");
					if (numPlaneSplit.length > 1) {
						for (int i = 1; i < numPlaneSplit.length; i++) {
							String numPlane = numPlaneSplit[i].split("<")[0];
							if (numPlane.length() == 0) {
								numPlane = "-1";
							}
							numPlanes.addLast(Integer.parseInt(numPlane));
						}
					}
				}
				
				for (int i = 0; i < planes.size(); i++) {
					fleet.put(planes.get(i), numPlanes.get(i));
				}
				
				airlineFleets.put(al, fleet);
			}
			
			System.out.println(airlineFleets);
		} catch (FileNotFoundException e) {
			System.out.println("File does not exist");
		} catch (IOException e) {
			System.out.println("IOException");
		}
	}
}

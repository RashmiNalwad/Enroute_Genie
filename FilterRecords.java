package images;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;

/*
 * Read file
 * Get lat-log for city@sourceA && mention_name.
 * Find the distance between the cities. If the distance is greater than 5 miles eliminate the record. 
 * Else write it to a file in json format in exact same way. 
 * 
 */

public class FilterRecords {

	public static void main(String[] args) {
		String fileName = "/Users/rashmi/Desktop/IIW_Current/blogs.json";
		String outFile = "/Users/rashmi/Desktop/GeniePlaces.json";
		processFile(fileName,outFile);
	}
	
	private static void processFile(String path,String outFile)
	{
			JSONParser parser = new JSONParser();
			try
			{
				JSONArray a = (JSONArray)parser.parse(new FileReader(path));
				BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
				
				//Map of Blog name with all Key value pairs.
				Map<String,ArrayList<HashMap<String,String>>> finalDataMap = new HashMap<String,ArrayList<HashMap<String,String>>>();
				
				for(Object o:a)
				{
					JSONObject jsonObject = (JSONObject)o;
					String tripCity = (String) jsonObject.get("City@sourceA");
					String blogMention = (String) jsonObject.get("mention_name@sourceB");
					String latLongsA[] = getLatLongPositions(tripCity);
					String latLongsB[] = getLatLongPositions(blogMention);
					
					System.out.println(tripCity + ":" + blogMention);
					Thread.sleep(4000);
					
					if(latLongsA != null && latLongsB != null && compareDistance(latLongsA,latLongsB))
					{
						if(!finalDataMap.containsKey(blogMention))
							finalDataMap.put(blogMention, new ArrayList<HashMap<String, String>>());
						
						HashMap<String,String> valueMap = new HashMap<String,String>();
						valueMap.put("Attraction", (String) jsonObject.get("Attraction"));
						valueMap.put("Address", (String) jsonObject.get("Address"));
						valueMap.put("Contact No", (String) jsonObject.get("ContactNo"));
						valueMap.put("Known For", (String) jsonObject.get("KnownFor"));
						valueMap.put("Rank", "Rank #"+ (String) jsonObject.get("Attraction") + "out of " + (String) jsonObject.get("Total Attractions") + "in " + blogMention); //rank #Num out of #Attractions.
						valueMap.put("Review", (String) jsonObject.get("Review"));
						valueMap.put("Review Count", (String) jsonObject.get("ReviewCount"));
						valueMap.put("Popularity", (String) jsonObject.get("mention_popularity")); //Mentioned in how many URLs in top 20 URLs
						valueMap.put("Youtube Links", (String) jsonObject.get("youTubeLinkIds"));
						valueMap.put("Trip Advisor Link", (String) jsonObject.get("URL"));
						valueMap.put("BlogLink1", (String) jsonObject.get("blogs/0@sourceB"));
						valueMap.put("BlogLink2", (String) jsonObject.get("blogs/1@sourceB"));
						valueMap.put("BlogLink3", (String) jsonObject.get("blogs/2@sourceB"));
						valueMap.put("BlogLink4", (String) jsonObject.get("blogs/3@sourceB"));
						valueMap.put("BlogLink5", (String) jsonObject.get("blogs/4@sourceB"));
						valueMap.put("BlogLink6", (String) jsonObject.get("blogs/5@sourceB"));
						valueMap.put("Latitude", latLongsA[0]);
						valueMap.put("Longitude", latLongsA[1]);
						finalDataMap.get(blogMention).add(valueMap);
					}
				}
				
				writeToOutput(bw,finalDataMap);
				bw.close();
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	
	private static void writeToOutput(BufferedWriter bw, Map<String, ArrayList<HashMap<String, String>>> finalDataMap) {
		try
		{
			bw.write("[\n");
			bw.write("{\n");
			
			for(Entry<String, ArrayList<HashMap<String, String>>> entry: finalDataMap.entrySet())
			{
				bw.write("{\n");
				bw.write("\""+ entry.getKey() + "\"" + ": [\n");
				
				for(HashMap<String,String> map: entry.getValue())
				{
					bw.write("{\n");
					for(Entry<String,String> ent: map.entrySet())
					{
						bw.write("\"" + ent.getKey() + "\"" + " : " + "\"" + ent.getValue() + "\",");
					}
					bw.write("},\n");
					
				}
				bw.write("]\n");
				bw.write("}\n");
			}
			
			
			bw.write("]\n");
			bw.write("}\n");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static boolean compareDistance(String[] latLongsA, String[] latLongsB) 
	{
		double lat1 = Double.parseDouble(latLongsA[0]);
		double lng1 = Double.parseDouble(latLongsA[1]);
		double lat2 = Double.parseDouble(latLongsA[0]);
		double lng2 = Double.parseDouble(latLongsB[1]);

		// lat1 and lng1 are the values of a previously stored location
		if (distance(lat1, lng1, lat2, lng2) < 1) { // if distance < 1 miles we take locations as equal
			return true;
		}
		return false;
	}
	
	private static double distance(double lat1, double lng1, double lat2, double lng2) {

	    double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);

	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);

	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	        * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

	    double dist = earthRadius * c;

	    return dist; // output distance, in MILES
	}


	private static String[] getLatLongPositions(String address) {
		try{
			int responseCode = 0;
		    String api = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(address, "UTF-8") + "&sensor=true";
		    URL url = new URL(api);
		    HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
		    httpConnection.connect();
		    responseCode = httpConnection.getResponseCode();
		    if(responseCode == 200)
		    {
		      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();;
		      Document document = builder.parse(httpConnection.getInputStream());
		      XPathFactory xPathfactory = XPathFactory.newInstance();
		      XPath xpath = xPathfactory.newXPath();
		      XPathExpression expr = xpath.compile("/GeocodeResponse/status");
		      String status = (String)expr.evaluate(document, XPathConstants.STRING);
		      if(status.equals("OK"))
		      {
		         expr = xpath.compile("//geometry/location/lat");
		         String latitude = (String)expr.evaluate(document, XPathConstants.STRING);
		         expr = xpath.compile("//geometry/location/lng");
		         String longitude = (String)expr.evaluate(document, XPathConstants.STRING);
		         return new String[] {latitude, longitude};
		      }
		      else
		      {
		         throw new Exception("Error from the API - response status: "+status);
		      }
		    }
		    return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
}

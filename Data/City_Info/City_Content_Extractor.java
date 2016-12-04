package images;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class City_Content_Extractor {

	public static void main(String[] args) {
		Path path = Paths.get("/Users/rashmi/Desktop/IIW_Current/ReviewComments/cityData_output");
		File dir = new File(path.toString());
		File[] files = dir.listFiles();
		Map<String,HashMap<String,String>> cityMap = new HashMap<String,HashMap<String,String>>(); //File name and city name.
		
		for(File file:files)
		{
			String fileName = file.getName().split("\\.")[0];
			parseJson(fileName,cityMap,file.getAbsolutePath()); // Extracting city names from city_city pair.
		}
		writeToOutputJSON(cityMap);
    }
	
	private static void writeToOutputJSON(Map<String, HashMap<String, String>> cityMap)
	{
		BufferedWriter bw = null;
		
		try
		{
			String cityDbPedia = "/Users/rashmi/Desktop/IIW_Current/ReviewComments/AllCityData.json";
			bw = new BufferedWriter(new FileWriter(cityDbPedia));
		
			bw.write("{\n");
			for(Entry<String,HashMap<String,String>> entry:cityMap.entrySet())
			{
				String city = entry.getKey();
				System.out.println(city);
				HashMap<String,String> value = entry.getValue();
				bw.write("\"" + city + "\":");
				bw.write("{\n");
				int i = 0; 
				for(Entry<String,String> ent:value.entrySet())
				{
					if(i == value.size()-1)
					{
						bw.write("\"" + ent.getKey() + "\":" + "\"" + ent.getValue() + "\"" + "\n");
					}
					else
					{
						bw.write("\"" + ent.getKey() + "\":" + "\"" + ent.getValue() + "\"," + "\n");
					}
				}
				bw.write("},\n");
			}
			bw.write("}");
			bw.close();
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// Extracting city names and their DBPEdia Info
	private static void parseJson(String fileName, Map<String, HashMap<String, String>> cityMap, String path)
	{
		JSONParser parser = new JSONParser();
		try
		{
			JSONObject a = (JSONObject)parser.parse(new FileReader(path));
			String mention = (String)a.get("mention_name");
			HashMap<String,String> map = null;
			if(cityMap.get(mention) == null)
			{
				map = new HashMap<String,String>();
			}
			else
			{
			    map = cityMap.get(mention);
			}
			map.put("Abstract", (String)a.get("abstract"));
			map.put("Label", (String)a.get("label"));
			map.put("Latitude", (String)a.get("mention_latitude"));
			map.put("Longitude", (String)a.get("mention_longitude"));
			map.put("comment", (String)a.get("comment"));
			map.put("YoutubeLink", (String)a.get("youTubeLinkIds"));
			map.put("mention_popularity",  (String)a.get("mention_popularity"));
			JSONArray arr = (JSONArray)a.get("imageUrls");
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i< arr.size();i++)
			{
				sb.append(arr.get(i) +",");
			}
			sb.deleteCharAt(sb.length()-1);
			map.put("ImageURL", sb.toString());
			cityMap.put(mention, map);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}

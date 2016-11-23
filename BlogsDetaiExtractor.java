package images;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class BlogsDetaiExtractor {

	public static void main(String[] args) {
		Path path = Paths.get("/Users/rashmi/Desktop/IIW_Current/geoCodeLocations_output");
		File dir = new File(path.toString());
		File[] files = dir.listFiles();
		Map<String,HashSet<String>> fileBlogsMap = new HashMap<String, HashSet<String>>(); //City name and Blogs link.
		Map<String,HashSet<String>> cityMap = new HashMap<String,HashSet<String>>(); //File name and city name.
		
		for(File file:files)
		{
			String fileName = file.getName().split("\\.")[0];
			if( !cityMap.containsKey(fileName))
			{
				parseJson(fileName,cityMap,fileBlogsMap,file.getAbsolutePath()); // Extracting city names from city_city pair.
			}
		}
		writeToOutputJSON(cityMap,fileBlogsMap);
    }
	
	private static void writeToOutputJSON(Map<String, HashSet<String>> cityMap, Map<String, HashSet<String>> fileBlogsMap)
	{
		BufferedWriter bw = null;
		BufferedWriter bw1 = null;
		
		try
		{
			String blogsFileName = "/Users/rashmi/Desktop/cityBlogs.json";
			String cityPairsName = "/Users/rashmi/Desktop/cityPairs.json";
			bw = new BufferedWriter(new FileWriter(blogsFileName));
			bw1 = new BufferedWriter(new FileWriter(cityPairsName));
			
			bw.write("[\n");
			for(Entry<String,HashSet<String>> entry:fileBlogsMap.entrySet())
			{
				StringBuilder sb = new StringBuilder();
				for(String val:entry.getValue())
				{
					sb.append(val+",");
				}
				sb.deleteCharAt(sb.length()-1);
				
				bw.write("{\n");
				bw.write("\""+"city"+ "\"" + ":" + "\"" + entry.getKey() + "\""+",\n");
				bw.write("\""+ "blogs" + "\"" +":" + "[" + sb.toString() + "]"+ "\n");
				bw.write("},\n");
			}
			bw.write("]");
			bw.close();
			
			
			bw1.write("[\n");
			for(Entry<String,HashSet<String>> entry:cityMap.entrySet())
			{
				StringBuilder sb = new StringBuilder();
				for(String val:entry.getValue())
				{
					sb.append(val+",");
				}
				sb.deleteCharAt(sb.length()-1);
				
				bw1.write("{\n");
				bw1.write("\"" + entry.getKey() + "\"" +":" + "\"" + sb.toString() + "\""+"\n");
				bw1.write("},\n");
			}
			bw1.write("]");
			bw1.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	// Extracting city names from city_city pair and extract blog links.
	private static void parseJson(String fileName, Map<String, HashSet<String>> cityMap, Map<String, HashSet<String>> fileBlogsMap, String path)
	{
		JSONParser parser = new JSONParser();
		try
		{
			JSONObject a = (JSONObject)parser.parse(new FileReader(path));
			JSONObject mention = (JSONObject) a.get("mentions");
			for(Object entry: mention.keySet())
			{
				String place =  (String) entry;
				JSONObject value = (JSONObject) mention.get(place);
				if(value.get("locType").equals("city"))
				{
					if(cityMap.get(fileName) == null)
					{
						cityMap.put(fileName, new HashSet<String>());
					}
					cityMap.get(fileName).add(place);
					
					//Populate File Blogs Map.
					if(value.get("blogLinks") != null)
					{
						JSONArray arr = (JSONArray) value.get("blogLinks");
						StringBuilder sb = new StringBuilder();
						for(Object as:arr)
						{
							sb.append("\""+as + "\""+",");
						}
						sb.deleteCharAt(sb.length()-1);
						
						if(!fileBlogsMap.containsKey(place))
						{
							fileBlogsMap.put(place, new HashSet<String>());
						}
						fileBlogsMap.get(place).add(sb.toString());
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

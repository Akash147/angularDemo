package com.cards;

import com.akash.Utilities;
import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchMetaInformation {
	private URL url;
	private Map<String, String> metaInformations;
	
	public FetchMetaInformation(String urlString) throws IOException{
		this.url = new URL(urlString);
		init();
	}

	private void init() throws IOException {
		Source source = new Source(url);
		Map<String, String> map = getMetaAttributes(source);
		metaInformations = parseAttributes(map);
		for (Map.Entry<String, String> entry : metaInformations.entrySet()) {
		    System.out.println(entry.getKey() + "::::" + entry.getValue());
		}
	}
	
	private Map<String, String> parseAttributes(Map<String, String> map) {
		Map<String, String> toReturn = new HashMap<String,String>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
		    System.out.println(entry.getKey() + "::::" + entry.getValue());
		    if(isTitle(entry.getKey()))
		    	toReturn.put("title", entry.getValue());
		    else if(isDescription(entry.getKey()))
		    	toReturn.put("description", entry.getValue());
		    else if(isImage(entry.getKey()))
		    	toReturn.put("image", entry.getValue());
		    else if(isLink(entry.getKey()))
		    	toReturn.put("link", entry.getValue());		    
		}
		System.out.println("======================");
		return toReturn;
	}

	private boolean isLink(String key) {
		if(key.contains("twitter:url")
				|| key.contains("al:web:url")
				|| key.contains("og:url"))
			return true;
		return false;
	}

	private boolean isImage(String key) {
		if(key.contains("image"))
			return true;
		return false;
	}

	private boolean isTitle(String key) {
		if(key.contains("title"))
			return true;
		return false;
	}
	
	private boolean isDescription(String key) {
		if(key.contains("description"))
			return true;
		return false;
	}

	private Map<String, String> getMetaAttributes(Source source) {
		Map<String, String> map = new HashMap<>();
		List<Element> elements = source.getAllElements("meta");
		for (Element element : elements){
			Attributes attrs = element.getAttributes();
			if(attrs.getCount()==2){
				Attribute a = attrs.get(0);
				Attribute b = attrs.get(1);
				if(canBeKey(a)){
					map.put(a.getValue(), b.getValue());
				}
				else if(canBeKey(b)){
					map.put(b.getValue(), a.getValue());
				}
			}
		}
		return map;
	}
	
	private boolean canBeKey(Attribute attr){
		if(!Utilities.isEmptyString(attr.getKey()) &&
				!Utilities.isEmptyString(attr.getValue()) &&
				(attr.getKey().equalsIgnoreCase("name") || 
						attr.getKey().equalsIgnoreCase("property")))
			return true;
		
		return false;
	}

	public static void main(String[] args) throws IOException {
//		String url = "http://www.wired.com/2016/01/how-to-survive-ces/";
		String url = "https://www.youtube.com/watch?v=j8cADX87-2I";
		FetchMetaInformation meta = new FetchMetaInformation(url);
	}

	public Map<String,String> getMeta() {
		return metaInformations;
	}
}

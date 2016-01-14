package com.cards;

import com.akash.Utilities;
import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Attributes;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

public class FetchMetaInformation {
	private final URLConnection conn;
	private final URL url;
	private Source sourceDocument;
	private Map<String, String> metaInformations;
	
	public FetchMetaInformation(String urlString) throws IOException{
		this.url = new URL(urlString);
		this.conn = url.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
		init();
	}

	private void init() throws IOException {
		sourceDocument = new Source(conn);
		Map<String, String> map = getMetaAttributes(sourceDocument);
		metaInformations = parseAttributes(map);

		checkIfMissingImportant(sourceDocument, metaInformations);
	}

	private void checkIfMissingImportant(Source sourceDocument, Map<String, String> metaInformations) {
		if(!metaInformations.containsKey("title")){
			metaInformations.put("title", getTitleFromDocument(sourceDocument));
		}
		if(!metaInformations.containsKey("site")){
			metaInformations.put("site", url.getHost().replaceFirst("www.",""));
		}

		if(!metaInformations.containsKey("url")){
			metaInformations.put("url", url.toString());
		}

		if(!metaInformations.containsKey("image")){
			String imageUrl = getImageFromDocument(sourceDocument);
			if(!Utilities.isEmptyString(imageUrl))
				metaInformations.put("image", imageUrl);
		}
	}

	private String getImageFromDocument(Source sourceDocument) {
		List<Element> imgElements = sourceDocument.getAllElements("img");
		NavigableMap<Integer, String> images = new TreeMap<>();
		for(Element element : imgElements) {
			System.out.println(element);
			String src = element.getAttributeValue("src");
			if(!src.startsWith("http"))
				src = this.url.getProtocol() + "://" + this.url.getHost() + "/" + (src.startsWith("/")?src.replaceFirst("/",""):src);
            if(src.contains("?"))
                src = src.substring(0,src.indexOf("?"));
			System.out.println("Image: " + src);
			if(!Utilities.isEmptyString(src)){
				try{
					URL imageUrl = new URL(src);
					URLConnection connection = imageUrl.openConnection();
					connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
					connection.setRequestProperty("Referer", this.url.toString());
					BufferedImage image = ImageIO.read(connection.getInputStream());

					if(image.getWidth()>300 && image.getHeight()>300)
						images.put(image.getWidth()*image.getHeight(), src);

				} catch (Exception ex){
					ex.printStackTrace();
				}
			}
		}

		for (Map.Entry<Integer, String> entry : images.entrySet()) {
			System.out.println(entry.getKey() + "::::" + entry.getValue());
		}
		if(images.size()>0)
			return images.lastEntry().getValue();

		return null;
	}

	private String getTitleFromDocument(Source sourceDocument) {
		Element titleElement = sourceDocument.getFirstElement("title");
		return titleElement.getTextExtractor().toString();
	}

	private Map<String, String> parseAttributes(Map<String, String> map) {
		Map<String, String> toReturn = new HashMap<String,String>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
		    if(isTitle(entry.getKey()))
				toReturn.put("title", entry.getValue());
		    else if(isDescription(entry.getKey()))
		    	toReturn.put("description", entry.getValue());
		    else if(isImage(entry.getKey())) {
                String imageURL = entry.getValue();
                if(imageURL.contains("?"))
                    imageURL = imageURL.substring(0,imageURL.indexOf("?"));
                toReturn.put("image", imageURL);
            }
		    else if(isLink(entry.getKey()))
		    	toReturn.put("url", entry.getValue());
			else if(isSiteName(entry.getKey()))
				toReturn.put("site", entry.getValue());
		}
		return toReturn;
	}

	private boolean isSiteName(String key) {
		if(key.contains("og:site_name")
				|| key.contains("al:android:app_name")
				|| key.contains("al:ios:app_name"))
			return true;
		return false;
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
		if(key.equals("og:title")||
                key.equals("twitter:title"))
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
		String url = "http://www.wired.com/2016/01/life-is-strange-hard-choices/";
//		String url = "http://myrepublica.com/feature-article/story/34602/prince-harry-to-visit-nepal-this-spring.html";
		FetchMetaInformation meta = new FetchMetaInformation(url);
		for (Map.Entry<String, String> entry : meta.getMeta().entrySet()) {
			System.out.println(entry.getKey() + "::::" + entry.getValue());
		}

	}

	public Map<String,String> getMeta() {
		return metaInformations;
	}
}

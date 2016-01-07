package com.cards;

import java.io.IOException;

import com.akash.Utilities;

import com.google.gson.Gson;

public class UrlToMetaData {
	
	public static String handle(String data) {
		if(Utilities.isEmptyString(data))
			return null;
		NewRequest request = new Gson().fromJson(data, NewRequest.class);
		if(request==null)
			return null;
		
		String uri = request.getUri();
		if(uri.startsWith("com://")){
			return "{title: 'hamropatro deeplink', " +
					"description: '" + data + "'" +
					"}";
		}
		else {
			try {
				FetchMetaInformation mf = new FetchMetaInformation(uri);
				return new Gson().toJson( mf.getMeta() );
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static class NewRequest{
		private String uri;
		private String title;
		private String description;
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}
	
}

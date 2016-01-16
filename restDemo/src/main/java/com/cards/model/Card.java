package com.cards.model;

import java.util.Map;
import java.util.UUID;

public class Card {
	private long id;
	private String type;
	private String iconImage;
	private String site;
	private String siteDesc;
	private String title;
	private String description;
	private String image;
	private String link;
	private String callToAction;
	private String mediaType;
	private String image1;
	private String image2;
	private String image3;
	private String imageCaption;
	private String imageCaption1;
	private String imageCaption2;
	private String imageCaption3;
	private Map<String, String> meta;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIconImage() {
		return iconImage;
	}
	public void setIconImage(String iconImage) {
		this.iconImage = iconImage;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getSiteDesc() {
		return siteDesc;
	}
	public void setSiteDesc(String siteDesc) {
		this.siteDesc = siteDesc;
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getCallToAction() {
		return callToAction;
	}
	public void setCallToAction(String callToAction) {
		this.callToAction = callToAction;
	}
	public String getMediaType() {
		return mediaType;
	}
	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
	public String getImage1() {
		return image1;
	}
	public void setImage1(String image1) {
		this.image1 = image1;
	}
	public String getImage2() {
		return image2;
	}
	public void setImage2(String image2) {
		this.image2 = image2;
	}
	public String getImage3() {
		return image3;
	}
	public void setImage3(String image3) {
		this.image3 = image3;
	}
	public String getImageCaption() {
		return imageCaption;
	}
	public void setImageCaption(String imageCaption) {
		this.imageCaption = imageCaption;
	}
	public String getImageCaption1() {
		return imageCaption1;
	}
	public void setImageCaption1(String imageCaption1) {
		this.imageCaption1 = imageCaption1;
	}
	public String getImageCaption2() {
		return imageCaption2;
	}
	public void setImageCaption2(String imageCaption2) {
		this.imageCaption2 = imageCaption2;
	}
	public String getImageCaption3() {
		return imageCaption3;
	}
	public void setImageCaption3(String imageCaption3) {
		this.imageCaption3 = imageCaption3;
	}
	public Map<String, String> getMeta() {
		return meta;
	}
	public void setMeta(Map<String, String> meta) {
		this.meta = meta;
	}
	
	public static Card getNewCard(String type, String title, String description) {
		Card card = new Card();
		card.type = type;
		card.title = title;
		card.description= description;
		card.id = getUniqueID();
		return card;
	}

	private static long getUniqueID(){
		return System.currentTimeMillis();
	}

    @Override
    public boolean equals(Object object) {
        boolean sameSame = false;

        if (object != null && object instanceof Card) {
            Card other = (Card) object;
            if(other.id!=0 && this.id!=0){
                if(other.id==this.id)
                    sameSame = true;
            }
            else if(other.getTitle().equals(this.getTitle()) &&
                    other.getDescription().equals(this.getDescription()) &&
                    other.getType().equals(this.getType()))
                sameSame = true;
        }

        return sameSame;
    }

    public void assignNewId() {
        this.id = getUniqueID();
    }
}

package fr.uoe.eucalyps.helper;

import java.io.Serializable;

public class EntPopup implements Serializable {
   private static final long serialVersionUID = 1L;
   
   public String text;
   public String imgId;

	public EntPopup() {}

	public EntPopup(String imgID, String text) {
		super();
		this.imgId = imgID;
		this.text = text;
	}
}
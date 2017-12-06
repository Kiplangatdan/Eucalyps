package fr.uoe.eucalyps.helper;

import java.io.Serializable;

public class Story extends Component implements Serializable {
   private static final long serialVersionUID = 1L;
   public String short_text;   
   public String long_text;
   public String bgId;
   
   public Story(final String bgId, final String imgId, final String short_text, final String long_text) {
      super();
      this.short_text = short_text;
      this.imgId = imgId;
      this.bgId = bgId;
      this.long_text = long_text;
      type = Component.TYPE_STORY;
   }
}
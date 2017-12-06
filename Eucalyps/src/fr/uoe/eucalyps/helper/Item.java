package fr.uoe.eucalyps.helper;

import java.io.Serializable;

public class Item extends Component implements Serializable {
   private static final long serialVersionUID = 1L;
   public String description;   
   public String hint;
   public int code;
   
   public Item(final String description, final String imgId, final String hint, final int code) {
      super();
      this.description = description;
      this.imgId = imgId;
      this.hint = hint;
      this.code = code;
      type = Component.TYPE_ITEM;
   }
}
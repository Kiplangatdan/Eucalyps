package eucalyps;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.content.res.Resources;


public class XMLHandlerStory extends DefaultHandler {
   protected ArrayList<StoryScreen> screens = new ArrayList<StoryScreen>();
   private final StringBuilder currentValue = new StringBuilder();
   private String image, en_text, por_text, audio;
   private int next[] = new int[2], type;
   private final Context context;
   private final Resources res;

   public XMLHandlerStory(final Context context) {
      super();
      this.context = context;
      this.res = context.getResources();
   }

   // Called when tag starts
   @Override
   public void startElement(final String uri, final String localName, final String qName,
         final Attributes attributes) throws SAXException {
      if (localName.equalsIgnoreCase("screen")) { //new story screen
         //en_text = null;
         //por_text = null;
         image = null;
         audio = null;

         final String type_s = attributes.getValue("type");
         if (type_s != null) {
            if (type_s.equalsIgnoreCase("story")) {
               type = StoryScreen.STORY_SCREEN;
               next = new int[1];
               next[0] = Integer.parseInt(attributes.getValue("next"));
            }
            else if (type_s.equalsIgnoreCase("static")) {
               type = StoryScreen.STATIC_SCREEN;
               next = new int[1];
               final String value = attributes.getValue("next");
               if (value == null)
                  next[0] = StoryScreen.THE_END;
               else
                  next[0] = Integer.parseInt(value);
            }
            else if (type_s.equalsIgnoreCase("choice")) {
               type = StoryScreen.CHOICE_SCREEN;
               next = new int[2];
               next[0] = Integer.parseInt(attributes.getValue("choice_a"));
               next[1] = Integer.parseInt(attributes.getValue("choice_b"));
            }
         }
      }
   }

   /** Called when tag closing ( ex:- <name>AndroidPeople</name>
    * -- </name> )*/
   @Override
   public void endElement(final String uri, final String localName, final String qName)
         throws SAXException {
      /** set value */
      if (localName.equalsIgnoreCase("image")) { //Name of the image
         image = currentValue.toString().trim();
      }
      /**else if (localName.equalsIgnoreCase("por_text")) { //Portuguese storytext
         por_text = currentValue.toString().trim();
      }
      else if (localName.equalsIgnoreCase("en_text")) { //English storytext
         en_text = currentValue.toString().trim();
      }**/
      else if (localName.equalsIgnoreCase("audio")) { //Story audio
         audio = currentValue.toString().trim();
      }
      else if (localName.equalsIgnoreCase("screen")) { //end tag
         if (type == StoryScreen.STATIC_SCREEN || type == StoryScreen.STORY_SCREEN)
            screens.add(new StoryScreen(res.getIdentifier("drawable/"+image, null, context.getPackageName()), por_text, en_text, res.getIdentifier("raw/"+audio, null, context.getPackageName()), next[0], type));
         else
            screens.add(new StoryScreen(res.getIdentifier("drawable/"+image, null, context.getPackageName()), por_text, en_text, res.getIdentifier("raw/"+audio, null, context.getPackageName()), next, type));
      }
      currentValue.delete(0, currentValue.length());
   }

   /** Called to get tag characters ( ex:- <name>AndroidPeople</name>
    * -- to get AndroidPeople Character ) */
   @Override
   public void characters(final char[] ch, final int start, final int length)
         throws SAXException {
      currentValue.append(ch, start, length);
   }
}
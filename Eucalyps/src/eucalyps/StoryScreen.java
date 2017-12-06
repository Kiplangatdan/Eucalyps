package eucalyps;

public class StoryScreen {
   public static final int THE_END = -1500;
   public static final int STATIC_SCREEN = 0;
   public static final int CHOICE_SCREEN = 2;
   
   protected int image;
   //protected String por_text, en_text;
   protected int audio;
   protected int[] next_screen;
   protected int type;
public static final int STORY_SCREEN = 1;
   
   public StoryScreen(final int image, final String por_text, final String en_text, final int audio, final int next_screen, final int type) {
      this.image = image;
      //this.por_text = por_text;
      //this.en_text = en_text;
      this.audio = audio;
      this.next_screen = new int[1];
      this.next_screen[0] = next_screen;
      this.type = type;
   }
   
   public StoryScreen(final int image, final String por_text, final String en_text, final int audio, final int next_screen[], final int type) {
      this.image = image;
      //this.por_text = por_text;
      //this.en_text = en_text;
      this.audio = audio;
      this.next_screen = next_screen;
      this.type = type;
   }

   /**
    * Returns story text depending on applications language settings.
    * @param language 0 is Portuguese, 1 is English
    * @return Story text in right language
    */
   /**public String getText(int language){
      if(language == SuraYaUKIMWIActivity.PORTUGUESE)
         return this.por_text;
      else{
         return this.en_text;
      }
   }**/
   
}
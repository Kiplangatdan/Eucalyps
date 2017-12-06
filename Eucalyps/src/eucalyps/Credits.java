package eucalyps;


import eucalyps.mine.R;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Credits extends MainActivity{
   TextView managing;
   TextView contentdevelopment;
   TextView programming;
   private Resources res=null;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      //Teurastetaan otsikkopalkki pois:
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      //Teurastetaan status-palkki pois (full screen):
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
      final View v = getLayoutInflater().inflate(R.layout.credits, null);
      v.setKeepScreenOn(true);
      setContentView(v);  
      res=this.getResources();
      managing = (TextView) findViewById(R.id.managing);
      contentdevelopment = (TextView) findViewById(R.id.contentdevelopment);
      programming = (TextView) findViewById(R.id.programming);

      
      managing.setText(Html.fromHtml("<b>" + res.getString(R.string.cr_managing)+"</b><br/>Marcus Duveskog<br/>Balozi Kirongo<br/>Livondo Julius<br/>Myriam Munezero<br/>Lazarus Etiegni"));

      contentdevelopment.setText(Html.fromHtml("<b>" + res.getString(R.string.cr_contentdevelopment)+"</b>" +
      		"<br/>Mwasambo Benjamin<br/>Virginia Nyawira<br/>Tabby Mungai<br/>Haraka Joseph"));
      contentdevelopment.setTextColor(Color.BLACK);

      programming.setText(Html.fromHtml("<b>"+res.getString(R.string.cr_programming)+"</b><br/>Moses Shitote<br/>Duncan C Kiplangat<br/>Felix Maru<br/>Dennis Korir<br/>Erick Bett<br/>Ronald Kemei<br/>Santu Akerman<br/>Tapani Toivonen<br/>"));

   }

   
   @Override
   public boolean onKeyDown(final int keyCode, final KeyEvent msg) {
      if (keyCode == KeyEvent.KEYCODE_BACK) {
         //sound.play(click, 1, 1, 1, 0, 0.5f);
         finish();
         return true;
      } 
      else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
         audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
         sound.play(click, 1, 1, 1, 0, 0.75f);
         return true;
      }
      
      else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
         audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
         sound.play(click, 1, 1, 1, 0, 1.25f);
         return true;
      } else
      return true;
   }
   
   @Override
   public boolean onKeyUp(final int keyCode, final KeyEvent msg) {
      return true;
   }

}

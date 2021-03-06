package eucalyps;

import eucalyps.mine.R;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends MainActivity{
   //Taken 5hrs(this is really long!) to get to the next activity
   protected int _splashTime=60000*60*5;
   private Thread splashThread;
   /*Called first when the activity is created. */

   public void onCreate(final Bundle savedInstanceState){
      super.onCreate(savedInstanceState);
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      //Remove title bar:
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      //Remove the system status-bar, and keep the screen on always
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      setContentView(R.layout.splashscreen);
      final SplashActivity sPlashActivity=this;
      //Thread for displaying the Splashscreen
      splashThread=new Thread(){
         public void run(){
            try {
               sleep(_splashTime);
            } catch(final InterruptedException e){}
            finally {
               //Starting the game in a new activity
               final Intent i= new Intent();
               i.setClass(sPlashActivity, HomeActivity.class);
               startActivity(i);
               finish();
               //stop();
            }
         }
      };
      splashThread.start();

   }
   //Function that will handle the touch
   public boolean onTouchEvent(final MotionEvent event){
      splashThread.interrupt();
      return true;
   }
   
   @Override
   public boolean onKeyDown(final int keyCode, final KeyEvent msg) {
      if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
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
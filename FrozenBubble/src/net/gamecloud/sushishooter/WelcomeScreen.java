package net.gamecloud.sushishooter;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class WelcomeScreen extends Activity{
   //Taken 5hrs(this is really long!) to get to the next activity
   protected int _splashTime=60000*60*5;
   private Thread splashThread;
   /*Called first when the activity is created. */

   public void onCreate(final Bundle savedInstanceState){
      super.onCreate(savedInstanceState);
      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
      //Remove title bar:
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      //Remove the system status-bar, and keep the screen on always
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      setContentView(R.layout.welcomescreen);
      final WelcomeScreen sPlashActivity=this;
      //Thread for displaying the Splashscreen
      splashThread=new Thread(){
         public void run(){
            try {
               sleep(_splashTime);
            } catch(final InterruptedException e){}
            finally {
               //Starting the game in a new activity
               final Intent i= new Intent();
               i.setClass(sPlashActivity, FrozenBubble.class);
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
   public boolean onKeyUp(final int keyCode, final KeyEvent msg) {
      return true;
   }
}
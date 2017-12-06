package eucalyps;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import eucalyps.mine.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class StoriActivity extends Activity {
   public static final int CANCEL = -1; //User pressed the back-button
   public static final int NOTHING = 0; //No visible dialog
   public static final int STORY = 1; //User touched the screen

   public static final int NOT_READY = -1; //Audio not-ready
   public static final int READY = 0;

   protected Vibrator v; //Vibrates the screen when user touches the story-image
   private ImageView img; //Contains the story-image
   private TextView texts; //Contains the story-text
   protected Timer timer; //Timer shows toast's at fixed rate until user touches the screen
   protected int visibleDialog = 0; //Is any dialog showing?
   protected Toast t; //Displays the "Touch the screen to continue" text
   private AudioManager audio;
   protected MediaPlayer player;
   private int audioPosition;
   protected boolean hasAudio;

   /** Called when the activity is first created. */
   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      t = Toast.makeText(getBaseContext(), "Touch the screen to continue", 3000);
      v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
      audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
      setContentView(R.layout.stori_layout);//Stori_layout
      hasAudio = getIntent().getExtras().getInt("audio") != 0;
      //If we are resuming from previous instance:
      if (savedInstanceState != null)
         onRestoreInstanceState(savedInstanceState);
      else //If not, use defaults:
         audioPosition = NOT_READY;
   }

   @Override
   public void onResume() {
      super.onResume();
      //Toast-Timer:
      if (timer != null)
         timer.cancel();
      timer = new Timer(false);
      final TimerTask tt = new TimerTask() {
         @Override
         public void run() {
            if (visibleDialog == NOTHING) {
               runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                     t.show();
                  }
               });
            }
         }
      };
      timer.scheduleAtFixedRate(tt, 4000, 4000);
      //Audio:
      if (hasAudio) {
         if (player == null) {
            player = new MediaPlayer();
            final AssetFileDescriptor fd = getResources().openRawResourceFd(getIntent().getExtras().getInt("audio"));
            try {
               player.reset();
               player.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getDeclaredLength());
               player.prepare();
               if (audioPosition == NOT_READY)
                  audioPosition = READY;
               else
                  player.seekTo(audioPosition);
            } catch (final IllegalArgumentException e) {
               e.printStackTrace();
            } catch (final IllegalStateException e) {
               e.printStackTrace();
            } catch (final IOException e) {
               e.printStackTrace();
            }
            try {
               fd.close();
            } catch (final IOException e) {
               e.printStackTrace();
            }
         }
         if (!player.isPlaying() && audioPosition != player.getDuration())
            player.start();
      }
   }

   @Override
   public void onSaveInstanceState(final Bundle save) {
      save.putInt("dialog", visibleDialog);
      if (hasAudio)
         save.putInt("audioPosition", player.getCurrentPosition());
      super.onSaveInstanceState(save);
   }

   @Override
   public void onRestoreInstanceState(final Bundle bundle) {
      visibleDialog = bundle.getInt("dialog");
      if (hasAudio)
         audioPosition = bundle.getInt("audioPosition");
   }

   @Override
   public void onPause() {
      super.onPause();
      t.cancel();
      timer.cancel();
      if (hasAudio && player != null) {
         player.pause();
         audioPosition = player.getCurrentPosition();
      }
   }

   @Override
   public void onContentChanged() {
      this.img = (ImageView)findViewById(R.id.story_image);
      img.setImageResource(getIntent().getExtras().getInt("image"));
   }

   @Override
   public boolean onKeyDown(final int keyCode, final KeyEvent event) {
      if(keyCode == KeyEvent.KEYCODE_BACK) {
         visibleDialog = CANCEL;
         t.cancel();
         showDialog(CANCEL);
         return true;
      }
      else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
         audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
         return true;
      }
      else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
         audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
         return true;
      }
      return false;
   }

   @Override
   public boolean onTouchEvent(final MotionEvent m) {
      if (m.getAction() == MotionEvent.ACTION_UP && visibleDialog == NOTHING) {
         timer.purge();
         timer.cancel();
         t.cancel();
         visibleDialog = STORY;
         v.vibrate(200);
         showDialog(STORY);
         return true;
      }
      return false;
   }

   @Override
   protected Dialog onCreateDialog(final int id) {
      Dialog dialog = null;
      if (id == CANCEL) {
         final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
         dlgAlert.setMessage("Are you sure you want to quit?");
         dlgAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
               if (hasAudio) {
                  player.stop();
                  player.release();
                  player = null;
               }
               setResult(Activity.RESULT_CANCELED);
         	 Intent intent = new Intent(StoriActivity.this, HomeActivity.class);
         		 intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         		 startActivity(intent);
         		 finish();
         	  
            }
         });
         dlgAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
               visibleDialog = NOTHING;
            }
         });
         dlgAlert.setCancelable(false);
         dialog = dlgAlert.create();
      }
      else if (id == STORY) {
          //dialog = new Dialog(this, android.R.style.Theme_Dialog);
          //final AlertController ac = new AlertController(this, dialog, dialog.getWindow());
          //Set-up the positive-button:
          //ac.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
             
             //@Override public void onClick(final DialogInterface dialog, final int which) {
                setResult(Activity.RESULT_OK);
                if (hasAudio) {
                   player.stop();
                   player.release();
                   player = null;
                }
                finish();
             }
          /**}, null);
          //Remove negative-button:
          ac.setButton(DialogInterface.BUTTON_NEGATIVE, null, null, null);
          //Set up the content view:
          final View view = getLayoutInflater().inflate(R.layout.dialog, null);
          text = (TextView) view.findViewById(R.id.text);
          text.setText(getIntent().getExtras().getString("text"));
          //kill all padding from the dialog window
          ac.setView(view, 0, 0, 0, 0);
          ac.installContent();
          dialog.setCancelable(false);
       }**/
       return dialog;
    }
 }
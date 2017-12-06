package eucalyps;

import android.view.Display;
import android.view.MotionEvent;

public class ChoiceActivity extends StoriActivity {
   public static final int TRUE_ENDING = 100;
   public static final int FICTIVE_ENDING = 99;

   @Override
   public boolean onTouchEvent(final MotionEvent m) {
      if (m.getAction() == MotionEvent.ACTION_UP && visibleDialog == NOTHING) {
         timer.purge();
         timer.cancel();
         t.cancel();
         v.vibrate(200);
         if (hasAudio && player != null) {
            player.stop();
            player.release();
            player = null;
         }
         final Display d = getWindowManager().getDefaultDisplay();
         if (m.getY() < d.getHeight()*0.5f) //If true ending
            setResult(TRUE_ENDING);
         else //If fictive ending
            setResult(FICTIVE_ENDING);
         finish();
         return true;
      }
      return false;
   }
}
package eucalyps;

import android.app.Activity;
import android.view.MotionEvent;

public class StaticActivity extends StoriActivity {

   @Override
   public boolean onTouchEvent(final MotionEvent m) {
      if (m.getAction() == MotionEvent.ACTION_UP && visibleDialog == NOTHING) {
         timer.purge();
         timer.cancel();
         t.cancel();
         v.vibrate(200);
         setResult(Activity.RESULT_OK);
         if (hasAudio && player != null) {
            player.stop();
            player.release();
            player = null;
         }
         finish();
         return true;
      }
      return false;
   }
}
	

    package eucalyps;
     
    import java.util.ArrayList;
import java.util.HashSet;
     
    import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
     
    public class WordPanel extends View {
       Wordsearch context;
       //Graphics related:
       int width, height; //Width & height of the screen
       float letter_w, letter_h; //Width & height of a letter
       final int nLetterX = 9; //Number of letters horizontally
       final int nLetterY = 12; //Number of letters vertically
       Paint p = new Paint(); //This object holds all the paint-related settings
       String[][] ar_letters; //This 2d array hold all the letters
       final StringBuffer sb = new StringBuffer(); //This is used to buffer to current word
       public final ArrayList<String> wordAnswers = new ArrayList<String>();
       private final ArrayList<Line> lines = new ArrayList<Line>();
       public ArrayList<String> userAnswers = new ArrayList<String>();
       int counter = 0;
       private String test ="";
       //Lines related:
       Point start; //Starting point of a line
       Point stop; //Ending point of a line
       Point start_acc; //The pixel-perfect version of the starting point
       Point stop_acc; //The pixel-perfect version of the ending point
       
       static HashSet<String> correctAnswers = new HashSet<String>();
       static HashSet<String> usersAnswers = new HashSet<String>();
       static boolean loytyVittu = false;
       
       static {
    	   correctAnswers.add("STREAM");
           correctAnswers.add("NEEM");
           correctAnswers.add("FRUIT");
           correctAnswers.add("NICHE");
           correctAnswers.add("FORESTRY");
           //Down
           correctAnswers.add("WATER");
           correctAnswers.add("MANGROVE");
           correctAnswers.add("RAINFALL");
           correctAnswers.add("LEAF");
           correctAnswers.add("HONEY");
       }
       
       //to change crossword, colored boxes are '\0'
       //and others are the letters of words
       final String[][] crossword = {  
                   { "S", "T", "R", "E", "A", "M", "\0", "\0", "\0" }, //0
                   { "\0", "\0", "A", "\0", "\0", "A", "\0", "\0", "\0" }, //1
                   { "W", "\0", "I", "\0", "\0", "N", "E", "E", "M" }, //2
                   { "A", "\0", "N", "\0", "\0", "G", "\0", "\0", "\0" }, //3
                   { "T", "\0", "F", "\0", "F", "R", "U", "I", "T" }, //4
                   { "E", "\0", "A", "\0", "\0", "O", "\0", "\0", "\0" }, //5
                   { "R", "\0", "L", "\0", "\0", "V", "\0", "\0", "H" }, //6
                   { "\0", "\0", "L", "\0", "\0", "E", "\0", "\0", "O" }, //7
                   { "L", "\0", "\0", "\0", "\0", "\0", "\0", "\0", "N" }, //8
                   { "E", "\0", "\0", "\0", "N", "I", "C", "H", "E" }, //9
                   { "A", "\0", "\0", "\0", "\0", "\0", "\0", "\0", "Y" }, //10
                   { "F", "O", "R", "E", "S", "T", "R", "Y", "\0" }, //11
         };
     
       public WordPanel(final Context context) {
          super(context);
          initialize(context);
       }
     
       public WordPanel(final Context context, final AttributeSet ab) {
          super(context, ab);
          initialize(context);
       }
     
       public WordPanel(final Context context, final AttributeSet ab, final int style) {
          super(context, ab, style);
          initialize(context);
       }
       
       public void onSaveState(final Bundle save) {
          save.putInt("answers_size", wordAnswers.size());
          for (int i = 0; i < wordAnswers.size(); i++)
             save.putString("answer"+i, wordAnswers.get(i));
         
          save.putInt("lines_size", lines.size());
          for (int i = 0; i < lines.size(); i++) {
             save.putInt("lines_startx"+i, lines.get(i).start.x);
             save.putInt("lines_starty"+i, lines.get(i).start.y);
             save.putInt("lines_stopx"+i, lines.get(i).stop.x);
             save.putInt("lines_stopy"+i, lines.get(i).stop.y);
          }
          save.putInt("counter", counter);
       }
       
       public void onRestoreState(final Bundle bundle) {
          wordAnswers.clear();
          int size = bundle.getInt("answers_size");
          for (int i = 0; i < size; i++)
             wordAnswers.add(bundle.getString("answer"+i));
          lines.clear();
          size = bundle.getInt("lines_size");
          for (int i = 0; i < size; i++) {
             final Line temp = new Line();
             temp.start = new Point(bundle.getInt("lines_startx"+i), bundle.getInt("lines_starty"+i));
             temp.stop = new Point(bundle.getInt("lines_stopx"+i), bundle.getInt("lines_stopy"+i));
             lines.add(temp);
          }
          counter = bundle.getInt("counter");
          context.counter.setText(""+counter+" / 10");
          context.currentScores.setText("Scores :"+(context.score+Math.round((float)counter/(float)wordAnswers.size()*Wordsearch.MAX_SCORE)));
          invalidate();
       }
     
       public void initialize(final Context context) {
          this.context = (Wordsearch)context;
          ar_letters = getTextArray();
          setFocusable(true);
          p.setColor(Color.BLACK);
          p.setStyle(Paint.Style.STROKE);
          p.setTextAlign(Align.CENTER);
          p.setTextSize(24);
          //Add correct answers that are in the crossword(order doesn't matter)
          //Across
          wordAnswers.add("STREAM");
          wordAnswers.add("NEEM");
          wordAnswers.add("FRUIT");
          wordAnswers.add("NICHE");
          wordAnswers.add("FORESTRY");
          //Down
          wordAnswers.add("WATER");
          wordAnswers.add("MANGROVE");
          wordAnswers.add("RAINFALL");
          wordAnswers.add("LEAF");
          wordAnswers.add("HONEY");
         
         
          //We cannot retrieve width/height of this View yet, because it is not laid out yet.
          post(new Runnable() { //By using View's post-method, we can make the view tell us the size once it is known
             public void run() {
                width = getWidth();
                height = getHeight();
                letter_w = (float)width/(float)nLetterX;
                letter_h = (float)height/(float)nLetterY;
                invalidate();
             }
          });
       }
     
       /**
        * Releases all the references and bitmaps
        */
       public void kill() {
          context = null;
       }
       
       public String getAnswer(){
               return test;
       }
     
       @Override
       public boolean onTouchEvent(final MotionEvent e) {
    	   		
    	   	   loytyVittu = false;
               if(e.getAction()==MotionEvent.ACTION_UP){
               AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                    context);
               
               final EditText input = new EditText(context);
     
                            alertDialogBuilder.setTitle("Answering");
                            alertDialogBuilder.setView(input);
                           
                            alertDialogBuilder
                                    .setMessage("Click yes to check your answer!")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                    String answer = input.getText().toString();
                                                    usersAnswers.add(answer);
                                                    for (String s : correctAnswers) {
                                                    	for (String s2 : usersAnswers) {
                                                    		if (s.equalsIgnoreCase(s2))
                                                    			loytyVittu = true;
                                         
                                                    	}
                                                    }
                                                    if (loytyVittu) {
                                                    	
                                                    	
                                                    }
                                                    //userAnswers.add(getAnswer());
                                                    boolean correct = checkAnswer(answer);
                                                    boolean found = false;
                                                    if(correct){
                                                            if (answer != null)
                                                              for(int i = 0;i<userAnswers.size();i++){     
                                                                if(userAnswers.get(i).equalsIgnoreCase(answer)){
                                                                    found = true;
                                                                }
                                                              }
                                                              if(found == false){
                                                                     userAnswers.add(answer);
                                                              }
                                                            context.sound.play(context.click, 1, 1, 1, 0, 1f);
                                                            final TextView t = (TextView)context.foundWords.getChildAt(userAnswers.size()+1);
                                                            System.out.println("listan koko (ei saisi olla 0): " + userAnswers.size());
                                                            if(t != null){
                                                              String wordToPaint = answer;
                                                              //System.out.println(userAnswers);
                                                              for (int i = 0; i < userAnswers.size(); i++) {
                                                                    //  System.out.println("taulukko josta pitaisi loytya: " + userAnswers.get(i));
                                                                    //  System.out.println("kayttajan antama jota etsitaan: " + answer);
                                                                      if (wordToPaint.equalsIgnoreCase(userAnswers.get(i))) {
                                                                                      break;
                                                                      }
                                                              }
                                                                                     
                                                              t.setText(answer);
                                                            }
                                                            context.counter.setText(""+counter+" / 10");
                                            context.currentScores.setText("Scores :"+(context.score+Math.round((float)counter/(float)wordAnswers.size()*Wordsearch.MAX_SCORE)));
                                                            counter++;
                                                            test=answer;
                                                            invalidate();
                                                            if(counter==wordAnswers.size()){
                                                                    context.showDialog(1);
                                                            }
                                                      }
                                           
                                            else{
                                                    AlertDialog.Builder ADB = new AlertDialog.Builder(context);
                                                            ADB.setTitle("Wrong Answer!");
                                                            ADB.setMessage("Your answer was wrong.");
                                                            ADB.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog,int id) {
                                                                       dialog.cancel();
                                                                    }
                                                            });
                                                            AlertDialog AD = ADB.create();
                                                            AD.show();
                                            }
                                            }
                                      })
                                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,int id) {
                                                    dialog.cancel();
                                            }
                                    });
     
                                    AlertDialog alertDialog = alertDialogBuilder.create();
     
                                    alertDialog.show();
     
               }
                                   
         invalidate(); //Ask for repaint
         return true; //Tell the system that the touch was handled
       }
     
       @Override
       public boolean onKeyDown(final int keyCode, final KeyEvent msg) {
          return true; //Tell the system that the touch was handled
       }
     
       @Override
       public boolean onKeyUp(final int keyCode, final KeyEvent msg) {
          return true; //Tell the system that the touch was handled
       }
     
       @Override
       public boolean onTrackballEvent(final MotionEvent e) {
          return true;
       }
       
       public ArrayList<Integer> getSpot(String text){
           String find = "";
               int x=0;
               boolean found = false;
               ArrayList<Integer> Values = new ArrayList<Integer>();
               Search1:
               for(int i = 0;i<crossword.length;i++){
                   Search2:
                       for(int j = 0; j<crossword[0].length;j++){
                          if(!crossword[i][j].equals("\0")){
                                   String a = text.charAt(x) + "";
                                       if(a.equalsIgnoreCase(crossword[i][j])){
                                         find += crossword[i][j];
                                         System.out.println(find + " " + i + " " + j);
                                         x++;
                                         Values.add(i);
                                         Values.add(j);
                                         if(find.equalsIgnoreCase(text)){
                                                 found=true;
                                                     break Search1;
                                         }
                                       }
                                       else{
                                        x=0;
                                       }
                               }
                               else{
                                       find = "";
                                       Values.clear();
                                       x=0;
                               }
                       }
                       if(found == true){
                         break;
                      }
               }
               if(found != true){
               Search3:
               for(int j = 0;j<crossword[0].length;j++){
                       Search4:
                       for(int i = 0; i<crossword.length;i++){
                         if(!crossword[i][j].equals("\0")){
                                       String a = text.charAt(x) + "";
                                       if(a.equalsIgnoreCase(crossword[i][j])){
                                         find += crossword[i][j];
                                         System.out.println(find + " " + i + " " + j);
                                         x++;
                                         Values.add(i);
                                         Values.add(j);
                                         if(find.equalsIgnoreCase(text)){
                                             found=true;
                                                     break Search3;
                                         }
                                       }
                                       else{
                                        x=0;
                                       }
                                       
                               }
                               else if(found == true){
                                      break;
                               }
                               else{
                                       find = "";
                                       Values.clear();
                                       x=0;
                               }
                       }
               }
               }
               return Values;
       }
     
       @Override
       public void onDraw(final Canvas c) {
               c.drawColor(Color.WHITE); //Clear previous frame
                  c.translate(letter_w*0.5f, letter_h*0.5f);
                  //Draw existing lines:
                  p.setColor(Color.GREEN);
                  p.setStrokeWidth(20);
                  for (final Line l : lines) {
                     c.drawLine(l.start.x*letter_w, l.start.y*letter_h, l.stop.x*letter_w, l.stop.y*letter_h, p);
                  }
                  //Draw the dynamic line if it exists:
                  if (start != null && stop != null) {
                     p.setColor(Color.BLUE);
                     c.drawLine(start.x*letter_w, start.y*letter_h, stop.x*letter_w, stop.y*letter_h, p);
                  }
                  p.setColor(Color.BLACK);
                  p.setStrokeWidth(0);
                  //Draw boxes:
                  c.translate(-letter_w*0.5f, -letter_h*0.5f);
                  for (int i = 0; i < nLetterX; i++) { //Vertical lines:
                     c.drawLine(i*letter_w, 0, i*letter_w, height, p);
                  }
                  c.drawLine(nLetterX*letter_w-1, 0, nLetterX*letter_w-1, height, p);
                  for (int i = 0; i < nLetterY; i++) { //Horizontal lines:
                     c.drawLine(0, i*letter_h, width, i*letter_h, p);
                  }
                  c.drawLine(0, nLetterY*letter_h-1, width, nLetterY*letter_h-1, p);
                 
                  c.translate(letter_w*-0.07f, letter_h*0.03f);
           for (int i = 0; i < crossword.length; i++) {
             for (int j = 0; j < crossword[0].length; j++) {
                if(crossword[i][j].equals("\0")){
               
                  final Paint paint = new Paint();
                  Path path = new Path();
                  float x = i*0.028f+i;
                  float y = j*-0.035f+j;
                  paint.setColor(Color.BLACK);
                  paint.setAntiAlias(true);
                  paint.setStyle(Paint.Style.FILL_AND_STROKE);
                 
                  float LH = letter_h * 0.98f;
                  float LW = letter_w * 1.04f;
                 
                  path.moveTo(y*LH, x*LW);
                  path.lineTo(y*LH, x*LW+LW);
                  path.lineTo(y*LH+LH, x*LW+LW);
                  path.lineTo(y*LH+LH, x*LW);
                  path.lineTo(y*LH, x*LW);
                 
                  path.close();
                  path.setFillType(Path.FillType.EVEN_ODD);
                  c.drawPath(path, paint);
                }
             }
          }
          c.translate(letter_w*0.5f, letter_h*0.7f);
          if(!getAnswer().equals("")){
         
          int i = 0;
          p.setTextSize(27);
          float LH = letter_h * 0.98f;
          float LW = letter_w * 1.04f;
          
          for (String s : usersAnswers) {
        	  ArrayList<Integer> values = getSpot(s);
        	  
        	  for(int x = 0, apu1 = 1; apu1<values.size(); x++ , apu1++){
                //  System.out.println(userAnswers.get(i)+ " " + Values.get(x) + " " +  Values.get(apu1));
                                c.drawText(crossword[values.get(x)][values.get(apu1)], values.get(apu1)*LH, values.get(x)*LW, p);
                                x++;
                                apu1++;
              }
          }
         }
          c.translate(letter_w*-0.35f, letter_h*-0.5f);
          float LH = letter_h * 0.95f;
          float LW = letter_w * 1.07f;
         int i=0;
         p.setTextSize(12);
         while(wordAnswers.size()>i){
              ArrayList<Integer> Values = getSpot(wordAnswers.get(i));
              int x = 0, apu1 = 1;
              int help = i+1;
              String number = "" + help;
              c.drawText(number, Values.get(apu1)*LH, Values.get(x)*LW, p);
           
            i++;
          }
       }
     
       /**
        * Calculate the index of the letter in the 2d array.
        * @param x Touchpoint X
        * @param y Touchpoint Y
        * @return Point-object containing the index
        */
       private Point calculateIndex(final float x, final float y) {
          final Point p = new Point();
          p.x = Math.round(x/width*nLetterX);
          p.y = Math.round(y/height*nLetterY);
          if (p.x < 0)
             p.x = 0;
          else if (p.x >= nLetterX)
             p.x = nLetterX-1;
          if (p.y < 0)
             p.y = 0;
          else if (p.y >= nLetterY)
             p.y = nLetterY-1;
          return p;
       }
     
       private void snapTo45(final Point start, final Point stop) {
          int angle = (int)Math.round(Math.toDegrees(Math.atan2(stop_acc.y - start_acc.y, stop_acc.x - start_acc.x)));
          //Round to closest angle divisible by 45:
          angle = (Math.round((float)angle/45f))*45;
          float dis = distance(start.x, start.y, stop.x, stop.y);
          do { //Roll this loop until the stop point is within the bounds:
             stop.x = (int) Math.round(start.x+Math.cos(Math.toRadians(angle))*dis);
             stop.y = (int) Math.round(start.y+Math.sin(Math.toRadians(angle))*dis);
             dis -= 1f;
          } while (stop.x >= nLetterX || stop.x < 0 || stop.y >= nLetterY || stop.y < 0);
       }
     
       /**
        * Get the string the player is currently drawing
        */
       public String getString() {
          sb.delete(0, sb.length());
          int dx, dy;
          if (start.x > stop.x)
             dx = -1;
          else if (start.x < stop.x)
             dx = 1;
          else
             dx = 0;
     
          if (start.y > stop.y)
             dy = -1;
          else if (start.y < stop.y)
             dy = 1;
          else
             dy = 0;
          int x = start.x;
          int y = start.y;
          sb.append(ar_letters[x][y]);
          while (x != stop.x || y != stop.y) {
             x += dx;
             y += dy;
             sb.append(ar_letters[x][y]);
          }
     
          return sb.toString();
       }
     
       public boolean checkAnswer(String answer){
               for(int i = 0;i<wordAnswers.size(); i++){
                       if(answer.equalsIgnoreCase(wordAnswers.get(i))){
                               return true;
                       }
               }
               return false;
       }
     
       /**
        * Calculates the euclidean distance between two points
        * @param x1 The X-coordinate of the first point
        * @param y1 The Y-coordinate of the first point
        * @param x2 The X-coordinate of the second point
        * @param y2 The Y-coordinate of the second point
        * @return The euclidean distance between the two given points.
        */
       public static float distance(final float x1, final float y1, final float x2, final float y2) {
          final float dx = x2 - x1;
          final float dy = y2 - y1;
          return FloatMath.sqrt((dx*dx) + (dy*dy));
       }
     
       private String[][] getTextArray() {
          final String[] stringArray = {
                "T", "U", "R", "K", "W", "E", "L", "M", "M", "A", "O", "S",
                "E", "W", "W", "H", "K", "K", "Y", "I", "T", "K", "T", "A",
                "N", "A", "E", "I", "N", "O", "S", "A", "S", "E", "E", "R",
                "M", "A", "R", "A", "O", "O", "S", "Y", "I", "R", "R", "D",
                "R", "A", "N", "E", "C", "O", "N", "Z", "O", "I", "A", "Y",
                "D", "T", "A", "Y", "L", "O", "N", "G", "I", "O", "U", "I",
                "A", "H", "C", "O", "S", "R", "I", "H", "F", "L", "A", "W",
                "P", "I", "O", "T", "O", "N", "A", "L", "R", "W", "U", "A",
                "G", "O", "W", "E", "T", "N", "J", "O", "R", "O", "R", "C",
                "D", "E", "Y" };
     
          final String[][] sArray = new String[nLetterX][nLetterY];
          for (int y = 0; y < nLetterY; y++) {
             for (int x = 0; x < nLetterX; x++) {
                sArray[x][y] = stringArray[y*nLetterX+x];
             }
          }
          return sArray;
       }
    }
     
    class Line {
       Point start;
       Point stop;
     
       public Line() {}
     
       public Line(final Point start, final Point stop) {
          this.start = start;
          this.stop = stop;
       }
    }


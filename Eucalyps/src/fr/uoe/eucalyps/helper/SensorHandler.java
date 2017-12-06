package fr.uoe.eucalyps.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;



public class SensorHandler implements SensorEventListener {
   private Display display;
   private SensorManager myManager;
   private Sensor accSensor;
   private final Sensor magSensor;
   private final float[] mGravity = new float[3];
   private final float[] mGeomagnetic = new float[3];
   private final float inR[] = new float[16];
   private final float outR[] = new float[16];
   private final float orientation[] = new float[3];
   private final float[][] noise_filter = new float[3][5];
   private byte noiseStep;

   public int rate = SensorManager.SENSOR_DELAY_GAME; //Sensor update speed
   public float tiltX;
   public float tiltY;
   public byte filterAmount = 5;
   public float gain = 0.8f;
   public boolean isActive = false;
   public boolean supported = false;

   public SensorHandler(final Context context) {
      //Set Sensor + Manager
      myManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
      //Sensor.TYPE_ORIENTATION is deprecated! To get orientation you must use accelerometer & magnetic field data! PERKELE.
      accSensor = myManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
      magSensor = myManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
      if(accSensor != null && magSensor != null) {
         supported = true;
      }
      else {
         supported = false;
         return;
      }

      final WindowManager w = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
      display = w.getDefaultDisplay();
   }

   public void setActive(final boolean active) {
      isActive = active;
      if (isActive && supported) {
         myManager.unregisterListener(this); //Remove old listener
         isActive = myManager.registerListener(this, accSensor, rate);
         myManager.registerListener(this, magSensor, rate);
         resetSensor();
      }
      else {
         myManager.unregisterListener(this);
      }
   }

   public void resetSensor() {
      for (int i = 0; i  < 3; i++) {
         for (int j = 0; j < filterAmount; j++) {
            noise_filter[i][j] = 0;
         }
      }
   }

   //Accelerometer:
   public void onAccuracyChanged(final Sensor sensor, final int accuracy) {}
   public void onSensorChanged(final SensorEvent e) {
      //If the sensor data is unreliable, return
      if (e.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
         return;

      if (e.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
         System.arraycopy(e.values, 0, mGravity, 0, e.values.length);
      else if (e.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
         System.arraycopy(e.values, 0, mGeomagnetic, 0, e.values.length);
      if (mGravity != null && mGeomagnetic != null) {
         final boolean success = SensorManager.getRotationMatrix(inR, null, mGravity, mGeomagnetic);
         if (success) {
            switch (display.getOrientation()) {
               case Surface.ROTATION_0:
                  SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR);
                  break;
               case Surface.ROTATION_90: //Horizontal
                  SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, outR);
                  break;
               case Surface.ROTATION_180: //Vertical
                  SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, outR);
                  break;
               case Surface.ROTATION_270:
                  SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
            }

            SensorManager.getOrientation(outR, orientation); //Radians.
            filterNoise(orientation); //azimut, pitch, roll

            tiltX = orientation[1];
            tiltY = orientation[2];
         }
      }
   }

   /**
    * Filtering works by getting an average of the last (filterAmount) values.
    * @param values orientation-array, which holds the most recent unfiltered z/x/y values.
    */
   private void filterNoise(final float[] values) {
      for (int i = 0; i < 3; i++) { //Iterate through z/x/y axis'
         noise_filter[i][noiseStep] = values[i]; //Store the most recent value to noise_filter array
         //Iterate through the noise_filter array (which holds last (filterAmount) values)
         values[i] = noise_filter[i][0]; //values[i] will act as a temp holder
         for (int j = 1; j < filterAmount; j++)
            values[i] += noise_filter[i][j]; //Sum up previous (filterAmount) steps

         values[i] = values[i]/filterAmount * gain; //Get average (divide by filterAmount) and apply gain
      }

      noiseStep++; //Next unfiltered value will stored in next slot
      if (noiseStep >= filterAmount)
         noiseStep = 0;
   }

   /*
    * The following variables and methods make coordinate remapping easier (it's confusing as hell) because they
    * allow you to change the axis' on the fly if you include these method calls for example in your onTouch().
    * NOTE: ONLY FOR DEBUGGING, YOU SHOULD NOT USE THESE IN THE FINAL PRODUCT.
    */
   public int firstAxis = 1; //These variables come from SensorManager class, for example this is SensorManager.AXIS_X
   public int secondAxis = 2; //SensorManager.AXIS_Y
   public void firstNext() {
      do {
         firstAxis++;
         if (firstAxis > 3 && firstAxis < 129)
            firstAxis = 129;
         else if (firstAxis > 131)
            firstAxis = 1;
         //Axis' cannot overlap. If this happens, take the next free axis:
      } while (firstAxis == secondAxis || Math.min(firstAxis, secondAxis)+128 == Math.max(firstAxis, secondAxis));
   }
   public void secondNext() {
      do {
         secondAxis++;
         if (secondAxis > 3 && secondAxis < 129)
            secondAxis = 129;
         else if (secondAxis > 131)
            secondAxis = 1;
      } while (firstAxis == secondAxis || Math.min(firstAxis, secondAxis)+128 == Math.max(firstAxis, secondAxis));
   }
   public void printValues() {
      switch (firstAxis) {
         case SensorManager.AXIS_X:
            Log.d("SensorHandler", "firstAxis: SensorManager.AXIS_X");
            break;
         case SensorManager.AXIS_Y:
            Log.d("SensorHandler", "firstAxis: SensorManager.AXIS_Y");
            break;   
         case SensorManager.AXIS_Z:
            Log.d("SensorHandler", "firstAxis: SensorManager.AXIS_Z");
            break;
         case SensorManager.AXIS_MINUS_X:
            Log.d("SensorHandler", "firstAxis: SensorManager.AXIS_MINUS_X");
            break;
         case SensorManager.AXIS_MINUS_Y:
            Log.d("SensorHandler", "firstAxis: SensorManager.AXIS_MINUS_Y");
            break;
         case SensorManager.AXIS_MINUS_Z:
            Log.d("SensorHandler", "firstAxis: SensorManager.AXIS_MINUS_Z");
            break;
      }
      switch (secondAxis) {
         case SensorManager.AXIS_X:
            Log.d("SensorHandler", "secondAxis: SensorManager.AXIS_X");
            break;
         case SensorManager.AXIS_Y:
            Log.d("SensorHandler", "secondAxis: SensorManager.AXIS_Y");
            break;   
         case SensorManager.AXIS_Z:
            Log.d("SensorHandler", "secondAxis: SensorManager.AXIS_Z");
            break;
         case SensorManager.AXIS_MINUS_X:
            Log.d("SensorHandler", "secondAxis: SensorManager.AXIS_MINUS_X");
            break;
         case SensorManager.AXIS_MINUS_Y:
            Log.d("SensorHandler", "secondAxis: SensorManager.AXIS_MINUS_Y");
            break;
         case SensorManager.AXIS_MINUS_Z:
            Log.d("SensorHandler", "secondAxis: SensorManager.AXIS_MINUS_Z");
            break;
      }
   }
}
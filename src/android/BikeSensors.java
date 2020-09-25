package cordova.plugin.bikesensors;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.boqun.port.uart.BoQunBike;
import com.boqun.port.uart.KeyCode;
import com.boqun.port.uart.MachineBean;
import com.boqun.port.uart.OnBikeDataListener;
import com.boqun.port.uart.SportState;

/**
 * This class echoes a string called from JavaScript.
 */
public class BikeSensors extends CordovaPlugin {

    Context context = this.cordova.getActivity().getApplicationContext();

    private static final String TAG = BikeSensors.class.getSimpleName();

    private boolean isHaveIncline = false;

    private int currentFanLevel = 0;

    private int currentLoad = 0, currentIncline = 0;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        
        if(action.equals("initBike"))
        {

            this.initBike(callbackContext);
            return true;

        }
        else if(action.equals("startBike"))
        {

            this.startBike(callbackContext);
            return true;
        }
        else if(action.equals("isPluginOK"))
        {

            this.isPluginOK(callbackContext);
            return true;
        }

        return false;
    }

    private void initBike(CallbackContext callback){
        try {
                
            BoQunBike.init(context, new OnBikeDataListener() {
                @Override
                public void onSportInitialize(MachineBean bean) {
                    StringBuilder builder = new StringBuilder();

                    builder.append("\r\n").append("WATT Group： ").append(bean.getWattGroup());
                    builder.append("\r\n").append("Wheel Diameter： ").append(bean.getWheelDiameter());
                    builder.append("\r\n").append("Client ID： ").append(bean.getClientId());
                    builder.append("\r\n").append("Min Load： ").append(bean.getMinLoad());
                    builder.append("\r\n").append("Max Load： ").append(bean.getMaxLoad());
                    builder.append("\r\n").append("Is there a fan： ").append((bean.isHaveFan() ? "Yes" : "No"));
                    builder.append("\r\n").append("Is there a incline： ").append((bean.isHaveIncline() ? "Yes" : "No"));
                    builder.append("\r\n").append("Min Incline： ").append(bean.getMinIncline());
                    builder.append("\r\n").append("Max Incline： ").append(bean.getMaxIncline());

                    MachineInfo.MIN_LOAD = bean.getMinLoad();
                    MachineInfo.MAX_LOAD = bean.getMaxLoad();
                    MachineInfo.MIN_INCLINE = bean.getMinIncline();
                    MachineInfo.MAX_INCLINE = bean.getMaxIncline();
                    MachineInfo.WHEEL_DIAMETER = bean.getWheelDiameter();

                    currentLoad = bean.getMinLoad();
                    currentIncline = bean.getMinIncline();

                    isHaveIncline = bean.isHaveIncline();


                    // Message msg = handler.obtainMessage(1);
                    // msg.obj = builder.toString();
                    // msg.sendToTarget();

                    Log.e(TAG, "onSportInitialize: " + builder.toString());
                }

                @Override
                public void onSportState(int state) {
                    String str = (state == SportState.STARTED ? "Start" : state == SportState.PAUSED ? "Pause" : state == SportState.STOPPED ? "Stop" : "Unknown");

                    // Message msg = handler.obtainMessage(3);
                    // msg.obj = "SportState：" + str;
                    // msg.sendToTarget();
                }

                @Override
                public void onSportData(int rpm, int heartRate) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("\r\n").append("RPM Value:： ").append(rpm);
                    builder.append("\r\n").append("PULSE Value： ").append(heartRate);

                    // Message msg = handler.obtainMessage(2);
                    // msg.obj = builder.toString();
                    // msg.sendToTarget();

                    Log.e(TAG, "onSportData: " + builder.toString());
                }

                @Override
                public void onExternalKeyEvent(int keyCode) {
                    String keyName = "Unknown Key";
                    switch (keyCode) {
                        case KeyCode.START_PAUSE:
                            keyName = "Start or Pause Key";
                            break;
                        case KeyCode.STOP:
                            keyName = "Stop key";
                            break;
                        case KeyCode.LOAD_UP:
                            keyName = "Load Up key";
                            break;
                        case KeyCode.LOAD_DOWN:
                            keyName = "Load Down Key";
                            break;
                        case KeyCode.INCLINE_UP:
                            keyName = "Incline Up Key";
                            break;
                        case KeyCode.INCLINE_DOWN:
                            keyName = "Incline Down Key";
                            break;
                        case KeyCode.FAN:
                            keyName = "Fan Key";
                            break;
                        default:
                            break;
                    }

                    // Message msg = handler.obtainMessage(3);
                    // msg.obj = "Press:" + keyName;
                    // msg.sendToTarget();

                    Log.e(TAG, "onExternalKeyEvent: " + keyCode);
                    
                }
            });

            callback.success("Creado el init");

        } catch (Exception ex) {

            callback.error("Ha ocurrido un error" + ex);
        }
    }
       
    

    private void startBike(CallbackContext callback){


            try {
                
                BoQunBike.start();
                BoQunBike.setLoadValue(currentIncline, 50);

                if (isHaveIncline) {
                    BoQunBike.setInclineValue(currentIncline, 100);
                }

                callback.success("start se está ejecutado");

            } catch (Exception ex) {

                callback.error("Ha ocurrido un error" + ex);
            }

    }

    private void isPluginOK(CallbackContext callback){
        callback.success("el plugin está OK");
    }
  
}


package com.boqun.serialportdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.boqun.port.uart.BoQunBike;
import com.boqun.port.uart.KeyCode;
import com.boqun.port.uart.MachineBean;
import com.boqun.port.uart.OnBikeDataListener;
import com.boqun.port.uart.SportState;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mTvInitValue;
    private TextView mTvLoadValue;
    private TextView mTvInclineValue;
    private Button mBtFactory;
    private TextView mTvSportValue;
    private Button mBtLoadUp;
    private Button mBtLoadDown;
    private Button mBtStartAndPause;
    private Button mBtStop;
    private Button mBtInclineUp;
    private Button mBtInclineDown;
    private Button mBtFan;

    private boolean isHaveIncline = false;

    private int currentFanLevel = 0;

    private int currentLoad = 0, currentIncline = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvInitValue = findViewById(R.id.mTvInitValue);
        mTvLoadValue = findViewById(R.id.mTvLoadValue);
        mTvInclineValue = findViewById(R.id.mTvInclineValue);
        mBtFactory = findViewById(R.id.mBtFactory);
        mTvSportValue = findViewById(R.id.mTvSportValue);
        mBtLoadUp = findViewById(R.id.mBtLoadUp);
        mBtLoadDown = findViewById(R.id.mBtLoadDown);
        mBtStartAndPause = findViewById(R.id.mBtStartAndPause);
        mBtStop = findViewById(R.id.mBtStop);
        mBtInclineUp = findViewById(R.id.mBtInclineUp);
        mBtInclineDown = findViewById(R.id.mBtInclineDown);
        mBtFan = findViewById(R.id.mBtFan);

        mBtStartAndPause.setOnClickListener(this);
        mBtStop.setOnClickListener(this);
        mBtLoadUp.setOnClickListener(this);
        mBtLoadDown.setOnClickListener(this);
        mBtInclineUp.setOnClickListener(this);
        mBtInclineDown.setOnClickListener(this);
        mBtFactory.setOnClickListener(this);
        mBtFan.setOnClickListener(this);

        try {
            BoQunBike.init(this, new OnBikeDataListener() {
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


                    Message msg = handler.obtainMessage(1);
                    msg.obj = builder.toString();
                    msg.sendToTarget();

                    Log.e(TAG, "onSportInitialize: " + builder.toString());
                }

                @Override
                public void onSportState(int state) {
                    String str = (state == SportState.STARTED ? "Start" : state == SportState.PAUSED ? "Pause" : state == SportState.STOPPED ? "Stop" : "Unknown");

                    Message msg = handler.obtainMessage(3);
                    msg.obj = "SportState：" + str;
                    msg.sendToTarget();
                }

                @Override
                public void onSportData(int rpm, int heartRate) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("\r\n").append("RPM Value:： ").append(rpm);
                    builder.append("\r\n").append("PULSE Value： ").append(heartRate);

                    Message msg = handler.obtainMessage(2);
                    msg.obj = builder.toString();
                    msg.sendToTarget();

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

                    Message msg = handler.obtainMessage(3);
                    msg.obj = "Press:" + keyName;
                    msg.sendToTarget();

                    Log.e(TAG, "onExternalKeyEvent: " + keyCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isSporting = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtStartAndPause:
                if (!isSporting) {
                    BoQunBike.start();
                    BoQunBike.setLoadValue(currentIncline, 50);
                    if (isHaveIncline) {
                        BoQunBike.setInclineValue(currentIncline, 100);
                    }
                    mBtStartAndPause.setText("Pause");
                } else {
                    BoQunBike.pause();
                    mBtStartAndPause.setText("Start");
                }
                isSporting = !isSporting;
                break;
            case R.id.mBtStop:
                BoQunBike.stop();

                isSporting = false;

                currentLoad = MachineInfo.MIN_LOAD;
                currentIncline = MachineInfo.MIN_INCLINE;

                mTvLoadValue.setText("LOAD:" + currentLoad);
                mTvInclineValue.setText("INCLINE:" + currentIncline);
                mBtStartAndPause.setText("Start");
                break;
            case R.id.mBtLoadUp:
                if (currentLoad < MachineInfo.MAX_LOAD) {
                    currentLoad += 1;
                    BoQunBike.setLoadValue(currentLoad);
                    mTvLoadValue.setText("LOAD:" + currentLoad);
                }
                break;
            case R.id.mBtLoadDown:
                if (currentLoad > MachineInfo.MIN_LOAD) {
                    currentLoad -= 1;
                    BoQunBike.setLoadValue(currentLoad);
                    mTvLoadValue.setText("LOAD:" + currentLoad);
                }
                break;
            case R.id.mBtInclineUp:
                if (currentIncline < MachineInfo.MAX_INCLINE) {
                    currentIncline += 1;
                    BoQunBike.setInclineValue(currentIncline);
                    mTvInclineValue.setText("INCLINE:" + currentIncline);
                }
                break;
            case R.id.mBtInclineDown:
                if (currentIncline > MachineInfo.MIN_INCLINE) {
                    currentIncline -= 1;
                    BoQunBike.setInclineValue(currentIncline);
                    mTvInclineValue.setText("INCLINE:" + currentIncline);
                }

                break;
            case R.id.mBtFan:
                if (MachineInfo.IS_HAVE_FAN) {
                    currentFanLevel = (currentFanLevel < 3) ? currentFanLevel += 1 : 0;
                    mBtFan.setText("FAN LEVEL:" + currentFanLevel);

                    BoQunBike.setFan(currentFanLevel);
                } else {
                    ToastUtil.show(getContext(), "No fan!");
                }
                break;
            case R.id.mBtFactory:
                startActivity(new Intent(this, FactoryActivity.class));
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    mTvInitValue.setText(String.valueOf(msg.obj));
                    mTvLoadValue.setText("LOAD:" + currentLoad);
                    mTvInclineValue.setText("INCLINE:" + currentIncline);
                    break;
                case 2:
                    mTvSportValue.setText(String.valueOf(msg.obj));
                    break;
                case 3:
                    ToastUtil.show(getContext(), String.valueOf(msg.obj));
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    public Context getContext() {
        return this;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        BoQunBike.destroy();
    }
}

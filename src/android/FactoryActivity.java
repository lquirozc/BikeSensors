package com.boqun.serialportdemo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.boqun.port.uart.BoQunBike;
import com.boqun.port.uart.OnAutoCorrectListener;
import com.boqun.port.uart.OnFactoryDataListener;


public class FactoryActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvWheelDiameterValue;
    private Button mBtWheelDiameterUp;
    private Button mBtWheelDiameterDown;
    private Button mBtWheelDiameterSave;
    private TextView mTvMotorStrokeLoadValue;
    private Button mBtMotorStrokeLoadUp;
    private Button mBtMotorStrokeLoadDown;
    private TextView mTvMotorStrokeAdcValue;
    private Button mBtMotorStrokeAdcUp;
    private Button mBtMotorStrokeAdcDown;
    private Button mBtMotorStrokeSave;
    private TextView mTvInclineStrokeLoadValue;
    private Button mBtInclineStrokeLoadUp;
    private Button mBtInclineStrokeLoadDown;
    private TextView mTvInclineStrokeAdcValue;
    private Button mBtInclineStrokeAdcUp;
    private Button mBtInclineStrokeAdcDown;
    private Button mBtInclineStrokeSave;
    private Button mBtInclineStrokeAuto;

    private int wheelDiameter = MachineInfo.WHEEL_DIAMETER;

    private int motorStrokeLoadValue = 1, motorStrokeAdcValue = 0;

    private int inclineStrokeLoadValue = 0, inclineStrokeAdcValue = 0;

    private Dialog autoControlDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.factory_layout);

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        findViews();

        mTvWheelDiameterValue.setText(String.valueOf(wheelDiameter));

        mTvMotorStrokeLoadValue.setText("LOAD:" + motorStrokeLoadValue);
        mTvMotorStrokeAdcValue.setText("ADC:" + motorStrokeAdcValue);

        mTvInclineStrokeLoadValue.setText("LOAD:" + inclineStrokeLoadValue);
        mTvInclineStrokeAdcValue.setText("ADC:" + inclineStrokeAdcValue);


        BoQunBike.getFactory().init(new OnFactoryDataListener() {
            @Override
            public void onFactoryLoadAdcChange(int value) {
                Message msg = handler.obtainMessage(1);
                msg.arg1 = value;
                msg.sendToTarget();
            }

            @Override
            public void onFactoryInclineAdcChange(int value) {
                Message msg = handler.obtainMessage(2);
                msg.arg1 = value;
                msg.sendToTarget();
            }

            @Override
            public void onFactoryAutoCorrection(int state, int value) {
                Message msg = handler.obtainMessage(3);
                msg.arg1 = value;
                msg.arg2 = state;
                msg.sendToTarget();
            }
        });

    }

    private void findViews() {

        mTvWheelDiameterValue = findViewById(R.id.mTvWheelDiameterValue);
        mBtWheelDiameterUp = findViewById(R.id.mBtWheelDiameterUp);
        mBtWheelDiameterDown = findViewById(R.id.mBtWheelDiameterDown);
        mBtWheelDiameterSave = findViewById(R.id.mBtWheelDiameterSave);
        mTvMotorStrokeLoadValue = findViewById(R.id.mTvMotorStrokeLoadValue);
        mBtMotorStrokeLoadUp = findViewById(R.id.mBtMotorStrokeLoadUp);
        mBtMotorStrokeLoadDown = findViewById(R.id.mBtMotorStrokeLoadDown);
        mTvMotorStrokeAdcValue = findViewById(R.id.mTvMotorStrokeAdcValue);
        mBtMotorStrokeAdcUp = findViewById(R.id.mBtMotorStrokeAdcUp);
        mBtMotorStrokeAdcDown = findViewById(R.id.mBtMotorStrokeAdcDown);
        mBtMotorStrokeSave = findViewById(R.id.mBtMotorStrokeSave);
        mTvInclineStrokeLoadValue = findViewById(R.id.mTvInclineStrokeLoadValue);
        mBtInclineStrokeLoadUp = findViewById(R.id.mBtInclineStrokeLoadUp);
        mBtInclineStrokeLoadDown = findViewById(R.id.mBtInclineStrokeLoadDown);
        mTvInclineStrokeAdcValue = findViewById(R.id.mTvInclineStrokeAdcValue);
        mBtInclineStrokeAdcUp = findViewById(R.id.mBtInclineStrokeAdcUp);
        mBtInclineStrokeAdcDown = findViewById(R.id.mBtInclineStrokeAdcDown);
        mBtInclineStrokeSave = findViewById(R.id.mBtInclineStrokeSave);
        mBtInclineStrokeAuto = findViewById(R.id.mBtInclineStrokeAuto);

        bindListener();
    }

    private void bindListener() {
        mBtWheelDiameterUp.setOnClickListener(this);
        mBtWheelDiameterDown.setOnClickListener(this);
        mBtWheelDiameterSave.setOnClickListener(this);
        mBtMotorStrokeLoadUp.setOnClickListener(this);
        mBtMotorStrokeLoadDown.setOnClickListener(this);
        mBtMotorStrokeAdcUp.setOnClickListener(this);
        mBtMotorStrokeAdcDown.setOnClickListener(this);
        mBtMotorStrokeSave.setOnClickListener(this);
        mBtInclineStrokeLoadUp.setOnClickListener(this);
        mBtInclineStrokeLoadDown.setOnClickListener(this);
        mBtInclineStrokeAdcUp.setOnClickListener(this);
        mBtInclineStrokeAdcDown.setOnClickListener(this);
        mBtInclineStrokeSave.setOnClickListener(this);
        mBtInclineStrokeAuto.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.mBtWheelDiameterUp) {
            wheelDiameter += 1;
            mTvWheelDiameterValue.setText(String.valueOf(wheelDiameter));
        } else if (id == R.id.mBtWheelDiameterDown) {
            wheelDiameter -= 1;
            mTvWheelDiameterValue.setText(String.valueOf(wheelDiameter));
        } else if (id == R.id.mBtWheelDiameterSave) {
            BoQunBike.getFactory().saveWheelDiameter(wheelDiameter);
        } else if (id == R.id.mBtMotorStrokeLoadUp) {
            motorStrokeLoadValue = Integer.parseInt(mTvMotorStrokeLoadValue.getText().toString().replace("LOAD:", ""));
            motorStrokeLoadValue += 1;
            BoQunBike.getFactory().setMotorStrokeLoad(motorStrokeLoadValue);
            mTvMotorStrokeLoadValue.setText("LOAD:" + motorStrokeLoadValue);
        } else if (id == R.id.mBtMotorStrokeLoadDown) {
            motorStrokeLoadValue = Integer.parseInt(mTvMotorStrokeLoadValue.getText().toString().replace("LOAD:", ""));
            motorStrokeLoadValue -= 1;
            BoQunBike.getFactory().setMotorStrokeLoad(motorStrokeLoadValue);
            mTvMotorStrokeLoadValue.setText("LOAD:" + motorStrokeLoadValue);
        } else if (id == R.id.mBtMotorStrokeAdcUp) {
            motorStrokeAdcValue = Integer.parseInt(mTvMotorStrokeAdcValue.getText().toString().replace("ADC:", ""));
            motorStrokeAdcValue += 1;
            mTvMotorStrokeAdcValue.setText("ADC:" + motorStrokeAdcValue);
        } else if (id == R.id.mBtMotorStrokeAdcDown) {
            motorStrokeAdcValue = Integer.parseInt(mTvMotorStrokeAdcValue.getText().toString().replace("ADC:", ""));
            motorStrokeAdcValue -= 1;
            mTvMotorStrokeAdcValue.setText("ADC:" + motorStrokeAdcValue);
        } else if (id == R.id.mBtMotorStrokeSave) {
            BoQunBike.getFactory().saveMotorStroke(motorStrokeLoadValue, motorStrokeAdcValue);
        } else if (id == R.id.mBtInclineStrokeLoadUp) {
            inclineStrokeLoadValue = Integer.parseInt(mTvInclineStrokeLoadValue.getText().toString().replace("LOAD:", ""));
            inclineStrokeLoadValue += 1;
            BoQunBike.getFactory().setInclineStrokeLoad(inclineStrokeLoadValue);
            mTvInclineStrokeLoadValue.setText("LOAD:" + inclineStrokeLoadValue);
        } else if (id == R.id.mBtInclineStrokeLoadDown) {
            inclineStrokeLoadValue = Integer.parseInt(mTvInclineStrokeLoadValue.getText().toString().replace("LOAD:", ""));
            inclineStrokeLoadValue -= 1;
            BoQunBike.getFactory().setInclineStrokeLoad(inclineStrokeLoadValue);
            mTvInclineStrokeLoadValue.setText("LOAD:" + inclineStrokeLoadValue);
        } else if (id == R.id.mBtInclineStrokeAdcUp) {
            inclineStrokeAdcValue = Integer.parseInt(mTvInclineStrokeAdcValue.getText().toString().replace("ADC:", ""));
            inclineStrokeAdcValue += 1;
            mTvInclineStrokeAdcValue.setText("ADC:" + inclineStrokeAdcValue);
        } else if (id == R.id.mBtInclineStrokeAdcDown) {
            inclineStrokeAdcValue = Integer.parseInt(mTvInclineStrokeAdcValue.getText().toString().replace("ADC:", ""));
            inclineStrokeAdcValue -= 1;
            mTvInclineStrokeAdcValue.setText("ADC:" + inclineStrokeAdcValue);
        } else if (id == R.id.mBtInclineStrokeSave) {
            BoQunBike.getFactory().saveInclineStroke(inclineStrokeLoadValue, inclineStrokeAdcValue);
        } else if (id == R.id.mBtInclineStrokeAuto) {
            View view = LayoutInflater.from(this).inflate(R.layout.msg_text_layout, null);
            TextView textView = view.findViewById(R.id.mTvMsg);
            textView.setText("Auto correcting...");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("InclineStroke");
            builder.setView(view);
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BoQunBike.getFactory().stopAutoCorrection();
                }
            });
            autoControlDialog = builder.create();

            BoQunBike.getFactory().startAutoCorrection(new OnAutoCorrectListener() {
                @Override
                public void onAutoCorrectStart() {
                    autoControlDialog.show();
                }

                @Override
                public void onAutoCorrectStop() {
                    if (autoControlDialog.isShowing()) {
                        autoControlDialog.dismiss();
                        autoControlDialog = null;
                    }
                }
            });
        }

    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    mTvMotorStrokeAdcValue.setText("ADC:" + msg.arg1);
                    break;
                case 2:
                    mTvInclineStrokeAdcValue.setText("ADC:" + msg.arg1);
                    break;
                case 3:
                    if (autoControlDialog != null) {
                        TextView view = autoControlDialog.findViewById(R.id.mTvMsg);
                        if (view != null) {
                            view.setText("State: " + msg.arg2 + " Value: " + msg.arg1);
                        }
                        if (msg.arg2 == 0) {
                            BoQunBike.getFactory().stopAutoCorrection();
                        }
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        BoQunBike.getFactory().exit();
    }
}

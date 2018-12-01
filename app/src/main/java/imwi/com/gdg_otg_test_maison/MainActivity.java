package imwi.com.gdg_otg_test_maison;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


public class MainActivity extends Activity implements UsbSerial.OnUsbReconnectHandler {
    private static final String TAG = "Main";

    private UsbSerial serial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serial = new UsbSerial(this);
        serial.Init(this);
        serial.Open();

        Button mFindButton = (Button) findViewById(R.id.btnFind);
        mFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serial.Write("hello");
            }
        });
        WakeScreenUp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        serial.Open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        serial.Close();
    }

    void WakeScreenUp(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    public void OnUsbReconnect() {
        Log.i(TAG, "USB reconected!");
        WakeScreenUp();
    }
}

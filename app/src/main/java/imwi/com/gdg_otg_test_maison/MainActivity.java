package imwi.com.gdg_otg_test_maison;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {
    private static final String TAG = "Main";

    private msOTG serial = new msOTG();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serial.Init(this);

        Button mFindButton = (Button) findViewById(R.id.btnFind);
        mFindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serial.Write("hello");
            }
        });

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
}

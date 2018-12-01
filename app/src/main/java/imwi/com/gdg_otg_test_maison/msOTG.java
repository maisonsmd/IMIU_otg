package imwi.com.gdg_otg_test_maison;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import imwi.com.gdg_otg_test_maison.usbserial.driver.UsbSerialDriver;
import imwi.com.gdg_otg_test_maison.usbserial.driver.UsbSerialPort;
import imwi.com.gdg_otg_test_maison.usbserial.driver.UsbSerialProber;

public class msOTG {

    private static final String TAG = "Main";
    private static UsbSerialPort sPort = null;
    private static UsbDeviceConnection mConnection;
    private PendingIntent mPermissionIntent;
    private Context mContext;


    public void Write(String text) {
        if (sPort == null) {
            Toast.makeText(mContext.getApplicationContext(), "cannot write serial", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "cannot write serial");
            return;
        }
        try {
            sPort.write(text.getBytes(), 10);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void Init(Context context) {
        mContext = context;

        mPermissionIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        mContext.registerReceiver(mUsbReceiver, filter);
    }

    public boolean Open() {
        sPort = FindConnection();
        if (sPort == null)
            return false;
        try {
            sPort.open(mConnection);
            sPort.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
            return true;
        } catch (Exception e) {
            Toast.makeText(mContext.getApplicationContext(), "Cannot open connection", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void Close() {
        try {
            if (sPort != null)
                sPort.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private UsbSerialPort FindConnection() {
        UsbManager usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);

        try {
            UsbSerialDriver driver = availableDrivers.get(0);
            UsbDevice device = driver.getDevice();
            if (availableDrivers.isEmpty())
                return null;

            usbManager.requestPermission(device, mPermissionIntent);

            mConnection = usbManager.openDevice(device);
            if (mConnection == null)
                return null;
            return driver.getPorts().get(0);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                        }
                    } else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };
}

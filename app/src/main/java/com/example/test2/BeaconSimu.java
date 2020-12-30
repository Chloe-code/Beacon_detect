package com.example.test2;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import static java.lang.Thread.sleep;

public class BeaconSimu extends Fragment {

    private EditText distance,rssi;
    private Button updatebutton;
    String result;
    private Handler handler = new Handler();

    public BeaconSimu() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v =  inflater.inflate(R.layout.beacon_simu, container, false);
        distance = v.findViewById(R.id.major_simu);
        rssi = v.findViewById(R.id.minor_simu);
        updatebutton = v.findViewById(R.id.make_beacon);

        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.post(task);
            }
        });
        return v;
    }
    private void init() {
        new Thread (){
            public void run() {
                result = ws_test2.beaconupdate(distance.getText().toString(),Integer.valueOf(rssi.getText().toString()));
            }
        }.start();
    }
    private Runnable task =new Runnable() {
        public void run() {
            init();
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(Integer.valueOf(result)==1)
            {
                Toast.makeText(getContext(),"update",Toast.LENGTH_SHORT).show();
            }
        }
    };
}

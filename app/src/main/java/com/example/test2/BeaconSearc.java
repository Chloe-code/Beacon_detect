package com.example.test2;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class BeaconSearc extends Fragment implements BeaconConsumer{

    private LinearLayout linearLayout;
    private RecyclerView rv;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerAdapter adapter;
    private BeaconManager beaconManager;
    private ProgressBar pb;
    private TextView tv1,tv2;
    private ArrayList<Integer> averagerssi = new ArrayList<>();
    int sum =0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getting beaconManager instance (object) for Main Activity class
        beaconManager = BeaconManager.getInstanceForApplication(getActivity());

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        //Binding MainActivity to the BeaconService.
        //beaconManager.bind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.beacon_search, container, false);
        linearLayout = v.findViewById(R.id.Relative_One);
        rv = v.findViewById(R.id.search_recycler);
        pb = v.findViewById(R.id.pb);
        tv1 = v.findViewById(R.id.datasum);
        tv2 = v.findViewById(R.id.rssiaverage);
        Button btnstart = v.findViewById(R.id.startagain);
        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
                Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
            }
        });
        Button btnstop = v.findViewById(R.id.stop);
        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
                Toast.makeText(getApplicationContext(), "stop", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    @Override
    public void onBeaconServiceConnect() {

        final Region region = new Region("myBeaons",null, null, null);
        beaconManager.addMonitorNotifier(new MonitorNotifier() {

            @Override
            public void didEnterRegion(Region region) {
                System.out.println("ENTER ------------------->");
                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                System.out.println("EXIT----------------------->");
                try {
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                System.out.println( "I have just switched from seeing/not seeing beacons: "+state);
            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {

            //This Override method tells us all the collections of beacons and their details that are detected within the range by device

            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                if (beacons.size() > 0) {
                    try{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.setVisibility(View.INVISIBLE);
                                linearLayout.setVisibility(View.GONE);
                                rv.setVisibility(View.VISIBLE);
                                layoutManager = new LinearLayoutManager(getActivity());
                                rv.setLayoutManager(layoutManager);
                            }
                       });
                    } catch (Exception e) {   }
                    final ArrayList<ArrayList<String>> arrayList = new ArrayList<ArrayList<String>>();

                    for (Beacon b:beacons){

                        String uuid = String.valueOf(b.getId1());
                        String major = String.valueOf(b.getId2());
                        String minor = String.valueOf(b.getId3());

                        double distance1 =b.getDistance();
                        String distance = String.valueOf(Math.round(distance1*100.0)/100.0);

                        ArrayList<String> arr = new ArrayList<String>();
                        if(uuid.equals("e2c56db5-dffb-48d2-b060-d0f5a71096e0"))
                        {
                            arr.add(uuid);
                            arr.add(major);
                            arr.add(minor);
                            arr.add(b.getRssi() + " dBm");
                            arrayList.add(arr);
                            if(uuid!=null)
                            {
                                Log.v("review","有收到資料");
                                averagerssi.add(b.getRssi());
                            }
                        }
                    }

                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                adapter = new RecyclerAdapter(arrayList);
                                rv.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e){   }
                }

                else if (beacons.size()==0) {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb.setVisibility(View.INVISIBLE);
                                linearLayout.setVisibility(View.VISIBLE);
                                rv.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception e){   }
                }
            }
        });
        try {
            //Tells the BeaconService to start looking for beacons that match the passed Region object.
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e){    }
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return getActivity().bindService(intent, serviceConnection, i);
    }

    // Override onDestroy Method
    @Override
    public void onDestroy() {
        super.onDestroy();
        //Unbinds an Android Activity or Service to the BeaconService to avoid leak.
        beaconManager.unbind(this);
    }

    public void start()
    {
        tv1.setText(null);
        tv2.setText(null);
        sum = 0;
        averagerssi = new ArrayList<>();
        beaconManager.bind(this);
    }
    public void stop()
    {
        for(int i=0; i<averagerssi.size(); i++)
        { sum = sum + averagerssi.get(i); }
        tv1.setText("Received :  "+String.valueOf(averagerssi.size()));
        tv2.setText("Average :  "+String.valueOf(sum/averagerssi.size()));
        beaconManager.unbind(this);
    }
}

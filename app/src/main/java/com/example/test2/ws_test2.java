package com.example.test2;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ws_test2
{
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final String URL = "http://**********/WebService/webservice.asmx";     //WebService URL

    public static String beaconupdate(String distance, Integer rssi)
    {
        String SOAP_ACTION = " http://tempuri.org/beaconupdate";
        String METHOD_NAME = "beaconupdate";
        try {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("distance",distance);
            request.addProperty("rssi",rssi);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.bodyOut = request;
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            envelope.encodingStyle = "utf-8";
            HttpTransportSE ht = new HttpTransportSE(URL);
            ht.call(SOAP_ACTION, envelope);

            //Get return data
            SoapObject object = (SoapObject) envelope.bodyIn;
            // Change result data to string
            String result = object.getProperty(0).toString();
            return result;
        } catch (Exception e) {
            return e.toString();
        }
    }
}

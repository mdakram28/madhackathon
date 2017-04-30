package com.example.kislay.govvote;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kislay.govvote.models.AadharInfo;
import com.example.kislay.govvote.services.GPSTracker;

import com.example.kislay.govvote.services.LocationAddress;
import com.google.android.gms.samples.vision.barcodereader.BarcodeCapture;
import com.google.android.gms.samples.vision.barcodereader.BarcodeGraphic;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.text.Text;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import xyz.belvi.mobilevisionbarcodescanner.BarcodeRetriever;

public class LoginActivity extends AppCompatActivity implements BarcodeRetriever{

    private EditText uid;
    TextView tv ;
    Button login ;
    public TextView textView;
    private static final String TAG = "barcode";
    GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        uid = (EditText)findViewById(R.id.adharuid);
        tv = (TextView)findViewById(R.id.uid);
        //barcode
        BarcodeCapture barcodeCapture = (BarcodeCapture) getSupportFragmentManager().findFragmentById(R.id.barcode);
        barcodeCapture.setRetrieval(this);
        barcodeCapture.setShowDrawRect(true);
        //location

                // create class object
                gps = new GPSTracker(LoginActivity.this);

                // check if GPS enabled
                if(gps.canGetLocation()){

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    LocationAddress locationAddress = new LocationAddress();
                    locationAddress.getAddressFromLocation(latitude, longitude,getApplicationContext(), new GeocoderHandler());
                    // \n is for new line
                    //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }


    }

    private void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                LoginActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        LoginActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    @Override
    public void onRetrieved(final Barcode barcode) {
        Log.d(TAG, "Barcode read: " + barcode.displayValue);
        String barCode = barcode.rawValue;
        try {
            AadharInfo newCard = new AadhaarXMLParser().parse(barCode);
            Log.d("name",newCard.getName());
            uid.setVisibility(View.GONE);
            tv.setText(newCard.getUid());

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("code retrieved")
                        .setMessage(barcode.displayValue);
                builder.show();
            }
        });*/
    }

    @Override
    public void onRetrievedMultiple(Barcode barcode, List<BarcodeGraphic> list) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onRetrievedFailed(String s) {

    }
    class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            textView = (TextView)findViewById(R.id.location);
            textView.setText(locationAddress);
        }
    }
}
class AadhaarXMLParser {
    // We don't use namespaces
    private static final String ns = null;

    private AadharInfo aadhaarCard;

    public AadharInfo parse(String xmlContent) throws XmlPullParserException, IOException {
        InputStream in = new ByteArrayInputStream(xmlContent.getBytes());
        aadhaarCard = new AadharInfo();
        //aadhaarCard.originalXML = xmlContent;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readFeed(parser);
        } finally {
            in.close();
        }
        return aadhaarCard;
    }

    private void readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {

        parser.require(XmlPullParser.START_TAG, ns, "PrintLetterBarcodeData");

        aadhaarCard.setUid(parser.getAttributeValue(null, "uid"));//
        aadhaarCard.setName(""+parser.getAttributeValue(null, "name"));// F  L
        aadhaarCard.setGender(parser.getAttributeValue(null, "gender")); // M F
        aadhaarCard.setYob(parser.getAttributeValue(null, "yob"));// Year
        aadhaarCard.setCo(""+parser.getAttributeValue(null, "co"));
        //aadhaarCard.se""+parser.getAttributeValue(null, "house")); //
        aadhaarCard.setPc(parser.getAttributeValue(null, "pc"));
        aadhaarCard.setLoc(parser.getAttributeValue(null, "loc"));
        aadhaarCard.setVtc(parser.getAttributeValue(null, "vtc")); //
        aadhaarCard.setPo(parser.getAttributeValue(null, "po"));
        aadhaarCard.setDist(""+parser.getAttributeValue(null, "dist")); //
        aadhaarCard.setSubdist(parser.getAttributeValue(null, "subdist"));
        aadhaarCard.setState(parser.getAttributeValue(null, "state")); //
        aadhaarCard.setPc(""+parser.getAttributeValue(null, "pc")); //
        aadhaarCard.setDob(""+parser.getAttributeValue(null, "dob"));
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}

package com.meikarta.hrapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity
        implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private LocationManager locationManager;
    Button btn_logout, btnin,btnout,btnbrin,btnbrout,btnovein,btnoverout;
    TextView txt_id, txt_username,txt_nickname,lat_location,long_location,txt_login,txt_logout;
    String id, user_id,user_nickname,latitude,longitude,time_in,time_out,latitudeout,longitudeout;
    SharedPreferences sharedpreferences;
    private static final int MY_LOCATION_REQUEST_CODE = 1;
    public static final String TAG_ID = "id";
    public static final String TAG_USERNAME = "user_id";
    public static final String TAG_NICKNAME = "user_nickname";
    public final static String TAG_LAT = "latitude_in";
    public final static String TAG_LONG = "longitude_in";
    public final static String TAG_LAT1 = "latitude_out";
    public final static String TAG_LONG1 = "longitude_out";
    public final static String TAG_LOGIN = "time_in";
    public final static String TAG_LOGOUT = "time_out";
    ProgressDialog pDialog;
    Intent intent;
    int success;
    ConnectivityManager conMgr;
    private String url = Server.URL + "regis.php";
    private String url1 = Server.URL + "time_out.php";

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";


    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        lat_location = (TextView) findViewById(R.id.lat_location);
        long_location = (TextView) findViewById(R.id.long_location);
        txt_id = (TextView) findViewById(R.id.txt_id);
        txt_username = (TextView) findViewById(R.id.txt_username);
        txt_nickname = (TextView) findViewById(R.id.txt_nickname);
        txt_login = (TextView) findViewById(R.id.txt_login);
        txt_logout = (TextView) findViewById(R.id.txt_logout);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btnin = (Button) findViewById(R.id.btnin);
        btnout = (Button) findViewById(R.id.btnout);
        /*btnbrin = (Button) findViewById(R.id.btnbrin);
        btnbrout = (Button) findViewById(R.id.btnbrout);
        btnovein = (Button) findViewById(R.id.btnoverin);
        btnoverout = (Button) findViewById(R.id.btnoverout);*/

        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        id = getIntent().getStringExtra(TAG_ID);
        user_id = getIntent().getStringExtra(TAG_USERNAME);
        user_nickname = getIntent().getStringExtra(TAG_NICKNAME);
        latitude = getIntent().getStringExtra(TAG_LAT);
        longitude = getIntent().getStringExtra(TAG_LONG);
        latitudeout = getIntent().getStringExtra(TAG_LAT1);
        longitudeout = getIntent().getStringExtra(TAG_LONG1);
        time_in = getIntent().getStringExtra(TAG_LOGIN);
        time_out = getIntent().getStringExtra(TAG_LOGOUT);
        txt_id.setText("NIK              : " + id);
        txt_username.setText("User ID       : " + user_id);
        txt_nickname.setText("Name          : " + user_nickname);
        txt_login.setText("IN                 : " + time_in);
        txt_logout.setText("OUT             : " + time_out);
/*        lat_location.setText("Latitude      : " + latitude);
       long_location.setText("Longitude     : " + longitude);*/


        btn_logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // update login session ke FALSE dan mengosongkan nilai id dan username
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(Login.session_status, false);
                editor.putString(TAG_ID, null);
                editor.putString(TAG_USERNAME, null);
                editor.putString(TAG_NICKNAME, null);
                editor.putString(TAG_LAT, null);
                editor.putString(TAG_LONG, null);
                editor.putString(TAG_LAT1, null);
                editor.putString(TAG_LONG1, null);
                editor.putString(TAG_LOGIN, null);
                editor.putString(TAG_LOGOUT, null);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, Login.class);
                finish();
                startActivity(intent);
            }
        });

        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }*/
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                mMap.setMyLocationEnabled(true);
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                // MY_PERMISSIONS_REQUEST_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        onLocationChanged(location);

        btnin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String nik = txt_id.getText().toString();
                String name = txt_username.getText().toString();
                String nickname = txt_nickname.getText().toString();
                String latitude = lat_location.getText().toString();
                String longitude = long_location.getText().toString();
                String login = txt_login.getText().toString();
                String logout = txt_logout.getText().toString();
                //String confirm_password = txt_confirm_password.getText().toString();
                checkRegister(nik, name,nickname,latitude,longitude,login,logout);

            }
        });
        btnout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String nik = txt_id.getText().toString();
                String name = txt_username.getText().toString();
                String nickname = txt_nickname.getText().toString();
                String latitudeout = lat_location.getText().toString();
                String longitudeout = long_location.getText().toString();
                String login = txt_login.getText().toString();
                String logout = txt_logout.getText().toString();
                //String confirm_password = txt_confirm_password.getText().toString();
                checkOut(nik, name,nickname,latitudeout,longitudeout,login,logout);

            }
        });
    }



    @Override
    public boolean onMyLocationButtonClick() {

        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).

        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // TODO: Before enabling the My Location layer, you must request
        // location permission from the user. This sample does not include
        // a request for location permission.
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }


    }*/

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        if(location!= null) {
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            long_location.setText("" + longitude + "");
            lat_location.setText("" + latitude + "");
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void checkRegister(final String nik, final String name,final String nickname, final String longitude, final String latitude,final String login, final String logout ) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Absensi ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Absensi Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Successfully Absensi!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        txt_username.setText("");
                        txt_id.setText("");
                        txt_login.setText("");
                        txt_logout.setText("");
                        txt_nickname.setText("");
                        lat_location.setText("");
                        long_location.setText("");

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("id", nik);
                params.put("time_in", login);
                params.put("time_out", logout);
                params.put("user_nickname", user_nickname);
                params.put("longitude_in", longitude);
                params.put("latitude_in", latitude);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void checkOut(final String nik, final String name,final String nickname, final String longitudeout, final String latitudeout,final String login, final String logout ) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Absensi ...");
        showDialog();

        StringRequest strOut = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Absensi Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {

                        Log.e("Successfully Absensi!", jObj.toString());

                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        txt_username.setText("");
                        txt_id.setText("");
                        txt_login.setText("");
                        txt_logout.setText("");
                        txt_nickname.setText("");
                        lat_location.setText("");
                        long_location.setText("");

                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("id", nik);
                params.put("time_in", login);
                params.put("time_out", logout);
                params.put("user_nickname", user_nickname);
                params.put("longitude_out", longitudeout);
                params.put("latitude_out", latitudeout);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strOut, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    @Override
    public void onBackPressed() {
        intent = new Intent(MainActivity.this, Login.class);
        finish();
        startActivity(intent);
    }
}
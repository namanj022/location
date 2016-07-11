package com.survey.worklake.lo;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationProvider.Provider,
        GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationProvider mLocationProvider;
    public List<LatLng> locationList = new ArrayList<>();
    public List<Marker> markerList = new ArrayList<>();
    public ArrayList<String> locationString = new ArrayList<>();
    public static List<Boolean> isFavourite = new ArrayList<>();
    public List<Float> distance = new ArrayList<>();
    ListView mdrawerList;
    DrawerLayout mDrawerLayout;
    LatLng dest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mLocationProvider = new LocationProvider(this, this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mdrawerList = (ListView) findViewById(R.id.drawerList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerTitle(GravityCompat.START,"Locations");
        mdrawerList.setAdapter(new CustomAdapter(locationString,this));
        mdrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mLocationProvider.disconnect();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mLocationProvider.connect();
    }
    public void setNewLocation(Location location) {
        LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
        String loc =getAddress(latlng);
        Marker m = mMap.addMarker(new MarkerOptions().position(latlng).title(loc));
        markerList.add(m);
        locationString.add(loc);
        isFavourite.add(false);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
        locationList.add(latlng);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        dest = latLng;
        String address;
        Geocoder mGeocoder = new Geocoder(this,Locale.getDefault());
        address = getAddress(latLng);
        Marker m = mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        markerList.add(m);
        locationString.add(address);

        draw(locationList.get(locationList.size() - 1), latLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        locationList.add(latLng);
        isFavourite.add(false);
        mdrawerList.setAdapter(new CustomAdapter(locationString,this));
    }
    public String getAddress(LatLng latLng) {
        List<Address> addresses;
        Geocoder mGeocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            return address + ", " + city + ", " + ", " + state + ", " + country;
        } catch (Exception e) {
            return "Error";
        }
    }



    public void draw(LatLng origin,LatLng destination) {
        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(origin,destination)
                .width(10)
                .color(Color.CYAN));
    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            LatLng latlng = locationList.get((int) id);
            markerList.get((int) id).showInfoWindow();
            mDrawerLayout.closeDrawer(GravityCompat.START);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));

        }
    }
    public void onFavouriteClick(View view) {
        for(int i = 0 ; i < locationList.size() ; i++) {
            if(isFavourite.get(i) == false) {
                markerList.get(i).setVisible(false);
            }
            else {
                markerList.get(i).setVisible(true);
            }
        }
    }
}



























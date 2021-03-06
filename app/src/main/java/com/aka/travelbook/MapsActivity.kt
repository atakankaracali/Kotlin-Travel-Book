package com.aka.travelbook

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener { myListener }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location) {
                if(location != null){

                    val sharedPreferences = this@MapsActivity.getSharedPreferences("com.aka.travelbook",Context.MODE_PRIVATE)
                    val firstTimeCheck = sharedPreferences.getBoolean("notFirstTime",false)
                    if(firstTimeCheck ==false){
                        mMap.clear()
                        val newUserLocation = LatLng(location.latitude,location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newUserLocation,15f))
                        sharedPreferences.edit().putBoolean("notFirstTime",true).apply()
                    }

                }

            }


        }
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
            }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null){
                val lastLocationLatLng = LatLng(lastLocation.latitude,lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocationLatLng,15f))
            }
        }

    }
    val myListener = object : GoogleMap.OnMapLongClickListener{
        override fun onMapLongClick(p0: LatLng?) {

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1){
            if(grantResults.size>0){
                if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2,2f,locationListener)
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
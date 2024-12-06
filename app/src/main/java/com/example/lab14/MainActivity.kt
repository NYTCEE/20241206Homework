package com.example.Rebuild14

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadMap()
    }

    private fun loadMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        if (checkLocationPermissions()) {
            setupMap(map)
        } else {
            requestLocationPermissions()
        }
    }

    private fun checkLocationPermissions() = LOCATION_PERMISSIONS.all { permission ->
        ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            LOCATION_PERMISSIONS,
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    @SuppressLint("MissingPermission")
    private fun setupMap(map: GoogleMap) {
        // 只有在確認已獲得權限後才啟用我的位置
        if (checkLocationPermissions()) {
            map.isMyLocationEnabled = true
        }

        val locations = listOf(
            LatLng(25.033611, 121.565000) to "台北101",
            LatLng(25.047924, 121.517081) to "台北車站"
        )

        // 新增地圖標記
        locations.forEach { (latLng, title) ->
            map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .draggable(true)
            )
        }

        // 繪製路線
        val polylineOptions = PolylineOptions()
            .add(LatLng(25.033611, 121.565000))
            .add(LatLng(25.032435, 121.534905))
            .add(LatLng(25.047924, 121.517081))
            .color(Color.BLUE)
            .width(10f)

        map.addPolyline(polylineOptions)

        // 移動視角
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(25.035, 121.54),
                13f
            )
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (checkLocationPermissions()) {
                // 重新載入地圖
                loadMap()
            } else {
                finish()
            }
        }
    }
}
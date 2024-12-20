

package com.example.st10180168_st10102524_wonder_wings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.st10180168_st10102524_wonder_wings.Game.GameActivity
import com.example.st10180168_st10102524_wonder_wings.Models.HotspotModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.concurrent.thread


class Hotpots : AppCompatActivity(), OnMapReadyCallback, LocationDataCallback {

    // Map and location
    private lateinit var locationName: String
    private var isPermissionGranted = false
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapView: MapView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var lat = 0.0
    private var lon = 0.0
    private var destlat = 0.0
    private var destlon = 0.0

    // Nav buttons
    private lateinit var fabMenu: FloatingActionButton
    private lateinit var menuGame: FloatingActionButton
    private lateinit var menuSettings: FloatingActionButton
    private lateinit var menuAddObservation: FloatingActionButton
    private lateinit var menuMyObs: FloatingActionButton
    private lateinit var menuChallenges: FloatingActionButton
    private var mapStyleChosen = 0

    // Menu movement
    private lateinit var fabClose: Animation
    private lateinit var fabOpen: Animation
    private lateinit var fabClock: Animation
    private lateinit var fabAnticlock: Animation
    private var isOpen = false

    //==============================================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotpots)

        // MAP code
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // NAV menu popup code
        fabClose = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_close)
        fabOpen = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_open)
        fabClock = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_clock)
        fabAnticlock = AnimationUtils.loadAnimation(applicationContext, R.anim.fab_rotate_anticlock)

        fabMenu = findViewById(R.id.fabMenu)
        menuGame = findViewById(R.id.menu_game)
        menuSettings = findViewById(R.id.menu_settings)
        menuAddObservation = findViewById(R.id.menu_addObservation)
        menuMyObs = findViewById(R.id.menu_viewObservation)
        menuChallenges = findViewById(R.id.menu_challenges)

        menuMyObs.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            intent.putExtra("desiredFragmentIndex", 1)
            startActivity(intent)
        }
        menuGame.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            close()
        }
        menuAddObservation.setOnClickListener {
            val intent = Intent(this, AddObservation::class.java)
            startActivity(intent)
            close()
        }
        menuSettings.setOnClickListener {
            val intent = Intent(this, Settings::class.java)
            intent.putExtra("desiredFragmentIndex", 0)
            startActivity(intent)
        }
        menuChallenges.setOnClickListener {
            close()
            loadChallengesFragment()
        }
        fabMenu.setOnClickListener {
            if (isOpen()) {
                close()
            } else {
                open()
            }
        }

        initializeMap()
    }

    //==============================================================================================
    // This method is called when getLocationData has completed.
    override fun onLocationDataReceived() {
        Log.d("Hotpots", "Location data received")
    }

    //==============================================================================================
    // Check for location perms, if granted get location and move map, if not ask
    private fun initializeMap() {
        // Initialize the map if permission has been granted
        if (isPermissionGranted) {
            // Initialize and configure the map here
            getCurrentLocation { lat, lon ->
                this.lat = lat
                this.lon = lon

                ToolBox.currentLat = lat
                ToolBox.currentLng = lon

                getNearByHotspots()
                addUserObs()
                val userLocation = LatLng(lat, lon)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
            }
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    //==============================================================================================
    // Loading user map style
    private fun loadMapStyle() {

        mapStyleChosen = if (ToolBox.users[0].mapStyleIsDark) {
            R.raw.dark
        } else {
            R.raw.light
        }

        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, mapStyleChosen
                )
            )

            if (!success) {
                println("Style parsing failed.")
            }
        } catch (e: IOException) {
            println("Could not load style. Error: ${e.message}")
        }
    }

    //==============================================================================================
    // When google maps is ready this code will execute
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        loadMapStyle()

        lifecycleScope.launch {
            doWork()
        }

        // Get the users current location
        getCurrentLocation { lat, lon ->
            this.lat = lat
            this.lon = lon

            ToolBox.currentLat = lat
            ToolBox.currentLng = lon

            //move camera
            val userLocation = LatLng(lat, lon)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
        }

        // On Click for marker
        mMap.setOnMarkerClickListener { marker ->
            // Setting location name
            locationName = marker.title.toString()

            ToolBox.destlat = marker.position.latitude
            ToolBox.destlng = marker.position.longitude

            ToolBox.newObslat = marker.position.latitude
            ToolBox.newObslng = marker.position.longitude

            // Getting location data to load in bottom sheet
            getLocationData(marker.position.latitude, marker.position.longitude, marker)
            true
        }
    }

    //==============================================================================================
    // Coroutine scope for getting nearby hotspots and user observations
    // Method to retrieve data while bottom sheet is active, so that there is no delay
    suspend fun doWork() = coroutineScope {
        launch {
            getNearByHotspots()
        }
        launch {
            addUserObs()
        }
    }

    //==============================================================================================
    // Show any user obs on the map in a different color
    private fun addUserObs() {
        if (ToolBox.usersObservations.isNotEmpty()) {
            val filteredObservations =
                ToolBox.usersObservations.filter { !it.IsAtHotspot }

            for (location in filteredObservations) {
                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            location.Location.latitude, location.Location.longitude
                        )
                    ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .title("User Sighting: " + location.BirdName)
                )
            }
        }
    }

    //==============================================================================================
    // Get all nearby hotspot to the user, based on their chosen distance
    private fun getNearByHotspots() {
        val apiWorker = APIWorker()
        val scope = CoroutineScope(Dispatchers.Default)

        thread {
            scope.launch {
                val hotspots = apiWorker.getHotspots(lat, lon)
                UpdateMarkers(hotspots)
                println("getting birds")
                ToolBox.birdsInTheRegion = apiWorker.getBirds()
                ToolBox.populated = true
                println("birds saved")
            }
        }
    }

    //==============================================================================================
    // Puts markers on map
    private fun UpdateMarkers(locations: List<HotspotModel>) {
        try {
            runOnUiThread {
                for (location in locations) {
                    mMap.addMarker(
                        MarkerOptions().position(LatLng(location.Lat, location.Lon))
                            .title(location.Name)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //==============================================================================================
    // Method to handel the fusedLocationClient logic and if no location is found use a hard coded location
    // This is a callback as it needs to finish what it is working on before the rest of the map logic can continue
    private fun getCurrentLocation(callback: (Double, Double) -> Unit) {
        // Check for location permission
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is granted
            isPermissionGranted = true
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val lat = location.latitude
                    val lon = location.longitude
                    callback(lat, lon)
                } ?: callback(-33.9249, 18.4241)
            }
        }
    }

    //==============================================================================================
    // Gets location data to be displayed in bottom sheet
    private fun getLocationData(lat: Double, lng: Double, marker: Marker) {
        val apiWorker = APIWorker()
        val scope = CoroutineScope(Dispatchers.Default)
        val bottomSheet = BottomSheetHotspot.newInstance(marker.title.toString(), lat, lng)
        bottomSheet.show(supportFragmentManager, BottomSheetHotspot.TAG)

        // Using threading to query external resources in the background
        thread {
            scope.launch {
                ToolBox.hotspotsSightings = apiWorker.getHotspotBirdData(lat, lng)

                destlat = lat
                destlon = lng

                // Updating the content of the bottom sheet with the received data
                runOnUiThread {
                    bottomSheet.updateHotspotSightings(ToolBox.hotspotsSightings)

                    // Bottom sheet click listener for navigation
                    bottomSheet.setButtonClickListener {
                        val intent = Intent(this@Hotpots, Navigation::class.java)
                        intent.putExtra("LATITUDE", ToolBox.currentLat)
                        intent.putExtra("LONGITUDE", ToolBox.currentLng)
                        intent.putExtra("DEST_LAT", destlat)
                        intent.putExtra("DEST_LNG", destlon)

                        startActivity(intent)
                    }
                }
            }
        }
    }

    //==============================================================================================
    // Requests location permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted = true
                initializeMap()
            } else {
                // Permission denied
            }
        }
    }

    //==============================================================================================
    // Open animation for the menu
    private fun open() {
        fabMenu.startAnimation(fabClock)
        fabMenu.isEnabled = true

        menuGame.startAnimation(fabOpen)
        menuGame.isEnabled = true

        menuSettings.startAnimation(fabOpen)
        menuSettings.isEnabled = true

        menuAddObservation.startAnimation(fabOpen)
        menuAddObservation.isEnabled = true

        menuMyObs.startAnimation(fabOpen)
        menuMyObs.isEnabled = true

        menuChallenges.startAnimation(fabOpen)
        menuChallenges.isEnabled = true

        isOpen = true

    }

    //==============================================================================================
    // Checks to see if user has open the menu
    private fun isOpen(): Boolean {
        if (isOpen) {
            menuGame.startAnimation(fabClose)
            menuSettings.startAnimation(fabClose)
            menuAddObservation.startAnimation(fabClose)
            menuMyObs.startAnimation(fabClose)
            menuChallenges.startAnimation(fabClose)
            fabMenu.startAnimation(fabAnticlock)
            return true

        } else {
            fabMenu.startAnimation(fabClock)
            menuGame.startAnimation(fabOpen)
            menuSettings.startAnimation(fabOpen)
            menuAddObservation.startAnimation(fabOpen)
            menuMyObs.startAnimation(fabOpen)
            menuChallenges.startAnimation(fabOpen)
            return false
        }
    }

    //==============================================================================================
    // Close animations for menu
    private fun close() {
        menuGame.startAnimation(fabClose)
        menuGame.isEnabled = false

        menuSettings.startAnimation(fabClose)
        menuSettings.isEnabled = false

        menuAddObservation.startAnimation(fabClose)
        menuAddObservation.isEnabled = false

        menuMyObs.startAnimation(fabClose)
        menuMyObs.isEnabled = false

        menuChallenges.startAnimation(fabClose)
        menuChallenges.isEnabled = false

        fabMenu.startAnimation(fabAnticlock)
        isOpen = false
    }

    //==============================================================================================
    // Showing/loading challenges fragment
    private fun loadChallengesFragment() {
        val challengesFragment = Challenges()
        fabMenu.isEnabled = false
        supportFragmentManager.beginTransaction().replace(android.R.id.content, challengesFragment)
            .addToBackStack(null).commit()
    }

    //==============================================================================================
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        fabMenu.isEnabled = true
    }
}

//==============================================================================================
/*
    Interface to handel callbacks to allow the popup for markers to wait for the correct data to be loaded.
    This is needed as without the callback the fragment will be loaded before the data has been saved, resulting in an empty fragment appearing
    now it only appears once the data has been loaded
 */
interface LocationDataCallback {
    fun onLocationDataReceived()
}
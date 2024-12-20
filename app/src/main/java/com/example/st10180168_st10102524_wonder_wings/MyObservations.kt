

package com.example.st10180168_st10102524_wonder_wings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment

class MyObservations : Fragment() {
    private lateinit var llObservationContainer: LinearLayout

    //==============================================================================================
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_observations, container, false)

        llObservationContainer = view.findViewById(R.id.myObservationContainer)

        // Fetch user observations and populate the list
        populateObservationViews()
        return view
    }

    //==============================================================================================
    //  Function Populates observation view dynamically
    private fun populateObservationViews() {
        llObservationContainer.removeAllViews()
        val inflater = LayoutInflater.from(requireContext())

        val filteredObservations =
            ToolBox.usersObservations.filter { it.UserID == ToolBox.users[0].UserID}

        for (userObservation in filteredObservations) {
            val observationView = inflater.inflate(R.layout.my_observations_display_layout, null)
            val line = inflater.inflate(R.layout.line, null)

            observationView.findViewById<TextView>(R.id.tvBirdName).text =
                userObservation.BirdName + " (x${userObservation.Amount})"

            val latitude = userObservation.Location.latitude
            val longitude = userObservation.Location.longitude

            // Displaying location - should display location normal name
            observationView.findViewById<TextView>(R.id.tvLocation).text =
                userObservation.PlaceName + "\n" + "Latitude: $latitude, Longitude: $longitude"

            observationView.findViewById<TextView>(R.id.tvDateSpotted).text =
                userObservation.Date.toString()

            if (userObservation.Note.isNotEmpty()) {
                observationView.findViewById<TextView>(R.id.tvViewObsNote).text =
                    userObservation.Note
            } else {
                observationView.findViewById<TextView>(R.id.tvViewObsNote).isVisible = false
            }

            observationView.setOnClickListener(){
                val intent = Intent(requireContext(), Navigation::class.java)
                intent.putExtra("LATITUDE", ToolBox.currentLat)
                intent.putExtra("LONGITUDE", ToolBox.currentLng)
                intent.putExtra("DEST_LAT", userObservation.Location.latitude)
                intent.putExtra("DEST_LNG", userObservation.Location.longitude)

                startActivity(intent)
            }

            // Other UI population code here
            llObservationContainer.addView(observationView)
            llObservationContainer.addView(line)
        }
    }
}
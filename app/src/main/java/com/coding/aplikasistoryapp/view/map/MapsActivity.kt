package com.coding.aplikasistoryapp.view.map

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.coding.aplikasistoryapp.R
import com.coding.aplikasistoryapp.databinding.ActivityMapsBinding
import com.coding.aplikasistoryapp.view.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        observeViewModel()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle()

        viewModel.listStory.observe(this) { storyResponse ->
            if (storyResponse?.listStory.isNullOrEmpty()) {
                Log.e(TAG, "Tidak ada data untuk ditampilkan.")
                return@observe
            }

            Log.d(TAG, "Total data di listStory: ${storyResponse?.listStory?.size}")

            storyResponse?.listStory?.forEachIndexed { index, story ->
                Log.d(TAG, "Data ke-$index: ${story.name}, Lat: ${story.lat}, Lon: ${story.lon}")
                if (story.lat != 0.0 && story.lon != 0.0) {
                    val latLng = LatLng(story.lat, story.lon)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(story.name)
                            .snippet(story.description)
                    )
                } else {
                    Log.e(TAG, "Data invalid: ${story.name}")
                }
            }

            storyResponse?.listStory?.firstOrNull()?.let {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.lat, it.lon), 10f))
            }
        }

    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun observeViewModel() {

        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Log.e(TAG, "Error: $errorMessage")
            }
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}

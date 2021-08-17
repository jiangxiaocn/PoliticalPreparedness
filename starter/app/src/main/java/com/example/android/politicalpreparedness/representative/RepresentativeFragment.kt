package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_representative.*
import java.util.Locale

class DetailFragment : Fragment() {
    // declare a global variable of FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var representativeAdapter: RepresentativeListAdapter

    companion object {
        //TODO: Add Constant for Location request
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    //TODO: Declare ViewModel
    private val viewModel: RepresentativeViewModel by lazy {

        val application = requireNotNull(this.activity).application
        val viewModelFactory =  RepresentativeViewModelFactory(application)
        ViewModelProvider(this,viewModelFactory)
                .get(RepresentativeViewModel::class.java)
    }
    private lateinit var binding: FragmentRepresentativeBinding


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Establish bindings
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_representative, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.address = Address("", "", "", "", "")

        val states = resources.getStringArray(R.array.states)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, states)
        binding.state.adapter = adapter

        //TODO: Establish button listeners for field and location search
        binding.buttonSearch.setOnClickListener {
            viewModel.getRepresentatives()
        }

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions(it)
        }

        //TODO: Define and assign Representative adapter
        //TODO: Populate Representative adapter
        representativeAdapter = RepresentativeListAdapter()
        binding.representativesRecyclerView.adapter = representativeAdapter
       /* viewModel.representatives.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                representativeAdapter =  binding.representativesRecyclerView.adapter as RepresentativeListAdapter
                representativeAdapter.submitList(it)
            }
        })*/
        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            representatives ->
            representativeAdapter.submitList(representatives)
        })
        return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.size > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }else{
                Toast.makeText(context, "Location permission was not granted. Please add permission in setitngs", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkLocationPermissions(view: View): Boolean {
        return if (isPermissionGranted()) {
            getLocation()
            true
        } else {
            //TODO: Request Location permissions
            requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION)
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return ContextCompat.checkSelfPermission(
                requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        //TODO: Get location from LocationServices
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
        // in onCreate() initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)
        /**
         * call this method for receive location
         * get location and give callback when successfully retrieve
         * function itself check location permission before access related methods
         *
         */
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location->
                    if (location != null) {
                        // use your location object
                        // get latitude , longitude and other info from this
                        val currentLocation = geoCodeLocation(location)
                        viewModel.address.value = currentLocation
                        viewModel.getRepresentatives()
                    }
                }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}
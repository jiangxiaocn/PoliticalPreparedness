package com.example.android.politicalpreparedness.election

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment: Fragment() {

    //TODO: Declare ViewModel
    //TODO: Add ViewModel values and create ViewModel
    private val viewModel: ElectionsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, ElectionsViewModelFactory(activity.application)).get(ElectionsViewModel::class.java)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add binding values
        val binding: FragmentElectionBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_election,
                container,
                false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        //TODO: Initiate recycler adapters
        //TODO: Populate recycler adapters
        //TODO: Link elections to voter info
        binding.upcomingElectionsRecyclerView.adapter= ElectionListAdapter(ElectionListener{
            findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it))
        })

        binding.savedElectionsRecyclerView.adapter= ElectionListAdapter(ElectionListener{
            findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it))
        })

        //TODO: Refresh adapters when fragment loads
        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer { elections ->
            elections.apply {
                val adapter = binding.upcomingElectionsRecyclerView.adapter as ElectionListAdapter
                adapter.elections = elections
            }
        })

        viewModel.savedElections.observe(viewLifecycleOwner, Observer { elections ->
            elections.apply {
                val adapter = binding.savedElectionsRecyclerView.adapter as ElectionListAdapter
                adapter.elections = elections
            }
        })

        return binding.root

    }
}
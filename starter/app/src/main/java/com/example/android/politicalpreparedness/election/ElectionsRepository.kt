package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class ElectionsRepository(private val database: ElectionDatabase) {

    val elections: LiveData<List<Election>> = database.electionDao.getAllElections()
    val voterInfoResponse =  MutableLiveData<VoterInfoResponse>()

    suspend fun insertElection(election: Election) {
        withContext(Dispatchers.IO) {
            database.electionDao.insertElection(election)
        }
        Log.i("election", election.isSaved.toString())
    }

    suspend fun refreshElections() {
        withContext(Dispatchers.IO) {
        try {
                val electionResponse = CivicsApi.retrofitService.getElectionsAsync().await()
                database.electionDao.insertElections(electionResponse.elections)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        }
    }

    fun getElection(id: Int) = database.electionDao.getElectionById(id)
    val savedElections: LiveData<List<Election>> = database.electionDao.getSavedElections()
}
package com.example.android.politicalpreparedness.election

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class VoterInfoViewModel(application: Application) :  AndroidViewModel(application) {
    private val database = ElectionDatabase.getInstance(application)
    private val electionsRepository = ElectionsRepository(database)

    //TODO: Add var and methods to support loading URLs
    var url = MutableLiveData<String>()
    val voterInfo = electionsRepository.voterInfoResponse

    //TODO: Add live data to hold voter info
    //TODO: Add var and methods to populate voter info
    private val electionId = MutableLiveData<Int>()
    val election = electionId.switchMap { id ->
        liveData {
            emitSource(electionsRepository.getElection(id))
        }
    }

    fun getElection(id: Int) {
        electionId.value = id
    }
    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */
    fun saveElection(election: Election) {
        election.isSaved = !election.isSaved
        viewModelScope.launch {
            electionsRepository.insertElection(election)
        }
    }

}
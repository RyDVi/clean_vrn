package com.rydvi.clean_vrn.ui.teams

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rydvi.clean_vrn.api.CollectedGarbage
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Team

class TeamsViewModel : ViewModel() {

    private val dataRepository: DataRepository = DataRepository
    private var dataTeams: MutableLiveData<Array<Team>>? = null
    private var dataCollectedGarbages:MutableLiveData<Array<CollectedGarbage>>? = null

    fun getTeams(): MutableLiveData<Array<Team>> {
        if(dataTeams==null){
            dataTeams = MutableLiveData()
            dataRepository.getTeams {
                dataTeams?.postValue(it)
            }
        }
        return dataTeams!!
    }

    fun refreshTeams(): MutableLiveData<Array<Team>>? {
        dataRepository.getTeams{
            dataTeams?.postValue(it)
        }
        return dataTeams
    }

    fun getCollectedGarbages(id_team:Long):MutableLiveData<Array<CollectedGarbage>>{
        if(dataCollectedGarbages==null){
            dataCollectedGarbages = MutableLiveData()
            dataRepository.getCollectedGarbages(id_team){
                dataCollectedGarbages?.postValue(it)
            }
        }
        return dataCollectedGarbages!!
    }

    fun refreshCollectedGarbages(id_team:Long):MutableLiveData<Array<CollectedGarbage>>?{
        dataRepository.getCollectedGarbages(id_team){
            dataCollectedGarbages?.postValue(it)
        }
        return dataCollectedGarbages
    }
}
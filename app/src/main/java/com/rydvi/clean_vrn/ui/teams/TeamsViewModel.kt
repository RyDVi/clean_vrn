package com.rydvi.clean_vrn.ui.teams

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rydvi.clean_vrn.api.CollectedGarbage
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Garbage
import com.rydvi.clean_vrn.api.Team

class TeamsViewModel : ViewModel() {


    private val dataRepository: DataRepository = DataRepository
    private var dataTeams: MutableLiveData<Array<Team>>? = null
    private var dataCollectedGarbages:MutableLiveData<Array<CollectedGarbage>>? = null
    private var dataGarbages: MutableLiveData<Array<Garbage>>?=null

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

    fun updateCollectedGarbages(id_team: Long, callbackSuccess:()->Unit, callbackFailed:()->Unit){
        if(dataCollectedGarbages!=null){
            dataRepository.updateCollectedGarbages(id_team, dataCollectedGarbages!!.value!!){
                callbackSuccess()
            }
        } else {
            callbackFailed()
        }
    }

    fun refreshCollectedGarbages(id_team:Long):MutableLiveData<Array<CollectedGarbage>>?{
        dataRepository.getCollectedGarbages(id_team){
            dataCollectedGarbages?.postValue(it)
        }
        return dataCollectedGarbages
    }

    fun getGarbages():MutableLiveData<Array<Garbage>>{
        if(dataGarbages==null){
            dataGarbages = MutableLiveData()
            dataRepository.getGarbages{
                dataGarbages?.postValue(it)
            }
        }
        return dataGarbages!!
    }

    fun refreshGarbages():MutableLiveData<Array<Garbage>>?{
        dataRepository.getGarbages{
            dataGarbages?.postValue(it)
        }
        return dataGarbages
    }
}
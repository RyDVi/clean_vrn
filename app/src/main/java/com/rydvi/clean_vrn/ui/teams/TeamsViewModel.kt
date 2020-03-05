package com.rydvi.clean_vrn.ui.teams

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Team

class TeamsViewModel : ViewModel() {

    private val dataRepository: DataRepository = DataRepository()
    private var dataTeams: MutableLiveData<Array<Team>>? = null

    fun getTeams(): MutableLiveData<Array<Team>> {
        if(dataTeams==null){
            dataTeams = MutableLiveData()
            dataRepository.getTeams {
                dataTeams?.postValue(it)
            }
        }
        return dataTeams!!
    }

    fun refreshGames(): MutableLiveData<Array<Team>>? {
        dataRepository.getTeams{
            dataTeams?.postValue(it)
        }
        return dataTeams
    }
}
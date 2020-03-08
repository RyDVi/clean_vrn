package com.rydvi.clean_vrn.ui.organizators

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Game
import com.rydvi.clean_vrn.api.Organizator

class OrganizatorsViewModel : ViewModel() {

    private val dataRepository: DataRepository = DataRepository
    private var dataOrganizators:MutableLiveData<Array<Organizator>>? = null

    fun getOrganizators(): MutableLiveData<Array<Organizator>> {
        if(dataOrganizators==null){
            dataOrganizators = MutableLiveData()
            dataRepository.getOrganizators {
                dataOrganizators?.postValue(it)
            }
        }
        return dataOrganizators!!
    }

    fun refreshOrganizators(): MutableLiveData<Array<Organizator>>? {
        dataRepository.getOrganizators{
            dataOrganizators?.postValue(it)
        }
        return dataOrganizators
    }
}
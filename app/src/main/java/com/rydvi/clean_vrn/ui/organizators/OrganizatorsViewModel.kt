package com.rydvi.clean_vrn.ui.organizators

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Game
import com.rydvi.clean_vrn.api.Organizator

class OrganizatorsViewModel : ViewModel() {

    private var dataOrganizators: MutableLiveData<Array<Organizator>>? = null

    fun getOrganizators(): MutableLiveData<Array<Organizator>> {
        if (dataOrganizators == null) {
            dataOrganizators = MutableLiveData()
            DataRepository.getOrganizators({
                dataOrganizators?.postValue(it)
            }, {})
        }
        return dataOrganizators!!
    }

    fun refreshOrganizators(): MutableLiveData<Array<Organizator>>? {
        DataRepository.getOrganizators({
            dataOrganizators?.postValue(it)
        }, {})
        return dataOrganizators
    }

    fun updateOrganizator(org: Organizator, callback: () -> Unit) {
        DataRepository.updateOrganizator(org, callback, {})
    }

    fun createOrganizator(org: Organizator, callback: (Organizator) -> Unit) {
        DataRepository.createOrganizator(org, callback, {})
    }

    fun generatePassword(id: Long, callback: (String) -> Unit) {
        DataRepository.generatePassword(id, callback, {})
    }

    fun deleteOrganizator(id: Long, callback: () -> Unit) {
        DataRepository.deleteOrganizator(id, callback, {})
    }
}
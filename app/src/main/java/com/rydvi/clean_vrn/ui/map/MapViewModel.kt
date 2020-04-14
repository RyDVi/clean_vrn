package com.rydvi.clean_vrn.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Error
import com.rydvi.clean_vrn.api.Game
import com.rydvi.clean_vrn.api.Place

class MapViewModel : ViewModel() {
    private var dataPlaces: MutableLiveData<Array<Place>>? = null

    fun createPlace(place: Place, success: (Place) -> Unit, failed: (Error) -> Unit) {
        DataRepository.createPlace(place, success, failed)
    }

    fun updatePlace(
        place: Place,
        success: () -> Unit,
        failed: (Error) -> Unit
    ) {
        DataRepository.updatePlace(place, success, failed)
    }

    fun removePlace(id: Long, success: () -> Unit, failed: (Error) -> Unit) {
        DataRepository.removePlace(id, success, failed)
    }

    fun getPlaces(): MutableLiveData<Array<Place>> {
        if (dataPlaces == null) {
            dataPlaces = MutableLiveData()
            DataRepository.getPlaces({
                dataPlaces?.postValue(it)
            }, {})
        }
        return dataPlaces!!
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is map Fragment"
    }
    val text: LiveData<String> = _text
}
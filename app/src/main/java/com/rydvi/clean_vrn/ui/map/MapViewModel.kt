package com.rydvi.clean_vrn.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rydvi.clean_vrn.api.DataRepository
import com.rydvi.clean_vrn.api.Error
import com.rydvi.clean_vrn.api.Place

class MapViewModel : ViewModel() {
    
    fun createPlace(place: Place, success: (Place) -> Unit, failed: (Error) -> Unit) {
       DataRepository.createPlace(place, success, failed)
    }

    fun updatePlace(
        place: Place,
        succcess: () -> Unit,
        failed: (Error) -> Unit
    ) {
        succcess()
    }

    fun deletePlace(place: Place, success: () -> Unit, failed: (Error) -> Unit) {
        success()
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is map Fragment"
    }
    val text: LiveData<String> = _text
}
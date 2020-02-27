package com.rydvi.clean_vrn.ui.organizators

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrganizatorsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is organizators Fragment"
    }
    val text: LiveData<String> = _text
}
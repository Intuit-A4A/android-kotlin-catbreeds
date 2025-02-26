package com.intuit.catapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.intuit.catapp.data.Breed
import com.intuit.catapp.data.CatService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BreedsViewModel: ViewModel() {

    init {
        // Initialize the service for API calls
        CatService.init()
    }

    private val _breedsLiveData = MutableLiveData<BreedsResult>()
    val breedsLiveData: LiveData<BreedsResult>
        get() = _breedsLiveData

    fun getBreeds() {
        viewModelScope.launch(Dispatchers.IO) {
            val service = CatService.getService()

            val apiResult = service.getBreeds()
            Log.d(BreedsViewModel::class.simpleName, "$apiResult")

            if (apiResult.isEmpty()) {
                _breedsLiveData.postValue(BreedsResult(error = "Empty list of breeds retrieved from API"))
            } else {
                _breedsLiveData.postValue(BreedsResult(data = apiResult))
            }
        }
    }

    data class BreedsResult(val data: List<Breed>? = emptyList(), val error: String? = null)
}

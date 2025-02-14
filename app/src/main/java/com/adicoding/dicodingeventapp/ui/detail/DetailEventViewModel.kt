package com.adicoding.dicodingeventapp.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adicoding.dicodingeventapp.data.network.ApiConfig
import com.adicoding.dicodingeventapp.data.response.ListEventsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailEventViewModel : ViewModel() {

    private val _eventDetail = MutableLiveData<ListEventsItem?>()
    val eventDetail: LiveData<ListEventsItem?> get() = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchEventDetails(eventId: Int) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService("YOUR_TOKEN_HERE")
                val response = withContext(Dispatchers.IO) {
                    apiService.getEventDetail(eventId)
                }

                _isLoading.value = false
                if (response.error == false) {
                    _eventDetail.value = response.event
                } else {
                    _errorMessage.value = response.message ?: "Terjadi kesalahan"
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Gagal memuat data: ${e.message}"
            }
        }
    }
}
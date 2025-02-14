package com.adicoding.dicodingeventapp.ui.finished

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adicoding.dicodingeventapp.data.network.ApiConfig
import com.adicoding.dicodingeventapp.data.response.ListEventsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FinishedEventViewModel : ViewModel() {

    private val _eventList = MutableLiveData<List<ListEventsItem>>()
    val eventList: LiveData<List<ListEventsItem>> get() = _eventList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchFinishedEvents() {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService("YOUR_TOKEN_HERE")
                val response = withContext(Dispatchers.IO) {
                    apiService.getEvents(active = 0)
                }

                _isLoading.value = false
                if (response.error == false) {
                    _eventList.value = response.listEvents?.filterNotNull() ?: emptyList()
                } else {
                    _errorMessage.value = response.message ?: "Terjadi kesalahan saat mengambil data."
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
            }
        }
    }

    fun searchFinishedEvents(query: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService("YOUR_TOKEN_HERE")
                val response = withContext(Dispatchers.IO) {
                    apiService.getEvents(active = 0, query = query)
                }

                _isLoading.value = false
                if (response.error == false) {
                    _eventList.value = response.listEvents?.filterNotNull() ?: emptyList()
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
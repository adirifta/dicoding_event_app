package com.adicoding.dicodingeventapp.ui.upcoming

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adicoding.dicodingeventapp.data.network.ApiConfig
import com.adicoding.dicodingeventapp.data.response.ListEventsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ActiveEventViewModel : ViewModel() {

    private val _events = MutableLiveData<List<ListEventsItem>>()
    val events: LiveData<List<ListEventsItem>> get() = _events

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchActiveEvents() {
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService("YOUR_TOKEN_HERE")
                val response = withContext(Dispatchers.IO) {
                    apiService.getEvents(active = 1)
                }

                if (response.error == false) {
                    _events.value = response.listEvents?.filterNotNull() ?: emptyList()
                } else {
                    _errorMessage.value = response.message ?: "Terjadi kesalahan saat mengambil data."
                    _events.value = emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
                _events.value = emptyList()
            }
        }
    }
}


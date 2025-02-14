package com.adicoding.dicodingeventapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adicoding.dicodingeventapp.data.network.ApiConfig
import com.adicoding.dicodingeventapp.data.response.ListEventsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.UnknownHostException

class HomeViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _activeEvents = savedStateHandle.getLiveData("active_events", emptyList<ListEventsItem>())
    val activeEvents: LiveData<List<ListEventsItem>> get() = _activeEvents

    private val _pastEvents = savedStateHandle.getLiveData("past_events", emptyList<ListEventsItem>())
    val pastEvents: LiveData<List<ListEventsItem>> get() = _pastEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun fetchEvents(limit: Int) {
        if (_activeEvents.value!!.isNotEmpty() && _pastEvents.value!!.isNotEmpty()) return

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val apiService = ApiConfig.getApiService("YOUR_TOKEN_HERE")

                val activeResponse = withContext(Dispatchers.IO) { apiService.getEvents(active = 1) }
                val pastResponse = withContext(Dispatchers.IO) { apiService.getEvents(active = 0) }

                _isLoading.value = false

                if (activeResponse.error == false) {
                    _activeEvents.value = activeResponse.listEvents?.filterNotNull()?.take(limit) ?: emptyList()
                } else {
                    _errorMessage.value = activeResponse.message ?: "Terjadi kesalahan"
                }

                if (pastResponse.error == false) {
                    _pastEvents.value = pastResponse.listEvents?.filterNotNull()?.take(limit) ?: emptyList()
                } else {
                    _errorMessage.value = pastResponse.message ?: "Terjadi kesalahan"
                }

            } catch (e: UnknownHostException) {
                _isLoading.value = false
                _errorMessage.value = "Tidak ada koneksi internet. Periksa jaringan Anda dan coba lagi."
            } catch (e: HttpException) {
                _isLoading.value = false
                _errorMessage.value = "Terjadi kesalahan saat menghubungi server (${e.code()})"
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Gagal memuat data: ${e.message}"
            }
        }
    }
}
package com.adicoding.dicodingeventapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adicoding.dicodingeventapp.data.network.ApiConfig
import com.adicoding.dicodingeventapp.databinding.FragmentHomeBinding
import com.adicoding.dicodingeventapp.ui.EventAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = ApiConfig.getApiService("YOUR_TOKEN_HERE")

        // Fetch active events
        CoroutineScope(Dispatchers.IO).launch {
            val activeResponse = apiService.getEvents(active = 1)
            withContext(Dispatchers.Main) {
                if (activeResponse.error == false) {
                    val activeEvents = activeResponse.listEvents?.filterNotNull() ?: emptyList()
                    val activeAdapter = EventAdapter(activeEvents)
                    binding.rvActiveEvents.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.rvActiveEvents.adapter = activeAdapter
                }
            }
        }

        // Fetch past events
        CoroutineScope(Dispatchers.IO).launch {
            val pastResponse = apiService.getEvents(active = 0)
            withContext(Dispatchers.Main) {
                if (pastResponse.error == false) {
                    val pastEvents = pastResponse.listEvents?.filterNotNull() ?: emptyList()
                    val pastAdapter = EventAdapter(pastEvents)
                    binding.rvPastEvents.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.rvPastEvents.adapter = pastAdapter
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
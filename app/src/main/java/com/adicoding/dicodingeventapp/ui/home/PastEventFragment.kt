package com.adicoding.dicodingeventapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.adicoding.dicodingeventapp.data.network.ApiConfig
import com.adicoding.dicodingeventapp.databinding.FragmentPastEventBinding
import com.adicoding.dicodingeventapp.ui.EventAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PastEventFragment : Fragment() {

    private var _binding: FragmentPastEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPastEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = ApiConfig.getApiService("YOUR_TOKEN_HERE")
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getEvents(active = 0)
            withContext(Dispatchers.Main) {
                if (response.error == false) {
                    val eventList = response.listEvents?.filterNotNull() ?: emptyList()
                    val adapter = EventAdapter(eventList)
                    binding.rvPastEvents.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvPastEvents.adapter = adapter
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

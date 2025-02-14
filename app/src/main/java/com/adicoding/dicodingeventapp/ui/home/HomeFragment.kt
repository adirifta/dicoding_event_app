package com.adicoding.dicodingeventapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adicoding.dicodingeventapp.databinding.FragmentHomeBinding
import com.adicoding.dicodingeventapp.ui.EventAdapter
import com.adicoding.dicodingeventapp.ui.detail.DetailEventActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupRetryButton()

        viewModel.fetchEvents(limit = 5)
    }

    private fun setupRecyclerView() {
        binding.rvActiveEvents.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvActiveEvents.adapter = EventAdapter(emptyList()) { _ -> }

        binding.rvPastEvents.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvPastEvents.adapter = EventAdapter(emptyList()) { _ -> }
    }

    private fun setupObservers() {
        viewModel.activeEvents.observe(viewLifecycleOwner) { activeEvents ->
            if (activeEvents.isNotEmpty()) {
                val adapter = EventAdapter(activeEvents) { event ->
                    val intent = Intent(context, DetailEventActivity::class.java)
                    intent.putExtra(DetailEventActivity.EXTRA_EVENT_ID, event.id)
                    startActivity(intent)
                }
                binding.rvActiveEvents.adapter = adapter
                binding.rvActiveEvents.visibility = View.VISIBLE
                binding.ivNoActiveEvents.visibility = View.GONE
            } else {
                binding.rvActiveEvents.visibility = View.GONE
                binding.ivNoActiveEvents.visibility = View.VISIBLE
            }
        }

        viewModel.pastEvents.observe(viewLifecycleOwner) { pastEvents ->
            val adapter = EventAdapter(pastEvents) { event ->
                val intent = Intent(context, DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_EVENT_ID, event.id)
                startActivity(intent)
            }
            binding.rvPastEvents.adapter = adapter
            binding.rvPastEvents.visibility = if (pastEvents.isNotEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                binding.errorLayout.visibility = View.VISIBLE
                binding.tvErrorMessage.text = message
                binding.btnRetry.visibility = View.VISIBLE
                binding.rvActiveEvents.visibility = View.GONE
                binding.rvPastEvents.visibility = View.GONE
                binding.tvActiveEvents.visibility = View.GONE
                binding.ivNoActiveEvents.visibility = View.GONE
                binding.tvPastEvents.visibility = View.GONE
            } else {
                binding.errorLayout.visibility = View.GONE
                binding.btnRetry.visibility = View.GONE
                binding.tvActiveEvents.visibility = View.VISIBLE
                binding.tvPastEvents.visibility = View.VISIBLE
            }
        }
    }

    private fun setupRetryButton() {
        binding.btnRetry.setOnClickListener {
            viewModel.fetchEvents(limit = 5)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
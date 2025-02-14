package com.adicoding.dicodingeventapp.ui.finished

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adicoding.dicodingeventapp.databinding.FragmentFinishedEventBinding
import com.adicoding.dicodingeventapp.ui.EventAdapter
import com.adicoding.dicodingeventapp.ui.detail.DetailEventActivity

class FinishedEventFragment : Fragment() {

    private var _binding: FragmentFinishedEventBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FinishedEventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupSearchView()
        setupRetryButton()
        viewModel.fetchFinishedEvents()
    }

    private fun setupRecyclerView() {
        binding.rvPastEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPastEvents.adapter = EventAdapter(emptyList()) { event ->
            val intent = Intent(context, DetailEventActivity::class.java)
            intent.putExtra(DetailEventActivity.EXTRA_EVENT_ID, event)
            startActivity(intent)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.searchFinishedEvents(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setupObservers() {
        viewModel.eventList.observe(viewLifecycleOwner) { events ->
            val adapter = EventAdapter(events) { event ->
                val intent = Intent(context, DetailEventActivity::class.java)
                intent.putExtra(DetailEventActivity.EXTRA_EVENT_ID, event.id)
                startActivity(intent)
            }
            binding.rvPastEvents.adapter = adapter

            if (events.isEmpty()) {
                showError("Tidak ada event yang tersedia.")
            } else {
                showContent()
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                showError(it)
            }
        }
    }

    private fun setupRetryButton() {
        binding.btnRetry.setOnClickListener {
            showLoading()
            viewModel.fetchFinishedEvents()
        }
    }

    private fun showError(message: String) {
        binding.rvPastEvents.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message
    }

    private fun showContent() {
        binding.rvPastEvents.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvPastEvents.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
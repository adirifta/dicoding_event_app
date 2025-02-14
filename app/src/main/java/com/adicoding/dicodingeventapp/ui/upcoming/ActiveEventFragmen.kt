package com.adicoding.dicodingeventapp.ui.upcoming

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.adicoding.dicodingeventapp.databinding.FragmentActiveEventBinding
import com.adicoding.dicodingeventapp.ui.EventAdapter
import com.adicoding.dicodingeventapp.ui.detail.DetailEventActivity

class ActiveEventFragment : Fragment() {

    private var _binding: FragmentActiveEventBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ActiveEventViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupRetryButton()
        viewModel.fetchActiveEvents()
    }

    private fun setupObservers() {
        viewModel.events.observe(viewLifecycleOwner) { eventList ->
            binding.progressBar.visibility = View.GONE

            if (eventList.isNotEmpty()) {
                val adapter = EventAdapter(eventList) { event ->
                    val intent = Intent(context, DetailEventActivity::class.java)
                    intent.putExtra(DetailEventActivity.EXTRA_EVENT_ID, event.id)
                    startActivity(intent)
                }
                binding.rvActiveEvents.layoutManager = LinearLayoutManager(requireContext())
                binding.rvActiveEvents.adapter = adapter
                showContent()
            } else {
                showError("Tidak ada event yang tersedia.")
            }
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
            viewModel.fetchActiveEvents()
        }
    }

    private fun showError(message: String) {
        binding.rvActiveEvents.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.errorLayout.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message
    }

    private fun showContent() {
        binding.rvActiveEvents.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvActiveEvents.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
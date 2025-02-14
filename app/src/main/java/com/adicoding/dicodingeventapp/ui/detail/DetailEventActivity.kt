package com.adicoding.dicodingeventapp.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.adicoding.dicodingeventapp.R
import com.adicoding.dicodingeventapp.data.response.ListEventsItem
import com.adicoding.dicodingeventapp.databinding.ActivityDetailEventBinding
import com.bumptech.glide.Glide

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private val viewModel: DetailEventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = getString(R.string.detail_event)
            setDisplayHomeAsUpEnabled(true)
        }

        val event: ListEventsItem? = intent.getParcelableExtra(EXTRA_EVENT)
        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, -1)

        if (event != null) {
            displayEventDetails(event)
        } else if (eventId != -1) {
            setupObservers()
            viewModel.fetchEventDetails(eventId)
        } else {
            Toast.makeText(this, "Event ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.eventDetail.observe(this) { event ->
            event?.let { displayEventDetails(it) }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n", "SetJavaScriptEnabled")
    private fun displayEventDetails(event: ListEventsItem) {
        binding.apply {
            Glide.with(this@DetailEventActivity).load(event.imageLogo).into(ivEventLogo)
            Glide.with(this@DetailEventActivity).load(event.mediaCover).into(ivEventCover)
            tvEventName.text = event.name
            tvEventOwner.text = "Diselenggarakan oleh: ${event.ownerName}"
            tvEventTime.text = "Waktu: ${event.beginTime}"
            tvEventQuota.text = "Sisa Kuota: ${(event.quota ?: 0) - (event.registrants ?: 0)}"

            tvEventDescription.text = Html.fromHtml(event.description, Html.FROM_HTML_MODE_LEGACY)

            btnEventLink.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
        const val EXTRA_EVENT = "extra_event"
    }
}

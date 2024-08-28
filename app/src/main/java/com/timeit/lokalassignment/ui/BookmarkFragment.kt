package com.timeit.lokalassignment.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.timeit.lokalassignment.data.local.JobDatabase
import com.timeit.lokalassignment.data.local.SavedJobRepository
import com.timeit.lokalassignment.data.model.JobDisplayData
import com.timeit.lokalassignment.databinding.FragmentBookmarkBinding
import com.timeit.lokalassignment.ui.Adapters.JobAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private lateinit var jobAdapter: JobAdapter
    private lateinit var jobDatabase: JobDatabase
    private lateinit var savedJobRepository: SavedJobRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jobDatabase = JobDatabase.getDatabase(requireContext())
        savedJobRepository = SavedJobRepository(jobDatabase.jobDao())

        jobAdapter = JobAdapter(requireContext(), mutableListOf(), ::onJobSaveClick, ::onJobUnSaveClick, true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = jobAdapter

        fetchSavedJobs()
    }

    private fun onJobUnSaveClick(jobId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            savedJobRepository.unsaveJob(jobId)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Job unsaved successfully", Toast.LENGTH_SHORT).show()
                fetchSavedJobs()
            }
        }
    }

    private fun onJobSaveClick(jobDisplayData: JobDisplayData) {

    }

    private fun fetchSavedJobs() {

        binding.loadingIndicator.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val savedJobs = jobDatabase.jobDao().getSavedJobs()
            withContext(Dispatchers.Main) {

                binding.loadingIndicator.visibility = View.GONE

                if (savedJobs.isNotEmpty()) {
                    binding.emptyState.visibility = View.GONE
                    val jobDisplayDataList = savedJobs.map { job ->
                        JobDisplayData(
                            jobId = job.id,
                            title = job.title,
                            location = job.location,
                            salary = job.salary,
                            phone = job.phone,
                            companyName = job.companyName,
                            thumbUrl = job.thumbUrl,
                            whatsappLink = job.whatsappLink
                        )
                    }.toMutableList()
                    jobAdapter.updateJobs(jobDisplayDataList)
                } else {
                    jobAdapter.updateJobs(mutableListOf())
                    Toast.makeText(context, "No saved jobs found", Toast.LENGTH_LONG).show()
                    binding.loadingIndicator.visibility = View.GONE
                    binding.emptyState.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

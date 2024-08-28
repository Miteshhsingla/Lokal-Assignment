package com.timeit.lokalassignment.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timeit.lokalassignment.utils.Resource
import com.timeit.lokalassignment.data.local.JobDatabase
import com.timeit.lokalassignment.data.local.JobEntity
import com.timeit.lokalassignment.data.local.SavedJobRepository
import com.timeit.lokalassignment.data.model.ApiResponse
import com.timeit.lokalassignment.data.model.JobDisplayData
import com.timeit.lokalassignment.data.remote.RetrofitClient
import com.timeit.lokalassignment.databinding.FragmentJobsBinding
import com.timeit.lokalassignment.ui.Adapters.JobAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response


class JobsFragment : Fragment() {

    private var _binding: FragmentJobsBinding? = null
    private val binding get() = _binding!!
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    private lateinit var jobAdapter : JobAdapter
    private lateinit var jobRepository: SavedJobRepository
    private lateinit var jobDatabase: JobDatabase
    private lateinit var savedJobRepository: SavedJobRepository
     lateinit var  apiResponse: Response<ApiResponse>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJobsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedJobRepository = SavedJobRepository(JobDatabase.getDatabase(requireContext()).jobDao())


        jobAdapter = JobAdapter(
            requireContext(),
            mutableListOf(),
            ::onJobSaveClick,
            ::onJobUnsaveClick,
            false
        )
        setupRecyclerView()

        fetchJobs(currentPage)
    }

    private fun onJobSaveClick(job: JobDisplayData) {
        CoroutineScope(Dispatchers.IO).launch {
            savedJobRepository.saveJob(
                JobEntity(
                    id = job.jobId,
                    title = job.title,
                    location = job.location,
                    salary = job.salary,
                    phone = job.phone,
                    companyName = job.companyName,
                    thumbUrl = job.thumbUrl,
                    whatsappLink = job.whatsappLink
                )
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Job saved!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun onJobUnsaveClick(s: String) {
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = jobAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                        ) {
                            fetchJobs(currentPage)
                        }
                    }
                }
            })
        }
    }




    private fun fetchJobs(page: Int) {
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val response: Resource<List<JobDisplayData>> = try {

                apiResponse = RetrofitClient.apiService.getJobs(page)

                if (apiResponse.isSuccessful) {
                    apiResponse.body()?.let { apiResponseBody ->
                        val jobDisplayDataList = apiResponseBody.results.filter { it.title != null }.map { job ->
                            JobDisplayData(
                                jobId = job.id.toString(),
                                title = job.title ?: "N/A",
                                location = job.primary_details?.Place ?: "Not Specified",
                                salary = job.primary_details?.Salary ?: "Not Specified",
                                phone = extractPhoneFromLink(job.custom_link),
                                companyName = job.company_name,
                                thumbUrl = job.creatives.firstOrNull()?.thumb_url ?: "Not Specified",
                                whatsappLink = job.contact_preference.whatsapp_link
                            )
                        }
                        Resource.Success(jobDisplayDataList)
                    } ?: Resource.Error("No Data Found")
                } else {
                    Resource.Error(apiResponse.message())
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "An error occurred")
            }

            withContext(Dispatchers.Main) {
                if (isAdded && view != null) {
                    when (response) {
                        is Resource.Success -> {
                            binding.loadingIndicator.visibility = View.GONE
                            response.data?.let { jobs ->
                                jobAdapter.addJobs(jobs)
                                currentPage++
                            }
                            if (response.data.isNullOrEmpty()) {
                                isLastPage = true
                            }
                        }
                        is Resource.Error -> {
                            binding.loadingIndicator.visibility = View.GONE
                            Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                        }
                        is Resource.Loading -> {
                            binding.loadingIndicator.visibility = View.VISIBLE
                        }

                        else -> {
                            binding.emptyState.visibility = View.VISIBLE
                        }
                    }
                    isLoading = false
                }
            }
        }
    }





    private fun extractPhoneFromLink(link: String?): String {
        return link?.removePrefix("tel:") ?: "N/A"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}


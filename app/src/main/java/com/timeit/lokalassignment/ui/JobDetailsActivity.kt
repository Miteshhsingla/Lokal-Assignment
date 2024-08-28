package com.timeit.lokalassignment.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.timeit.lokalassignment.data.local.JobDatabase
import com.timeit.lokalassignment.data.local.JobEntity
import com.timeit.lokalassignment.data.local.SavedJobRepository
import com.timeit.lokalassignment.data.model.JobDisplayData
import com.timeit.lokalassignment.databinding.ActivityJobDetailsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class JobDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJobDetailsBinding
    private lateinit var savedJobRepository: SavedJobRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        savedJobRepository = SavedJobRepository(JobDatabase.getDatabase(this).jobDao())

        val jobId = intent.getStringExtra("jobId").toString()
        val title = intent.getStringExtra("title").toString()
        val location = intent.getStringExtra("location").toString()
        val salary = intent.getStringExtra("salary").toString()
        val phone= intent.getStringExtra("phone").toString()
        val thumbUrl = intent.getStringExtra("thumbUrl").toString()
        val companyName = intent.getStringExtra("companyName").toString()
        val whatsappLink = intent.getStringExtra("whatsapp").toString()

        if(salary == "-"){
            binding.tvSalary.text = "Salary not specified"
        }
        else{
            binding.tvSalary.text = "Salary: " + salary
        }

        if(salary == "-"){
            binding.tvLocation.text ="Location: Location not specified"
        }
        else{
            binding.tvLocation.text ="Location: " + location
        }

        binding.tvTitle.text = title
        binding.tvPhone.text = "Contact: " + phone
        binding.tvCompanyName.text =  companyName

        thumbUrl?.let {
            Glide.with(this)
                .load(it)
                .into(binding.ivThumbnail)
        }

        binding.saveJob.setOnClickListener {
            onJobSaveClick(
                JobDisplayData(
                    jobId = jobId,
                    title = title,
                    location = location,
                    salary = salary,
                    phone = phone,
                    companyName = companyName,
                    thumbUrl = thumbUrl,
                    whatsappLink = whatsappLink
            )
            )
        }


        binding.contactwp.setOnClickListener{
            val url = whatsappLink
            openExternalLink(url)
        }

    }

    private fun openExternalLink(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }



    fun onJobSaveClick(job: JobDisplayData) {
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
                binding.saveJob.text = "Saved"
                Toast.makeText(this@JobDetailsActivity, "Job saved!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

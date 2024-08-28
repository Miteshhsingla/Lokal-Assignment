package com.timeit.lokalassignment.ui.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.timeit.lokalassignment.data.model.JobDisplayData
import com.timeit.lokalassignment.databinding.JobCardBinding
import com.timeit.lokalassignment.ui.JobDetailsActivity

class JobAdapter(private var context: Context,
    private val jobs: MutableList<JobDisplayData>,
                 private val onSaveClick: (JobDisplayData) -> Unit,
                 private val onUnSaveClick: (jobId:String) -> Unit,
                 private val showUnsaveOption: Boolean
) : RecyclerView.Adapter<JobAdapter.JobViewHolder>() {

    inner class JobViewHolder(val binding: JobCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = JobCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = jobs[position]

            holder.binding.jobTitle.text = job.title
            holder.binding.jobLocation.text = job.location
            holder.binding.jobSalary.text = job.salary
            holder.binding.jobPhone.text = job.phone

            holder.binding.buttonViewDetails.setOnClickListener {

                val intent = Intent(context, JobDetailsActivity::class.java)
                intent.putExtra("jobId",job.jobId)
                intent.putExtra("title",job.title)
                intent.putExtra("location",job.location)
                intent.putExtra("salary",job.salary)
                intent.putExtra("phone",job.phone)
                intent.putExtra("thumbUrl",job.thumbUrl)
                intent.putExtra("companyName",job.companyName)
                intent.putExtra("whatsapp",job.whatsappLink)
                context.startActivity(intent)
            }


    }

    override fun getItemCount() = jobs.size

    fun addJobs(newJobs: List<JobDisplayData>) {
        val oldSize = jobs.size
        jobs.addAll(newJobs)
        notifyItemRangeInserted(oldSize, newJobs.size)
    }

    fun updateJobs(newJobs: List<JobDisplayData>) {
        jobs.clear()
        jobs.addAll(newJobs)
        notifyDataSetChanged()
    }
}

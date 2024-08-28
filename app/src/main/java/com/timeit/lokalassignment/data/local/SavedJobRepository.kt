package com.timeit.lokalassignment.data.local



class SavedJobRepository(private val jobDao: JobDao) {

     fun saveJob(job: JobEntity) {
        jobDao.saveJob(job)
    }

     fun unsaveJob(jobId: String) {
        jobDao.unsaveJob(jobId)
    }

     fun getSavedJobs(): List<JobEntity> {
        return jobDao.getSavedJobs()
    }

     fun getJobById(jobId: String): JobEntity? {
        return jobDao.getJobById(jobId)
    }
}

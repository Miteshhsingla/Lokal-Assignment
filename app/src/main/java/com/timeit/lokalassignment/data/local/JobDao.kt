package com.timeit.lokalassignment.data.local

import androidx.room.*

@Dao
interface JobDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun saveJob(job: JobEntity)

    @Query("DELETE FROM jobs WHERE id = :jobId")
    fun unsaveJob(jobId : String)

    @Query("SELECT * FROM jobs")
     fun getSavedJobs(): List<JobEntity>

    @Query("SELECT * FROM jobs WHERE id = :jobId")
     fun getJobById(jobId: String): JobEntity?
}

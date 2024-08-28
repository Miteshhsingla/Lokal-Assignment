package com.timeit.lokalassignment.data.remote

import com.timeit.lokalassignment.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("common/jobs")
    suspend fun getJobs(@Query("page") page: Int): Response<ApiResponse>
}

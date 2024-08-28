package com.timeit.lokalassignment.data.repository


import com.timeit.lokalassignment.utils.Resource
import com.timeit.lokalassignment.data.model.ApiResponse
import com.timeit.lokalassignment.data.remote.RetrofitClient

class JobRepository {

    suspend fun getJobs(page: Int): Resource<ApiResponse> {
        return try {
            val response = RetrofitClient.apiService.getJobs(page)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.Success(it)
                } ?: Resource.Error("No Data Found")
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }
}

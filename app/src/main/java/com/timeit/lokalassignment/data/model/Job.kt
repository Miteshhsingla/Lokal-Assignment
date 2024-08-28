package com.timeit.lokalassignment.data.model

data class ApiResponse(
    val results: List<Job>
)


data class Job(
    val id: String?,
    val title: String?,
    val primary_details: PrimaryDetails?,
    val company_name: String,
    val custom_link: String?,
    val creatives: List<Creative>,
    val contact_preference: ContactPreference
)

data class PrimaryDetails(
    val Place: String?,
    val Salary: String?,
    val Experience: String?,
)

data class Creative(
    val thumb_url: String,
)

data class ContactPreference(
    val whatsapp_link: String,
)


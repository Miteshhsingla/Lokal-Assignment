package com.timeit.lokalassignment.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey val id: String,
    val title: String?,
    val location: String?,
    val salary: String?,
    val phone: String?,
    val companyName:String?,
    val thumbUrl:String?,
    val whatsappLink:String?,
)




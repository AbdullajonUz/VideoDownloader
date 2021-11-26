package uz.abdullajon.videodownloader.data.models

import androidx.room.Entity


@Entity(tableName = "video")
data class VideoModel(
    private val url:String
)
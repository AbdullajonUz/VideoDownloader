package uz.abdullajon.videodownloader.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "video")
data class VideoModel(
    val url: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
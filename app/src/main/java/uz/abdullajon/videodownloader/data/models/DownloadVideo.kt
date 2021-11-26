package uz.abdullajon.videodownloader.data.models

data class DownloadVideo(
    val url: String,
    val currentProgress: Int,
    val videoSize: Float
)
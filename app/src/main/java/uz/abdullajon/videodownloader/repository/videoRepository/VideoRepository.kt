package uz.abdullajon.videodownloader.repository.videoRepository

import kotlinx.coroutines.flow.Flow
import uz.abdullajon.videodownloader.data.models.VideoModel

interface VideoRepository {

    fun getVideoList(): Flow<List<VideoModel>>

    fun addVideo(videoModel: VideoModel): Flow<Long>
}
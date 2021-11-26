package uz.abdullajon.videodownloader.repository.videoRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import uz.abdullajon.videodownloader.data.db.dao.VideoDao
import uz.abdullajon.videodownloader.data.models.VideoModel
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(private val videoDao: VideoDao) : VideoRepository {
    override fun getVideoList(): Flow<List<VideoModel>> = flow {
        emit(videoDao.getAllEvents())
    }

    override fun addVideo(videoModel: VideoModel): Flow<Long> = flow {
        emit(videoDao.addVideo(videoModel))
    }
}
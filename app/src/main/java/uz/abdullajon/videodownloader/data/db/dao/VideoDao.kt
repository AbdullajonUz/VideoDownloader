package uz.abdullajon.videodownloader.data.db.dao

import androidx.room.*
import uz.abdullajon.videodownloader.data.models.VideoModel

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addVideo(videoItem: VideoModel): Long

    @Update
    suspend fun updateData(videoItem: VideoModel)

    @Query("SELECT * FROM video")
    fun getAllEvents(): List<VideoModel>

}
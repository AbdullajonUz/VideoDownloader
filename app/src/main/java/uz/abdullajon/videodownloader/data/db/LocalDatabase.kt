package uz.abdullajon.videodownloader.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.abdullajon.videodownloader.data.db.dao.VideoDao
import uz.abdullajon.videodownloader.data.models.VideoModel

@Database(
    version = 1,
    exportSchema = false,
    entities = [VideoModel::class]
)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun videoDao(): VideoDao

}
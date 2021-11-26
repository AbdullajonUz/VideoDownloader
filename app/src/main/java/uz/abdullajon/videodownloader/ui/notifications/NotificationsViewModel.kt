package uz.abdullajon.videodownloader.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.abdullajon.videodownloader.data.models.VideoModel
import uz.abdullajon.videodownloader.repository.videoRepository.VideoRepository
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(private val videoRepository: VideoRepository) :
    ViewModel() {


    private val _videoList = MutableStateFlow<List<VideoModel>>(listOf())
    val videoList: StateFlow<List<VideoModel>> = _videoList.asStateFlow()

    private val _addVideo = MutableStateFlow(0L)
    val addVideo: StateFlow<Long> = _addVideo.asStateFlow()


    fun addVideoItem(videoModel: VideoModel) {
        viewModelScope.launch(Dispatchers.IO) {
            videoRepository.addVideo(videoModel).collect {
                _addVideo.value = it
            }
        }
    }

    fun getVideoList() {
        viewModelScope.launch(Dispatchers.IO) {
            videoRepository.getVideoList().collect {
                _videoList.value = it
            }
        }
    }
}
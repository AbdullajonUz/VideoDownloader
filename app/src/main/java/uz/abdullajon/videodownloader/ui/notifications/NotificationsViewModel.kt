package uz.abdullajon.videodownloader.ui.notifications

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.abdullajon.videodownloader.repository.videoRepository.VideoRepository
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(videoRepository: VideoRepository) : ViewModel() {


}
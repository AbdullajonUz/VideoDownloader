package uz.abdullajon.videodownloader.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.offline.DownloaderFactory
import dagger.hilt.android.AndroidEntryPoint
import uz.abdullajon.videodownloader.databinding.FragmentNotificationsBinding

@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    val VIDEO_URL = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4"
    val MAX_PREVIEW_CACHE_SIZE_IN_BYTES = 15L * 1024L * 1024L


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.etUrl.setText(VIDEO_URL)
        loadAction()
        return root
    }

    private fun loadAction() {
        binding.btnDownload.setOnClickListener {
            Log.d("TTTT", binding.etUrl.text.toString())
            downloadItem(VIDEO_URL)
        }
    }

    private fun downloadItem(videoUrl: String) {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
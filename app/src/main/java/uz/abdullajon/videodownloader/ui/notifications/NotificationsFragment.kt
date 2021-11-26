package uz.abdullajon.videodownloader.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.AndroidEntryPoint
import uz.abdullajon.videodownloader.databinding.FragmentNotificationsBinding
import uz.abdullajon.videodownloader.util.DemoUtil
import uz.abdullajon.videodownloader.util.DownloadTracker

@AndroidEntryPoint
class NotificationsFragment : Fragment(), DownloadTracker.Listener {

    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private var _binding: FragmentNotificationsBinding? = null

    private var downloadTracker: DownloadTracker? = null

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
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etUrl.setText(VIDEO_URL)
        downloadTracker = DemoUtil.getDownloadTracker(requireContext())
        loadAction()

    }

    override fun onStart() {
        super.onStart()
        downloadTracker!!.addListener(this)
    }

    override fun onStop() {
        super.onStop()
        downloadTracker!!.removeListener(this)
    }


    private fun loadAction() {
        binding.btnDownload.setOnClickListener {
            Log.d("TTTT", binding.etUrl.text.toString())
            downloadItem(VIDEO_URL)
        }
    }

    private fun downloadItem(videoUrl: String) {
        onSampleDownloadButtonClicked(videoUrl)

    }

    private fun onSampleDownloadButtonClicked(videoUrl: String) {
        val renderersFactory = DemoUtil.buildRenderersFactory( /* context= */
            requireContext(),
            true
        )
        downloadTracker!!.toggleDownload(
            childFragmentManager, MediaItem.fromUri(videoUrl), renderersFactory
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDownloadsChanged() {

    }
}
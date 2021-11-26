package uz.abdullajon.videodownloader.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.abdullajon.videodownloader.data.models.DownloadVideo
import uz.abdullajon.videodownloader.databinding.VideosItemBinding

class VideoDownloaderAdapter : ListAdapter<DownloadVideo, VideoDownloaderAdapter.VH>(diffUtil) {

    inner class VH(val itemVideosItemBinding: VideosItemBinding) :
        RecyclerView.ViewHolder(itemVideosItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(VideosItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val data = currentList[position]

        holder.itemVideosItemBinding.tvItemName.text = data.url
    }
}

private val diffUtil = object : DiffUtil.ItemCallback<DownloadVideo>() {
    override fun areItemsTheSame(oldItem: DownloadVideo, newItem: DownloadVideo): Boolean =
        oldItem.url == newItem.url


    override fun areContentsTheSame(oldItem: DownloadVideo, newItem: DownloadVideo): Boolean =
        oldItem == newItem
}
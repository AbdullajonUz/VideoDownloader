package uz.abdullajon.videodownloader.service

import android.app.Notification
import android.content.Context
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util
import uz.abdullajon.videodownloader.R
import uz.abdullajon.videodownloader.util.DOWNLOAD_NOTIFICATION_CHANNEL_ID
import uz.abdullajon.videodownloader.util.DemoUtil
import uz.abdullajon.videodownloader.util.FOREGROUND_NOTIFICATION_ID
import uz.abdullajon.videodownloader.util.JOB_ID

class VideoDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_NOTIFICATION_CHANNEL_ID, R.string.app_name, 0
) {
    override fun getDownloadManager(): DownloadManager {

        // This will only happen once, because getDownloadManager is guaranteed to be called only once
        // in the life cycle of the process.
        val downloadManager: DownloadManager? = DemoUtil.getDownloadManager( /* context= */this)
        val downloadNotificationHelper: DownloadNotificationHelper? =
            DemoUtil.getDownloadNotificationHelper( /* context= */this)
        downloadManager?.addListener(
            TerminalStateNotificationHelper(
                this,
                downloadNotificationHelper!!,
                FOREGROUND_NOTIFICATION_ID + 1
            )
        )
        return downloadManager!!
    }

    override fun getScheduler(): Scheduler? {
        return PlatformScheduler(this, JOB_ID)
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        return DemoUtil.getDownloadNotificationHelper( /* context= */this)!!
            .buildProgressNotification( /* context= */
                this,
                R.drawable.ic_download,  /* contentIntent= */
                null,  /* message= */
                null,
                downloads,
                notMetRequirements
            )
    }

    private class TerminalStateNotificationHelper(
        context: Context, notificationHelper: DownloadNotificationHelper, firstNotificationId: Int
    ) :
        DownloadManager.Listener {
        private val context: Context
        private val notificationHelper: DownloadNotificationHelper
        private var nextNotificationId: Int
        override fun onDownloadChanged(
            downloadManager: DownloadManager, download: Download, finalException: Exception?
        ) {
            val notification: Notification
            notification = if (download.state == Download.STATE_COMPLETED) {
                notificationHelper.buildDownloadCompletedNotification(
                    context,
                    R.drawable.ic_download,  /* contentIntent= */
                    null,
                    Util.fromUtf8Bytes(download.request.data)
                )
            } else if (download.state == Download.STATE_FAILED) {
                notificationHelper.buildDownloadFailedNotification(
                    context,
                    R.drawable.ic_download,  /* contentIntent= */
                    null,
                    Util.fromUtf8Bytes(download.request.data)
                )
            } else if (download.state == Download.STATE_DOWNLOADING) {
                return
            } else {
                return
            }
            NotificationUtil.setNotification(context, nextNotificationId++, notification)
        }

        init {
            this.context = context.applicationContext
            this.notificationHelper = notificationHelper
            nextNotificationId = firstNotificationId
        }
    }
}
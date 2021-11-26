package uz.abdullajon.videodownloader.util

import android.content.Context
import androidx.annotation.Nullable
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.RenderersFactory
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.ext.cronet.CronetDataSource
import com.google.android.exoplayer2.ext.cronet.CronetUtil
import com.google.android.exoplayer2.offline.ActionFileUpgradeUtil
import com.google.android.exoplayer2.offline.DefaultDownloadIndex
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Log
import org.chromium.net.CronetEngine
import uz.abdullajon.videodownloader.BuildConfig
import java.io.File
import java.io.IOException
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.Executors

/** Utility methods for the demo app.  */
object DemoUtil {
    const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"

    /**
     * Whether the demo application uses Cronet for networking. Note that Cronet does not provide
     * automatic support for cookies (https://github.com/google/ExoPlayer/issues/5975).
     *
     *
     * If set to false, the platform's default network stack is used with a [CookieManager]
     * configured in [.getHttpDataSourceFactory].
     */
    private const val USE_CRONET_FOR_NETWORKING = true
    private const val TAG = "DemoUtil"
    private const val DOWNLOAD_ACTION_FILE = "actions"
    private const val DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions"
    private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
    private var dataSourceFactory:  DataSource.Factory? = null
    private var httpDataSourceFactory:  HttpDataSource.Factory? = null

    private var databaseProvider: DatabaseProvider? = null

    private var downloadDirectory: File? = null

    private var downloadCache: Cache? = null

    private var downloadManager: DownloadManager? = null

    private var downloadTracker: DownloadTracker? = null

    private var downloadNotificationHelper: DownloadNotificationHelper? = null

    /** Returns whether extension renderers should be used.  */
    fun useExtensionRenderers(): Boolean {
        return BuildConfig.USE_DECODER_EXTENSIONS
    }

    fun buildRenderersFactory(
        context: Context, preferExtensionRenderer: Boolean
    ): RenderersFactory {
        @DefaultRenderersFactory.ExtensionRendererMode val extensionRendererMode: Int =
            if (useExtensionRenderers()) if (preferExtensionRenderer) DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER else DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON else DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        return DefaultRenderersFactory(context.applicationContext)
            .setExtensionRendererMode(extensionRendererMode)
    }

    @Synchronized
    fun getHttpDataSourceFactory(context: Context): HttpDataSource.Factory {
        var context = context
        if (httpDataSourceFactory == null) {
            if (USE_CRONET_FOR_NETWORKING) {
                context = context.applicationContext
                @Nullable val cronetEngine: CronetEngine? = CronetUtil.buildCronetEngine(context)
                if (cronetEngine != null) {
                    httpDataSourceFactory =
                        CronetDataSource.Factory(cronetEngine, Executors.newSingleThreadExecutor())
                }
            }

            if (httpDataSourceFactory == null) {
                // We don't want to use Cronet, or we failed to instantiate a CronetEngine.
                val cookieManager = CookieManager()
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
                CookieHandler.setDefault(cookieManager)
                httpDataSourceFactory = DefaultHttpDataSource.Factory()
            }
        }
        return httpDataSourceFactory!!
    }

    /** Returns a [DataSource.Factory].  */
    @Synchronized
    fun getDataSourceFactory(context: Context): DataSource.Factory? {
        var context = context
        if (dataSourceFactory == null) {
            context = context.applicationContext
            val upstreamFactory: DefaultDataSource.Factory =
                DefaultDataSource.Factory(context, getHttpDataSourceFactory(context))
            dataSourceFactory =
                buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache(context))
        }
        return dataSourceFactory
    }

    @Synchronized
    fun getDownloadNotificationHelper(
        context: Context
    ): DownloadNotificationHelper? {
        if (downloadNotificationHelper == null) {
            downloadNotificationHelper =
                DownloadNotificationHelper(context, DOWNLOAD_NOTIFICATION_CHANNEL_ID)
        }
        return downloadNotificationHelper
    }

    @Synchronized
    fun getDownloadManager(context: Context): DownloadManager? {
        ensureDownloadManagerInitialized(context)
        return downloadManager
    }

    @Synchronized
    fun getDownloadTracker(context: Context): DownloadTracker? {
        ensureDownloadManagerInitialized(context)
        return downloadTracker
    }

    @Synchronized
    private fun getDownloadCache(context: Context): Cache {
        if (downloadCache == null) {
            val downloadContentDirectory =
                File(getDownloadDirectory(context), DOWNLOAD_CONTENT_DIRECTORY)
            downloadCache = SimpleCache(
                downloadContentDirectory, NoOpCacheEvictor(), getDatabaseProvider(context)
            )
        }
        return downloadCache!!
    }

    @Synchronized
    private fun ensureDownloadManagerInitialized(context: Context) {
        if (downloadManager == null) {
            val downloadIndex = DefaultDownloadIndex(getDatabaseProvider(context))
            upgradeActionFile(
                context,
                DOWNLOAD_ACTION_FILE,
                downloadIndex,  /* addNewDownloadsAsCompleted= */
                false
            )
            upgradeActionFile(
                context,
                DOWNLOAD_TRACKER_ACTION_FILE,
                downloadIndex,  /* addNewDownloadsAsCompleted= */
                true
            )
            downloadManager = DownloadManager(
                context,
                getDatabaseProvider(context),
                getDownloadCache(context)!!,
                getHttpDataSourceFactory(context),
                Executors.newFixedThreadPool( /* nThreads= */6)
            )
            downloadTracker =
                DownloadTracker(context, getHttpDataSourceFactory(context), downloadManager)
        }
    }

    @Synchronized
    private fun upgradeActionFile(
        context: Context,
        fileName: String,
        downloadIndex: DefaultDownloadIndex,
        addNewDownloadsAsCompleted: Boolean
    ) {
        try {
            ActionFileUpgradeUtil.upgradeAndDelete(
                File(getDownloadDirectory(context), fileName),  /* downloadIdProvider= */
                null,
                downloadIndex,  /* deleteOnFailure= */
                true,
                addNewDownloadsAsCompleted
            )
        } catch (e: IOException) {
            Log.e(TAG, "Failed to upgrade action file: $fileName", e)
        }
    }

    @Synchronized
    private fun getDatabaseProvider(context: Context): DatabaseProvider {
        if (databaseProvider == null) {
            databaseProvider = StandaloneDatabaseProvider(context)
        }
        return databaseProvider!!
    }

    @Synchronized
    private fun getDownloadDirectory(context: Context): File? {
        if (downloadDirectory == null) {
            downloadDirectory = context.getExternalFilesDir( /* type= */null)
            if (downloadDirectory == null) {
                downloadDirectory = context.filesDir
            }
        }
        return downloadDirectory
    }

    private fun buildReadOnlyCacheDataSource(
        upstreamFactory: DataSource.Factory, cache: Cache
    ): CacheDataSource.Factory {
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
}
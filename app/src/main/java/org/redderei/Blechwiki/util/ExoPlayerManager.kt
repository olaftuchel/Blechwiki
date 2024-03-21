package org.redderei.Blechwiki.util

import android.content.*

//import com.google.android.exoplayer2.hls.HlsMediaSource;
class ExoPlayerManager private constructor(mContext: Context?) {
    /*
    var playerView: PlayerView
    var dataSourceFactory: DefaultDataSourceFactory
    var uriString = ""
    var mPlayList: ArrayList<String>? = null
    var playlistIndex = 0
    var listner: playerCallBack? = null
    private val mPlayer: SimpleExoPlayer
    fun setPlayerListener(mPlayerCallBack: playerCallBack?) {
        listner = mPlayerCallBack
    }

    fun playStream(urlToPlay: String) {
        Log.d(TAG, "ExoPlayerManager: playStream")
        uriString = urlToPlay
        var mp4VideoUri = Uri.parse(uriString)
        var videoSource: MediaSource? = null
        val filenameArray = urlToPlay.split("\\.".toRegex()).toTypedArray()
        if (uriString.toUpperCase().contains("M3U8")) {
            //videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediasource(mp4VideoUri);
            Toast.makeText(Util.Companion.context, "ATTENTION: Can't play M3U8 media", Toast.LENGTH_SHORT).show()
        } else {
            mp4VideoUri = Uri.parse(urlToPlay)
//            videoSource = ExtractorMediaSource(mp4VideoUri, dataSourceFactory, DefaultExtractorsFactory(), null, null)
            videoSource = ProgressiveMediaSource.Factory(dataSourceFactory, DefaultExtractorsFactory()).createMediaSource(Uri.parse(urlToPlay))
        }


        // Prepare the player with the source.
        mPlayer.prepare(videoSource!!)
        mPlayer.playWhenReady = true
    }

    @JvmName("setPlayerVolume")
    fun setPlayerVolume(vol: Float) {
        mPlayer.volume = vol
    }

    @JvmName("setUriString1")
    fun setUriString(uri: String) {
        uriString = uri
    }

    @JvmName("setPlaylist")
    fun setPlaylist(uriArrayList: ArrayList<String>?, index: Int, callBack: playerCallBack?) {
        mPlayList = uriArrayList
        playlistIndex = index
        listner = callBack
        playStream(mPlayList!![playlistIndex])
    }

    @JvmName("setPlaySwitch")
    fun playerPlaySwitch() {
        if (uriString !== "") {
            mPlayer.playWhenReady = !mPlayer.playWhenReady
        }
    }

    @JvmName("stopPlayer")
    fun stopPlayer(state: Boolean) {
        mPlayer.playWhenReady = !state
    }

    fun destroyPlayer() {
        mPlayer.stop()
    }

    @JvmName("isPlayerPlaying")
    fun isPlayerPlaying(): Boolean {
        return mPlayer.playWhenReady
    }

    @JvmName("readURLs")
    fun readURLs(url: String?): ArrayList<String>? {
        if (url == null) return null
        val allURls = ArrayList<String>()
        return try {
            val urls = URL(url)
            val `in` = BufferedReader(InputStreamReader(urls
                    .openStream()))
            var str: String
            while (`in`.readLine().also { str = it } != null) {
                allURls.add(str)
            }
            `in`.close()
            allURls
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        /**
         * declare some usable variable
         */
        private val BANDWIDTH_METER = DefaultBandwidthMeter()
        private const val TAG = "ExoPlayerManager"
        private var mInstance: ExoPlayerManager? = null

        /**
         * Return ExoPlayerManager instance
         * @param mContext
         * @return
         */
        fun getSharedInstance(mContext: Context?): ExoPlayerManager? {
            Log.d(TAG, "ExoPlayerManager: getSharedInstance")
            if (mInstance == null) {
                mInstance = ExoPlayerManager(mContext)
            }
            return mInstance
        }
    }

    /**
     * private constructor
     * @param mContext
     */
    init {
        val bandwithFraction = .8f
        val minDurationForQualityIncreaseMs = 1000
        val minDurationForQualityDecreaseMs = 2000
        val minDurationToRetainAfterDiscardMs = 2000
        val videoTrackSelectionFactory: ExoTrackSelection.Factory = AdaptiveTrackSelection.Factory(minDurationForQualityIncreaseMs, minDurationForQualityDecreaseMs, minDurationToRetainAfterDiscardMs, bandwithFraction)
        val trackSelector: TrackSelector =  DefaultTrackSelector(videoTrackSelectionFactory)
        mPlayer = ExoPlayerFactory.newSimpleInstance(mContext!!, trackSelector)
        playerView = PlayerView(mContext)
        playerView.useController = true
        playerView.requestFocus()
        playerView.player = mPlayer
        Log.v(TAG, "ExoPlayerManager")
        val mp4VideoUri = Uri.parse(uriString)
        dataSourceFactory = DefaultDataSourceFactory(mContext, com.google.android.exoplayer2.util.Util.getUserAgent(mContext, "androidwave"), BANDWIDTH_METER)
        val videoSource: MediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mp4VideoUri)
        mPlayer.prepare(videoSource)
        mPlayer.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {
                Log.i(TAG, "ExoPlayerManager: onTimelineChanged ")
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray, trackSelections: TrackSelectionArray) {
                Log.i(TAG, "ExoPlayerManager: onTracksChanged ")
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.i(TAG, "onLoadingChanged ")
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Log.i(TAG, "onPlayerStateChanged ")
                if (playbackState == 4 && mPlayList != null && playlistIndex + 1 < mPlayList!!.size) {
                    Log.e(TAG, ": Song Changed...")
                    playlistIndex++
                    listner!!.onItemClickOnItem(playlistIndex)
                    playStream(mPlayList!![playlistIndex])
                } else if (playbackState == 4 && mPlayList != null && playlistIndex + 1 == mPlayList!!.size) {
                    mPlayer.playWhenReady = false
                }
                if (playbackState == 4 && listner != null) {
                    listner!!.onPlayingEnd()
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.i(TAG, "onRepeatModeChanged ")
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                Log.i(TAG, "ExoPlayerManager: onShuffleModeEnabledChanged ")
            }

            override fun onPlayerError(error: ExoPlaybackException) {
                Log.i(TAG, "ExoPlayerManager: onPlayerError ")
            }

            override fun onPositionDiscontinuity(reason: Int) {
                Log.i(TAG, "ExoPlayerManager: onPositionDiscontinuity ")
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                Log.i(TAG, "ExoPlayerManager: onPlaybackParametersChanged ")
            }

            override fun onSeekProcessed() {
                Log.i(TAG, "ExoPlayerManager: onSeekProcessed ")
            }
        })
    }
 */
}
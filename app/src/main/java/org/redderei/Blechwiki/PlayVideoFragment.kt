package org.redderei.Blechwiki

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ui.PlayerView
import org.redderei.Blechwiki.R


// add this to project build.gradle:
// repositories {
//    maven { url "https://jitpack.io" }
// }
// https://androidwave.com/play-youtube-video-in-exoplayer/
class PlayVideoFragment : Fragment() {
    var mYoutubeLink = ""

    companion object {
        const val ARG_ITEM_VIDEOURI = "ARG_ITEM_VIDEOURI"
        const val ARG_ITEM_TITEL = "ARG_ITEM_TITEL"
        private const val NAME_TITLE = "title"
    }

    // String mYoutubeLink = "https://www.youtube.com/watch?v=wDOZMT2FdKY&list=PLgrjE8ONib08Mn6_LuLD0VmMQsSF-uywU&index=6&t=0s";
    interface CallBacks {
        fun callbackObserver(obj: Any?)
        interface playerCallBack {
            fun onItemClickOnItem(albumId: Int?)
            fun onPlayingEnd()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        Log.v(ContentValues.TAG, "PlayVideoFragment: onCreateView")
        mYoutubeLink = requireArguments().getString(PlayVideoFragment.ARG_ITEM_VIDEOURI).toString()
        val title = requireArguments().getString(PlayVideoFragment.ARG_ITEM_TITEL).toString()
        requireActivity().title = title
        val view = inflater.inflate(R.layout.video, container, false)
        extractYoutubeUrl()
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.v(ContentValues.TAG, "PlayVideoFragment: onAttach")
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
                true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                val fm = parentFragmentManager
                fm.popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
                this,  // LifecycleOwner
                callback)
    }

    /**
     * private void tellFragments(){
     * List<Fragment> fragments = getSupportFragmentManager().getFragments();
     * for(Fragment f : fragments){
     * if(f != null && f instanceof BaseFragment)
     * ((BaseFragment)f).onBackPressed();
     * }
     * }
    </Fragment> */
    private fun extractYoutubeUrl() {
        Log.v(ContentValues.TAG, "extractYoutubeUrl: onCreateView")
/*
        @SuppressLint("StaticFieldLeak") val mExtractor: YouTubeExtractor = object : YouTubeExtractor(requireContext()) {
            override fun onExtractionComplete(sparseArray: SparseArray<YtFile>, videoMeta: VideoMeta) {
                if (sparseArray != null) {
                    playVideo(sparseArray[18].url)
                }
            }
        }
        mExtractor.extract(mYoutubeLink, true, true)
*/
    }

    private fun playVideo(downloadUrl: String) {
        Log.v(ContentValues.TAG, "PlayVideoFragment: playVideo")
        val mPlayerView = requireView().findViewById<View>(R.id.video_view) as PlayerView


//        mPlayerView.player = ExoPlayerManager.getSharedInstance(context).getPlayerView().getPlayer()


////        ExoPlayerManager.getSharedInstance(context)!!.playStream(downloadUrl)
    }

    override fun onPause() {
        super.onPause()
        Log.d(ContentValues.TAG, "PlayVideoFragment (onPause)")
////        ExoPlayerManager.getSharedInstance(context)!!.destroyPlayer()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.d(ContentValues.TAG, "PlayVideoFragment (onSaveInstanceState)")
        if (requireActivity().title.toString() !== requireActivity().getString(R.string.app_name)) {
            // Serialize and persist the activated item position.
            savedInstanceState.putCharSequence(PlayVideoFragment.NAME_TITLE, requireActivity().title.toString())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            if (savedInstanceState.getCharSequence(PlayVideoFragment.Companion.NAME_TITLE) !== requireActivity().getString(R.string.app_name)) {
                // Checks for the title the screen
                requireActivity().title = savedInstanceState.getCharSequence(PlayVideoFragment.Companion.NAME_TITLE)
            }
        }
    }
}
package org.redderei.Blechwiki

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass
import org.redderei.Blechwiki.adapter.TitelInBuchAdapter
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass.Companion.buchFundstellen
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass.Companion.komponistFundstellen
import org.redderei.Blechwiki.repository.BuchViewModel
import org.redderei.Blechwiki.repository.KomponistViewModel
import org.redderei.Blechwiki.util.RecyclerTouchListener
import org.redderei.Blechwiki.util.SideIndex

// https://developer.android.com/reference/android/app/Fragment
/**
 * Created by ot775x on 24.06.2018.
 * Gets called from BuchFragment and KomponistFragment for
 * a. content of one book
 * b. songs written by one composer
 */
class FundstellenTitelFragment : Fragment() {
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
    var mBuchListView: List<TitelInBuchClass> = ArrayList()
    private lateinit var mAdapter: TitelInBuchAdapter
    private var mapIndex: Map<String, Int>? = null
    private var recyclerView: RecyclerView? = null
    private var searchString: String? = null
    private lateinit var rootView: View
    private var sortType = ""
    private var buchViewModel: BuchViewModel? = null
    private var komponistViewModel: KomponistViewModel? = null

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        // for setting the fragment title via ActivityDetail
        const val ARG_ITEM_LONGNAME = "ARG_ITEM_LONGNAME"
        const val ARG_ITEM_BUCH = "ARG_ITEM_BUCH"
        const val ARG_ITEM_KOMPONIST = "ARG_ITEM_KOMPONIST"
        private const val NAME_TITLE = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("FundstellenTitelFragment", "onCreate")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (requireArguments().containsKey(FundstellenTitelFragment.Companion.ARG_ITEM_BUCH)) {
            searchString = requireArguments().getString(FundstellenTitelFragment.Companion.ARG_ITEM_BUCH).toString()
            val clickAction = "GetBuchFundstellen"
            sortType = "Nr"
            Log.d("FundstellenTitelFragment", "onCreate ARG_ITEM_BUCH, searchString=$searchString")
            mAdapter = TitelInBuchAdapter(buchFundstellen, mBuchListView)
            buchViewModel = ViewModelProvider(this).get(BuchViewModel::class.java)
            GlobalScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    buchViewModel!!.getBuchDetails(searchString?.toInt()!!).observe(requireActivity())
                        {titel: List<TitelInBuchClass> ->
                            Log.v("FundstellenTitelFragment", "onCreate: mAdapter changed")
                            mAdapter.setListEntries(titel)
                            val mOnClickListener = View.OnClickListener { view: View -> onClick(view) }
                            // calculate Index List and show it up
                            mapIndex = SideIndex.getFundstellenBuchIndexList(mAdapter, sortType)
                            SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                        }
                }
            }
        } else if (requireArguments().containsKey(FundstellenTitelFragment.Companion.ARG_ITEM_KOMPONIST)) {
            searchString = requireArguments().getString(FundstellenTitelFragment.Companion.ARG_ITEM_KOMPONIST).toString()
            val clickAction = "GetKomponistFundstellen"
            sortType = "ABC"
            Log.d("FundstellenTitelFragment", "onCreate ARG_ITEM_TITEL, searchString=$searchString")
            mAdapter = TitelInBuchAdapter(komponistFundstellen, mBuchListView)
            komponistViewModel = ViewModelProvider(this).get(KomponistViewModel::class.java)
            GlobalScope.launch(Dispatchers.IO) {
                withContext(Dispatchers.Main) {
                    komponistViewModel!!.getKomponistDetails(searchString?.toInt()!!).observe(requireActivity())
                        {titel: List<TitelInBuchClass> ->
                            Log.v("FundstellenTitelFragment", "onCreate: mAdapter changed")
                            mAdapter.setListEntries(titel)
                            val mOnClickListener = View.OnClickListener { view: View -> onClick(view) }
                            // calculate Index List and show it up
                            mapIndex = SideIndex.getFundstellenBuchIndexList(mAdapter, sortType)
                            SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                        }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("FundstellenTitelFragment", "onCreateView")
        super.onCreateView(inflater, container, savedInstanceState)
        // Show the dummy content as text in a TextView.
        rootView = inflater.inflate(R.layout.fragment_blank, container, false)
        recyclerView = rootView.findViewById<View>(R.id.rv_recycler_view) as RecyclerView

        // getContext instead of getApplicationContext ??
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        recyclerView!!.adapter = mAdapter
        recyclerView!!.addOnItemTouchListener(RecyclerTouchListener(context, recyclerView, object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {  // play video
                        val videoTitle = mAdapter!!.mList[position].titel
                        val videoUrl = mAdapter!!.mList[position].audioURL
                        if (videoUrl !== "") {   // only in case of GetBuchFundstellen there might be a video-url
                            val arguments = Bundle()
                            arguments.putString(PlayVideoFragment.ARG_ITEM_TITEL, videoTitle)
                            arguments.putString(PlayVideoFragment.ARG_ITEM_VIDEOURI, videoUrl)
                            val fragment = PlayVideoFragment()
                            fragment.arguments = arguments
                            val fragmentTrans = parentFragmentManager.beginTransaction()
                            fragmentTrans.replace(R.id.activity_detail_container, fragment)
                            fragmentTrans.addToBackStack("FundstellenTitel").commit()
                        }
                    }

                    override fun onLongClick(view: View?, position: Int) {  // actually not idea for usage
                    }
                }))
      return rootView
    }

    override fun onResume() {
        super.onResume()
        Log.d("FundstellenTitelFragment", "onResume!!!!!!!!")
        requireActivity().title = requireArguments().getString(FundstellenTitelFragment.Companion.ARG_ITEM_LONGNAME).toString()
    }

    fun onClick(view: View) {
        Log.d("FundstellenTitelFragment", "onClick")
        val selectedIndex = view as TextView
        val index = mapIndex!![selectedIndex.text]!!
        recyclerView!!.layoutManager!!.scrollToPosition(index)
    }
/**
    fun soapQuery() {
        Log.d("FundstellenTitelFragment", "(soapQuery)")
        val mOnClickListener: View.OnClickListener
        mOnClickListener = View.OnClickListener { view: View -> onClick(view) }
        mContext = requireActivity().applicationContext as Context
        soapTask = SoapTask(mContext)
        soapTask!!.setDownloadCompleteListener(object : SoapTask.DownloadCompleteListener {
            override fun onUpdate(mBuchListView: List<*>?) {
                Log.v("FundstellenTitelFragment", "onUpdate")
                if (soapTask!!.isCancelled) {
                    Toast.makeText(context, "FundstellenTitelFragment: Network Error", Toast.LENGTH_SHORT).show()
                } else {
                    mAdapter.filter.filter("") {
                        mapIndex = SideIndex.getFundstellenBuchIndexList(mAdapter, sortType)
                        SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                    }
                }
            }
        })
        soapTask!!.execute(clickAction, searchString, mBuchListView)
        if (soapTask!!.isCancelled) {
            Toast.makeText(context, "FundstellenTitel: Network Error", Toast.LENGTH_SHORT).show()
        }
    }
*/
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("FundstellenTitelFragment", "onCreateOptionsMenu")
        val mOnClickListener: View.OnClickListener
        mOnClickListener = View.OnClickListener { view: View -> onClick(view) }
        // Inflate the menu.
        // Adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_general, menu)
        super.onCreateOptionsMenu(menu, inflater)

        // filtering from actionbar: item 52 https://stackoverflow.com/questions/7230893/android-search-with-fragments
        // define search view
        val item = menu.findItem(R.id.menu_action_search)
        val sv = SearchView((activity as ActivityDetail?)!!.supportActionBar!!.themedContext)
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItemCompat.SHOW_AS_ACTION_IF_ROOM)
        MenuItemCompat.setActionView(item, sv)
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Log.d("FundstellenTitelFragment", "onQueryTextSubmit: $query")
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                Log.d("FundstellenTitelFragment","onQueryTextChange: $query")
                mAdapter!!.filter.filter(query) {
                mapIndex = SideIndex.getFundstellenBuchIndexList(mAdapter, sortType)
                SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                }
                return false
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("FundstellenTitelFragment","onOptionsItemSelected")
        return when (item.itemId) {
            R.id.menu_action_ueber -> {
                //                Toast.makeText(getActivity(), "action about", Toast.LENGTH_SHORT).show();
                val intent = Intent(context, About::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("FundstellenTitelFragment", "onPause")
//        if (soapTask != null) soapTask!!.cancel(true)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.d("FundstellenTitelFragment", "onSaveInstanceState")
        if (requireActivity().title.toString() !== requireActivity().getString(R.string.app_name)) {
            // Serialize and persist the activated item position.
            savedInstanceState.putCharSequence(FundstellenTitelFragment.NAME_TITLE, requireActivity().title.toString())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            if (savedInstanceState.getCharSequence(FundstellenTitelFragment.Companion.NAME_TITLE) !== requireActivity().getString(R.string.app_name)) {
                // Checks for the title the screen
                requireActivity().title = savedInstanceState.getCharSequence(FundstellenTitelFragment.NAME_TITLE)
            }
        }
    }
}

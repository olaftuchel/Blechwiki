package org.redderei.Blechwiki

import android.content.Context
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass
import org.redderei.Blechwiki.adapter.TitelInBuchAdapter

// https://developer.android.com/reference/android/app/Fragment
class FundstellenTitelFragment
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
    : Fragment() {
    private var clickAction = "GetTitelFundstellen" // just right this titel is found
    var mBuchListView: List<TitelInBuchClass> = ArrayList()
    private lateinit var mAdapter: TitelInBuchAdapter
    private var mContext: Context? = null
    private var mapIndex: Map<String, Int>? = null
    private var recyclerView: RecyclerView? = null
    private var searchString: String? = null
    private lateinit var rootView: View
    private var sortType = ""

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
/*
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v(ContentValues.TAG, "FundstellenTitelFragment: onCreate")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (requireArguments().containsKey(FundstellenTitelFragment.Companion.ARG_ITEM_BUCH)) {
            searchString = requireArguments().getString(FundstellenTitelFragment.Companion.ARG_ITEM_BUCH).toString()
            clickAction = "GetBuchFundstellen"
            sortType = "Nr"
            Log.d(ContentValues.TAG, "FundstellenTitelFragment: onCreate ARG_ITEM_BUCH, searchString=$searchString")
        } else if (requireArguments().containsKey(FundstellenTitelFragment.Companion.ARG_ITEM_KOMPONIST)) {
            searchString = requireArguments().getString(FundstellenTitelFragment.Companion.ARG_ITEM_KOMPONIST).toString()
            clickAction = "GetKomponistFundstellen"
            sortType = "ABC"
            Log.d(ContentValues.TAG, "FundstellenTitelFragment: onCreate ARG_ITEM_TITEL, searchString=$searchString")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.v(ContentValues.TAG, "FundstellenTitelFragment: onCreateView")
        // Show the dummy content as text in a TextView.
        rootView = inflater.inflate(R.layout.fragment_blank, container, false)
        recyclerView = rootView.findViewById<View>(R.id.rv_recycler_view) as RecyclerView
        mAdapter = TitelInBuchAdapter(requireContext(), mBuchListView)

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
                    arguments.putString(PlayVideoFragment.Companion.ARG_ITEM_TITEL, videoTitle)
                    arguments.putString(PlayVideoFragment.Companion.ARG_ITEM_VIDEOURI, videoUrl)
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
//        soapQuery()
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(ContentValues.TAG, "FundstellenTitelFragment: onActivityCreated")
    }

    override fun onResume() {
        super.onResume()
        Log.d(ContentValues.TAG, "FundstellenTitelFragment (onResume)")
        requireActivity().title = requireArguments().getString(FundstellenTitelFragment.Companion.ARG_ITEM_LONGNAME).toString()
    }

    fun onClick(view: View) {
        Log.v(ContentValues.TAG, "FundstellenTitelFragment (onClick)")
        val selectedIndex = view as TextView
        val index = mapIndex!![selectedIndex.text]!!
        recyclerView!!.layoutManager!!.scrollToPosition(index)
    }
/*
    fun soapQuery() {
        Log.d(ContentValues.TAG, "FundstellenTitelFragment (soapQuery)")
        val mOnClickListener: View.OnClickListener
        mOnClickListener = View.OnClickListener { view: View -> onClick(view) }
        mContext = requireActivity().applicationContext as Context
        soapTask = SoapTask(mContext)
        soapTask!!.setDownloadCompleteListener(object : SoapTask.DownloadCompleteListener {
            override fun onUpdate(mBuchListView: List<*>?) {
                Log.v(ContentValues.TAG, "FundstellenTitelFragment: onUpdate")
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d(ContentValues.TAG, "FundstellenTitelFragment (onCreateOptionsMenu)")
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
                Log.v(ContentValues.TAG, "FundstellenTitelFragment (onQueryTextSubmit): $query")
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                Log.v(ContentValues.TAG, "FundstellenTitelFragment (onQueryTextChange): $query")
                if (mAdapter != null) {
                    mAdapter!!.filter.filter(query) {
                        mapIndex = SideIndex.getFundstellenBuchIndexList(mAdapter, sortType)
                        SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                    }
                }
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(ContentValues.TAG, "FundstellenTitelFragment: onOptionsItemSelected")
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
        Log.d(ContentValues.TAG, "FundstellenTitelFragment (onPause)")
//        if (soapTask != null) soapTask!!.cancel(true)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(ContentValues.TAG, "FundstellenTitelFragment (onAttach)")
    }

    override fun onStart() {
        super.onStart()
        Log.d(ContentValues.TAG, "FundstellenTitelFragment (onStart)")
    }

    override fun onStop() {
        super.onStop()
        Log.d(ContentValues.TAG, "FundstellenTitelFragment (onStop)")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(ContentValues.TAG, "FundstellenTitelFragment (onDestroyView)")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(ContentValues.TAG, "FundstellenTitelFragment (onDestroy)")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(ContentValues.TAG, "FundstellenTitelFragment (onDetach)")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.d(ContentValues.TAG, "FundstellenTitelFragment (onSaveInstanceState)")
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
 */
}

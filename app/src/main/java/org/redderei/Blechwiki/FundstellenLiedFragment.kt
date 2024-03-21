package org.redderei.Blechwiki

import android.content.*
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.redderei.Blechwiki.adapter.TitelInBuchAdapter
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass
//import org.redderei.posaunote.util.ActivityLifeCycleObserver
//import org.redderei.posaunote.util.RecyclerTouchListener.ClickListener
import java.util.*

/**
 * Created by ot775x on 24.06.2018.
 */
class FundstellenLiedFragment
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
    : Fragment() {
    private var clickAction = ""
    var mBuchListView: List<TitelInBuchClass> = ArrayList()
    private lateinit var mAdapter: TitelInBuchAdapter
    private var mContext: Context? = null
    private var mapIndex: Map<String, Int>? = null
    private var recyclerView: RecyclerView? = null
    private var searchString: String? = null
    private lateinit var rootView: View
    private val sortType = "ABC"

    companion object {
        // for setting the fragment title via ActivityDetail
        const val ARG_ITEM_LIED = "ARG_ITEM_LIED"
        const val ARG_ITEM_IXUR = "ARG_ITEM_IXUR"
        private const val NAME_TITLE = "title"
    }
/*
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v(ContentValues.TAG, "FundstellenLiedFragment: onCreate")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        clickAction = "GetEGLiederFundstellen"
        // what to search for (in this case IxUr)
        searchString = requireArguments().getString(ARG_ITEM_IXUR).toString()
        requireActivity().title = requireArguments().getString(ARG_ITEM_LIED).toString()
        Log.d(ContentValues.TAG, "FundstellenLiedFragment: onCreate ARG_ITEM_LIED searchString=$searchString")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.v(ContentValues.TAG, "FundstellenLiedFragment: onCreateView")
        // Show the dummy content as text in a TextView.
        rootView = inflater.inflate(R.layout.fragment_blank, container, false)
        recyclerView = rootView.findViewById<View>(R.id.rv_recycler_view) as RecyclerView
        mAdapter = TitelInBuchAdapter(requireContext(), mBuchListView)

        // getContect instead of getApplicationContext ??
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        recyclerView!!.adapter = mAdapter
        recyclerView!!.addOnItemTouchListener(RecyclerTouchListener(context, recyclerView, object :
            RecyclerTouchListener.ClickListener {
            override fun onClick(view: View?, position: Int) {  // show main buch properties
//                Toast.makeText(getContext(), "FundstellenLiedFragment(onCreateView(onClick)), position="+position, Toast.LENGTH_SHORT).show();
            }

            override fun onLongClick(view: View?, position: Int) {}
        }))
        soapQuery()
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(ActivityLifeCycleObserver {
        // code your stuff here
        Log.d(ContentValues.TAG, "FundstellenLiedFragment: onAttach")
        })
    }

 /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(ContentValues.TAG, "FundstellenLiedFragment: onActivityCreated")
    }
*/
    fun onClick(view: View) {
        Log.v(ContentValues.TAG, "FundstellenLiedFragment (onClick)")
        val selectedIndex = view as TextView
        val index = mapIndex!![selectedIndex.text]!!
        recyclerView!!.layoutManager!!.scrollToPosition(index)
    }

    fun soapQuery() {
        Log.d(ContentValues.TAG, "FundstellenLiedFragment (soapQuery)")
        /*
        val mOnClickListener: View.OnClickListener
        mOnClickListener = View.OnClickListener { view: View -> onClick(view) }
        mContext = requireActivity().applicationContext as Context
        soapTask = SoapTask(mContext)
        soapTask!!.setDownloadCompleteListener(object :
            SoapTask.Companion.MySoapTask.DownloadCompleteListener {
            override fun onUpdate(mBuchListview: List<*>?) {
                Log.v(ContentValues.TAG, "FundstellenLiedFragment: onUpdate")
                if (soapTask!!.isCancelled) {
                    Toast.makeText(context, "FundstellenLiedFragment: Network Error", Toast.LENGTH_SHORT).show()
                } else {
                    Sorter.Companion.sortFundstellenLied(mAdapter, -1)
                    mAdapter!!.filter.filter("") {
                        mapIndex = SideIndex.getFundstellenBuchIndexList(mAdapter, sortType)
                        SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                    }
                }
            }
        })
        soapTask!!.execute(clickAction, searchString, mBuchListView)
        if (soapTask!!.isCancelled) {
            Toast.makeText(context, "FundstellenLied: Network Error", Toast.LENGTH_SHORT).show()
        }

         */
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d(ContentValues.TAG, "FundstellenLiedFragment (onCreateOptionsMenu)")
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
                Log.v(ContentValues.TAG, "FundstellenLiedFragment (onQueryTextSubmit): $query")
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                Log.v(ContentValues.TAG, "FundstellenLiedFragment (onQueryTextChange): $query")
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
        Log.d(ContentValues.TAG, "FundstellenLiedFragment: onOptionsItemSelected")
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
        Log.d(ContentValues.TAG, "FundstellenLiedFragment (onPause)")
//        if (soapTask != null) soapTask!!.cancel(true)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.d(ContentValues.TAG, "FundstellenTitelFragment (onSaveInstanceState)")
        if (requireActivity().title.toString() !== activity!!.getString(R.string.app_name)) {
            // Serialize and persist the activated item position.
            savedInstanceState.putCharSequence(FundstellenLiedFragment.Companion.NAME_TITLE, requireActivity().title.toString())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            if (savedInstanceState.getCharSequence(FundstellenLiedFragment.Companion.NAME_TITLE) !== activity!!.getString(R.string.app_name)) {
                // Checks for the title the screen
                requireActivity().title = savedInstanceState.getCharSequence(FundstellenLiedFragment.Companion.NAME_TITLE)
            }
        }
    }
 */
}

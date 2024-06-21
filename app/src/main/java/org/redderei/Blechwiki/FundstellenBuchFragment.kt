package org.redderei.Blechwiki

import android.content.*
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.redderei.Blechwiki.adapter.TitelInBuchAdapter
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass.Companion.titelFundstellen
import org.redderei.Blechwiki.repository.TitelViewModel
import org.redderei.Blechwiki.util.RecyclerTouchListener
import org.redderei.Blechwiki.util.SideIndex
import java.util.*

/**
 * Created by ot775x on 24.06.2018.
 * Gets called from TitelFragment for one title, it shows up books where to find this title
 */
class FundstellenBuchFragment: Fragment() {
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
    var mTitelListView: List<TitelInBuchClass> = ArrayList()
    private lateinit var mAdapter: TitelInBuchAdapter
    private var mapIndex: Map<String, Int>? = null
    private var recyclerView: RecyclerView? = null
    private var searchString: String? = null
    private lateinit var rootView: View
    private val sortType = "ABC"
    private var titelViewModel: TitelViewModel? = null  // needs to be initialized with anything or abstract

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        // for setting the fragment title via ActivityDetail
        const val ARG_ITEM_TITEL = "ARG_ITEM_TITEL"
        const val ARG_ITEM_TITEL_IX = "ARG_ITEM_TITEL_IX"
        /*
        needs to be clarified
        */
        private const val NAME_TITLE = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v("FundstellenBuchFragment", "onCreate")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // searchString contains integer
        searchString = requireArguments().getString(ARG_ITEM_TITEL_IX).toString()
        requireActivity().title = requireArguments().getString(ARG_ITEM_TITEL).toString()
        Log.d("FundstellenBuchFragment", "onCreate ARG_ITEM_TITEL_IX searchString=$searchString")
        mAdapter = TitelInBuchAdapter(titelFundstellen, mTitelListView)
        titelViewModel = ViewModelProvider(this).get(TitelViewModel::class.java)
        // Update the cached copy of the words in the adapter.
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                titelViewModel!!.getTitelDetails(searchString?.toInt()!!).observe(requireActivity())
                {buecher: List<TitelInBuchClass> ->
                    Log.v("FundstellenBuchFragment", "onCreate: mAdapter changed")
                    mAdapter.setListEntries(buecher)
                    val mOnClickListener = View.OnClickListener { view: View -> onClick(view) }
                    // calculate Index List and show it up
                    mapIndex = SideIndex.getFundstellenBuchIndexList(mAdapter, sortType)
                    SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.v("FundstellenBuchFragment", "onCreateView")
        super.onCreateView(inflater, container, savedInstanceState)
        // Show the dummy content as text in a TextView.
        rootView = inflater.inflate(R.layout.fragment_blank, container, false)
        recyclerView = rootView.findViewById<View>(R.id.rv_recycler_view) as RecyclerView

        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        recyclerView!!.adapter = mAdapter
        recyclerView!!.addOnItemTouchListener(RecyclerTouchListener(context, recyclerView,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {  // show main buch properties
//                Toast.makeText(getContext(), "FundstellenBuchFragment(onCreateView(onClick)), position="+position, Toast.LENGTH_SHORT).show();
                    }

                    override fun onLongClick(view: View?, position: Int) {}
                }))
        return rootView
    }

    fun onClick(view: View) {
        Log.v("FundstellenBuchFragment", "onClick")
        val selectedIndex = view as TextView
        val index = mapIndex!![selectedIndex.text]!!
        recyclerView!!.layoutManager!!.scrollToPosition(index)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("FundstellenBuchFragment", "onCreateOptionsMenu")
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
                Log.v("FundstellenBuchFragment", "onQueryTextSubmit: $query")
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                Log.v("FundstellenBuchFragment","onQueryTextChange: $query")
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

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("FundstellenBuchFragment", "onOptionsItemSelected")
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
        Log.d(ContentValues.TAG, "FundstellenBuchFragment (onPause)")
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.d("FundstellenBuchFragment", "nSaveInstanceState")
        if (requireActivity().title.toString() !== activity!!.getString(R.string.app_name)) {
            // Serialize and persist the activated item position.
            savedInstanceState.putCharSequence(FundstellenBuchFragment.Companion.NAME_TITLE, requireActivity().title.toString())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            if (savedInstanceState.getCharSequence(FundstellenBuchFragment.Companion.NAME_TITLE) !== activity!!.getString(R.string.app_name)) {
                // Checks for the title the screen
                requireActivity().title = savedInstanceState.getCharSequence(FundstellenBuchFragment.Companion.NAME_TITLE)
            }
        }
    }
}

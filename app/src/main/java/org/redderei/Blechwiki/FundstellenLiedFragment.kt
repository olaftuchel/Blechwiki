package org.redderei.Blechwiki

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.redderei.Blechwiki.MainActivity.Companion.appContext
import org.redderei.Blechwiki.adapter.TitelInBuchAdapter
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass
import org.redderei.Blechwiki.gettersetter.TitelInBuchClass.Companion.egLiedFundstellen
import org.redderei.Blechwiki.repository.LiedViewModel
import org.redderei.Blechwiki.util.ActivityLifeCycleObserver
import org.redderei.Blechwiki.util.RecyclerTouchListener
import org.redderei.Blechwiki.util.SideIndex
import java.util.*

/**
 * Created by ot775x on 24.06.2018.
 * Gets called from LiedFragment for one EG song, it shows up  where to find this title
 */
class FundstellenLiedFragment : Fragment(), View.OnClickListener {
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
    var mTitelInBuchList: List<TitelInBuchClass> = ArrayList()
    private lateinit var mAdapter: TitelInBuchAdapter
    private var mapIndex: Map<String, Int>? = null
    private var recyclerView: RecyclerView? = null
    private var searchString: String? = null
    private lateinit var rootView: View
    private val sortType = "ABC"
    private var liedViewModel: LiedViewModel? = null  // needs to be initialized with anything or abstract

    companion object {
        // for setting the fragment title via ActivityDetail
        const val ARG_ITEM_LIED = "ARG_ITEM_LIED"
        const val ARG_ITEM_IXUR = "ARG_ITEM_IXUR"
        private const val NAME_TITLE = "title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v("FundstellenLiedFragment","onCreate")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        // what to search for (in this case IxUr)
        searchString = requireArguments().getString(ARG_ITEM_IXUR).toString()
        requireActivity().title = requireArguments().getString(ARG_ITEM_LIED).toString()
        Log.d("FundstellenLiedFragment", "onCreate ARG_ITEM_LIED searchString=$searchString")
        mAdapter = TitelInBuchAdapter(egLiedFundstellen, mTitelInBuchList)
        liedViewModel = ViewModelProvider(this).get(LiedViewModel::class.java)
        // Update the cached copy of the words in the adapter.
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                liedViewModel!!.getLiedDetails(searchString?.toInt()!!).observe(requireActivity())
                    {buecher: List<TitelInBuchClass> ->
                        Log.v("FundstellenLiedFragment", "onCreate: onChanged: mAdapter changed")
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
        Log.v("FundstellenLiedFragment","onCreateView")
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
            object: RecyclerTouchListener.ClickListener {
            override fun onClick(view: View?, position: Int) {  // show main buch properties
                Toast.makeText(getContext(), "FundstellenLiedFragment(onCreateView(onClick)), position="+position, Toast.LENGTH_SHORT).show();
            }

            override fun onLongClick(view: View?, position: Int) {}
        }))
        return rootView
    }
/*
    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity?.lifecycle?.addObserver(ActivityLifeCycleObserver {
        // code your stuff here
        Log.d("FundstellenLiedFragment","FundstellenLiedFragment: onAttach")
        })
    }
*/
 /*
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("FundstellenLiedFragment","FundstellenLiedFragment: onActivityCreated")
    }
*/
    override fun onClick(view: View) {
        Log.v("FundstellenLiedFragment","FundstellenLiedFragment (onClick)")
        val selectedIndex = view as TextView
        val index = mapIndex!![selectedIndex.text]!!
        recyclerView!!.layoutManager!!.scrollToPosition(index)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("FundstellenLiedFragment","FundstellenLiedFragment (onCreateOptionsMenu)")
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
                Log.v("FundstellenLiedFragment","FundstellenLiedFragment (onQueryTextSubmit): $query")
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                Log.v("FundstellenLiedFragment","FundstellenLiedFragment (onQueryTextChange): $query")
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
        Log.d("FundstellenLiedFragment","FundstellenLiedFragment: onOptionsItemSelected")
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
        Log.d("FundstellenLiedFragment","onPause")
//        if (soapTask != null) soapTask!!.cancel(true)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.d("FundstellenLiedFragment","onSaveInstanceState")
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
}

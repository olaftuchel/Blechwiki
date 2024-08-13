package org.redderei.Blechwiki

import android.app.SearchManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
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
import org.redderei.Blechwiki.MainActivity.Companion.appContext
import org.redderei.Blechwiki.gettersetter.KomponistClass
import org.redderei.Blechwiki.repository.KomponistViewModel
import org.redderei.Blechwiki.adapter.KomponistAdapter
import org.redderei.Blechwiki.util.RecyclerTouchListener
import org.redderei.Blechwiki.util.SideIndex

/**
 * A simple [Fragment] subclass.
 */
class KomponistFragment : Fragment(), View.OnClickListener {
    private var mList: List<KomponistClass> = ArrayList()
    private lateinit var mAdapter: KomponistAdapter
    private var mapIndex: Map<String, Int>? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var rootView: View
    private val mDualPane = false
    private var mActivatedPosition = ListView.INVALID_POSITION
    private var komponistViewModel: KomponistViewModel? = null

    companion object {
        private const val STATE_ACTIVATED_POSITION = "activated_position"
    }

//    fun onUpdate(mList: List<*>?) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("KomponistFragment", "onCreate: savedInstanceState=$savedInstanceState")
        val mOnClickListener = View.OnClickListener { view: View -> onClick(view) }
        mAdapter = KomponistAdapter(mList)

        komponistViewModel = ViewModelProvider(this).get(KomponistViewModel::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                komponistViewModel!!.getAllKomponist("")?.observe(appContext) { komponist -> // Update the cached copy of the words in the adapter.
                        Log.d("KomponistFragment", "onCreate: mAdapter changed ")
                        mAdapter!!.setListEntries(komponist)
                        // calculate Index List and show it up
                        mapIndex = SideIndex.getKomponistIndexList(mAdapter!!)
                        SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                    }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d("KomponistFragment", "onCreateView: savedInstanceState=$savedInstanceState")

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(KomponistFragment.Companion.STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(KomponistFragment.Companion.STATE_ACTIVATED_POSITION))
        }

        // For having optionsmenu http://droidmentor.com/how-to-use-fragment-specific-menu-in-android/
        setHasOptionsMenu(true)
        rootView = inflater.inflate(R.layout.fragment_blank, container, false)
        recyclerView = rootView.findViewById<View>(R.id.rv_recycler_view) as RecyclerView

        // getContect instead of getApplicationContext ??
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        recyclerView!!.adapter = mAdapter
        recyclerView!!.addOnItemTouchListener(RecyclerTouchListener(context, recyclerView, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View?, position: Int) {
                onMyItemClick(position)
            }

            override fun onLongClick(view: View?, position: Int) {}
        }))
        return rootView
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("KomponistFragment", "onActivityCreated, savedInstanceState=$savedInstanceState")

//        // 4. Access the ListView
//        mListView = getListView();
//
//        // The detail container view will be present only in the
//        // large-screen layouts (res/values-large and
//        // res/values-sw600dp). If this view is present, then the
//        // activity should be in two-pane mode.
//        View detailsFrame = getActivity().findViewById(R.id.fragment_detail_container);
//        mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
//
//        if (savedInstanceState != null) {
//            // Restore last state for checked position.
//            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
//        }
    }

    // -------- Alphabetischer Index neben der Liste --------
    // http://androidopentutorials.com/android-listview-with-alphabetical-side-index/
    override fun onClick(view: View) {
        Log.v("KomponistFragment", "onClick")
        val selectedIndex = view as TextView
        val index = mapIndex!![selectedIndex.text]!!
        recyclerView!!.layoutManager!!.scrollToPosition(index)
    }

    fun onMyItemClick(position: Int) {
        Log.d("KomponistFragment", "onListItemClick: position=$position")
        val shortName = mAdapter!!.mList[position].id.toString()
        val longName = mAdapter!!.mList[position].komponist
        if (mDualPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            val arguments = Bundle()
            arguments.putString(FundstellenTitelFragment.ARG_ITEM_KOMPONIST, shortName)
            arguments.putString(FundstellenTitelFragment.ARG_ITEM_LONGNAME, longName)
            val fragment = FundstellenTitelFragment()
            fragment.arguments = arguments
            requireFragmentManager().beginTransaction().replace(R.id.activity_detail_container, fragment).commit()
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            val detailIntent = Intent(activity, ActivityDetail::class.java)
            detailIntent.putExtra("CALLING_FRAGMENT", "KomponistFragment")
            detailIntent.putExtra(FundstellenTitelFragment.ARG_ITEM_KOMPONIST, shortName)
            detailIntent.putExtra(FundstellenTitelFragment.ARG_ITEM_LONGNAME, longName)
            startActivity(detailIntent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //if (menu.size() == 0)
        run {
            Log.d("KomponistFragment", "onCreateOptionsMenu")
            val mOnClickListener: View.OnClickListener
            mOnClickListener = View.OnClickListener { view: View -> this.onClick(view) }
            // Inflate the menu.
            // Adds items to the action bar if it is present.
            menu.clear() //without clear menu items are duplicated
            inflater.inflate(R.menu.menu_general, menu)
            super.onCreateOptionsMenu(menu, inflater)
            val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
            val searchView = menu.findItem(R.id.menu_action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            searchView.maxWidth = Int.MAX_VALUE
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d("KomponistFragment", "onQueryTextSubmit: $query")
                    mAdapter!!.filter.filter(query)
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    Log.d("KomponistFragment", "onQueryTextChange: $query")
                    if (mAdapter != null) {
                        mAdapter!!.filter.filter(query) { // calculate Index List and show it up
                            mapIndex = SideIndex.getKomponistIndexList(mAdapter)
                            SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                        }
                    }
                    return false
                }
            })
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("KomponistFragment", "onOptionsItemSelected")
        return when (item.itemId) {
            R.id.menu_action_search -> true
            R.id.menu_action_ueber -> {
                //                Toast.makeText(getActivity(), "action about", Toast.LENGTH_SHORT).show();
                val intent = Intent(context, About::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("KomponistFragment", "onSaveInstanceState")
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(KomponistFragment.Companion.STATE_ACTIVATED_POSITION, mActivatedPosition)
        }
    }

    //
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(mActivity, "landscape", Toast.LENGTH_SHORT).show();
            Log.v("KomponistFragment", "onConfigurationChanged: landscape")
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(mActivity, "portrait", Toast.LENGTH_SHORT).show();
            Log.v("KomponistFragment", "onConfigurationChanged: portrait")
        }
    }

    private fun setActivatedPosition(position: Int) {
        Log.d("KomponistFragment", "setActivatedPosition: position=$position")
        //        if (position == ListView.INVALID_POSITION) {
//            getListView().setItemChecked(mActivatedPosition, false);
//        } else {
//            getListView().setItemChecked(position, true);
//        }
        mActivatedPosition = position
    }

    override fun onStart() {
        super.onStart()
        Log.d("KomponistFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("KomponistFragment", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("KomponistFragment", "onPause")
//        soapTask?.cancel(true)
    }

    override fun onStop() {
        super.onStop()
        Log.d("KomponistFragment", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("KomponistFragment", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("KomponistFragment", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("KomponistFragment", "onDetach")
    }
}

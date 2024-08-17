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
import androidx.lifecycle.*
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.redderei.Blechwiki.MainActivity.Companion.appContext
import org.redderei.Blechwiki.adapter.BuchAdapter
import org.redderei.Blechwiki.gettersetter.BuchClass
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.repository.BuchViewModel
import org.redderei.Blechwiki.util.RecyclerTouchListener
import org.redderei.Blechwiki.util.SideIndex

/**
 * A simple [Fragment] subclass.
 */
class BuchFragment : Fragment(), View.OnClickListener {
    var mBuchList: List<BuchClass> = ArrayList()
    private lateinit var mAdapter: BuchAdapter
    private var mapIndex: Map<String, Int>? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var rootView: View
    private val mDualPane = false
    private var mActivatedPosition = ListView.INVALID_POSITION
    private var blechViewModel: BuchViewModel? = null

    companion object {
        private const val STATE_ACTIVATED_POSITION = "activated_position"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("BuchFragment", "onCreate: savedInstanceState=$savedInstanceState")
        val mOnClickListener = View.OnClickListener { view: View -> onClick(view) }
        mAdapter = BuchAdapter(mBuchList)

        blechViewModel = ViewModelProvider(this).get(BuchViewModel::class.java)
        GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                blechViewModel!!.getAllBuecher("")?.observe(appContext, { buecher -> // Update the cached copy of the words in the adapter.
                        Log.d("BuchFragment", "onCreate:  mAdapter changed ")
                        mAdapter!!.setListEntries(buecher)
                        // calculate Index List and show it up
                        mapIndex = SideIndex.getBuchIndexList(mAdapter!!)
                        SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                    })
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d("BuchFragment", "onCreateView: savedInstanceState=$savedInstanceState")

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(BuchFragment.Companion.STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(BuchFragment.Companion.STATE_ACTIVATED_POSITION))
        }

        // For having optionsmenu http://droidmentor.com/how-to-use-fragment-specific-menu-in-android/
        setHasOptionsMenu(true)
        rootView = inflater.inflate(R.layout.fragment_blank, container, false)
        recyclerView = rootView.findViewById<View>(R.id.rv_recycler_view) as RecyclerView

        // getContext instead of getApplicationContext ??
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        recyclerView!!.adapter = mAdapter
        recyclerView!!.addOnItemTouchListener(RecyclerTouchListener(context, recyclerView, object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        onMyItemClick(position)
                    }

                    override fun onLongClick(view: View?, position: Int) {
                        Log.v("BuchFragment", "onCreateView): onLongClick")
                        // Toast.makeText(mActivity, "OnLongClick position=" + position, Toast.LENGTH_SHORT).show();
                        val detailIntent = Intent(activity, BuchDetailActivity::class.java)
                        detailIntent.putExtra(BuchDetailActivity.ARG_ITEM_BUCH, mAdapter!!.mList[position].buch)
                        detailIntent.putExtra(BuchDetailActivity.ARG_ITEM_UNTERTITEL, mAdapter!!.mList[position].untertitel)
                        detailIntent.putExtra(BuchDetailActivity.ARG_ITEM_ERSCHEINJAHR, mAdapter!!.mList[position].erscheinjahr)
                        detailIntent.putExtra(BuchDetailActivity.ARG_ITEM_HERAUSGEBER, mAdapter!!.mList[position].herausgeber)
                        detailIntent.putExtra(BuchDetailActivity.ARG_ITEM_HERAUSGEBERVORNAME, mAdapter!!.mList[position].herausgvorname)
                        detailIntent.putExtra(BuchDetailActivity.ARG_ITEM_VERLAG, mAdapter!!.mList[position].verlag)
                        detailIntent.putExtra(BuchDetailActivity.ARG_ITEM_VERLAGSNUMMER, mAdapter!!.mList[position].verlagsnummer)
                        detailIntent.putExtra(BuchDetailActivity.ARG_ITEM_IMGURL, Constant.imgURL + mAdapter!!.mList[position].buchkurz + ".jpg")
// without content of imgUrl
//                detailIntent.putExtra(BuchDetailActivity.ARG_ITEM_IMGURL, "https://de.wikipedia.org/wiki/Buch#/media/Datei:Bibel-1.jpg")
                        startActivity(detailIntent)
                    }
                }))
        return rootView
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("BuchFragment", "onActivityCreated: savedInstanceState=$savedInstanceState")
        // idea of the blow is: dependant of device size show (see onMyItemClick)
        // a. just all books
        // or. all books and details of one book nearby for large devices

//        // 4. Access the ListView
//        mBuchListView = getListView();
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
        Log.v("BuchFragment", "onClick")
        val selectedIndex = view as TextView
        val index = mapIndex!![selectedIndex.text]!!
        recyclerView!!.layoutManager!!.scrollToPosition(index)
    }

    fun onMyItemClick(position: Int) {
        Log.d("BuchFragment", "onListItemClick: position=$position")
        val idString = mAdapter!!.mList[position].buchId.toString()
        val longName = mAdapter!!.mList[position].buch
        if (mDualPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            val arguments = Bundle()
            arguments.putString(FundstellenTitelFragment.ARG_ITEM_BUCH, idString)
            arguments.putString(FundstellenTitelFragment.ARG_ITEM_LONGNAME, longName)
            val fragment = FundstellenTitelFragment()
            fragment.arguments = arguments
            requireFragmentManager().beginTransaction().replace(R.id.activity_detail_container, fragment).commit()
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            val detailIntent = Intent(activity, ActivityDetail::class.java)
            detailIntent.putExtra("CALLING_FRAGMENT", "BuchFragment")
            detailIntent.putExtra(FundstellenTitelFragment.ARG_ITEM_BUCH, idString)
            detailIntent.putExtra(FundstellenTitelFragment.ARG_ITEM_LONGNAME, longName)
            startActivity(detailIntent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d("BuchFragment", "onCreateOptionsMenu")
        //if (menu.size() == 0)
        run {
            val mOnClickListener = View.OnClickListener { view: View -> this.onClick(view) }
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
                    Log.v("BuchFragment", "onQueryTextSubmit query: $query")
                    mAdapter!!.filter.filter(query)
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    Log.v("BuchFragment", "onQueryTextChange query: >$query<")
                    if (mAdapter != null) {
                        mAdapter!!.filter.filter(query) {
                            mapIndex = SideIndex.getBuchIndexList(mAdapter!!)
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
        Log.d("BuchFragment", "onOptionsItemSelected")
        return when (item.itemId) {
            R.id.menu_action_search -> true
            R.id.menu_action_ueber -> {
                val intent = Intent(context, About::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("BuchFragment", "onSaveInstanceState")
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(BuchFragment.Companion.STATE_ACTIVATED_POSITION, mActivatedPosition)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(mActivity, "landscape", Toast.LENGTH_SHORT).show();
            Log.v("BuchFragment", "onConfigurationChanged: landscape")
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(mActivity, "portrait", Toast.LENGTH_SHORT).show();
            Log.v("BuchFragment", "onConfigurationChanged: portrait")
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
     */
    private fun setActivatedPosition(position: Int) {
        Log.d("BuchFragment", "setActivatedPosition: [$position]")
        //        if (position == ListView.INVALID_POSITION) {
//            getListView().setItemChecked(mActivatedPosition, false);
//        } else {
//            getListView().setItemChecked(position, true);
//        }
        mActivatedPosition = position
    }

    override fun onStart() {
        super.onStart()
        Log.d("BuchFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("BuchFragment", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("BuchFragment", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("BuchFragment", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("BuchFragment", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("BuchFragment", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("BuchFragment", "onDetach")
    }
}

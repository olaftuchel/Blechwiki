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
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.redderei.Blechwiki.MainActivity.Companion.appContext
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.gettersetter.LiedClass
import org.redderei.Blechwiki.adapter.LiedAdapter
import org.redderei.Blechwiki.repository.LiedViewModel
import org.redderei.Blechwiki.util.*
import org.redderei.posaunote.R

/**
 * Basis for Tabviews:
 * https://android.jlelse.eu/tablayout-and-viewpager-in-your-android-app-738b8840c38a
 * Basis for master / detail:
 * https://inducesmile.com/android/android-fragment-masterdetail-flow-tutorial-in-android-studio/
 * Basis for RecyclerView and filtering:
 * https://www.androidhive.info/2016/01/android-working-with-recycler-view/
 * A simple [Fragment] subclass.
 */
class LiedFragment : Fragment(), View.OnClickListener {
    private val mLiedList: List<LiedClass> = ArrayList()
    private lateinit var mAdapter: LiedAdapter
    private var mapIndex: Map<String, Int>? = null
    private var mKircheOld = ""
    private var mKirche = ""
    private var recyclerView: RecyclerView? = null
    private lateinit var rootView: View
    private val mDualPane = false
    private var sortType = ""
//??    public final mBlechViewModel = getArguments()
    private var liedViewModel: LiedViewModel? = null
    private val sharedPreference: SharedPreference = SharedPreference(appContext)

    // The current activated item position. Only used on tablets
    private var mActivatedPosition = ListView.INVALID_POSITION


    companion object {
        /**
         * Check below parameters for Tablet implementation
         * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
         */
        private const val STATE_ACTIVATED_POSITION = "activated_position"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ContentValues.TAG, "LiedFragment (onCreate) ")
        val mOnClickListener = View.OnClickListener { view: View -> onClick(view) }
        mAdapter = LiedAdapter(mLiedList)
        // Get a new or existing ViewModel from the ViewModelProvider.
        // Lieder einer Landeskirche (Teil)
        mKirche = sharedPreference.getValueString(Constant.PREF_KIRCHE).toString()
        sortType = sharedPreference.getValueString(Constant.PREF_SORTTYPE).toString()
//        mBlechViewModel = ViewModelProvider(this).get(BlechViewModel::class.java)
//        MainActivity.appContext.blechViewModel!!.getAllLieder(mKirche, sortType, "")?.observe(this, { lieder -> // Update the cached copy of the words in the adapter.
        // Update the cached copy of the words in the adapter.
            liedViewModel!!.getAllLieder(mKirche, sortType, "")?.observe(this, {lieder ->
            Log.v(ContentValues.TAG, "LiedFragment (onCreate: onChanged): mAdapter changed ")
            mAdapter.setListEntries(lieder)
            // calculate Index List and show it up
            mapIndex = SideIndex.getLiedIndexList(mAdapter, sortType)
            SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(ContentValues.TAG, "LiedFragment (onCreateView): savedInstanceState=$savedInstanceState")

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION))
        }

        // For having optionsmenu http://droidmentor.com/how-to-use-fragment-specific-menu-in-android/
        setHasOptionsMenu(true)
        rootView = inflater.inflate(R.layout.fragment_blank, container, false)

        // RecyclerView https://stackoverflow.com/questions/34579614/how-to-implement-recyclerview-with-cardview-rows-in-a-fragment-with-tablayout
        // Inflate the layout for this fragment
        recyclerView = rootView.findViewById<View>(R.id.rv_recycler_view) as RecyclerView

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
        Log.d(ContentValues.TAG, "LiedFragment (onActivityCreated): savedInstanceState=$savedInstanceState")

//       // The detail container view will be present only in the
//       // large-screen layouts (res/values-large and
//       // res/values-sw600dp). If this view is present, then the
//       // activity should be in two-pane mode.
//         View detailsFrame = rootView.findViewById(R.id.fragment_detail_container);
//         mDualPane = detailsFrame != null && getResources().getBoolean(R.bool.twoPaneMode);
//         mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
//
//         if (savedInstanceState != null) {
//             // Restore last state for checked position.
//             mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
//         }
    }

    //click on side index to scroll to specific item
    override fun onClick(view: View) {
        Log.v(ContentValues.TAG, "LiedFragment: (onClick)")
        val selectedIndex = view as TextView

        // scroll down to line "index"
        val index = mapIndex!![selectedIndex.text]!!
        recyclerView!!.layoutManager!!.scrollToPosition(index)
    }

    ////    @Override
    fun onMyItemClick(position: Int) {
        Log.d(ContentValues.TAG, "LiedFragment (onMyItemClick): position=$position")
        val ixUr = mAdapter!!.mLiedList[position].ixUr
        val idString = mAdapter!!.mLiedList[position].lied

        // https://inducesmile.com/android/android-fragment-masterdetail-flow-tutorial-in-android-studio/
        if (mDualPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            val arguments = Bundle()
            arguments.putString(FundstellenLiedFragment.ARG_ITEM_IXUR, ixUr)
            arguments.putString(FundstellenLiedFragment.ARG_ITEM_LIED, idString)
            val fragment = FundstellenLiedFragment()
            fragment.arguments = arguments
            requireFragmentManager().beginTransaction().replace(R.id.activity_detail_container, fragment).commit()
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            val detailIntent = Intent(activity, ActivityDetail::class.java)
            detailIntent.putExtra("CALLING_FRAGMENT", "LiedFragment")
            detailIntent.putExtra(FundstellenLiedFragment.ARG_ITEM_IXUR, ixUr)
            detailIntent.putExtra(FundstellenLiedFragment.ARG_ITEM_LIED, idString)
            startActivity(detailIntent)
        }
    }

    // How to use fragment specific menu in android ?
    // Options menu continuing http://droidmentor.com/how-to-use-fragment-specific-menu-in-android/
    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //if (menu.size() == 0)
        run {
            Log.d(ContentValues.TAG, "LiedFragment (onCreateOptionsMenu)")
            val mOnClickListener = View.OnClickListener { view: View -> this.onClick(view) }
            // Inflate the menu.
            // Adds items to the action bar if it is present.
            menu.clear() //without clear menu items are duplicated
            inflater.inflate(R.menu.menu_lied_fragment, menu)
            super.onCreateOptionsMenu(menu, inflater)

            // filtering from actionbar: https://www.androidhive.info/2017/11/android-recyclerview-with-search-filter-functionality/?utm_source=recyclerview&utm_medium=site&utm_campaign=refer_article
            // Associate searchable configuration with the SearchView
            val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
            val searchView = menu.findItem(R.id.menu_action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            searchView.maxWidth = Int.MAX_VALUE
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d(ContentValues.TAG, "LiedFragment (onQueryTextSubmit): " + query + "and Kirche " + mKirche)
                    mAdapter!!.filter.filter(query)
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    Log.v(ContentValues.TAG, "LiedFragment (onQueryTextChange): filter>$query<")
                    if (mAdapter != null) {
                        mAdapter!!.filter.filter(query) {
                            mapIndex = SideIndex.getLiedIndexList(mAdapter, sortType)
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
        Log.d(ContentValues.TAG, "LiedFragment (onOptionsItemSelected)")
        val mOnClickListener: View.OnClickListener
        mOnClickListener = View.OnClickListener { view: View -> onClick(view) }
        return when (item.itemId) {
            R.id.menu_action_search -> true
            R.id.menu_action_kirche -> {
                // see MainActivity displayWelcome for more comments
                mKircheOld = sharedPreference.getValueString(Constant.PREF_KIRCHE).toString()
                val a = AlertDialogWithList()
                mKirche = a.withItems(mKircheOld)
                sharedPreference.save(Constant.PREF_KIRCHE, mKirche!!)
                Log.v(ContentValues.TAG, "LiedFragment (onOptionsItemSelected): mKirche=$mKirche selected")
                liedViewModel!!.getAllLieder(mKirche, sortType, "")?.observe(requireActivity(), { lieder -> // Update the cached copy of the words in the adapter.
                    Log.d(ContentValues.TAG, "LiedFragment (onCreate: onChanged): mAdapter changed ")
                    mAdapter!!.setListEntries(lieder)
                    // calculate Index List and show it up
                    mapIndex = SideIndex.getLiedIndexList(mAdapter, sortType)
                    SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                })

//                val p = PromptForResult()
//                p.promptForResult(context, mKirche, p : PromptRunnable() {
//                    override fun run() {
//                        val mKirche = value
//                        sharedPreference.save(Constant.PREF_KIRCHE, mKirche!!)
//                        Log.v(ContentValues.TAG, "LiedFragment (onOptionsItemSelected): mKirche=$mKirche selected")
//                        MainActivity.appContext.blechViewModel!!.getAllLieder(mKirche, sortType, "")?.observe(activity!!, { lieder -> // Update the cached copy of the words in the adapter.
//                            Log.d(ContentValues.TAG, "LiedFragment (onCreate: onChanged): mAdapter changed ")
//                            mAdapter!!.setListEntries(lieder)
//                            // calculate Index List and show it up
//                            mapIndex = SideIndex.getLiedIndexList(mAdapter, sortType)
//                            SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
//                        })
//                    }
//                })
                true
            }
            R.id.menu_action_sort_ABC -> {
                //                Toast.makeText(getActivity(), "sort ABC", Toast.LENGTH_SHORT).show();
                sharedPreference.save(Constant.PREF_SORTTYPE, "ABC")
                sortType = "ABC"
                liedViewModel!!.getAllLieder(mKirche, sortType, "")?.observe(this, { lieder -> // Update the cached copy of the words in the adapter.
                    Log.d(ContentValues.TAG, "LiedFragment (onCreate: onChanged): sortType changed to $sortType")
                    mAdapter!!.setListEntries(lieder)
                    // calculate Index List and show it up
                    mapIndex = SideIndex.getLiedIndexList(mAdapter, sortType)
                    SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                })
                true
            }
            R.id.menu_action_sort_Nr -> {
                //                Toast.makeText(getActivity(), "sort Nr", Toast.LENGTH_SHORT).show();
                sharedPreference.save(Constant.PREF_SORTTYPE, "Nr")
                sortType = "Nr"
                liedViewModel!!.getAllLieder(mKirche, sortType, "")?.observe(this, { lieder -> // Update the cached copy of the words in the adapter.
                    Log.d(ContentValues.TAG, "LiedFragment (onCreate: onChanged): sortType changed to $sortType")
                    mAdapter!!.setListEntries(lieder)
                    // calculate Index List and show it up
                    mapIndex = SideIndex.getLiedIndexList(mAdapter, sortType)
                    SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                })
                true
            }
            R.id.menu_action_sort_Anlass -> {
                //                Toast.makeText(getActivity(), "sort Thema", Toast.LENGTH_SHORT).show();
                sharedPreference.save(Constant.PREF_SORTTYPE, "Anlass")
                sortType = "Anlass"
                liedViewModel!!.getAllLieder(mKirche, sortType, "")?.observe(this, { lieder -> // Update the cached copy of the words in the adapter.
                    Log.d(ContentValues.TAG, "LiedFragment (onCreate: onChanged): sortType changed to $sortType")
                    mAdapter.setListEntries(lieder)
                    // calculate Index List and show it up
                    mapIndex = SideIndex.getLiedIndexList(mAdapter, sortType)
                    SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
                })
                true
            }
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
        Log.d(ContentValues.TAG, "LiedFragment (onSaveInstanceState)")
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(ContentValues.TAG, "LiedFragment (onConfigurationChanged)")
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v(ContentValues.TAG, "LiedFragment: onConfigurationChanged: landscape")
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v(ContentValues.TAG, "LiedFragment: onConfigurationChanged: portrait")
        }
    }
    // https://mobikul.com/how-to-make-master-details-layout-for-small-mobile-devices/
    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
     */
    fun setActivateOnItemClick(activateOnItemClick: Boolean) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
////        getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }

    private fun setActivatedPosition(position: Int) {
        Log.d(ContentValues.TAG, "LiedFragment (setActivatedPosition): position=$position")
        //        if (position == ListView.INVALID_POSITION) {
//            getListView().setItemChecked(mActivatedPosition, false);
//        } else {
//            getListView().setItemChecked(position, true);
//        }
        mActivatedPosition = position
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(ContentValues.TAG, "LiedFragment (onAttach)")
    }

    override fun onStart() {
        super.onStart()
        Log.d(ContentValues.TAG, "LiedFragment (onStart)")
    }

    override fun onResume() {
        super.onResume()
        Log.d(ContentValues.TAG, "LiedFragment (onResume)")
    }

    override fun onPause() {
        super.onPause()
        Log.d(ContentValues.TAG, "LiedFragment (onPause)")
    }

    override fun onStop() {
        super.onStop()
        Log.d(ContentValues.TAG, "LiedFragment (onStop)")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(ContentValues.TAG, "LiedFragment (onDestroyView)")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(ContentValues.TAG, "LiedFragment (onDestroy)")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(ContentValues.TAG, "LiedFragment (onDetach)")
    }
}
package org.redderei.Blechwiki


import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope
import org.redderei.Blechwiki.MainActivity.Companion.appContext
import org.redderei.Blechwiki.gettersetter.Constant
import org.redderei.Blechwiki.gettersetter.LiedClass
import org.redderei.Blechwiki.adapter.LiedAdapter
import org.redderei.Blechwiki.repository.LiedViewModel
import org.redderei.Blechwiki.util.*

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
    //, NoticeDialogFragment.NoticeDialogListener {
    private var mList: List<LiedClass> = ArrayList()
    private lateinit var mAdapter: LiedAdapter
    private var mapIndex: Map<String, Int>? = null
    private var mKirche = ""
    private var sortType = ""
    private var recyclerView: RecyclerView? = null
    private lateinit var rootView: View
    private val mDualPane = false
    private var liedViewModel: LiedViewModel? = null
    private val sharedPreference: SharedPreference = SharedPreference(appContext)
    // Job and Dispatcher are combined into a CoroutineContext
    // https://developer.android.com/kotlin/coroutines/coroutines-adv?hl=de
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    // The current activated item position. Only used on tablets
    private var mActivatedPosition = ListView.INVALID_POSITION
    val mOnClickListener = View.OnClickListener { view: View -> onClick(view) }

    companion object {
        /**
         * Check below parameters for Tablet implementation
         * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
         */
        private const val STATE_ACTIVATED_POSITION = "activated_position"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("LiedFragment", "onCreate ")
        mAdapter = LiedAdapter(mList)
        // Get a new or existing ViewModel from the ViewModelProvider.
        // Lieder einer Landeskirche (Teil)
        mKirche = sharedPreference.getValueString(Constant.PREF_KIRCHE).toString()
        sortType = sharedPreference.getValueString(Constant.PREF_SORTTYPE).toString()

        liedViewModel = ViewModelProvider(this).get(LiedViewModel::class.java)
        // Update the cached copy of the words in the adapter.
        changeLiederSelection(mKirche, sortType, "")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d("LiedFragment", "onCreateView: savedInstanceState=$savedInstanceState")

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
        recyclerView!!.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        recyclerView!!.adapter = mAdapter
        recyclerView!!.addOnItemTouchListener(
            RecyclerTouchListener(
                context,
                recyclerView,
                object : RecyclerTouchListener.ClickListener {
                    override fun onClick(view: View?, position: Int) {
                        onMyItemClick(position)
                    }

                    override fun onLongClick(view: View?, position: Int) {}
                })
        )
        return rootView
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("LiedFragment", "onActivityCreated: savedInstanceState=$savedInstanceState")

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

        Log.v("LiedFragment", "onClick")
        val selectedIndex = view as TextView

        // scroll down to line "index"
        val index = mapIndex!![selectedIndex.text]!!
        recyclerView!!.layoutManager!!.scrollToPosition(index)
    }

    ////    @Override
    fun onMyItemClick(position: Int) {
        Log.d("LiedFragment", "onMyItemClick: position=$position")
        val ixUr = mAdapter!!.mList[position].ixUr
        val idString = mAdapter!!.mList[position].lied

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
            // use childFragmentManager instead
            requireFragmentManager().beginTransaction()
                .replace(R.id.activity_detail_container, fragment).commit()
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
        run {
            Log.d("LiedFragment", "onCreateOptionsMenu")
            val mOnClickListener = View.OnClickListener { view: View -> this.onClick(view) }
            // Inflate the menu.
            // Adds items to the action bar if it is present.
            menu.clear() //without clear menu items are duplicated
            inflater.inflate(R.menu.menu_lied_fragment, menu)
            super.onCreateOptionsMenu(menu, inflater)

            // filtering from actionbar: https://www.androidhive.info/2017/11/android-recyclerview-with-search-filter-functionality/?utm_source=recyclerview&utm_medium=site&utm_campaign=refer_article
            // Associate searchable configuration with the SearchView
            val searchManager =
                requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
            val searchView = menu.findItem(R.id.menu_action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
            searchView.maxWidth = Int.MAX_VALUE
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d("LiedFragment", "onQueryTextSubmit: " + query + "and Kirche " + mKirche)
                    mAdapter!!.filter.filter(query)
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    Log.v("LiedFragment", "onQueryTextChange: filter>$query<")
                    if (mAdapter != null) {
                        mAdapter!!.filter.filter(query) {
                            mapIndex = SideIndex.getLiedIndexList(mAdapter, sortType)
                            SideIndex.displayIndex(
                                mapIndex,
                                rootView,
                                layoutInflater,
                                mOnClickListener
                            )
                        }
                    }
                    return false
                }
            })
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("LiedFragment", "onOptionsItemSelected")
        return when (item.itemId) {
            R.id.menu_action_search -> true
            R.id.menu_action_kirche -> {
                val builder = AlertDialog.Builder(MainActivity.appContext)

                with(builder)
                {
                    setTitle("Bitte Gesangbuch auswählen")
                    setItems(Constant.mBuchLang) { dialog, which ->
                        sharedPreference.save(Constant.PREF_KIRCHE, Constant.mBuchKurz[which])
                        changeLiederSelection(Constant.mBuchKurz[which], sortType, "")
                    }
                    create()
                    show()
                }
                true
            }

            R.id.menu_action_sort_ABC -> {
                sharedPreference.save(Constant.PREF_SORTTYPE, "ABC")
                sortType = "ABC"
                changeLiederSelection(mKirche, sortType, "")
                true
            }

            R.id.menu_action_sort_Nr -> {
                sharedPreference.save(Constant.PREF_SORTTYPE, "Nr")
                sortType = "Nr"
                changeLiederSelection(mKirche, sortType, "")
                true
            }

            R.id.menu_action_sort_Anlass -> {
                sharedPreference.save(Constant.PREF_SORTTYPE, "Anlass")
                sortType = "Anlass"
                changeLiederSelection(mKirche, sortType, "")
                true
            }

            R.id.menu_action_ueber -> {
                val intent = Intent(context, About::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Lieder lokal aus Datenbank abfragen und anzeigen
     * für bestimmte Landeskirche, Sortierung und Teilselektion
     */
    fun changeLiederSelection(mKirche: String, sortType: String, query: String) {
        GlobalScope.launch(Dispatchers.Main){
            liedViewModel!!.getAllLieder(mKirche, sortType, query)?.observe(
                requireActivity()
            ) { lieder -> // Update the cached copy of the words in the adapter.
                Log.d("LiedFragment", "ChangeLiederSelection: mAdapter changed ")
                mAdapter.setListEntries(lieder)
                // calculate Index List and show it up
                mapIndex = SideIndex.getLiedIndexList(mAdapter, sortType)
                SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("LiedFragment", "onSaveInstanceState")
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(  "LiedFragment","onConfigurationChanged")
        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v("LiedFragment", "onConfigurationChanged: landscape")
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v("LiedFragment", "onConfigurationChanged: portrait")
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
        Log.d("LiedFragment","setActivatedPosition: position=$position")
        //        if (position == ListView.INVALID_POSITION) {
//            getListView().setItemChecked(mActivatedPosition, false);
//        } else {
//            getListView().setItemChecked(position, true);
//        }
        mActivatedPosition = position
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("LiedFragment", "onAttach")
    }

    override fun onStart() {
        super.onStart()
        Log.d("LiedFragment", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("LiedFragment", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("LiedFragment", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("LiedFragment", "onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("LiedFragment", "onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LiedFragment", "onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("LiedFragment", "onDetach")
    }
}

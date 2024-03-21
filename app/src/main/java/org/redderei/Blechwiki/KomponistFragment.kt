package org.redderei.Blechwiki

import android.view.*
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.redderei.Blechwiki.gettersetter.KomponistClass
import org.redderei.Blechwiki.repository.BlechViewModel
import org.redderei.Blechwiki.adapter.KomponistAdapter

/**
 * A simple [Fragment] subclass.
 */
class KomponistFragment : Fragment() {
//class KomponistFragment : Fragment(), View.OnClickListener {
    var mKomponistList: List<KomponistClass> = ArrayList()
    private lateinit var mAdapter: KomponistAdapter
    private val mActivity: MainActivity? = null
    private var mapIndex: Map<String, Int>? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var rootView: View
    private val mDualPane = false
    private var mActivatedPosition = ListView.INVALID_POSITION
    private val index: String? = null
    private var blechViewModel: BlechViewModel? = null
/*
    companion object {
        private const val STATE_ACTIVATED_POSITION = "activated_position"
    }

    fun onUpdate(mKomponistList: List<*>?) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ContentValues.TAG, "KomponistFragment (onCreate): savedInstanceState=$savedInstanceState")
        val mOnClickListener = View.OnClickListener { view: View -> onClick(view) }
        mAdapter = KomponistAdapter(mKomponistList)
        blechViewModel = ViewModelProvider(this).get(BlechViewModel::class.java)
        blechViewModel!!.getAllKomponist("")?.observe(this, { komponist -> // Update the cached copy of the words in the adapter.
            Log.d(ContentValues.TAG, "KomponistFragment (onCreate: onChanged): mAdapter changed ")
            mAdapter!!.setListEntries(komponist)
            // calculate Index List and show it up
            mapIndex = SideIndex.getKomponistIndexList(mAdapter!!)
            SideIndex.displayIndex(mapIndex, rootView, layoutInflater, mOnClickListener)
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        Log.d(ContentValues.TAG, "KomponistFragment (onCreateView): savedInstanceState=$savedInstanceState")

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(ContentValues.TAG, "KomponistFragment (onActivityCreated)")

//        // 4. Access the ListView
//        mKomponistListView = getListView();
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
        Log.v(ContentValues.TAG, "KomponistFragment (onClick)")
        val selectedIndex = view as TextView
        val index = mapIndex!![selectedIndex.text]!!
        recyclerView!!.layoutManager!!.scrollToPosition(index)
    }

    // end -------- Alphabetischer Index neben der Liste --------
    fun onMyItemClick(position: Int) {
        Log.d(ContentValues.TAG, "KomponistFragment (onListItemClick): position=$position")
        val shortName = mAdapter!!.mKomponistList[position].kurz
        val longName = mAdapter!!.mKomponistList[position].Komponist
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //if (menu.size() == 0)
        run {
            Log.d(ContentValues.TAG, "KomponistFragment (onCreateOptionsMenu)")
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
                    Log.d(ContentValues.TAG, "KomponistFragment (onQueryTextSubmit): $query")
                    mAdapter!!.filter.filter(query)
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    Log.d(ContentValues.TAG, "KomponistFragment (onQueryTextChange): $query")
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(ContentValues.TAG, "KomponistFragment (onOptionsItemSelected)")
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
        Log.d(ContentValues.TAG, "KomponistFragment (onSaveInstanceState)")
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
            Log.v(ContentValues.TAG, "KomponistFragment (onConfigurationChanged): landscape")
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            Toast.makeText(mActivity, "portrait", Toast.LENGTH_SHORT).show();
            Log.v(ContentValues.TAG, "KomponistFragment (onConfigurationChanged): portrait")
        }
    }

    private fun setActivatedPosition(position: Int) {
        Log.d(ContentValues.TAG, "KomponistFragment (setActivatedPosition): position=$position")
        //        if (position == ListView.INVALID_POSITION) {
//            getListView().setItemChecked(mActivatedPosition, false);
//        } else {
//            getListView().setItemChecked(position, true);
//        }
        mActivatedPosition = position
    }

    override fun onStart() {
        super.onStart()
        Log.d(ContentValues.TAG, "KomponistFragment (onStart)")
    }

    override fun onResume() {
        super.onResume()
        Log.d(ContentValues.TAG, "KomponistFragment (onResume)")
    }

    override fun onPause() {
        super.onPause()
        Log.d(ContentValues.TAG, "KomponistFragment (onPause)")
//        soapTask?.cancel(true)
    }

    override fun onStop() {
        super.onStop()
        Log.d(ContentValues.TAG, "KomponistFragment (onStop)")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(ContentValues.TAG, "KomponistFragment (onDestroyView)")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(ContentValues.TAG, "KomponistFragment (onDestroy)")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(ContentValues.TAG, "KomponistFragment (onDetach)")
    }
 */
}

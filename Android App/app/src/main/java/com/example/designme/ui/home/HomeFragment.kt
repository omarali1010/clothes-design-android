package com.example.designme.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.designme.Adapters.homeAdapter
import com.example.designme.R
import com.example.designme.databinding.FragmentHomeBinding
import com.example.designme.util.NetworkResult
import com.example.designme.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * the main Fragment
 */
@AndroidEntryPoint
class HomeFragment : Fragment(), SearchView.OnQueryTextListener {


    private var _binding: FragmentHomeBinding? = null
    private val mainViewModel: MainViewModel by viewModels()
    private val mAdapter by lazy { homeAdapter() }

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        setHasOptionsMenu(true)

        setupRecyclerView()
        requestApiData()

        return binding.root
    }


    private fun setupRecyclerView() {
        binding.homeRecycler.adapter = mAdapter
        binding.homeRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.designsmenu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    /**
     * Two function for searching with a specified name
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(p0: String?): Boolean {

        return true
    }


    /**
     * filtering menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.female -> {
                filterApidata("Women")
                true
            }

            R.id.male -> {
                filterApidata("Men")
                true
            }

            R.id.children -> {
                filterApidata("Kids")
                true
            }
            R.id.all -> {
                requestApiData()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }

    }

    private fun requestApiData() {
        mainViewModel.getDesigns()
        mainViewModel.DesignsResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    response.data?.let { mAdapter.setData(it) }
                    mAdapter.notifyDataSetChanged()

                }
                is NetworkResult.Error -> {

                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                    readDatabase()
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    /**
     * seaching the API with a specify name which is given by the User
     */
    private fun searchApiData(searchQuery: String) {
        mainViewModel.searchDesigns(searchQuery)
        mainViewModel.searchedDesignsResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    val design = response.data
                    design?.let { mAdapter.setData(it) }
                    mAdapter.notifyDataSetChanged()

                }
                is NetworkResult.Error -> {
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    /**
     * filtering for a specific Category
     */
    private fun filterApidata(searchQuery: String) {
        mainViewModel.filterDesigns(searchQuery)
        mainViewModel.filterResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    val foodRecipe = response.data
                    foodRecipe?.let { mAdapter.setData(it) }
                    mAdapter.notifyDataSetChanged()

                }
                is NetworkResult.Error -> {
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * loading data from the room database
     */
    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readDesigns.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    mAdapter.setData(database.first().designs)
                }
            }
        }
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            mainViewModel.readDesigns.observeOnce(viewLifecycleOwner) { database ->
                if (database.isNotEmpty() /*&&  TODO : !args.backFromBottomSheet */) {
                    Log.d("RecipesFragment", "readDatabase called!")
                    mAdapter.setData(database.first().designs)
                }
            }
        }
    }






        fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
            observe(lifecycleOwner, object : Observer<T> {
                override fun onChanged(t: T?) {
                    removeObserver(this)
                    observer.onChanged(t)
                }
            })
        }


}


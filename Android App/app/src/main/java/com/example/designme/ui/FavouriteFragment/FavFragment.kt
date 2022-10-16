package com.example.designme.ui.FavouriteFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.designme.Adapters.favouriteAdapter
import com.example.designme.databinding.FragmentFavouriteBinding
import com.example.designme.models.designs
import com.example.designme.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 *
 * the class for the Favourite fragment
 */
@AndroidEntryPoint
class FavFragment : Fragment() {

    private var _binding: FragmentFavouriteBinding? = null
    private val mainViewModel: MainViewModel by viewModels()
    private val binding get() = _binding!!
    private val mAdapter by lazy { favouriteAdapter(mainViewModel) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setHasOptionsMenu(true)
        setupRecyclerView()
        loadDataFromCache()

        return root
    }

    private fun setupRecyclerView() {
        binding.homeRecycler.adapter = mAdapter
        binding.homeRecycler.layoutManager = LinearLayoutManager(requireContext())
    }

    /**
     * reading the favourite table from the database and notifying the Adapter for changes
     */
    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readFavoriteDesigns.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    mAdapter.favdesignings = designs()
                    database.forEach { item ->
                        mAdapter.setData(item.design)
                    }

                    mAdapter.notifyDataSetChanged()
                }else{
                    mAdapter.favdesignings = designs()
                    mAdapter.notifyDataSetChanged()

                }
            }

        }

    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.designme.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.designme.R
import com.example.designme.data.database.entites.favEntity
import com.example.designme.databinding.FragmentDetailsBinding
import com.example.designme.viewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

/**
 * a class for the Design details
 */
@AndroidEntryPoint
class Details_Fragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val mainViewModel: MainViewModel by viewModels()

    private val args : Details_FragmentArgs by navArgs()


    private var DesignSaved = false
    private var savedDesignId =""

    private lateinit var menuItem: MenuItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)


        // val args = arguments
        //   val myBundle: shop_moduleItem = args!!.getParcelable<shop_moduleItem>("shop_module") as shop_moduleItem

        binding.titleTextView.text =args.itemdesign.title
        /**
         * coal is used here to load the photo from the server
         */
        if (args.itemdesign.image.startsWith("/"))
            binding.image.load("http:/10.0.2.2:8080${args.itemdesign.image}") {
                crossfade(600)
            }

        binding.itemColor.text =args.itemdesign.color
        binding.itemCategory.text =args.itemdesign.category
        binding.itemsize.text =args.itemdesign.size
        binding.summaryTextView.text =args.itemdesign.about

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.details_menu, menu)
        menuItem = menu.findItem(R.id.save_to_favorites_menu)
         checkSavedDesigns(menuItem)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val action = Details_FragmentDirections.actionDetailsFragmentToNavigationHome()
            binding.root.findNavController().navigate(action)
        } else if (item.itemId == R.id.save_to_favorites_menu && !DesignSaved) {
           saveToFavorites(item,binding.titleTextView.text.toString())
        } else if (item.itemId == R.id.save_to_favorites_menu && DesignSaved) {
           removeFromFavorites(item)
        }
        return super.onOptionsItemSelected(item)
    }


    /**
     * checks if the design is already saved in the local database and specifiying the color to yellow if so
     */
    private fun checkSavedDesigns(menuItem: MenuItem) {
        mainViewModel.readFavoriteDesigns.observe(this) { favoritesEntity ->
            try {
                for (savedDesign in favoritesEntity) {
                    if (savedDesign.design.title == args.itemdesign.title) {
                        changeMenuItemColor(menuItem, R.color.yellow)
                        savedDesignId = savedDesign.title
                        DesignSaved = true
                    }
                }
            } catch (e: Exception) {
                Log.d("DetailsActivity", e.message.toString())
            }
        }
    }


    /**
     * saving a Design in the favourite table in the Local database
     */
    private fun saveToFavorites(item: MenuItem,title : String) {
        val favoritesEntity =
            favEntity(
                title,
                args.itemdesign
            )
        mainViewModel.insertFavoriteDesign(favoritesEntity)
        changeMenuItemColor(item, R.color.yellow)
        showSnackBar("Design saved to Favourites.")
        DesignSaved = true
    }

    /**
     * removing a design for the favourite table and returning the button color to white
     */
    private fun removeFromFavorites(item: MenuItem) {
        val favoritesEntity =
            favEntity(
                savedDesignId,
                args.itemdesign
            )
        mainViewModel.deleteFavoriteDesign(favoritesEntity)
        changeMenuItemColor(item, R.color.white)
        showSnackBar("Desgin removed from Favourites.")
        DesignSaved = false
    }

    /**
     * for showing the user if the design is saved or unsaved
     */
    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.fragmentDet,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}
            .show()
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon.setTint(ContextCompat.getColor(requireContext(), color))
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
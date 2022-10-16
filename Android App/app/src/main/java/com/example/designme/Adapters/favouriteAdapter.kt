package com.example.designme.Adapters

import android.util.Log
import android.view.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.designme.data.database.entites.favEntity
import com.example.designme.databinding.RowdesignBinding
import com.example.designme.models.designs
import com.example.designme.models.designsItem
import com.example.designme.ui.FavouriteFragment.FavFragmentDirections
import com.example.designme.viewModels.MainViewModel

/**
 * Adapter for the favourite RecyclerView
 */
class favouriteAdapter(private val mainViewModel: MainViewModel
): RecyclerView.Adapter<favouriteAdapter.MyViewHolder>() {

      var  favdesignings: designs = designs()

    inner class MyViewHolder( val binding: RowdesignBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = RowdesignBinding.inflate(LayoutInflater.from(parent.context) , parent,false)
        return MyViewHolder(view)    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.binding.titleTextView.text = favdesignings[position].title
        holder.binding.categoryTextView.text = favdesignings[position].category
        holder.binding.sizeTextView.text = favdesignings[position].size
        holder.binding.colorTextView.text = favdesignings[position].color
        holder.binding.descriptionTextView.text = favdesignings[position].about
        println("http:/10.0.2.2:8080${favdesignings[position].image}")
        if (favdesignings[position].image.startsWith("/"))
            holder.binding.designImageView.load("http:/10.0.2.2:8080${favdesignings[position].image}") {
                crossfade(600)
            }


        holder.binding.root.setOnClickListener {
            try {


                val action = FavFragmentDirections.actionNavigationDashboardToDetailsFragment(favdesignings[position])
                holder.binding.root.findNavController().navigate(action)

            } catch (e: Exception) {
                Log.d("setOnClickListener", e.toString())
            }
        }


        holder.binding.DesignsRowLayout.setOnLongClickListener {
      mainViewModel.deleteFavoriteDesign(favEntity(favdesignings[position].title,favdesignings[position]))
            true
        }
    }
    override fun getItemCount(): Int = favdesignings.size


    fun setData(newData : designsItem){

        favdesignings.add(newData)


    }





}

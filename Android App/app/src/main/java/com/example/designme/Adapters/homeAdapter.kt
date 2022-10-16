package com.example.designme.Adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.designme.databinding.RowdesignBinding
import com.example.designme.models.designs
import com.example.designme.ui.home.HomeFragmentDirections
import com.example.designme.viewModels.MainViewModel
import java.lang.Exception



/**
 * Adapter for the Main RecyclerView
 */
class homeAdapter : RecyclerView.Adapter<homeAdapter.MyViewHolder>() {

    private  var  designings: designs = designs()
    inner class MyViewHolder( val binding: RowdesignBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = RowdesignBinding.inflate(LayoutInflater.from(parent.context) , parent,false)
        return MyViewHolder(view)    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        Log.d("items", " on bind${designings[position]}\n}")
        holder.binding.titleTextView.text = designings[position].title
        holder.binding.categoryTextView.text = designings[position].category
        holder.binding.sizeTextView.text = designings[position].size
        holder.binding.colorTextView.text = designings[position].color
        holder.binding.descriptionTextView.text = designings[position].about
if (designings[position].image.startsWith("/"))
        holder.binding.designImageView.load("http:/10.0.2.2:8080${designings[position].image}") {
            crossfade(600)
        }

        holder.binding.root.setOnClickListener {
            try {

                    val action = HomeFragmentDirections.actionNavigationHomeToDetailsFragment(designings[position])
                holder.binding.root.findNavController().navigate(action)
            } catch (e: Exception) {
                Log.d("holder.binding.root.setOnClickListener ", e.toString())
            }
        }


    }
    override fun getItemCount(): Int = designings.size

    fun setData(newData :designs){

        designings = newData


    }






}



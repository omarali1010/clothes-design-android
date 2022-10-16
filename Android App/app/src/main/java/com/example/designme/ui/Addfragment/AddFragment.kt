package com.example.designme.ui.Addfragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.example.designme.Adapters.homeAdapter
import com.example.designme.databinding.FragmentAddBinding
import com.example.designme.models.designsItem
import com.example.designme.util.NetworkResult
import com.example.designme.viewModels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*

/**
 * Adding Fragment Code
 */
@AndroidEntryPoint
class AddFragment : Fragment()  {
    private var _binding: FragmentAddBinding? = null
    private val mainViewModel: MainViewModel by viewModels()
    private val mAdapter by lazy { homeAdapter() }
    private val binding get() = _binding!!
    private var uri : Uri? = null




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.addphotobutton.setOnClickListener {
            pickImageFromGallery()
        }
        binding.submit.setOnClickListener {
            getdataFromUser()
            binding.color.text.clear()
            binding.title.text.clear()
            binding.size.text.clear()
            binding.about.text.clear()
            binding.radiogroupcat.check(binding.catkids.id)
            binding.imageView.setImageResource(0)
        }

        
        return root
    }

    /**
     * a function to get a Photo from the Phone gallery
     */
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }


    /**
     * here we will take the uri of the photo we got from the gallery transferring it into inputstream and then sending it to the server
     *
     */
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
          uri = data?.data
           binding.imageView.setImageURI(data?.data)
//            uri?.let { mainViewModel.uploadPhoto(it) }
         val inputStream =uri?.let { context?.contentResolver?.openInputStream(it) }
            val imageByteArray = inputStream?.readBytes()
            val client = HttpClient(CIO)
            lifecycleScope.launch {
                try {

                    if (imageByteArray != null) {

                        client.submitFormWithBinaryData(
                            url = "   http://10.0.2.2:8080/images",
                            formData = formData {
                                append("description", "Ktor logoo")
                                append("image", imageByteArray, Headers.build {
                                    append(HttpHeaders.ContentType, "image/png")
                                    append(
                                        HttpHeaders.ContentDisposition,
                                        "filename=${binding.title.text}.png"
                                    )
                                })
                            }
                        )


                    } else {
                        Log.d("error", "no Uri")

                    }
                } catch (e: Exception) {
                    Log.d("error :", "${e.stackTrace}")
                }

            }

        }
    }


    /**
     * Sending the data from the fragment edittext and the photo to the server and checkes if the name already used or not
     */
    private fun getdataFromUser(){
        var cat  = "Kids"
        var flagtitle = false
       val title = binding.title.text.toString()
       val color = binding.color.text.toString()
        when (binding.radiogroupcat.checkedRadioButtonId) {
            binding.catkids.id ->  cat ="Kids"
            binding.catmen.id -> cat = "Men"
            binding.catwomen.id -> cat = "Women"

        }
       val size = binding.size.text.toString()
       val about = binding.about.text.toString()

        lifecycleScope.launch {
            mainViewModel.readDesigns.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {

                    database.forEach { item ->
                        item.designs.forEach { totem ->

                            if (totem.title==title){
                                flagtitle =true
                            }
                        }


                    }

                    mAdapter.notifyDataSetChanged()
                }
            }


           while (binding.title.text.toString().isNullOrEmpty()){
                delay(500)
            }
            val myImage = "/images/${binding.title.text}.png"
            if (!flagtitle){
                val designsItem = designsItem(about,cat,color,myImage,0,size,title)
                mainViewModel.addDesign(designsItem)
                requestApiData()
                Toast.makeText(requireContext(),"Your Design is Added successfuly",Toast.LENGTH_LONG).show()

            }else{
                Toast.makeText(requireContext(),"The Title is Already Used",Toast.LENGTH_LONG).show()

            }
        }





   }

    /**
     *
     * just fetching the data from the server
     */

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
}
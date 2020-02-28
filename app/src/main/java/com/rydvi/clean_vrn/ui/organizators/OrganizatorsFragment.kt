package com.rydvi.clean_vrn.ui.organizators

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.ui.organizators.dummy.DummyContent

class OrganizatorsFragment : Fragment() {

    private lateinit var galleryViewModel: OrganizatorsViewModel
    private var twoPane: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(OrganizatorsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
//        val textView: TextView = root.findViewById(R.id.text_gallery)
//        galleryViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        val organzator_list = root.findViewById<RecyclerView>(R.id.organizator_list)
        setupRecyclerView(organzator_list)
        return root
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = OrganizatorItemRecyclerViewAdapter(activity!!, DummyContent.ITEMS, twoPane)
    }
}
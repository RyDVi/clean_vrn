package com.rydvi.clean_vrn.ui.organizators

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.ui.organizators.dummy.DummyContent
import kotlinx.android.synthetic.main.organizator_list_content.view.*

class OrganizatorItemRecyclerViewAdapter(
    private val parentActivity: FragmentActivity,
    private val values: List<DummyContent.DummyItem>,
    private val twoPane: Boolean
) :
    RecyclerView.Adapter<OrganizatorItemRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as DummyContent.DummyItem
            if (twoPane) {
                val fragment = OrganizatorDetailFragment().apply {
                    arguments = Bundle().apply {
                        putString(OrganizatorDetailFragment.ARG_ITEM_ID, item.id)
                    }
                }
                parentActivity.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.organizator_detail_container, fragment)
                    .commit()
            } else {
                val intent = Intent(v.context, OrganizatorDetailActivity::class.java).apply {
                    putExtra(OrganizatorDetailFragment.ARG_ITEM_ID, item.id)
                }
                v.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.organizator_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.content

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.id_text
        val contentView: TextView = view.content
    }
}
package com.rydvi.clean_vrn.ui.map

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import com.rydvi.clean_vrn.R


class MapSearchListAdapter(
    mContext: Context,
    cursor: Cursor?,
    sv: SearchView
) :
    CursorAdapter(mContext, cursor, false) {
    private val mLayoutInflater: LayoutInflater
    private val searchView: SearchView

    override fun newView(
        context: Context?,
        cursor: Cursor?,
        parent: ViewGroup?
    ): View {
        return mLayoutInflater.inflate(R.layout.list_item_search_map, parent, false)
    }

    override fun bindView(
        view: View,
        context: Context?,
        cursor: Cursor
    ) {
        val locationText = view.findViewById<TextView>(R.id.location_name)
        locationText.text = "LOCATION"

        view.setOnClickListener { view ->
            searchView.isIconified = true
        }
    }

    init {
        searchView = sv
        mLayoutInflater = LayoutInflater.from(mContext)
    }
}
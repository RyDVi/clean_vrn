package com.rydvi.clean_vrn.ui.teams

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.CollectedGarbage
import com.rydvi.clean_vrn.ui.interfaces.OnEditText
import com.rydvi.clean_vrn.ui.login.afterTextChanged
import kotlinx.android.synthetic.main.collected_garbarage_edit_listitem.view.*

class CollectedGarbarageItemRecyclerViewAdapter(
    private val parentActivity: FragmentActivity,
    private val values: MutableLiveData<Array<CollectedGarbage>>
) :
    RecyclerView.Adapter<CollectedGarbarageItemRecyclerViewAdapter.ViewHolder>(), OnEditText {

    private var items: ArrayList<EditText> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.collected_garbarage_edit_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values.value!![position]
        holder.classView.text = item.garbage_name.toString()
        holder.countView.setText(if (item.count != null) item.count.toString() else "0")
        items.add(holder.countView)
        holder.countView.afterTextChanged { text ->
            if (!onCheckTextsHasError()) {
                values.value!![position].coefficient = text.toIntOrNull()
            }
        }
        with(holder.itemView) {
            tag = item
        }
    }

    override fun getItemCount() = values.value!!.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val countView: TextInputEditText = view.inp_text_count
        val classView: TextView = view.text_garbage_name
//        val nameView: TextView = view.team_name
//        val numberView: TextView = view.team_number
//        val sumPointsView: TextView = view.team_sum_points
    }

    override fun onCheckTextsHasError(): Boolean {
        var hasError = false
        for (item in items) {
            val text = item.text.toString()
            val count = text.toIntOrNull()
            if (count === null || count < 0) {
                item.error =
                    parentActivity.resources.getString(R.string.err_inp_game_coefficient)
                hasError = true
            }
        }
        return hasError
    }
}
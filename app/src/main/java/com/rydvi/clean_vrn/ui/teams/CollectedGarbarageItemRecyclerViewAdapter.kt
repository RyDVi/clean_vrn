package com.rydvi.clean_vrn.ui.teams

import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rydvi.clean_vrn.R
import com.rydvi.clean_vrn.api.CollectedGarbage
import com.rydvi.clean_vrn.ui.login.afterTextChanged
import kotlinx.android.synthetic.main.collected_garbarage_edit_listitem.view.*

class CollectedGarbarageItemRecyclerViewAdapter(
    private val parentActivity: FragmentActivity,
    private val values: MutableLiveData<Array<CollectedGarbage>>
) :
    RecyclerView.Adapter<CollectedGarbarageItemRecyclerViewAdapter.ViewHolder>() {


    init {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.collected_garbarage_edit_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = values.value!![position]
        holder.classView.text = item.garbage_name.toString()
        holder.countView.setText(if (item.count != null) item.count.toString() else "0")
        holder.countView.afterTextChanged {
            val count = it.toIntOrNull()
            if (count!==null&&count<0){
                //TODO: Вывод ошибки
            } else {
                values.value!![position].count = it.toIntOrNull()
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
}
package com.example.periodictable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterInfo: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return InfoHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_info, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as InfoHolder).bind(InfoList.propertyName[position], InfoList.elements[currAtomicNo-1].property[position])
    }

    override fun getItemCount(): Int {
        return InfoList.propertyName.size
    }

    class InfoHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val infoText: TextView = itemView.findViewById(R.id.info_row)
        fun bind(property_name: String, property_val: String){
            infoText.text = "$property_name: $property_val"
        }
    }
}
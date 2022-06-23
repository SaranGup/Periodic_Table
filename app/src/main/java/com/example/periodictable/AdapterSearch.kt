package com.example.periodictable

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import java.lang.Integer.max
import java.util.concurrent.TimeUnit
import kotlin.math.min
import kotlin.properties.Delegates

class AdapterSearch: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    data class Element(
        val name: String,
        val atomic_no: Int
    )

    private val elements: MutableList<Element> = mutableListOf()
    private var selectedElements: MutableList<Element>

    interface OnItemClickListener {
        fun onItemClick(atomic_no: Int)
    }

    private lateinit var mListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    init {
        for(i in 0 until InfoList.elements.size) {
            elements.add(Element(InfoList.elements[i].property("Name"), i+1))
        }

        selectedElements = mutableListOf()
        selectedElements.addAll(elements)
    }

    private fun stringDiff(a: String, b: String): Double {
        val minSize = min(a.length, b.length)
        val maxSize = max(a.length, b.length)
        var diff = 0.0

        for(i in 0 until minSize) {
            if(a[i]!=b[i]) diff++
        }

        if(diff!=0.0) diff += (maxSize - minSize) * 0.1

        return diff
    }

    fun update(name: String) {
        selectedElements.clear()
        if(name.isEmpty()) {
            selectedElements.addAll(elements)
            return
        }
        name.filter { !it.isWhitespace() }

        for(element in elements){
            if(stringDiff(name, element.name)<=(name.length/2)) selectedElements.add(element)
        }

        selectedElements.sortWith(compareBy {stringDiff(it.name, name)})
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RowHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_search, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RowHolder).bind(selectedElements[position], mListener)
    }

    override fun getItemCount(): Int {
        return selectedElements.size
    }

    class RowHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val text: TextView = itemView.findViewById(R.id.element_name)
        private var color by Delegates.notNull<Int>()
        private var paddingOffset by Delegates.notNull<Int>()

        private fun setPressed() {
            val backgroundView = itemView.findViewById<CardView>(R.id.search_container)
            paddingOffset = backgroundView.paddingLeft

            val drawable = backgroundView.background as LayerDrawable
            drawable.mutate()
            val border: GradientDrawable = drawable.findDrawableByLayerId(R.id.border) as GradientDrawable
            val antiBorder: GradientDrawable = drawable.findDrawableByLayerId(R.id.antiBorder) as GradientDrawable
            border.setColor(ContextCompat.getColor(itemView.context, R.color.box_background))
            antiBorder.setColor(ContextCompat.getColor(itemView.context, R.color.blank))

            backgroundView.setPadding(0)
            backgroundView.background = drawable
        }

        private fun setReleased() {
            val backgroundView = itemView.findViewById<CardView>(R.id.search_container)

            val drawable = backgroundView.background as LayerDrawable
            drawable.mutate()
            val border: GradientDrawable = drawable.findDrawableByLayerId(R.id.border) as GradientDrawable
            val antiBorder: GradientDrawable = drawable.findDrawableByLayerId(R.id.antiBorder) as GradientDrawable
            border.setColor(ContextCompat.getColor(itemView.context, color))
            antiBorder.setColor(ContextCompat.getColor(itemView.context, R.color.box_background))

            backgroundView.setPadding(paddingOffset, 0, 0, paddingOffset)
            backgroundView.background = drawable
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(element: Element, listener: OnItemClickListener) {
            text.text = element.name

            color = when(InfoList.elements[element.atomic_no-1].property("Type")) {
                "Reactive Non-metals" -> R.color.reactive_non_metals
                "Nobel Gases" -> R.color.noble_gases
                "Alkali Metals" -> R.color.alkali_metals
                "Alkaline Earth Metals" -> R.color.alkaline_earth_metals
                "Metalloids" -> R.color.metalloids
                "Transition Metals" -> R.color.transition_metals
                "Post-transition Metals" -> R.color.post_transition_metals
                "Lanthanides" -> R.color.lanthinides
                "Actinides" -> R.color.actinides
                else -> R.color.unknown_properties
            }

            itemView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    setPressed()
                } else if (event.action == MotionEvent.ACTION_UP) {
                    setReleased()
                    TimeUnit.MILLISECONDS.sleep(50)
                    listener.onItemClick(element.atomic_no)
                }
                true
            }

            val drawable: Drawable? = AppCompatResources.getDrawable(itemView.context, R.drawable.gradient_long)
            drawable!!.mutate()
            val layerDrawable: LayerDrawable = drawable as LayerDrawable
            val border: GradientDrawable = layerDrawable.findDrawableByLayerId(R.id.border) as GradientDrawable
            border.setColor(ContextCompat.getColor(itemView.context, color))
            val antiBorder: GradientDrawable = layerDrawable.findDrawableByLayerId(R.id.antiBorder) as GradientDrawable
            antiBorder.setColor(ContextCompat.getColor(itemView.context, R.color.box_background))
            itemView.findViewById<CardView>(R.id.search_container).background = drawable
        }
    }
}
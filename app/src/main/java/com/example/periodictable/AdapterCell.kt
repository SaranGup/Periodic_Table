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
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class AdapterCell(Side: Int): RecyclerView.Adapter<RecyclerView.ViewHolder> () {

    private val boxSide: Int = Side

    interface OnItemClickListener {
        fun onItemClick(atomic_no: Int)
    }

    private lateinit var mListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    private val tablePos = arrayOf(
        intArrayOf(1,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  2),
        intArrayOf(3,  4,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  5,  6,  7,  8,  9,  10),
        intArrayOf(11, 12, 0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  13, 14, 15, 16, 17, 18),
        intArrayOf(19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36),
        intArrayOf(37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54),
        intArrayOf(55, 56, 57, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86),
        intArrayOf(87, 88, 89, 104,105,106,107,108,109,110,111,112,113,114,115,116,117,118),
        intArrayOf(0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0),
        intArrayOf(0,  0,  0,  58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 0),
        intArrayOf(0,  0,  0,  90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100,101,102,103,0),
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            R.layout.cell_element -> {
                CellHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_element, parent, false), mListener)
            }
            R.layout.cell_empty -> {
                EmptyHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_empty, parent, false))
            }
            else -> {
                throw IllegalArgumentException("Invalid ViewType in CellAdapter/onCreateViewHolder")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            R.layout.cell_element -> {
                val atomicNo = (tablePos[position%10][position/10]).toString()
                val elementData = InfoList.elements[atomicNo.toInt()-1]
                val elementSymbol = elementData.property("Symbol")
                val elementName = elementData.property("Name")
                (holder as CellHolder).bind(DataViewCell(atomicNo, elementSymbol, elementName))
            }
        }
        val layoutParams: ViewGroup.LayoutParams = holder.itemView.layoutParams
        layoutParams.height = boxSide
        layoutParams.width = boxSide
        if(position%10==7) layoutParams.width = boxSide/2
        holder.itemView.layoutParams = layoutParams
    }

    override fun getItemCount(): Int {
        return tablePos.size * tablePos[0].size
    }

    override fun getItemViewType(position: Int): Int {
        return when(tablePos[position%10][position/10]) {
            0 -> {
                R.layout.cell_empty
            }
            else -> {
                R.layout.cell_element
            }
        }
    }

    class CellHolder(ItemView: View, listener: OnItemClickListener): RecyclerView.ViewHolder(ItemView) {
        private val atomicNo: TextView = ItemView.findViewById(R.id.atomicNo)
        private val elementSymbol: TextView = ItemView.findViewById(R.id.elementSymbol)
        private val elementName: TextView = ItemView.findViewById(R.id.element_name)
        private val mListener = listener
        var color by Delegates.notNull<Int>()
        private var padding by Delegates.notNull<Int>()

        private fun setPressed() {
            val backgroundView = itemView.findViewById<LinearLayoutCompat>(R.id.cell_linear_layout)
            padding = backgroundView.paddingTop

            val drawable = backgroundView.background as LayerDrawable
            drawable.mutate()
            val border: GradientDrawable = drawable.findDrawableByLayerId(R.id.border) as GradientDrawable
            val antiBorder: GradientDrawable = drawable.findDrawableByLayerId(R.id.antiBorder) as GradientDrawable
            border.setColor(ContextCompat.getColor(itemView.context, R.color.box_background))
            antiBorder.setColor(ContextCompat.getColor(itemView.context, R.color.blank))

            val lowVal = (padding*0.75).toInt()
            val highVal = (padding*1.25).toInt()
            backgroundView.setPadding(lowVal, highVal, highVal, lowVal)
            backgroundView.background = drawable
        }

        private fun setReleased() {
            val backgroundView = itemView.findViewById<LinearLayoutCompat>(R.id.cell_linear_layout)

            val drawable = backgroundView.background as LayerDrawable
            drawable.mutate()
            val border: GradientDrawable = drawable.findDrawableByLayerId(R.id.border) as GradientDrawable
            val antiBorder: GradientDrawable = drawable.findDrawableByLayerId(R.id.antiBorder) as GradientDrawable
            border.setColor(ContextCompat.getColor(itemView.context, color))
            antiBorder.setColor(ContextCompat.getColor(itemView.context, R.color.box_background))

            backgroundView.setPadding(padding)
            backgroundView.background = drawable
        }


        @SuppressLint("ClickableViewAccessibility")
        fun bind(element: DataViewCell){
            atomicNo.text = element.atomicNo
            elementSymbol.text = element.elementSymbol
            elementName.text = element.elementName

            color = when(InfoList.elements[element.atomicNo.toInt()-1].property("Type")) {
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
                    mListener.onItemClick(element.atomicNo.toInt())
                }
                true
            }

            val drawable: Drawable? = AppCompatResources.getDrawable(itemView.context, R.drawable.gradient_cell)
            drawable!!.mutate()
            val layerDrawable: LayerDrawable = drawable as LayerDrawable
            val border: GradientDrawable = layerDrawable.findDrawableByLayerId(R.id.border) as GradientDrawable
            border.setColor(ContextCompat.getColor(itemView.context, color))
            val antiBorder: GradientDrawable = layerDrawable.findDrawableByLayerId(R.id.antiBorder) as GradientDrawable
            antiBorder.setColor(ContextCompat.getColor(itemView.context, R.color.box_background))
            itemView.findViewById<LinearLayoutCompat>(R.id.cell_linear_layout).background = drawable
        }
    }

    class EmptyHolder(ItemView: View): RecyclerView.ViewHolder(ItemView)
}
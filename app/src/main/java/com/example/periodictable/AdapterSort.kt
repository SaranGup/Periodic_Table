package com.example.periodictable

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class AdapterSort(names: MutableList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var propertyIndex: Int = -1
    private val propertyNames = names

    data class ElementProperty (
        val atomicNo: Int,
        val propertyVal: String)

    private val elementProperties = mutableListOf<ElementProperty>()
    private var maxVal = InfoList.elements.size.toDouble()
    private var descending = true

    init {
        for(i in 1..InfoList.elements.size) elementProperties.add(ElementProperty(i,i.toString()))
        elementProperties.reverse()
    }

    interface OnItemClickListener {
        fun onItemClick(atomic_no: Int)
    }

    private lateinit var mListener: OnItemClickListener

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    private fun lastValidIndex(): Int {
        for(i in 0 until elementProperties.size){
            try {
                elementProperties[i].propertyVal.toDouble()
            } catch (e: NumberFormatException) {
                return i-1
            }
        }
        return elementProperties.size-1
    }

    private fun score(prop: String): Double{
        val score: Double = try {
            prop.toDouble()
        } catch (e: NumberFormatException) {
            if(descending) Double.MIN_VALUE
            else Double.MAX_VALUE
        }
        return if(descending) score * -1
        else score
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateElements(pI: Int) {

        propertyIndex = pI
        elementProperties.clear()

        for(i in 0 until 118) {
            elementProperties.add(ElementProperty(i+1, InfoList.elements[i].property(propertyNames[propertyIndex])))
        }

        sortElements()
    }

    fun updateOrder(order: Boolean) {
        descending = order

        sortElements()
    }

    private fun sortElements() {
        elementProperties.sortWith(compareBy {score(it.propertyVal)})

        maxVal = if(descending) elementProperties[0].propertyVal.toDouble()
        else elementProperties[lastValidIndex()].propertyVal.toDouble()

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RowHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_amount, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RowHolder).bind(elementProperties[position], maxVal, mListener)
    }

    override fun getItemCount(): Int {
        return InfoList.elements.size
    }

    class RowHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val value: TextView = itemView.findViewById(R.id.value)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.amount)
        private var paddingOffset by Delegates.notNull<Int>()
        private var amountContainer: ConstraintLayout = itemView.findViewById(R.id.amount_container)
        var color by Delegates.notNull<Int>()

        private fun setPressed() {
            val backgroundView = itemView.findViewById<ConstraintLayout>(R.id.amount_container)
            paddingOffset = backgroundView.paddingLeft

            val drawable = backgroundView.background as LayerDrawable
            drawable.mutate()
            val border: GradientDrawable = drawable.findDrawableByLayerId(R.id.border) as GradientDrawable
            val antiBorder: GradientDrawable = drawable.findDrawableByLayerId(R.id.antiBorder) as GradientDrawable
            border.setColor(ContextCompat.getColor(itemView.context, R.color.box_background))
            antiBorder.setColor(ContextCompat.getColor(itemView.context, R.color.blank))

            backgroundView.setPadding(0, paddingOffset, paddingOffset, 0)
            backgroundView.background = drawable
        }

        private fun setReleased() {
            val backgroundView = itemView.findViewById<ConstraintLayout>(R.id.amount_container)

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
        fun bind(elementProperty: ElementProperty, maxVal: Double, listener: OnItemClickListener) {

            val length: Double = try {
                elementProperty.propertyVal.toDouble()/maxVal
            } catch (e: NumberFormatException) {
                0.0
            }

            val text = "${InfoList.elements[elementProperty.atomicNo-1].property("Name")}: " + (try {
                elementProperty.propertyVal.toDouble().toString()
            } catch (e: NumberFormatException) {
                elementProperty.propertyVal
            })
            value.text = text

            val maxWidth = amountContainer.measuredWidth
            val calcWidth = maxWidth*length

            color = when(InfoList.elements[elementProperty.atomicNo-1].property("Type")) {
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
            if(calcWidth<0.1F) color = R.color.background

            itemView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    setPressed()
                } else if (event.action == MotionEvent.ACTION_UP) {
                    setReleased()
                    TimeUnit.MILLISECONDS.sleep(50)
                    listener.onItemClick(elementProperty.atomicNo)
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
            amountContainer.background = drawable

//            val amountDrawable: LayerDrawable = AppCompatResources.getDrawable(itemView.context, R.drawable.progress) as LayerDrawable
//            amountDrawable.mutate()
//            val amount: Drawable = amountDrawable.findDrawableByLayerId(R.id.amount)
//            val antiAmount: Drawable = amountDrawable.findDrawableByLayerId(R.id.antiAmount)
//            amount.setBounds(0, 0, maxWidth -calcWidth.toInt(), 0)
//            antiAmount.setBounds(calcWidth.toInt(), 0, 0, 0)
//            progressBar.background = amountDrawable

            progressBar.progress = (elementProperty.propertyVal.toDouble()/maxVal*100).toInt()
            val progressHeight = amountContainer.height
            val defaultHeight = itemView.resources.displayMetrics.density

            progressBar.scaleY = (progressHeight/defaultHeight)
        }
    }
}
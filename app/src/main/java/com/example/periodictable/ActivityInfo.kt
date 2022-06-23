package com.example.periodictable

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ActivityInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        val infoTable: RecyclerView = findViewById(R.id.info_list)
        infoTable.layoutManager = LinearLayoutManager(this)
        val iAdapter = AdapterInfo()
        infoTable.adapter = iAdapter

        val imageContainer = findViewById<View>(R.id.image_container)
        val layoutParams = imageContainer.layoutParams

        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutParams.height = resources.displayMetrics.widthPixels

        } else {
            layoutParams.width = resources.displayMetrics.heightPixels
        }
        imageContainer.layoutParams = layoutParams

        val elementImage = findViewById<ImageView>(R.id.element_image)
        val picID =resources.getIdentifier("element__${currAtomicNo}_", "drawable", this.packageName)
        try {
            elementImage.setImageDrawable(ContextCompat.getDrawable(this, picID))
        } catch (e: Resources.NotFoundException) {
            elementImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.nuclear_element))
        }

    }
}
package com.example.periodictable

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

var currAtomicNo = 0
var currFragment: Fragment? = null

class MainActivity : AppCompatActivity() {

    private lateinit var fTable: FragmentTable
    private lateinit var fSearch: FragmentSearch
    private lateinit var fSort: FragmentSort

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setSupportActionBar(findViewById(R.id.toolbar_main))
    }

    override fun onStart() {
        super.onStart()
        InfoList.loadElements("elements.xml",resources)

        fTable = FragmentTable()
        fSearch = FragmentSearch()
        fSort = FragmentSort()

        if(currFragment==null) currFragment = fTable

        showFragment(currFragment!!)
        fTable.setSwitchListener(object: FragmentTable.SwitchListener {
            override fun switchFragment(atomic_no: Int) {
                toInfo(atomic_no)
            }
        })

        fSearch.setSwitchListener(object: FragmentSearch.SwitchListener {
            override fun switchFragment(atomic_no: Int) {
                toInfo(atomic_no)
            }
        })

        fSort.setSwitchListener(object: FragmentSort.SwitchListener {
            override fun switchFragment(atomic_no: Int) {
                toInfo(atomic_no)
            }
        })

        val tableButton: TextView = findViewById(R.id.periodic_table_button)
        val searchButton: TextView = findViewById(R.id.search_section_button)
        val sortButton: TextView = findViewById(R.id.sort_section_button)

        tableButton.setOnClickListener {showFragment(fTable)}
        searchButton.setOnClickListener {showFragment(fSearch)}
        sortButton.setOnClickListener {showFragment(fSort)}
    }

    override fun onResume() {
        super.onResume()
        showFragment(currFragment!!)
    }

    private fun showFragment(fragmentClass: Fragment) {
        val replacement = supportFragmentManager.beginTransaction()
        replacement.replace(R.id.main_fragment, fragmentClass)
        replacement.commit()
        currFragment = fragmentClass
    }

    fun toInfo(atomic_no: Int) {
        currAtomicNo = atomic_no
        val toInfo = Intent(this@MainActivity, ActivityInfo::class.java)
        startActivity(toInfo)
    }
}
package com.example.periodictable

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class FragmentSearch : Fragment(R.layout.activity_info) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    interface SwitchListener {
        fun switchFragment(atomic_no: Int)
    }

    lateinit var sListener: SwitchListener

    fun setSwitchListener(listener: SwitchListener)
    {
        sListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list: RecyclerView? = getView()?.findViewById(R.id.search_list)
        list?.layoutManager = LinearLayoutManager(context)
        val adapter = AdapterSearch()
        list?.adapter = adapter

//        activity?.actionBar?.displayOptions = android.app.ActionBar.DISPLAY_SHOW_CUSTOM
//        activity?.actionBar?.setDisplayShowCustomEnabled(true)
//        activity?.actionBar?.setCustomView(R.layout.action_search)
//        activity?.actionBar?.elevation = 0.0F

        val textField: EditText? = requireView().findViewById(R.id.name_input)

        textField?.addTextChangedListener(object : TextWatcher {

            val delay: Long = 1000
            var timer = Timer()

            override fun afterTextChanged(s: Editable?) {
                timer = Timer()
                timer.schedule(object: TimerTask() {
                    override fun run() {
                        this@FragmentSearch.activity?.runOnUiThread {
                            adapter.update(s.toString())
                            if (list != null) {
                                (list.adapter as AdapterSearch).update(s.toString())
                                (list.adapter as AdapterSearch).notifyDataSetChanged()
                            }
                        }
                    }
                }, delay)

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                timer.cancel()
                timer.purge()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        })

        adapter.setOnItemClickListener(object : AdapterSearch.OnItemClickListener {
            override fun onItemClick(atomic_no: Int) {
                sListener.switchFragment(atomic_no)
            }
        })
    }
}
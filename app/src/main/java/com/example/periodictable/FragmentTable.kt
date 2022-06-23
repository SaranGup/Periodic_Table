package com.example.periodictable


import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class FragmentTable: Fragment(R.layout.fragment_table) {

    interface SwitchListener {
        fun switchFragment(atomic_no: Int)
    }

    lateinit var sListener: SwitchListener

    fun setSwitchListener(listener: SwitchListener)
    {
        sListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val table: RecyclerView? = getView()?.findViewById(R.id.mainTable)

        table?.layoutManager = GridLayoutManager(this.context, 10, LinearLayoutManager.HORIZONTAL, false)


        var height = 0

        requireView().post {
            val orientation = requireView().resources.configuration.orientation
            height =
                if (orientation == Configuration.ORIENTATION_PORTRAIT) requireView().measuredHeight
                else requireView().measuredWidth

            val adapter = AdapterCell(height / 10)
            table?.adapter = adapter
            adapter.setOnItemClickListener(object : AdapterCell.OnItemClickListener {
                override fun onItemClick(atomic_no: Int) {
                    sListener.switchFragment(atomic_no)
                }
            })
        }
    }


}
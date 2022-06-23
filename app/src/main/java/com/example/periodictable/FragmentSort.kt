package com.example.periodictable

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSort.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSort : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var options: Spinner
    private lateinit var list: RecyclerView
    private lateinit var order: Spinner

    interface SwitchListener {
        fun switchFragment(atomic_no: Int)
    }

    lateinit var sListener: SwitchListener

    fun setSwitchListener(listener: SwitchListener)
    {
        sListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sort, container, false)
    }

    private fun initUi() {
        options = requireView().findViewById(R.id.spinner)
        list = requireView().findViewById(R.id.sort_list)
        order = requireView().findViewById(R.id.order)

        val spinnerContent = mutableListOf<String>()

        for(i in listOf(0,3,7,8,9,10,11,12,13,14,15,16,17)) {
            spinnerContent.add(InfoList.propertyName[i])
        }

        val spinnerAdapter = ArrayAdapter(this.requireContext(), androidx.transition.R.layout.support_simple_spinner_dropdown_item, spinnerContent)
        options.adapter = spinnerAdapter

        list.layoutManager = LinearLayoutManager(this.requireContext())
        val listAdapter = AdapterSort(spinnerContent)
        list.adapter = listAdapter

        val orderAdapter = ArrayAdapter(this.requireContext(), androidx.transition.R.layout.support_simple_spinner_dropdown_item, listOf("Ascending", "Descending"))
        order.adapter = orderAdapter

        options.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                listAdapter.updateElements(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        order.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                listAdapter.updateOrder(position != 0)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        listAdapter.setOnItemClickListener(object : AdapterSort.OnItemClickListener{
            override fun onItemClick(atomic_no: Int) {
                sListener.switchFragment(atomic_no)
            }
        })
        listAdapter.updateElements(0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SortFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FragmentSort().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
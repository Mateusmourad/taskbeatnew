package com.devspace.taskbeats

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.android.material.snackbar.Snackbar

class CreateTaskBottomSheet (

    private val onCreatedClicked: (TaskUiData) -> Unit,
    private val categoryList: List<CategoryUiData>
    ) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_task_bottom_sheet, container, false)

        val btnCreate = view.findViewById<Button>(R.id.btn_task_create)
        val tieTaskName = view.findViewById<TextInputEditText>(R.id.tie_task_name)

        var taskCategory: String? = null

        btnCreate.setOnClickListener {
            val name = tieTaskName.text.toString()
            if(taskCategory != null) {
                onCreatedClicked.invoke(
                   TaskUiData(
                       name = name,
                       category = taskCategory
                   )
                )
                dismiss()
            } else {
                Snackbar.make(btnCreate,"Please select a category", Snackbar.LENGTH_LONG).show()
            }

        }

       val categoryStr = categoryList.map {it.name}

        val spinner: Spinner = view.findViewById(R.id.category_list)
        ArrayAdapter(
            requireActivity().baseContext,
            android.R.layout.simple_spinner_item,
            categoryStr.toList()
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter

        }

        spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                TODO("Not yet implemented")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }


        return view

    }

}



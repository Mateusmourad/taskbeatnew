package com.devspace.taskbeats

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.widget.TextView

class InfoBottomSheet (
    private val tittle: String,
    private val description: String,
    private val btnText: String,
    private val onClicked: () -> Unit
    ) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.info_bottom_sheet, container, false)

        val tvTitle = view.findViewById<TextView>(R.id.tv_info_title)
        val btnAction = view.findViewById<Button>(R.id.btn_info)
        val tvDesc= view.findViewById<TextView>(R.id.tv_info_description)

       tvTitle.text = tittle
        tvDesc.text = description
        btnAction.text = btnText

        btnAction.setOnClickListener {
            onClicked.invoke()
            dismiss()
        }

        return view
    }
}



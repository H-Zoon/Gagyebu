package com.intern.gagyebu.dialog

import android.app.AlertDialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.intern.gagyebu.databinding.OptionSelectBinding

class OptionSelectDialog : DialogFragment() {

    private lateinit var optionListener: OptionDialogListener


    fun setListener(listener: OptionDialogListener) {
        this.optionListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val binding = OptionSelectBinding.inflate(requireActivity().layoutInflater)

        var filter: String = Options.DEFAULT.toString()
        var order = Options.day.toString()

        builder.setTitle("필터링")

        binding.filterGroup.setOnCheckedChangeListener { group, checkedId ->
            filter = when (checkedId) {
                binding.filterIncome.id -> Options.SPEND.toString()

                binding.filterSpend.id ->  Options.INCOME.toString()

                else -> Options.DEFAULT.toString()
            }
            Log.d("filter",checkedId.toString())
        }

        binding.orderGroup.setOnCheckedChangeListener { group, checkedId ->
            order = when (checkedId) {
                binding.orderAmount.id -> Options.amount.toString()

                else -> Options.day.toString()

            }
            Log.d("order",checkedId.toString())
        }

        binding.confirm.setOnClickListener{
            optionListener.option(filter, order)
            this.dialog?.cancel()
        }

        binding.cancel.setOnClickListener{
            this.dialog?.cancel()
        }

        builder.setView(binding.root)
        return builder.create()
    }

}
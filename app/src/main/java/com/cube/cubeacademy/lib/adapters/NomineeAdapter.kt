package com.cube.cubeacademy.lib.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.LayoutRes
import com.cube.cubeacademy.databinding.ViewNomineeListItemBinding
import com.cube.cubeacademy.lib.models.Nominee

class NomineeAdapter(context: Context, @LayoutRes private val layoutResource: Int, nomineeList: List<Nominee>) : ArrayAdapter<Nominee>(context, layoutResource, nomineeList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    @SuppressLint("SetTextI18n")
    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View{
        val binding: ViewNomineeListItemBinding =
            if (convertView != null) ViewNomineeListItemBinding.bind(convertView)
            else ViewNomineeListItemBinding.inflate(LayoutInflater.from(context), parent, false)

        val item = getItem(position)
        binding.name.text = "${item!!.firstName} ${item.lastName}"

        return binding.root
    }
}
package com.cube.cubeacademy.lib.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cube.cubeacademy.databinding.ViewNominationListItemBinding
import com.cube.cubeacademy.lib.models.Nomination
import com.cube.cubeacademy.lib.models.Nominee

class NominationsRecyclerViewAdapter : ListAdapter<Nomination, NominationsRecyclerViewAdapter.ViewHolder>(DIFF_CALLBACK) {

    var nomineeList: List<Nominee> = emptyList()

    class ViewHolder(val binding: ViewNominationListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewNominationListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            /**
             * TODO: This should show the nominee name instead of their id! Where can you get their name from?
             */
            val nominee = nomineeList.find { nominee -> nominee.nomineeId == item.nomineeId }
            "${nominee?.firstName} ${nominee?.lastName}".also { name.text = it }
            reason.text = item.reason
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Nomination>() {
            override fun areItemsTheSame(oldItem: Nomination, newItem: Nomination) = oldItem.nominationId == newItem.nominationId
            override fun areContentsTheSame(oldItem: Nomination, newItem: Nomination) = oldItem == newItem
        }
    }
}
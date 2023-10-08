package com.happyplaces.presentation.di


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.happyplaces.database.HappyPlace
import com.happyplaces.databinding.ItemHappyPlaceBinding

class HappyPlaceAdapter : ListAdapter<HappyPlace, HappyPlaceAdapter.ViewHolder>(HappyPlaceDiffCallback()) {

    inner class ViewHolder(private val binding: ItemHappyPlaceBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item:HappyPlace){
            binding.tvTitle.text = item.title
            binding.tvDescription.text = item.description
            binding.ivPlaceImage.setImageURI(item.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHappyPlaceBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class HappyPlaceDiffCallback : DiffUtil.ItemCallback<HappyPlace>(){
    override fun areItemsTheSame(oldItem: HappyPlace, newItem: HappyPlace): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HappyPlace, newItem: HappyPlace): Boolean {
        return oldItem.id == newItem.id
    }
}
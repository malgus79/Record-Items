package com.recorditemsapp.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.recorditemsapp.R
import com.recorditemsapp.databinding.ItemRecordBinding
import com.recorditemsapp.model.entity.RecordEntity

class RecordListAdapter(private var listener: OnClickListener) :
    ListAdapter<RecordEntity, RecyclerView.ViewHolder>(RecordDiffCallback()) {

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val record = getItem(position)
        with(holder as ViewHolder) {
            setListener(record)
            binding.tvName.text = record.name
            binding.cbFavorite.isChecked = record.isFavorite
            Glide.with(mContext)
                .load(record.photoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.imgPhoto)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemRecordBinding.bind(view)

        fun setListener(recordEntity: RecordEntity) {
            with(binding.root) {
                setOnClickListener { listener.onClick(recordEntity) }
                setOnLongClickListener {
                    listener.onDeleteRecord(recordEntity)
                    true
                }
            }

            binding.cbFavorite.setOnClickListener {
                listener.onFavoriteRecord(recordEntity)
            }
        }
    }

    class RecordDiffCallback : DiffUtil.ItemCallback<RecordEntity>() {
        override fun areItemsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RecordEntity, newItem: RecordEntity): Boolean {
            return oldItem == newItem
        }
    }
}
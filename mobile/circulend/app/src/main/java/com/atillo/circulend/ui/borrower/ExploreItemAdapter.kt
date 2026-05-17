package com.atillo.circulend.ui.borrower

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.atillo.circulend.R
import com.bumptech.glide.Glide

data class ExploreItemUi(
    val name: String,
    val category: String,
    val status: String,
    val imageUrl: String?
)

class ExploreItemAdapter : RecyclerView.Adapter<ExploreItemAdapter.VH>() {
    private val items = mutableListOf<ExploreItemUi>()

    fun submitList(newItems: List<ExploreItemUi>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_explore_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val ivItem: ImageView = view.findViewById(R.id.ivItem)
        private val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        private val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        private val tvStatus: TextView = view.findViewById(R.id.tvStatus)

        fun bind(item: ExploreItemUi) {
            tvItemName.text = item.name
            tvCategory.text = item.category
            tvStatus.text = item.status

            val isAvailable = item.status.equals("AVAILABLE", ignoreCase = true)
            tvStatus.setTextColor(
                ContextCompat.getColor(
                    itemView.context,
                    if (isAvailable) android.R.color.holo_green_dark else android.R.color.holo_red_dark
                )
            )

            Glide.with(itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(ivItem)
        }
    }
}
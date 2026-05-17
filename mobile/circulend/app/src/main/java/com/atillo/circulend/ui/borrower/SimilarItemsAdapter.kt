package com.atillo.circulend.ui.borrower

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.atillo.circulend.R
import com.bumptech.glide.Glide

class SimilarItemsAdapter(
    private val onItemClick: (ExploreItemUi) -> Unit
) : RecyclerView.Adapter<SimilarItemsAdapter.VH>() {
    private val items = mutableListOf<ExploreItemUi>()

    fun submitList(newItems: List<ExploreItemUi>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_similar_card, parent, false)
        return VH(v, onItemClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size

    class VH(
        view: View,
        private val onItemClick: (ExploreItemUi) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val ivImage: ImageView = view.findViewById(R.id.ivSimilarImage)
        private val tvName: TextView = view.findViewById(R.id.tvSimilarName)
        private val tvCategory: TextView = view.findViewById(R.id.tvSimilarCategory)
        private val tvStatus: TextView = view.findViewById(R.id.tvSimilarStatus)
        private val btnView: TextView = view.findViewById(R.id.btnSimilarViewDetails)

        fun bind(item: ExploreItemUi) {
            tvName.text = item.name
            tvCategory.text = item.category
            tvStatus.text = item.status

            Glide.with(itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .centerCrop()
                .into(ivImage)

            btnView.setOnClickListener { onItemClick(item) }
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}

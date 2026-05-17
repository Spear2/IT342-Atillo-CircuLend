package com.atillo.circulend.ui.borrower

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atillo.circulend.R
import com.atillo.circulend.data.repository.CatalogResult
import com.atillo.circulend.data.repository.ItemRepository
import com.atillo.circulend.ui.auth.LoginActivity
import com.atillo.circulend.util.SessionManager
import com.bumptech.glide.Glide

class ItemDetailsFragment : Fragment(R.layout.fragment_item_details) {
    private lateinit var item: ExploreItemUi
    private val itemRepository = ItemRepository()
    private val similarAdapter = SimilarItemsAdapter { selected ->
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, newInstance(selected))
            .addToBackStack("item_details")
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item = ExploreItemUi(
            itemId = requireArguments().getLong(ARG_ITEM_ID),
            categoryId = arguments?.let {
                if (it.containsKey(ARG_CATEGORY_ID)) it.getLong(ARG_CATEGORY_ID) else null
            },
            name = requireArguments().getString(ARG_NAME).orEmpty(),
            category = requireArguments().getString(ARG_CATEGORY).orEmpty(),
            status = requireArguments().getString(ARG_STATUS).orEmpty(),
            imageUrl = requireArguments().getString(ARG_IMAGE_URL),
            assetTag = requireArguments().getString(ARG_ASSET_TAG).orEmpty(),
            description = requireArguments().getString(ARG_DESCRIPTION)
        )

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val ivImage = view.findViewById<ImageView>(R.id.ivDetailImage)
        val tvName = view.findViewById<TextView>(R.id.tvDetailName)
        val tvStatus = view.findViewById<TextView>(R.id.tvDetailStatus)
        val tvCategory = view.findViewById<TextView>(R.id.tvDetailCategory)
        val tvDescription = view.findViewById<TextView>(R.id.tvDetailDescription)
        val btnBorrow = view.findViewById<Button>(R.id.btnBorrow)
        val tvSeeAllSimilar = view.findViewById<TextView>(R.id.tvSeeAllSimilar)
        val rvSimilarItems = view.findViewById<RecyclerView>(R.id.rvSimilarItems)
        val tvNoSimilarItems = view.findViewById<TextView>(R.id.tvNoSimilarItems)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        tvName.text = item.name
        tvStatus.text = item.status.uppercase()
        tvCategory.text = item.category
        tvDescription.text = item.description?.ifBlank { "No description available." }
            ?: "No description available."

        Glide.with(this)
            .load(item.imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_foreground)
            .centerCrop()
            .into(ivImage)

        val available = item.status.equals("AVAILABLE", ignoreCase = true)
        btnBorrow.isEnabled = available

        rvSimilarItems.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvSimilarItems.adapter = similarAdapter

        tvSeeAllSimilar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        loadSimilarItems(tvNoSimilarItems)

        btnBorrow.setOnClickListener {
            BorrowItemBottomSheetFragment.newInstance(
                itemId = item.itemId,
                itemName = item.name
            ).show(parentFragmentManager, BorrowItemBottomSheetFragment.TAG)
        }

        parentFragmentManager.setFragmentResultListener(
            BorrowItemBottomSheetFragment.RESULT_KEY,
            viewLifecycleOwner
        ) { _, _ ->
            parentFragmentManager.setFragmentResult(
                RESULT_KEY_BORROWED,
                bundleOf("ok" to true)
            )
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadSimilarItems(tvNoSimilarItems: TextView) {
        itemRepository.getItems(
            categoryId = item.categoryId,
            page = 0,
            size = 12
        ) { result ->
            activity?.runOnUiThread {
                when (result) {
                    is CatalogResult.Success -> {
                        val mapped = result.items
                            .filter { it.itemId != item.itemId }
                            .map {
                                ExploreItemUi(
                                    itemId = it.itemId,
                                    categoryId = it.categoryId,
                                    name = it.name,
                                    category = (it.categoryName ?: "Uncategorized").uppercase(),
                                    status = it.status,
                                    imageUrl = it.imageFileUrl,
                                    assetTag = it.assetTag ?: "",
                                    description = it.description
                                )
                            }
                        similarAdapter.submitList(mapped)
                        tvNoSimilarItems.visibility = if (mapped.isEmpty()) View.VISIBLE else View.GONE
                    }

                    is CatalogResult.Unauthorized -> {
                        SessionManager(requireContext()).clear()
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                        requireActivity().finish()
                    }

                    is CatalogResult.Error -> {
                        tvNoSimilarItems.visibility = View.VISIBLE
                        tvNoSimilarItems.text = result.message
                    }
                }
            }
        }
    }

    companion object {
        const val RESULT_KEY_BORROWED = "item_details_borrowed_result"

        private const val ARG_ITEM_ID = "arg_item_id"
        private const val ARG_CATEGORY_ID = "arg_category_id"
        private const val ARG_NAME = "arg_name"
        private const val ARG_CATEGORY = "arg_category"
        private const val ARG_STATUS = "arg_status"
        private const val ARG_IMAGE_URL = "arg_image_url"
        private const val ARG_ASSET_TAG = "arg_asset_tag"
        private const val ARG_DESCRIPTION = "arg_description"

        fun newInstance(item: ExploreItemUi): ItemDetailsFragment {
            return ItemDetailsFragment().apply {
                arguments = bundleOf(
                    ARG_ITEM_ID to item.itemId,
                    ARG_CATEGORY_ID to item.categoryId,
                    ARG_NAME to item.name,
                    ARG_CATEGORY to item.category,
                    ARG_STATUS to item.status,
                    ARG_IMAGE_URL to item.imageUrl,
                    ARG_ASSET_TAG to item.assetTag,
                    ARG_DESCRIPTION to item.description
                )
            }
        }
    }
}

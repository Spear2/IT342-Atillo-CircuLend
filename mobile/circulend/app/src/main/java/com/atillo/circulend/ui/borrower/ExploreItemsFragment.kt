package com.atillo.circulend.ui.borrower

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.atillo.circulend.R
import com.atillo.circulend.data.repository.CatalogResult
import com.atillo.circulend.data.repository.ItemRepository
import com.atillo.circulend.ui.auth.LoginActivity
import com.atillo.circulend.util.SessionManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText

class ExploreItemsFragment : Fragment(R.layout.fragment_explore_items) {

    data class CategoryOption(val id: Long?, val label: String)

    private val categoryOptions = listOf(
        CategoryOption(null, "All"),
        CategoryOption(1L, "Electronics"),
        CategoryOption(2L, "Sports"),
        CategoryOption(3L, "Technology"),
        CategoryOption(4L, "Exercise"),
        CategoryOption(5L, "Instruments")
    )

    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var rv: RecyclerView
    private lateinit var etSearch: TextInputEditText
    private lateinit var chipCategories: ChipGroup
    private lateinit var chipStatus: ChipGroup
    private lateinit var tvPagination: TextView
    private lateinit var tvShowing: TextView
    private lateinit var btnPrev: ImageButton
    private lateinit var btnNext: ImageButton

    private val adapter = ExploreItemAdapter { item ->
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, ItemDetailsFragment.newInstance(item))
            .addToBackStack("item_details")
            .commit()
    }
    private val itemRepository = ItemRepository()

    private var selectedCategoryId: Long? = null
    private var selectedStatus: String? = null // null=all, AVAILABLE, BORROWED
    private var searchQuery: String? = null

    private var currentPage = 0
    private val pageSize = 12
    private var totalPages = 0
    private var totalElements = 0L

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        rv = view.findViewById(R.id.rvExploreItems)
        etSearch = view.findViewById(R.id.etSearch)
        chipCategories = view.findViewById(R.id.chipGroupCategories)
        chipStatus = view.findViewById(R.id.chipGroupStatus)
        tvPagination = view.findViewById(R.id.tvPagination)
        tvShowing = view.findViewById(R.id.tvShowing)
        btnPrev = view.findViewById(R.id.btnPrevPage)
        btnNext = view.findViewById(R.id.btnNextPage)

        rv.layoutManager = GridLayoutManager(requireContext(), 2)
        rv.adapter = adapter

        setupCategoryChips()
        setupStatusChips()
        setupSearch()
        setupPagination()
        setupDetailsBorrowResultListener()
        swipeRefresh.setOnRefreshListener { loadItems(resetPage = false) }

        loadItems(resetPage = true)
    }

    private fun setupDetailsBorrowResultListener() {
        parentFragmentManager.setFragmentResultListener(
            ItemDetailsFragment.RESULT_KEY_BORROWED,
            viewLifecycleOwner
        ) { _, _ ->
            loadItems(resetPage = false)
        }
    }

    private fun setupCategoryChips() {
        chipCategories.removeAllViews()

        categoryOptions.forEachIndexed { index, option ->
            val chip = Chip(requireContext()).apply {
                text = option.label
                isCheckable = true
                id = View.generateViewId()
                isChecked = index == 0
            }
            chip.setOnClickListener {
                selectedCategoryId = option.id
                loadItems(resetPage = true)
            }
            chipCategories.addView(chip)
        }
    }

    private fun setupStatusChips() {
        chipStatus.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            val checkedChip = group.findViewById<Chip>(checkedIds.first())
            selectedStatus = when (checkedChip.text.toString().uppercase()) {
                "ALL" -> null
                "AVAILABLE" -> "AVAILABLE"
                "BORROWED" -> "BORROWED"
                else -> null
            }
            loadItems(resetPage = true)
        }
    }

    private fun setupSearch() {
        etSearch.doAfterTextChanged { editable ->
            searchRunnable?.let { handler.removeCallbacks(it) }
            searchRunnable = Runnable {
                val q = editable?.toString()?.trim().orEmpty()
                searchQuery = if (q.isBlank()) null else q
                loadItems(resetPage = true)
            }
            handler.postDelayed(searchRunnable!!, 350)
        }
    }

    private fun setupPagination() {
        btnPrev.setOnClickListener {
            if (currentPage > 0) {
                currentPage--
                loadItems(resetPage = false)
            }
        }
        btnNext.setOnClickListener {
            if (currentPage + 1 < totalPages) {
                currentPage++
                loadItems(resetPage = false)
            }
        }
    }

    private fun loadItems(resetPage: Boolean) {
        if (resetPage) currentPage = 0
        swipeRefresh.isRefreshing = true

        itemRepository.getItems(
            query = searchQuery,
            categoryId = selectedCategoryId,
            status = selectedStatus,
            page = currentPage,
            size = pageSize
        ) { result ->
            activity?.runOnUiThread {
                swipeRefresh.isRefreshing = false

                when (result) {
                    is CatalogResult.Success -> {
                        totalPages = result.totalPages
                        totalElements = result.totalElements
                        currentPage = result.page

                        val mapped = result.items.map {
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
                        adapter.submitList(mapped)
                        updatePaginationUi(mapped.size)
                    }

                    is CatalogResult.Unauthorized -> {
                        SessionManager(requireContext()).clear()
                        Toast.makeText(requireContext(), "Session expired. Please login again.", Toast.LENGTH_LONG).show()
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                        requireActivity().finish()
                    }

                    is CatalogResult.Error -> {
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updatePaginationUi(currentPageCount: Int) {
        val pageDisplay = if (totalPages == 0) 0 else currentPage + 1
        tvPagination.text = "Page $pageDisplay of $totalPages"

        val start = if (totalElements == 0L) 0 else (currentPage * pageSize) + 1
        val end = if (totalElements == 0L) 0 else (start + currentPageCount - 1).coerceAtMost(totalElements.toInt())
        tvShowing.text = "Showing $start-$end of $totalElements items"

        btnPrev.isEnabled = currentPage > 0
        btnNext.isEnabled = totalPages > 0 && currentPage + 1 < totalPages
    }
}
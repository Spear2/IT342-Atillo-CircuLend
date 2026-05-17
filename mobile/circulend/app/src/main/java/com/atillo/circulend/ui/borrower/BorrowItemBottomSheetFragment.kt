package com.atillo.circulend.ui.borrower

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import com.atillo.circulend.R
import com.atillo.circulend.data.repository.BorrowResult
import com.atillo.circulend.data.repository.TransactionRepository
import com.atillo.circulend.ui.auth.LoginActivity
import com.atillo.circulend.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BorrowItemBottomSheetFragment : BottomSheetDialogFragment(R.layout.bottom_sheet_borrow_confirm) {
    private val repo = TransactionRepository()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemId = requireArguments().getLong(ARG_ITEM_ID)
        val name = requireArguments().getString(ARG_NAME).orEmpty()

        val tvBorrowItemName = view.findViewById<TextView>(R.id.tvBorrowItemName)
        val tvBorrowTimestamp = view.findViewById<TextView>(R.id.tvBorrowTimestamp)
        val etAssetTag = view.findViewById<TextInputEditText>(R.id.etAssetTag)
        val btnConfirmBorrow = view.findViewById<Button>(R.id.btnConfirmBorrow)

        tvBorrowItemName.text = "Item: $name"
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a"))
        tvBorrowTimestamp.text = "Borrowed at: $now"

        btnConfirmBorrow.setOnClickListener {
            val assetTag = etAssetTag.text?.toString()?.trim().orEmpty()
            if (assetTag.isBlank()) {
                etAssetTag.error = "Asset tag is required"
                etAssetTag.requestFocus()
                return@setOnClickListener
            }

            btnConfirmBorrow.isEnabled = false
            repo.borrow(itemId = itemId, assetTag = assetTag) { result ->
                activity?.runOnUiThread {
                    when (result) {
                        is BorrowResult.Success -> {
                            Toast.makeText(
                                requireContext(),
                                "Borrow successful",
                                Toast.LENGTH_LONG
                            ).show()
                            parentFragmentManager.setFragmentResult(
                                RESULT_KEY,
                                bundleOf("ok" to true)
                            )
                            dismiss()
                        }

                        is BorrowResult.Unauthorized -> {
                            SessionManager(requireContext()).clear()
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            requireActivity().finish()
                        }

                        is BorrowResult.Error -> {
                            btnConfirmBorrow.isEnabled = true
                            Toast.makeText(
                                requireContext(),
                                result.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "borrow_item_bottom_sheet"
        const val RESULT_KEY = "borrow_result"

        private const val ARG_ITEM_ID = "arg_item_id"
        private const val ARG_NAME = "arg_name"

        fun newInstance(itemId: Long, itemName: String): BorrowItemBottomSheetFragment {
            return BorrowItemBottomSheetFragment().apply {
                arguments = bundleOf(
                    ARG_ITEM_ID to itemId,
                    ARG_NAME to itemName
                )
            }
        }
    }
}

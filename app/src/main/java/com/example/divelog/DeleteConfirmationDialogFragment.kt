package com.example.divelog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class DeleteConfirmationDialogFragment : DialogFragment() {

    private lateinit var certification: Certification

    companion object {
        private const val ARG_CERTIFICATION = "certification"

        fun newInstance(certification: Certification): DeleteConfirmationDialogFragment {
            val fragment = DeleteConfirmationDialogFragment()
            val args = Bundle().apply {
                putParcelable(ARG_CERTIFICATION, certification)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        certification = arguments?.getParcelable(ARG_CERTIFICATION)!!

        return AlertDialog.Builder(requireContext())
            .setTitle("Delete Certification")
            .setMessage("Are you sure you want to delete this certification?")
            .setPositiveButton("Delete") { dialog, which ->
                deleteCertification()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun deleteCertification() {
        val dbHelper = DiveDatabaseHelper(requireContext())
        dbHelper.deleteCertification(certification) // Pass the certification object directly

        // Refresh the certifications list in the previous fragment
        val parentFragment = parentFragmentManager.findFragmentById(R.id.fragment_container) as? ViewCertificationsFragment
        parentFragment?.loadCertifications()
    }
}

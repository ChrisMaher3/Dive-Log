package com.example.divelog

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment

class MyDivesFragment : Fragment() {

    private lateinit var divesListView: ListView
    private lateinit var sortButton: Button

    private var dives: MutableList<Dive> = mutableListOf()
    private lateinit var adapter: DiveAdapter

    companion object {
        fun newInstance(dives: List<Dive>): MyDivesFragment {
            val fragment = MyDivesFragment()
            val args = Bundle()
            args.putParcelableArrayList("dives", ArrayList(dives))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the list of dives from the arguments (if any)
        arguments?.let {
            dives = it.getParcelableArrayList("dives") ?: mutableListOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_dives, container, false)

        // Initialize views
        divesListView = view.findViewById(R.id.divesListView)
        sortButton = view.findViewById(R.id.sortButton)

        // Set up the custom adapter for the ListView
        adapter = DiveAdapter(requireContext(), dives)
        divesListView.adapter = adapter

        // Handle long click for dive deletion
        divesListView.setOnItemLongClickListener { parent, view, position, id ->
            showDeleteDiveDialog(position)
            true
        }

        // Handle single tap to show dive details
        divesListView.setOnItemClickListener { parent, view, position, id ->
            val selectedDive = dives[position]
            val diveDetailFragment = DiveDetailFragment.newInstance(selectedDive)
            // Navigate to DiveDetailFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, diveDetailFragment)
                .addToBackStack(null) // Add to back stack so user can navigate back
                .commit()
        }

        // Set up sorting button click listener
        sortButton.setOnClickListener {
            showSortOptionsDialog()
        }

        return view
    }

    // Show a confirmation dialog to delete a dive
    private fun showDeleteDiveDialog(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Dive")
            .setMessage("Are you sure you want to delete this dive?")
            .setPositiveButton("Delete") { _, _ ->
                deleteDive(position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Method to delete a dive
    private fun deleteDive(position: Int) {
        // Remove dive from the list and database
        val diveToRemove = dives[position]
        val repository = DiveRepository(requireContext())
        repository.deleteDive(diveToRemove)

        // Remove from list and update the adapter
        dives.removeAt(position)
        adapter.notifyDataSetChanged()

        Toast.makeText(requireContext(), "Dive deleted", Toast.LENGTH_SHORT).show()
    }

    // Show sorting options dialog
    private fun showSortOptionsDialog() {
        val options = arrayOf("By Location", "By Depth", "By Duration", "By Date")
        AlertDialog.Builder(requireContext())
            .setTitle("Sort Dives")
            .setItems(options) { dialog, which ->
                showSortOrderDialog(which)
            }
            .show()
    }

    // Show sort order options dialog (Ascending or Descending)
    private fun showSortOrderDialog(sortIndex: Int) {
        val orderOptions = arrayOf("Ascending", "Descending")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Sort Order")
            .setItems(orderOptions) { dialog, orderIndex ->
                when (orderIndex) {
                    0 -> {
                        // Ascending order
                        when (sortIndex) {
                            0 -> sortDivesByLocation(ascending = true)
                            1 -> sortDivesByDepth(ascending = true)
                            2 -> sortDivesByDuration(ascending = true)
                            3 -> sortDivesByDate(ascending = true)
                        }
                    }
                    1 -> {
                        // Descending order
                        when (sortIndex) {
                            0 -> sortDivesByLocation(ascending = false)
                            1 -> sortDivesByDepth(ascending = false)
                            2 -> sortDivesByDuration(ascending = false)
                            3 -> sortDivesByDate(ascending = false)
                        }
                    }
                }
            }
            .show()
    }

    // Sorting methods
    private fun sortDivesByLocation(ascending: Boolean) {
        if (ascending) {
            dives.sortBy { it.location }
        } else {
            dives.sortByDescending { it.location }
        }
        adapter.notifyDataSetChanged()
    }

    private fun sortDivesByDepth(ascending: Boolean) {
        if (ascending) {
            dives.sortBy { it.maxDepth }
        } else {
            dives.sortByDescending { it.maxDepth }
        }
        adapter.notifyDataSetChanged()
    }

    private fun sortDivesByDuration(ascending: Boolean) {
        if (ascending) {
            dives.sortBy { it.duration }
        } else {
            dives.sortByDescending { it.duration }
        }
        adapter.notifyDataSetChanged()
    }

    private fun sortDivesByDate(ascending: Boolean) {
        if (ascending) {
            dives.sortBy { it.date }
        } else {
            dives.sortByDescending { it.date }
        }
        adapter.notifyDataSetChanged()
    }
}

package com.chris.divelog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
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
        arguments?.let {
            dives = it.getParcelableArrayList("dives") ?: mutableListOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_dives, container, false)

        divesListView = view.findViewById(R.id.divesListView)
        sortButton = view.findViewById(R.id.sortButton)

        adapter = DiveAdapter(requireContext(), dives)
        divesListView.adapter = adapter

        val sharedPreferences = requireContext().getSharedPreferences("SortPreferences", Context.MODE_PRIVATE)
        val sortIndex = sharedPreferences.getInt("sortIndex", -1)
        val sortAscending = sharedPreferences.getBoolean("sortAscending", true)

        if (sortIndex != -1) {
            applySorting(sortIndex, sortAscending)
        }

        sortButton.setOnClickListener {
            showSortOptionsDialog()
        }

        divesListView.setOnItemLongClickListener { _, _, position, _ ->
            showDeleteDiveDialog(position)
            true
        }

        divesListView.setOnItemClickListener { parent, _, position, _ ->
            val selectedDive = dives[position]
            val diveDetailFragment = DiveDetailFragment.newInstance(selectedDive)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, diveDetailFragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

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

    private fun deleteDive(position: Int) {
        val diveToRemove = dives[position]
        Log.d("DeleteDive", "Deleting dive: $diveToRemove")
        val repository = DiveRepository(requireContext())
        repository.deleteDive(diveToRemove)
        dives.removeAt(position)
        adapter.notifyDataSetChanged()
        Toast.makeText(requireContext(), "Dive deleted", Toast.LENGTH_SHORT).show()
    }

    private fun showSortOptionsDialog() {
        val options = arrayOf("By Location", "By Depth", "By Duration", "By Date")
        AlertDialog.Builder(requireContext())
            .setTitle("Sort Dives")
            .setItems(options) { _, which ->
                showSortOrderDialog(which)
            }
            .show()
    }

    private fun showSortOrderDialog(sortIndex: Int) {
        val orderOptions = arrayOf("Ascending", "Descending")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Sort Order")
            .setItems(orderOptions) { _, orderIndex ->
                val ascending = orderIndex == 0
                saveSortingPreferences(sortIndex, ascending)
                applySorting(sortIndex, ascending)
            }
            .show()
    }

    private fun saveSortingPreferences(sortIndex: Int, ascending: Boolean) {
        val sharedPreferences = requireContext().getSharedPreferences("SortPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("sortIndex", sortIndex)
            putBoolean("sortAscending", ascending)
            apply()
        }
    }

    private fun applySorting(sortIndex: Int, ascending: Boolean) {
        when (sortIndex) {
            0 -> sortDivesByLocation(ascending)
            1 -> sortDivesByDepth(ascending)
            2 -> sortDivesByDuration(ascending)
            3 -> sortDivesByDate(ascending)
        }
    }

    private fun sortDivesByLocation(ascending: Boolean) {
        dives.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.location })
        if (!ascending) dives.reverse()
        adapter.notifyDataSetChanged()
    }

    private fun sortDivesByDepth(ascending: Boolean) {
        dives.sortBy { it.maxDepth }
        if (!ascending) dives.reverse()
        adapter.notifyDataSetChanged()
    }

    private fun sortDivesByDuration(ascending: Boolean) {
        dives.sortBy { it.duration }
        if (!ascending) dives.reverse()
        adapter.notifyDataSetChanged()
    }

    private fun sortDivesByDate(ascending: Boolean) {
        dives.sortBy { it.date }
        if (!ascending) dives.reverse()
        adapter.notifyDataSetChanged()
    }
}
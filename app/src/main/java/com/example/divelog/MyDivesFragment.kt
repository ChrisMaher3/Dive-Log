package com.chris.divelog

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyDivesFragment : Fragment() {

    private lateinit var divesRecyclerView: RecyclerView
    private lateinit var sortButton: Button
    private lateinit var searchView: SearchView

    private var allDives: MutableList<Dive> = mutableListOf()
    private var currentDisplayedDives: MutableList<Dive> = mutableListOf()

    private lateinit var adapter: DiveAdapter

    private var currentSearchQuery: String = ""
    private var currentSortIndex: Int = -1
    private var currentSortAscending: Boolean = true

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
            allDives = it.getParcelableArrayList("dives") ?: mutableListOf()
            currentDisplayedDives.addAll(allDives)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_dives, container, false)

        divesRecyclerView = view.findViewById(R.id.divesRecyclerView)
        sortButton = view.findViewById(R.id.sortButton)
        searchView = view.findViewById(R.id.searchView)

        // ✅ Enhance SearchView to activate typing on any click
        searchView.setOnClickListener {
            searchView.isIconified = false
            searchView.requestFocus()

            val searchEditText = searchView.findViewById<androidx.appcompat.widget.SearchView.SearchAutoComplete>(
                androidx.appcompat.R.id.search_src_text
            )
            searchEditText?.let {
                it.requestFocus()
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        divesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DiveAdapter(requireContext(), currentDisplayedDives,
            onClick = { selectedDive ->
                val diveDetailFragment = DiveDetailFragment.newInstance(selectedDive)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, diveDetailFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onLongClick = { position ->
                showDeleteDiveDialog(position)
            }
        )
        divesRecyclerView.adapter = adapter

        val sharedPreferences = requireContext().getSharedPreferences("SortPreferences", Context.MODE_PRIVATE)
        currentSortIndex = sharedPreferences.getInt("sortIndex", -1)
        currentSortAscending = sharedPreferences.getBoolean("sortAscending", true)

        updateDisplayedDives()

        sortButton.setOnClickListener {
            showSortOptionsDialog()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentSearchQuery = newText.orEmpty()
                updateDisplayedDives()
                return true
            }
        })

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
        val diveToRemove = currentDisplayedDives[position]
        Log.d("DeleteDive", "Deleting dive: $diveToRemove")

        val repository = DiveRepository(requireContext())
        repository.deleteDive(diveToRemove)

        allDives.remove(diveToRemove)
        updateDisplayedDives()
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
                currentSortIndex = sortIndex
                currentSortAscending = ascending
                saveSortingPreferences(currentSortIndex, currentSortAscending)
                updateDisplayedDives()
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

    private fun updateDisplayedDives() {
        val filteredList = if (currentSearchQuery.isNotBlank()) {
            allDives.filter { dive ->
                dive.location.contains(currentSearchQuery, ignoreCase = true) ||
                        dive.date.contains(currentSearchQuery, ignoreCase = true)
            }.toMutableList()
        } else {
            allDives.toMutableList()
        }

        val finalDisplayList = if (currentSortIndex != -1) {
            applySorting(filteredList, currentSortIndex, currentSortAscending)
        } else {
            filteredList
        }

        currentDisplayedDives.clear()
        currentDisplayedDives.addAll(finalDisplayList)
        adapter.notifyDataSetChanged()
    }

    private fun applySorting(listToSort: MutableList<Dive>, sortIndex: Int, ascending: Boolean): MutableList<Dive> {
        val sortedList = listToSort.toMutableList()
        when (sortIndex) {
            0 -> sortedList.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER) { it.location })
            1 -> sortedList.sortBy { it.maxDepth }
            2 -> sortedList.sortBy { it.duration }
            3 -> sortedList.sortBy { it.date }
        }
        if (!ascending) sortedList.reverse()
        return sortedList
    }
}

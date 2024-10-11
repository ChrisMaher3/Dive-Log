package com.example.divelog

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import android.widget.Toast

class MyDivesFragment : Fragment() {

    private lateinit var divesListView: ListView
    private lateinit var addDiveButton: Button

    private var dives: MutableList<Dive> = mutableListOf()
    private lateinit var adapter: ArrayAdapter<Dive>

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
        addDiveButton = view.findViewById(R.id.addDiveButton)

        // Set up the adapter for the ListView
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, dives)
        divesListView.adapter = adapter

        // Handle long click for dive deletion
        divesListView.setOnItemLongClickListener { parent, view, position, id ->
            showDeleteDiveDialog(position)
            true
        }

        // Set up "Add Dive" button click listener
        addDiveButton.setOnClickListener {
            // Navigate to the AddDiveFragment to add a new dive
            (activity as MainActivity).loadFragment(AddDiveFragment())
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
}

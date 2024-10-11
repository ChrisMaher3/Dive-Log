package com.example.divelog

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MyDivesFragment : Fragment() {

    private lateinit var divesRecyclerView: RecyclerView
    private lateinit var addDiveButton: Button

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

        // Log the number of dives loaded to ensure it's working
        Log.d("MyDivesFragment", "Number of dives loaded: ${dives.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_dives, container, false)

        // Initialize views
        divesRecyclerView = view.findViewById(R.id.divesRecyclerView)
        addDiveButton = view.findViewById(R.id.addDiveButton)

        // Set up the RecyclerView with a LinearLayoutManager
        divesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Check if the list of dives is empty
        if (dives.isEmpty()) {
            Toast.makeText(requireContext(), "No dives available", Toast.LENGTH_SHORT).show()
        } else {
            // Set up the adapter for RecyclerView
            adapter = DiveAdapter(dives) { dive, position -> onLongClickDive(dive, position) }
            divesRecyclerView.adapter = adapter
        }

        // Set up "Add Dive" button click listener
        addDiveButton.setOnClickListener {
            // Navigate to the AddDiveFragment to add a new dive
            (activity as MainActivity).loadFragment(AddDiveFragment())
        }

        return view
    }

    // Handle long click for dive deletion
    private fun onLongClickDive(dive: Dive, position: Int) {
        showDeleteDiveDialog(position)
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
        adapter.notifyItemRemoved(position)

        Toast.makeText(requireContext(), "Dive deleted", Toast.LENGTH_SHORT).show()
    }
}

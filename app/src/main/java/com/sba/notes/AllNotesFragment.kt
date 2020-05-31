package com.sba.notes

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.sba.notes.database.NotesViewModel
import com.sba.notes.databinding.FragmentAllNotesBinding


class AllNotesFragment : Fragment() {
    private var fbind: FragmentAllNotesBinding? = null
    private lateinit var notesViewModel: NotesViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      //  setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_all_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAllNotesBinding.bind(view)
        fbind = binding
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        val adapter = activity?.applicationContext?.let { NotesAdapter() }
        binding.noteRecycler.adapter = adapter
        binding.noteRecycler.setHasFixedSize(true)
        binding.noteRecycler.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        notesViewModel.allNotes.observe(viewLifecycleOwner, androidx.lifecycle.Observer { notes ->
            adapter?.submitList(notes)


        })

        //notesViewModel.insertNote(note = Notes(title="Hello World",description = "First Note"))

        binding.newNoteFAB.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_allNotesFragment_to_editNoteFragment)
        }


        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val mNote = adapter?.getNote(viewHolder.adapterPosition)
                    mNote?.let { notesViewModel.deleteNote(it) }

                    if (mNote != null) {
                        Snackbar.make(view, "Note Deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo") {
                                notesViewModel.insertNote(mNote)
                            }.show()
                    }

                }
            }


        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.noteRecycler)


    }


    override fun onDestroyView() {
        super.onDestroyView()
        fbind = null
    }

    private fun setTheme() {

    }

//    private fun deleteALL() {
//        //val deleteDialog = activity?.let { DeleteAllAlertDialoge(it) }
//      //  deleteDialog?.startDeleteDialoge()
//
//    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//
//        inflater.inflate(R.menu.menu, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.delete_all_menu ->deleteALL()
//            R.id.dark_mode_switch->setTheme()
//        }
//        return super.onOptionsItemSelected(item)
//    }


}
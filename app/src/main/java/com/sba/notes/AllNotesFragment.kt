package com.sba.notes

import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.*
import com.google.android.material.snackbar.Snackbar
import com.ncorti.slidetoact.SlideToActView
import com.sba.notes.database.NotesViewModel
import com.sba.notes.databinding.FragmentAllNotesBinding
import kotlin.properties.Delegates


class AllNotesFragment : Fragment() {

    private var fbind: FragmentAllNotesBinding? = null
    private lateinit var notesViewModel: NotesViewModel
    private val sharedPrefKey = "appSettings"
    private val nightModeKey = "NightMode"
    lateinit var appPref: SharedPreferences
    lateinit var sharedPrefsEdit: SharedPreferences.Editor
    var nightModeStatus by Delegates.notNull<Int>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_all_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAllNotesBinding.bind(view)
        fbind = binding
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)

        appPref = activity?.getSharedPreferences(sharedPrefKey, 0)!!
        nightModeStatus = appPref.getInt("NightMode", 3)


        setTheme(nightModeStatus)


        val adapter = activity?.applicationContext?.let { NotesAdapter() }
        binding.noteRecycler.adapter = adapter
        binding.noteRecycler.setHasFixedSize(true)
        binding.noteRecycler.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        notesViewModel.allNotes.observe(viewLifecycleOwner, androidx.lifecycle.Observer { notes ->
            adapter?.submitList(notes)


        })


        binding.newNoteFAB.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_allNotesFragment_to_editNoteFragment)
        }


        val itemTouchHelperCallback = object :
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

    private fun setTheme(nightStatus: Int) {
        when (nightStatus) {
            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Log.d("AllNoteFrag","Light theme SetTheme()")
            }
            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Log.d("AllNoteFrag","Dark theme SetTheme()")
            }
            else -> {
                Log.d("AllNoteFrag","System theme SetTheme()")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
            }
        }

    }

    private fun setThemeDialog() {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.alert_dialog_theme_select, null)
        val dialog = AlertDialog.Builder(activity)
            .setView(view)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val themeRadioGroup = view.findViewById<RadioGroup>(R.id.theme_button_group)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            view.findViewById<RadioButton>(R.id.deafultRadioButton).text =
                getString(R.string.system_default)
        } else {
            view.findViewById<RadioButton>(R.id.deafultRadioButton).text =
                getString(R.string.follow_battery_saver)
        }

        when (nightModeStatus) {
            1 -> view.findViewById<RadioButton>(R.id.lightRadioButton).isChecked = true
            2 -> view.findViewById<RadioButton>(R.id.darkRadioButton).isChecked = true
            3 -> view.findViewById<RadioButton>(R.id.deafultRadioButton).isChecked = true
        }

        themeRadioGroup.setOnCheckedChangeListener { _, id ->
            sharedPrefsEdit = appPref.edit()
            when (id) {
                R.id.lightRadioButton -> {
                    sharedPrefsEdit.putInt(nightModeKey, 1)
                    sharedPrefsEdit.apply()
                    nightModeStatus=1
                    Log.d("AllNoteFrag","Light theme")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                R.id.darkRadioButton -> {
                    sharedPrefsEdit.putInt(nightModeKey, 2)
                    sharedPrefsEdit.apply()
                    nightModeStatus=2
                    Log.d("AllNoteFrag","Dark theme")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                R.id.deafultRadioButton -> {
                    sharedPrefsEdit.putInt(nightModeKey, 3)
                    sharedPrefsEdit.apply()
                    nightModeStatus=3
                    Log.d("AllNoteFrag","System theme")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    )
                    else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)

                }
            }
            dialog.dismiss()
        }

    }

    private fun deleteALLDialog() {

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.alert_dialog_delete_all, null)
        val slide = view.findViewById<SlideToActView>(R.id.slideConfirm)
        val dialog = AlertDialog.Builder(activity)
            .setView(view)
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        slide.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener {

            override fun onSlideComplete(slider: SlideToActView) {
                Log.d("Test", "Deleted")

                deleteALL()
                dialog.dismiss()
            }
        }


    }

    private fun deleteALL() {
        notesViewModel.deleteAllNote()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all_menu -> deleteALLDialog()
            R.id.dark_mode_menu -> setThemeDialog()
        }
        return super.onOptionsItemSelected(item)
    }


}

package com.example.manakos.activites.Counter

import android.R
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import com.example.manakos.activites.Indication.IndicationsActivity
import com.example.manakos.activites.Indication.WorkIndicationActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityListCountersBinding
import com.example.manakos.models.Counter

class ListCountersActivity : AppCompatActivity() {
    lateinit var binding: ActivityListCountersBinding

    private var counters = ArrayList<Counter>()
    private var flats = ArrayList<String>()
    lateinit var flatsAdapter: ArrayAdapter<String>
    lateinit var adapter: ArrayAdapter<String>
    lateinit var types: Array<String>
    lateinit var usedArr: Array<String>

    var id: Int = 0
    var role = ""
    lateinit var settings: SharedPreferences


    private lateinit var databaseRequests: DatabaseRequests
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListCountersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseRequests =  DatabaseRequests(this@ListCountersActivity)
        settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)
        checkUser()
        types = resources.getStringArray(com.example.manakos.R.array.typesCounterFilter)
        usedArr = resources.getStringArray(com.example.manakos.R.array.typesCounterUsed)

        binding.buttonThen.setOnClickListener(View.OnClickListener {
            val i = Intent(this, WorkIndicationActivity::class.java)
            i.putExtra("id_counter", counters[binding.listCounters.checkedItemPosition].id)
            startActivity(i)
        })

        binding.buttonBack.setOnClickListener(View.OnClickListener {
            val i = Intent(this, IndicationsActivity::class.java)
            startActivity(i)
        })
        flatsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, flats)
        binding.listViewFlats.adapter = flatsAdapter
        binding.listViewFlats.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            binding.serchViewFlat.setQuery(selectedItem, true)
        }
        binding.filters.visibility = View.GONE;
        binding.filterOpen.setOnClickListener(View.OnClickListener {
            binding.filters.visibility = View.VISIBLE;
        })
        binding.serchViewFlat.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.serchViewFlat.clearFocus()
                if(flats.contains(query)) flatsAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                flatsAdapter.filter.filter(newText)
                return false
            }
        })
        binding.buttonBack2.setOnClickListener(View.OnClickListener { binding.filters.visibility = View.GONE; })
        binding.btnFilter.setOnClickListener(View.OnClickListener {
            filterData()
        })
        binding.btnReset.setOnClickListener(View.OnClickListener {
            resetData()
        })
    }

    private fun resetData() {
        var values = emptyArray<String>()
        for (i in 0..counters.size - 1){
            values += ("Tipe: " + counters[i].type + "\nNomor Kos: " + databaseRequests.selectFlatNumberFromId(counters[i].id_flat) + ", penghitung: " + counters[i].number)
        }
        adapter = ArrayAdapter(this@ListCountersActivity, R.layout.simple_list_item_single_choice, values)
        binding.filters.visibility = View.GONE
        binding.serchViewFlat.setQuery("", true)
        binding.spinnerType.setSelection(0)
        binding.listCounters.adapter = adapter
    }

    private fun filterData() {
        var id_flat = ""
        val type = types[types.indexOfFirst {  it.equals(binding.spinnerType.selectedItem)}]
        var newCounters = ArrayList<Counter>()
        for(counter: Counter in counters)
        {
            if(counter.type.contains(type)) newCounters.add(counter)
        }
        if(binding.serchViewFlat.query.toString() != "")
        {
            id_flat = databaseRequests.selectIdFlatFromNumber(binding.serchViewFlat.query.toString()).toString()
            newCounters = newCounters.filter {  it.id_flat == (id_flat.toInt())  } as ArrayList<Counter>
        }
        var values = emptyArray<String>()
        for (i in 0..newCounters.size - 1){
            values += ("Tipe: " + newCounters[i].type + "\nNomor Kos: " + databaseRequests.selectFlatNumberFromId(newCounters[i].id_flat) + ", penghitung: " + newCounters[i].number)
        }
        adapter = ArrayAdapter(this@ListCountersActivity, R.layout.simple_list_item_single_choice, values)
        binding.listCounters.adapter = adapter
        binding.filters.visibility = View.GONE
    }

    private fun checkUser() {
        id = settings.getInt("id", 0)
        role = settings.getString("role", "").toString()
        fillData(id)
    }

    private fun fillData(id: Int) {
        if(id == 0) counters = databaseRequests.selectCountersWhereUsed()
        else {
            val flatsI = databaseRequests.selectFlatsFromIdTenant(id)
            for (item in flatsI){
                counters.addAll(databaseRequests.selectCountersFromIdFlat(item.id))
            }
        }
        var values = emptyArray<String>()
        for (i in 0..counters.size - 1){
            values += ("Tipe: " + counters[i].type + "\nNomor Kos: " + databaseRequests.selectFlatNumberFromId(counters[i].id_flat) + ", penghitung: " + counters[i].number)
        }

        adapter = ArrayAdapter(this@ListCountersActivity, R.layout.simple_list_item_single_choice, values)
        binding.listCounters.choiceMode = ListView.CHOICE_MODE_SINGLE
        binding.listCounters.minimumHeight = 130
        binding.listCounters.adapter = adapter

        val id_counter = intent.getIntExtra("id_counter", 0)
        if(id_counter != 0){
            binding.listCounters.setItemChecked(counters.indexOfFirst { it.id == id_counter }, true)
        }
        else{
            binding.listCounters.setItemChecked(0, true)
        }
        if(counters.size == 0) binding.buttonThen.isEnabled = false
        flats = databaseRequests.selectNumberFlats()
    }
}

package com.example.manakos.activites.Counter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manakos.R
import com.example.manakos.activites.Flat.FlatsActivity
import com.example.manakos.activites.Flat.ListFlatsActivity
import com.example.manakos.activites.Indication.IndicationsActivity
import com.example.manakos.activites.Payment.PaymentsActivity
import com.example.manakos.activites.RatesAndNormatives.RatesAndNormativesActivity
import com.example.manakos.activites.Tenant.TenantsActivity
import com.example.manakos.activites.other.MenuActivity
import com.example.manakos.activites.users.AccountActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityCountersBinding
import com.example.manakos.models.Counter
import com.example.manakos.recyclerview.CounterViewAdapter

class CountersActivity : AppCompatActivity() {
    lateinit var binding: ActivityCountersBinding
    lateinit var toggle: ActionBarDrawerToggle

    private lateinit var databaseRequests: DatabaseRequests
    private var counters = ArrayList<Counter>()
    private var flats = ArrayList<String>()
    lateinit var flatsAdapter: ArrayAdapter<String>
    lateinit var adapter: CounterViewAdapter
    lateinit var types: Array<String>
    lateinit var usedArr: Array<String>

    var id: Int = 0
    var role = ""
    lateinit var settings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)
        types = resources.getStringArray(R.array.typesCounterFilter)
        usedArr = resources.getStringArray(R.array.typesCounterUsed)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@CountersActivity, drawer,
                R.string.open,
                R.string.close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true);

            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.flatsItem ->{
                        val i = Intent(this@CountersActivity, FlatsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.homeItem ->{
                        val i = Intent(this@CountersActivity, MenuActivity::class.java)
                        startActivity(i)
                    }
                    R.id.tenantsItem ->{
                        val i = Intent(this@CountersActivity, TenantsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.accountItem ->{
                        val i = Intent(this@CountersActivity, AccountActivity::class.java)
                        startActivity(i)
                    }
                    R.id.сountersItem ->{
                        val i = Intent(this@CountersActivity, CountersActivity::class.java)
                        startActivity(i)
                    }
                    R.id.indicationsItem ->{
                        val i = Intent(this@CountersActivity, IndicationsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.ratesItem ->{
                        val i = Intent(this@CountersActivity, RatesAndNormativesActivity::class.java)
                        startActivity(i)
                    }
                    R.id.paymentsItem ->{
                        val i = Intent(this@CountersActivity, PaymentsActivity::class.java)
                        startActivity(i)
                    }
                }
                true
            }
        }

        binding.recycleViewCounters.setLayoutManager(LinearLayoutManager(this@CountersActivity));
        databaseRequests =  DatabaseRequests(this@CountersActivity)
        checkUser()
        flatsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, flats)
        binding.listViewFlats.adapter = flatsAdapter
        binding.listViewFlats.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            binding.serchViewFlat.setQuery(selectedItem, true)
        }
        binding.addCounter.setOnClickListener(View.OnClickListener {addCounter()})
        binding.filters.visibility = View.GONE
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
        binding.buttonBack.setOnClickListener(View.OnClickListener { binding.filters.visibility = View.GONE; })
        binding.btnFilter.setOnClickListener(View.OnClickListener {
            filterData()
        })
        binding.btnReset.setOnClickListener(View.OnClickListener {
            resetData()
        })
    }

    private fun resetData() {
        adapter = CounterViewAdapter(counters,this@CountersActivity, this)
        binding.recycleViewCounters.setAdapter(adapter)
        binding.filters.visibility = View.GONE

        binding.serchViewFlat.setQuery("", true)
        binding.spinnerType.setSelection(0)
        binding.spinnerUsed.setSelection(0)
    }

    private fun filterData() {
        val used: String
        if(usedArr.get(usedArr.indexOfFirst { it.equals(binding.spinnerUsed.selectedItem) }) == "Ya") used = "true"
        else if(usedArr.get(usedArr.indexOfFirst { it.equals(binding.spinnerUsed.selectedItem) }) == "Tidak") used = "false"
        else used = ""
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
            newCounters = newCounters.filter { it.used.toString().contains(used)  } as ArrayList<Counter>
        }
        else
        {
            newCounters = newCounters.filter { it.used.toString().contains(used) } as ArrayList<Counter>
        }
        adapter = CounterViewAdapter(newCounters,this@CountersActivity, this)
        binding.recycleViewCounters.setAdapter(adapter)
        binding.filters.visibility = View.GONE;
    }

    private fun addCounter() {
        val i = Intent(this@CountersActivity, ListFlatsActivity::class.java)
        startActivity(i)
    }

    private fun fillData(id: Int) {
        if(id == 0) counters = databaseRequests.selectCounters()
        else{
            val flatsI = databaseRequests.selectFlatsFromIdTenant(id)
            for (item in flatsI){
                counters.addAll(databaseRequests.selectCountersFromIdFlat(item.id))
            }
        }
        adapter = CounterViewAdapter(counters, this, this)
        binding.recycleViewCounters.setAdapter(adapter)

        flats = databaseRequests.selectNumberFlats()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item))
            true
        return super.onOptionsItemSelected(item)
    }

    fun OnClickMenu(view: View?) {
        openDrawer(binding.drawer)
    }

    fun openDrawer(drawerLayout: DrawerLayout?) {
        drawerLayout!!.openDrawer(GravityCompat.START)
    }

    fun checkUser(){
        id = settings.getInt("id", 0)
        role = settings.getString("role", "").toString()
        if(role == "tenant"){
            binding.navView.menu.removeItem(R.id.ratesItem)
            binding.forAdmin.visibility = View.GONE
        }
        fillData(id)
    }
}


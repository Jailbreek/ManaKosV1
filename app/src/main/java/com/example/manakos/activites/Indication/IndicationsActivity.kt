package com.example.manakos.activites.Indication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manakos.R
import com.example.manakos.activites.users.AccountActivity
import com.example.manakos.activites.Counter.CountersActivity
import com.example.manakos.activites.Counter.ListCountersActivity
import com.example.manakos.activites.Flat.FlatsActivity
import com.example.manakos.activites.other.MenuActivity
import com.example.manakos.activites.Payment.PaymentsActivity
import com.example.manakos.activites.RatesAndNormatives.RatesAndNormativesActivity
import com.example.manakos.activites.Tenant.TenantsActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityIndicationsBinding
import com.example.manakos.models.Counter
import com.example.manakos.models.Indication
import com.example.manakos.recyclerview.IndicationViewAdapter

class IndicationsActivity : AppCompatActivity() {
    lateinit var binding: ActivityIndicationsBinding
    lateinit var toggle: ActionBarDrawerToggle

    private lateinit var databaseRequests: DatabaseRequests
    private var indications = ArrayList<Indication>()
    private var flats = ArrayList<String>()
    lateinit var flatsAdapter: ArrayAdapter<String>
    private var periods = ArrayList<String>()
    lateinit var periodsAdapter: ArrayAdapter<String>
    lateinit var adapter: IndicationViewAdapter
    lateinit var types: Array<String>

    var id: Int = 0
    var role = ""
    lateinit var settings: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIndicationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        types = resources.getStringArray(R.array.typesCounterFilter)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@IndicationsActivity, drawer,
                R.string.open,
                R.string.close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()
            settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.flatsItem ->{
                        val i = Intent(this@IndicationsActivity, FlatsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.homeItem ->{
                        val i = Intent(this@IndicationsActivity, MenuActivity::class.java)
                        startActivity(i)
                    }
                    R.id.tenantsItem ->{
                        val i = Intent(this@IndicationsActivity, TenantsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.accountItem ->{
                        val i = Intent(this@IndicationsActivity, AccountActivity::class.java)
                        startActivity(i)
                    }
                    R.id.сountersItem ->{
                        val i = Intent(this@IndicationsActivity, CountersActivity::class.java)
                        startActivity(i)
                    }
                    R.id.indicationsItem ->{
                        val i = Intent(this@IndicationsActivity, IndicationsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.ratesItem ->{
                        val i = Intent(this@IndicationsActivity, RatesAndNormativesActivity::class.java)
                        startActivity(i)
                    }
                    R.id.paymentsItem ->{
                        val i = Intent(this@IndicationsActivity, PaymentsActivity::class.java)
                        startActivity(i)
                    }
                }
                true
            }
        }
        binding.recycleViewIndications.layoutManager = LinearLayoutManager(this@IndicationsActivity)
        databaseRequests =  DatabaseRequests(this@IndicationsActivity)
        checkUser()
        binding.addIndication.setOnClickListener(View.OnClickListener {addIndication()})


        flatsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, flats)
        binding.listViewFlats.adapter = flatsAdapter
        binding.listViewFlats.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            binding.serchViewFlat.setQuery(selectedItem, true)
        }
        periodsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, periods)
        binding.listViewPeriods.adapter = periodsAdapter
        binding.listViewPeriods.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            binding.serchViewPeriod.setQuery(selectedItem, true)
        }
        binding.filters.visibility = View.GONE
        binding.filterOpen.setOnClickListener(View.OnClickListener {
            binding.filters.visibility = View.VISIBLE
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
        binding.serchViewPeriod.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.serchViewPeriod.clearFocus()
                if(periods.contains(query)) periodsAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                periodsAdapter.filter.filter(newText)
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

    private fun checkUser() {
        id = settings.getInt("id", 0)
        role = settings.getString("role", "").toString()
        if(role == "tenant"){
            binding.navView.menu.removeItem(R.id.ratesItem)
        }
        fillData(id)
    }

    private fun resetData() {
        adapter = IndicationViewAdapter(indications,this@IndicationsActivity, this)
        binding.recycleViewIndications.adapter = adapter
        binding.filters.visibility = View.GONE
        binding.serchViewFlat.setQuery("", true)
        binding.serchViewPeriod.setQuery("", true)
        binding.spinnerType.setSelection(0)
    }

    private fun filterData() {
        var id_flat = ""
        val countersType: ArrayList<Counter>
        var newIndications = ArrayList<Indication>()
        val type = types[types.indexOfFirst { it == binding.spinnerType.selectedItem }]
        if(type != "")
        {
            countersType = databaseRequests.selectCountersFromType(type)
            for(counter: Counter in countersType)
            {
                newIndications.addAll(indications.filter { it.id_counter == counter.id } )
            }
        }
        else
        {
            newIndications = indications
        }
        var newIndicationsFilter = ArrayList<Indication>()
        if(binding.serchViewFlat.query.toString() != "")
        {
            id_flat = databaseRequests.selectIdFlatFromNumber(binding.serchViewFlat.query.toString()).toString()
            val countersFlat = databaseRequests.selectCountersFromIdFlat(id_flat.toInt())
            for(counter: Counter in countersFlat)
            {
                newIndicationsFilter.addAll(newIndications.filter { it.id_counter == counter.id } )
            }
            newIndicationsFilter = newIndicationsFilter.filter { it.period.contains(binding.serchViewPeriod.query) } as ArrayList<Indication>
        }
        else
        {
            newIndicationsFilter = newIndications.filter { it.period.contains(binding.serchViewPeriod.query) } as ArrayList<Indication>
        }
        adapter = IndicationViewAdapter(newIndicationsFilter,this@IndicationsActivity, this)
        binding.recycleViewIndications.adapter = adapter
        binding.filters.visibility = View.GONE
    }

    private fun addIndication() {
        val i = Intent(this@IndicationsActivity, ListCountersActivity::class.java)
        startActivity(i)
    }

    private fun fillData(id: Int) {
        if(id == 0) indications = databaseRequests.selectIndications()
        else
        {
            val flatsI = databaseRequests.selectFlatsFromIdTenant(id)
            val counters = ArrayList<Counter>()
            for (item in flatsI){
                counters.addAll(databaseRequests.selectCountersFromIdFlat(item.id))
            }
            for(item in counters){
                indications.addAll(databaseRequests.selectIndicationsFromIdCounter(item.id))
            }
        }
        val adapter = IndicationViewAdapter(indications, this, this)
        binding.recycleViewIndications.adapter = adapter

        flats = databaseRequests.selectNumberFlats()
        periods = databaseRequests.selectPeriodsFromIndication()
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
}
package com.example.manakos.activites.Payment

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
import com.example.manakos.activites.Flat.FlatsActivity
import com.example.manakos.activites.Indication.IndicationsActivity
import com.example.manakos.activites.other.MenuActivity
import com.example.manakos.activites.RatesAndNormatives.RatesAndNormativesActivity
import com.example.manakos.activites.Tenant.TenantsActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityPaymentsBinding
import com.example.manakos.models.Payment
import com.example.manakos.recyclerview.PaymentViewAdapter

class PaymentsActivity : AppCompatActivity() {
    lateinit var binding: ActivityPaymentsBinding
    lateinit var toggle: ActionBarDrawerToggle

    private var payments = ArrayList<Payment>();
    private lateinit var databaseRequests: DatabaseRequests;

    private var flats = ArrayList<String>()
    lateinit var flatsAdapter: ArrayAdapter<String>
    private var periods = ArrayList<String>()
    lateinit var periodsAdapter: ArrayAdapter<String>
    lateinit var adapter: PaymentViewAdapter
    lateinit var services: Array<String>
    lateinit var statuses: Array<String>

    var id: Int = 0
    var role = ""
    lateinit var settings: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)
        services = resources.getStringArray(R.array.services)
        statuses = resources.getStringArray(R.array.typesCounterUsed)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@PaymentsActivity, drawer,
                R.string.open,
                R.string.close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true);

            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.flatsItem ->{
                        val i = Intent(this@PaymentsActivity, FlatsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.homeItem ->{
                        val i = Intent(this@PaymentsActivity, MenuActivity::class.java)
                        startActivity(i)
                    }
                    R.id.tenantsItem ->{
                        val i = Intent(this@PaymentsActivity, TenantsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.accountItem ->{
                        val i = Intent(this@PaymentsActivity, AccountActivity::class.java)
                        startActivity(i)
                    }
                    R.id.сountersItem ->{
                        val i = Intent(this@PaymentsActivity, CountersActivity::class.java)
                        startActivity(i)
                    }
                    R.id.indicationsItem ->{
                        val i = Intent(this@PaymentsActivity, IndicationsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.ratesItem ->{
                        val i = Intent(this@PaymentsActivity, RatesAndNormativesActivity::class.java)
                        startActivity(i)
                    }
                    R.id.paymentsItem ->{
                        val i = Intent(this@PaymentsActivity, PaymentsActivity::class.java)
                        startActivity(i)
                    }
                }
                true
            }
        }
        binding.recycleViewPayments.setLayoutManager(LinearLayoutManager(this@PaymentsActivity));
        databaseRequests =  DatabaseRequests(this@PaymentsActivity)
        binding.addFlat.setOnClickListener(View.OnClickListener {
            val i = Intent(this@PaymentsActivity, AddPaymentsActivity::class.java)
            startActivity(i)
        })
        binding.getReport.setOnClickListener(View.OnClickListener {
            val i = Intent(this@PaymentsActivity, AddPaymentsActivity::class.java)
            i.putExtra("report", 1)
            startActivity(i)
        })
        checkUser()

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
            binding.forAdmin.visibility = View.GONE
            binding.forAdmin1.visibility = View.GONE
        }
        fillData(id)
    }

    private fun resetData() {
        adapter = PaymentViewAdapter(payments,this@PaymentsActivity, this)
        binding.recycleViewPayments.setAdapter(adapter)
        binding.filters.visibility = View.GONE;
        binding.serchViewFlat.setQuery("", true)
        binding.serchViewPeriod.setQuery("", true)
        binding.spinnerService.setSelection(0)
        binding.spinnerStatus.setSelection(0)
    }

    private fun filterData() {
        var id_flat = ""
        var id_service = 0
        val service = services[services.indexOfFirst { it == binding.spinnerService.selectedItem }]
        var newPayments = ArrayList<Payment>()
        val status: String
        if(statuses.get(statuses.indexOfFirst { it.equals(binding.spinnerStatus.selectedItem) }) == "Ya") status = "true"
        else if(statuses.get(statuses.indexOfFirst { it.equals(binding.spinnerStatus.selectedItem) }) == "Tidak") status = "false"
        else status = ""
        if(service != "")
        {
            id_service = databaseRequests.selectIdRateFromName(service)
            newPayments = payments.filter { it.id_rate == id_service  } as ArrayList<Payment>
            newPayments = newPayments.filter { it.status.toString().contains(status)  } as ArrayList<Payment>
        }
        else{
            newPayments = payments.filter { it.status.toString().contains(status) } as ArrayList<Payment>
        }
        if(binding.serchViewFlat.query.toString() != "")
        {
            id_flat = databaseRequests.selectIdFlatFromNumber(binding.serchViewFlat.query.toString()).toString()
            newPayments = newPayments.filter { it.id_flat == id_flat.toInt()} as ArrayList<Payment>
            newPayments = newPayments.filter { it.period.contains(binding.serchViewPeriod.query)} as ArrayList<Payment>
        }
        else
        {
            newPayments = newPayments.filter { it.period.contains(binding.serchViewPeriod.query) } as ArrayList<Payment>
        }
        adapter = PaymentViewAdapter(newPayments,this@PaymentsActivity, this)
        binding.recycleViewPayments.setAdapter(adapter)
        binding.filters.visibility = View.GONE;
    }

    private fun fillData(id: Int) {
        if (id == 0) payments = databaseRequests.selectPayments()
        else {
            val flatsI = databaseRequests.selectFlatsFromIdTenant(id)
            for (item in flatsI){
                payments.addAll(databaseRequests.selectPaymentsFromIdFlat(item.id))
            }
        }
        val adapter = PaymentViewAdapter(payments, this, this)
        binding.recycleViewPayments.adapter = adapter


        flats = databaseRequests.selectNumberFlats()
        periods = databaseRequests.selectPeriodsFromPayments()
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

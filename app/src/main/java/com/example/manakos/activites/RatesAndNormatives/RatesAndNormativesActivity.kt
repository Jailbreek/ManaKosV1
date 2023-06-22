package com.example.manakos.activites.RatesAndNormatives

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
import com.example.manakos.activites.Payment.PaymentsActivity
import com.example.manakos.activites.Tenant.TenantsActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityRatesBinding
import com.example.manakos.models.Normative
import com.example.manakos.models.Rate
import com.example.manakos.recyclerview.NormativeViewAdapter
import com.example.manakos.recyclerview.RateViewAdapter

class RatesAndNormativesActivity : AppCompatActivity() {
    lateinit var binding: ActivityRatesBinding
    lateinit var toggle: ActionBarDrawerToggle

    private lateinit var databaseRequests: DatabaseRequests
    private var rates = ArrayList<Rate>();
    private var normatives = ArrayList<Normative>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            toggle = ActionBarDrawerToggle(this@RatesAndNormativesActivity, drawer,
                R.string.open,
                R.string.close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true);

            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.flatsItem ->{
                        val i = Intent(this@RatesAndNormativesActivity, FlatsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.homeItem ->{
                        val i = Intent(this@RatesAndNormativesActivity, MenuActivity::class.java)
                        startActivity(i)
                    }
                    R.id.tenantsItem ->{
                        val i = Intent(this@RatesAndNormativesActivity, TenantsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.accountItem ->{
                        val i = Intent(this@RatesAndNormativesActivity, AccountActivity::class.java)
                        startActivity(i)
                    }
                    R.id.сountersItem ->{
                        val i = Intent(this@RatesAndNormativesActivity, CountersActivity::class.java)
                        startActivity(i)
                    }
                    R.id.indicationsItem ->{
                        val i = Intent(this@RatesAndNormativesActivity, IndicationsActivity::class.java)
                        startActivity(i)
                    }
                    R.id.ratesItem ->{
                        val i = Intent(this@RatesAndNormativesActivity, RatesAndNormativesActivity::class.java)
                        startActivity(i)
                    }
                    R.id.paymentsItem ->{
                        val i = Intent(this@RatesAndNormativesActivity, PaymentsActivity::class.java)
                        startActivity(i)
                    }
                }
                true
            }
        }
        binding.recycleViewRates.setLayoutManager(LinearLayoutManager(this@RatesAndNormativesActivity));
        binding.recycleViewNormatives.setLayoutManager(LinearLayoutManager(this@RatesAndNormativesActivity));
        databaseRequests =  DatabaseRequests(this@RatesAndNormativesActivity)
        fillData()
    }

    private fun fillData() {
        rates = databaseRequests.selectRates()
        normatives = databaseRequests.selectNormatives()
        val adapterRates = RateViewAdapter(rates, this, this)
        binding.recycleViewRates.setAdapter(adapterRates)
        val adapterNormatives = NormativeViewAdapter(normatives, this, this)
        binding.recycleViewNormatives.setAdapter(adapterNormatives)
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

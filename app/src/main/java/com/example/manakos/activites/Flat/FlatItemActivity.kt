package com.example.manakos.activites.Flat

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityFlatItemBinding
import com.example.manakos.models.Flat
import com.example.manakos.models.Tenant
import com.example.manakos.recyclerview.TenantViewAdapter

class FlatItemActivity : AppCompatActivity() {
    lateinit var binding: ActivityFlatItemBinding

    private var tenants = ArrayList<Tenant>()
    private var flat: Flat = Flat()

    var id: Int = 0
    var role = ""
    lateinit var settings: SharedPreferences

    private lateinit var databaseRequests: DatabaseRequests

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlatItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)
        databaseRequests =  DatabaseRequests(this@FlatItemActivity)
        fillData(intent.getIntExtra("id", 0))
        if (flat.id_tenant != null) binding.recycleViewTenants.layoutManager = LinearLayoutManager(this@FlatItemActivity)
        binding.update.setOnClickListener(View.OnClickListener { updateFlat() })
        binding.delete.setOnClickListener(View.OnClickListener { deleteFlat() })
        chelUser()
    }

    private fun chelUser() {
        id = settings.getInt("id", 0)
        role = settings.getString("role", "").toString()
        if(role == "tenant"){
            binding.forAdmin.visibility = View.GONE
        }
    }

    private fun fillData(id: Int) {
        flat = databaseRequests.selectFlatFromId(id)

        binding.txtVwNumberFlat.text = flat.flat_number
        binding.txtVwPersonalAccount.text = flat.personal_account
        binding.txtVwTotalArea.text = flat.total_area.toString()
        binding.txtVwUsableArea.text = flat.usable_area.toString()
        binding.txtVwEntranceNumber.text = flat.entrance_number
        binding.txtVwNumberOfRooms.text = flat.number_of_rooms
        binding.txtVwNumberOfResidents.text = flat.number_of_registered_residents.toString()
        binding.txtVwNumberOfOwners.text = flat.number_of_owners.toString()

        if(flat.id_tenant != 0 && flat.id_tenant != null){
            tenants.add(databaseRequests.selectTenantsFromId(flat.id_tenant))
            val adapter = TenantViewAdapter(tenants, this, this)
            binding.recycleViewTenants.adapter = adapter
        }
    }


    private fun updateFlat(){
        val i = Intent(this@FlatItemActivity, WorkFlatActivity::class.java)
        i.putExtra("id",flat.id)
        startActivity(i)
    }

    fun OnClickBtnBack (view: View?){
        val i = Intent(this, FlatsActivity::class.java)
        startActivity(i)
    }

    private fun deleteFlat(){
        val rows = databaseRequests.selectCountFlatsFromCountersAndPayments(flat.id)
        if(rows == 0)
        {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setTitle("Hapus Kos")
            builder.setMessage("Yakin ingin menghapus Kos ini?")

            builder.setPositiveButton(
                "Yes",
                DialogInterface.OnClickListener { dialog, _ ->
                    val cursor = databaseRequests.deleteFlat(flat.id)
                    if (cursor == -1)  Toast.makeText(this@FlatItemActivity, "Kesalahan dalam menghapus data", Toast.LENGTH_SHORT).show()
                    else {
                        Toast.makeText(this@FlatItemActivity, "Kos dihapus", Toast.LENGTH_SHORT).show()
                        val i = Intent(this, FlatsActivity::class.java)
                        startActivity(i)
                    }
                    dialog.dismiss()
                })

            builder.setNegativeButton(
                "No",
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })

            builder.show()
        }
        else
        {
//            Toast.makeText(this@FlatItemActivity, "penghapusan kos tidak dapat dilakukan karena masih terlibat dalam proses penagihan", Toast.LENGTH_SHORT).
//            show()
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setNegativeButton(
                "Оk",
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
            builder.setMessage("penghapusan kos tidak dapat dilakukan karena masih terlibat dalam proses penagihan")
            builder.show()
        }
    }
}
package com.example.manakos.activites.Indication

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import com.example.manakos.activites.Counter.CountersActivity
import com.example.manakos.activites.Counter.WorkCounterActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityCounterItemBinding
import com.example.manakos.databinding.ActivityIndicationItemBinding
import com.example.manakos.databinding.ActivityIndicationsBinding
import com.example.manakos.models.Counter
import com.example.manakos.models.Indication

class IndicationItemActivity : AppCompatActivity() {
    lateinit var binding: ActivityIndicationItemBinding

    private var indication = Indication()
    private lateinit var databaseRequests: DatabaseRequests

    var id: Int = 0
    var role = ""
    lateinit var settings: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIndicationItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseRequests =  DatabaseRequests(this@IndicationItemActivity)
        settings = getSharedPreferences("my_storage", Context.MODE_PRIVATE)

        fillData(intent.getIntExtra("id", 0))
        binding.update.setOnClickListener(View.OnClickListener {
            updateIndication()
        })
        binding.delete.setOnClickListener(View.OnClickListener {
            deleteIndication()
        })

        binding.buttonBack.setOnClickListener(View.OnClickListener {
            val i = Intent(this, IndicationsActivity::class.java)
            startActivity(i)
        })
        chekUser()
    }

    private fun chekUser() {
        id = settings.getInt("id", 0)
        role = settings.getString("role", "").toString()
        if(role == "tenant"){
            binding.forAdmin.visibility = View.GONE
        }
    }

    private fun deleteIndication() {
        val rows = databaseRequests.selectCountFromPayment(indication.period)
        if(rows == 0)
        {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setTitle("Hapus Indikasi")
            builder.setMessage("Apakah anda yakin ingin menghapus indikasi ini?")

            builder.setPositiveButton(
                "Ya",
                DialogInterface.OnClickListener { dialog, which ->
                    val cursor = databaseRequests.deleteIndication(indication.id)
                    if (cursor == -1)  Toast.makeText(this@IndicationItemActivity, "Terjadi Kesalahan saat penghapusan!", Toast.LENGTH_SHORT).show()
                    else {
                        Toast.makeText(this@IndicationItemActivity, "Indikasi dihapus", Toast.LENGTH_SHORT).show()
                        val i = Intent(this, IndicationsActivity::class.java)
                        startActivity(i)
                    }
                    dialog.dismiss()
                })

            builder.setNegativeButton(
                "Tidak",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })

            builder.show()
        }
        else
        {
//            Toast.makeText(this@IndicationItemActivity, "Penghapusan tidak dapat dilakukan karena pencatatan transaksi untuk periode ini telah dibuat!", Toast.LENGTH_SHORT).show()
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setNegativeButton(
                "OK",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            builder.setMessage("Penghapusan tidak dapat dilakukan karena pencatatan transaksi untuk periode ini telah dibuat!")
            builder.show()
        }
    }

    private fun updateIndication() {
        var count = 0
        count += databaseRequests.selectCountFromPayment(indication.period)
        if(count!=0)
        {
//            Toast.makeText(this@IndicationItemActivity, "Perubahan tidak dapat dilakukan karena pencatatan transaksi telah dibuat untuk periode ini.", Toast.LENGTH_SHORT).show()
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setNegativeButton(
                "OK",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
            builder.setMessage("Perubahan tidak dapat dilakukan karena pencatatan transaksi telah dibuat untuk periode ini.")
            builder.show()
        }
        else{
            val i = Intent(this, WorkIndicationActivity::class.java)
            i.putExtra("id", indication.id)
            startActivity(i)
        }
    }

    private fun fillData(id: Int) {
        indication = databaseRequests.selectIndicationFromId(id)

        binding.txtVwNumberFlat.text = databaseRequests.selectFlatNumberFromId(databaseRequests.selectIdFlatFromCounter(indication.id_counter))
        binding.txtVwCounter.text = databaseRequests.selectTypeFromCounter(indication.id_counter)
        binding.txtVwPeriod.text = indication.period
        binding.txtVwValue.text = indication.value.toString()
    }
}

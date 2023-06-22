package com.example.manakos.activites.Flat

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.manakos.activites.Tenant.ListTenantsActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityWorkFlatBinding
import com.example.manakos.models.Flat
import com.google.gson.Gson

class WorkFlatActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorkFlatBinding
    lateinit var flat: Flat
    private lateinit var databaseRequests: DatabaseRequests

    override fun onCreate(savedInstanceState: Bundle?) {
        flat = Flat()
        super.onCreate(savedInstanceState)
        binding = ActivityWorkFlatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseRequests =  DatabaseRequests(this@WorkFlatActivity)


        val str = intent.getStringExtra("flat")
        fillData(intent.getIntExtra("id", 0), str)

        binding.buttonBack.setOnClickListener(View.OnClickListener { (transition()) })
        binding.buttonWork.setOnClickListener(View.OnClickListener { workFlat() })

        //val separator: Char = DecimalFormatSymbols.getInstance().getDecimalSeparator()
        //binding.editTextTotalArea.setKeyListener(DigitsKeyListener.getInstance("0123456789" + separator))
    }

    private fun workFlat() {
        flat.flat_number = binding.editTextNumberFlat.text.toString()
        flat.personal_account = binding.editTextPersonalAccount.text.toString()
        //flat.total_area = binding.editTextTotalArea.toString().replace(',','.').toFloat()
        //flat.usable_area = binding.editTextUsableArea.toString().replace(',','.').toFloat()
        try {
            flat.total_area = binding.editTextTotalArea.text.toString().toFloat()
        }
        catch (_: Exception){}
        try {
            flat.usable_area = binding.editTextUsableArea.text.toString().toFloat()
        }
        catch (_: Exception){}
        flat.entrance_number = binding.editEntranceNumber.text.toString()
        flat.number_of_rooms = binding.editNumberOfRoom.text.toString()
        try {
            flat.number_of_registered_residents = binding.editNumberOfResidents.text.toString().toInt()
        }
        catch (_: Exception){}
        try {
            flat.number_of_owners = binding.editNumberOfOwners.text.toString().toInt()
        }
        catch (_: Exception){}

        var error: String? = null

        if(binding.editNumberOfOwners.text.toString().length < 1 || flat.number_of_owners == 0) error = "number_of_owners"
        if(binding.editNumberOfResidents.text.toString().length < 1) error = "number_of_registered_residents"
        if(flat.number_of_rooms.length < 1) error = "number_of_rooms"
        if(flat.entrance_number.length < 1) error = "entrance_number"
        if(binding.editTextUsableArea.text.toString().length < 1 || (flat.usable_area > flat.total_area)) error = "usable_area"
        if(binding.editTextTotalArea.text.toString().length < 1) error = "total_area"
        if(flat.personal_account.length < 8) error = "personal_account"
        if(flat.flat_number.length < 1) error = "flat"


        var count = 0
        if (flat.id == null || flat.id == 0) count = databaseRequests.selectFlatsWhereNumberFlatAndPersonalAccount(flat.flat_number, flat.personal_account)
        else count = databaseRequests.selectFlatsWhereNumberFlatAndPersonalAccountNotId(flat.id, flat.flat_number, flat.personal_account)
        if(count != 0) error = "count"

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("Ошибка")
        builder.setNegativeButton(
            "Ок",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })

        if(error == null)
        {
            val i = Intent(this@WorkFlatActivity, ListTenantsActivity::class.java)
            i.putExtra("flat", Gson().toJson(flat))
            startActivity(i)
        }
        else{
            when(error){
                "count" ->
                    builder.setMessage("Kos dengan nomor atau Token Listrik tersebut sudah ada.")
                "number_of_owners" ->
                    builder.setMessage("Input jumlah pemilik rumah salah: panjang string tidak kurang dari 1, nilai string tidak bisa 0")
                "number_of_registered_residents" ->
                    builder.setMessage("Input jumlah penghuni terdaftar salah: panjang string tidak kurang dari 1.")
                "number_of_rooms" ->
                    builder.setMessage("Input jumlah kamar salah: panjang string tidak kurang dari 1.")
                "entrance_number" ->
                    builder.setMessage("Input nomor pintu masuk salah: panjang string tidak kurang dari 1.")
                "usable_area" ->
                    builder.setMessage("Input yang dapat digunakan tidak valid: panjang string minimal 1.")
                "total_area" ->
                    builder.setMessage("Entri total area yang salah: panjang garis minimal 1, total area tidak boleh kurang dari area yang dapat digunakan.")
                "personal_account" ->
                    builder.setMessage("Input Token Listrik salah: panjang string tidak kurang dari 8.")
                "flat" ->
                    builder.setMessage("Input nomor Kos salah: panjang string tidak kurang dari 1.")
            }
            builder.show()
        }
    }

    private fun transition() {
        if (flat.id == 0 || flat.id == null) {
            val i = Intent(this, FlatsActivity::class.java)
            startActivity(i)
        } else {
            val i = Intent(this, FlatItemActivity::class.java)
            i.putExtra("id", flat.id)
            startActivity(i)
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun fillData(id: Int, str: String?) {
        if(str == null){
            if(id != 0){
                binding.textView.text = "Pengeditan"
                flat = databaseRequests.selectFlatFromId(id)

                binding.editTextNumberFlat.setText(flat.flat_number)
                binding.editTextPersonalAccount.setText(flat.personal_account)
                binding.editTextTotalArea.setText(flat.total_area.toString())
                binding.editTextUsableArea.setText(flat.usable_area.toString())
                binding.editEntranceNumber.setText(flat.entrance_number)
                binding.editNumberOfRoom.setText(flat.number_of_rooms)
                binding.editNumberOfResidents.setText(flat.number_of_registered_residents.toString())
                binding.editNumberOfOwners.setText(flat.number_of_owners.toString())
            }
            else{
                binding.textView.text = "Create"
            }
        }
        else
        {
            flat = Gson().fromJson(str, Flat::class.javaObjectType)
            if(flat.id != null) binding.textView.text = "Pengeditan"
            else binding.textView.text = "Create"

                binding.editTextNumberFlat.setText(flat.flat_number)
                binding.editTextPersonalAccount.setText(flat.personal_account)
                binding.editTextTotalArea.setText(flat.total_area.toString())
                binding.editTextUsableArea.setText(flat.usable_area.toString())
                binding.editEntranceNumber.setText(flat.entrance_number)
                binding.editNumberOfRoom.setText(flat.number_of_rooms)
                binding.editNumberOfResidents.setText(flat.number_of_registered_residents.toString())
                binding.editNumberOfOwners.setText(flat.number_of_owners.toString())
        }
    }
}

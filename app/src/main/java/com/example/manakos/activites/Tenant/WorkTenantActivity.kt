package com.example.manakos.activites.Tenant

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityWorkTenantBinding
import com.example.manakos.models.Tenant
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class WorkTenantActivity : AppCompatActivity() {
    lateinit var binding: ActivityWorkTenantBinding
    lateinit var tenant: Tenant

    private lateinit var databaseRequests: DatabaseRequests

    val myCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        tenant = Tenant()
        super.onCreate(savedInstanceState)
        binding = ActivityWorkTenantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseRequests = DatabaseRequests(this@WorkTenantActivity)

        binding.editTextDate.setOnClickListener(View.OnClickListener {
            val date =
                OnDateSetListener { view, year, month, day ->
                    myCalendar.set(Calendar.YEAR, year)
                    myCalendar.set(Calendar.MONTH, month)
                    myCalendar.set(Calendar.DAY_OF_MONTH, day)
                    updateLabel()
                }
            DatePickerDialog(
                this@WorkTenantActivity,
                date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        })

        fillData(intent.getIntExtra("id", 0))

        binding.buttonWork.setOnClickListener(View.OnClickListener {
            workTenant()
        })

        binding.buttonBack.setOnClickListener(View.OnClickListener { (transition()) })
    }

    private fun fillData(id: Int) {
        tenant.id_tenant = id
        if (id != 0) {
            binding.textView.text = "Edit"
            binding.buttonWork.text = "Ubah"
            tenant = databaseRequests.selectTenantsFromId(id)

            binding.editTextFullName.setText(tenant.full_name)
            binding.editTextDate.setText(tenant.date_of_registration.toString())
            binding.editTextNumberOfFamilyMembers.setText(tenant.number_of_family_members.toString())
            binding.editTextPhoneNumber.setText(tenant.phone_number)
            binding.editTextEmail.setText(tenant.email)
        } else {
            binding.textView.text = "Tambah"
            binding.buttonWork.text = "Tambahkan"
        }
    }

    private fun updateLabel() {
        val myFormat = "dd-MM-yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale("ru"))
        binding.editTextDate.setText(dateFormat.format(myCalendar.time))
    }

    private fun workTenant() {
        val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        tenant.full_name = binding.editTextFullName.text.toString()
        try {
            tenant.date_of_registration = LocalDate.parse(binding.editTextDate.text.toString(), dtf)
        } catch (_: Exception) {
        }
        tenant.number_of_family_members = binding.editTextNumberOfFamilyMembers.text.toString()
        tenant.email = binding.editTextEmail.text.toString()
        tenant.phone_number = binding.editTextPhoneNumber.text.toString()

        var error: String? = null

        if(tenant.email.isNotEmpty() && !isEmailValid(tenant.email)) error = "email"
        if(tenant.full_name.length < 2) error = "full_name"
        if(tenant.phone_number.isEmpty()) error = "phone_number"
        if(tenant.date_of_registration == null) error = "date_of_registration"
        if(tenant.number_of_family_members.toString().isEmpty()) error = "number_of_family_members"

        var count = 0
        if (tenant.id_tenant == null || tenant.id_tenant == 0) {
            if(tenant.email.equals("")) count += databaseRequests.selectTenantsWherePhoneNumber(tenant.phone_number)
            else count += databaseRequests.selectTenantsWherePhoneNumberAndEmail(tenant.phone_number, tenant.email)
        }
        else {
            if(tenant.email.equals("")) count += databaseRequests.selectTenantsWherePhoneNumberNotId(tenant.id_tenant, tenant.phone_number)
            else count += databaseRequests.selectTenantsWherePhoneNumberAndEmailNotId(tenant.id_tenant, tenant.phone_number, tenant.email)
        }
        if(count != 0) error = "count"

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
        builder.setNegativeButton(
            "Оk",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })

        if(error == null)
        {
            if(tenant.id_tenant != null && tenant.id_tenant != 0) databaseRequests.updateTenant(tenant)
            else databaseRequests.createTenant(tenant)
            transition()
        }
        else{
            when(error){
                "count" ->
                    builder.setMessage("Penyewa dengan email atau nomor telepon tersebut sudah ada.")
                "email" ->
                    builder.setMessage("Input email tidak valid: tidak sesuai dengan format email.")
                "phone_number" ->
                    builder.setMessage("Input nomor telepon tidak valid: panjang string harus 11 karakter.")
                "number_of_family_members" ->
                    builder.setMessage("Input jumlah anggota keluarga tidak valid: panjang string minimal 1 karakter.")
                "date_of_registration" ->
                    builder.setMessage("Input tanggal pendaftaran tidak valid: nilai tidak ada.")
                "full_name" ->
                    builder.setMessage("Input Nama Lengkap tidak valid: panjang string minimal 2 karakter.")
            }
            builder.show()
        }
    }

    fun isEmailValid(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }


    private fun transition() {
        if (tenant.id_tenant == 0 || tenant.id_tenant == null) {
            val i = Intent(this, TenantsActivity::class.java)
            startActivity(i)
        } else {
            val i = Intent(this, TenantItemActivity::class.java)
            i.putExtra("id", tenant.id_tenant)
            startActivity(i)
        }
    }
}


package com.example.manakos.activites.Payment

//noinspection SuspiciousImport
import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.manakos.database.DatabaseRequests
import com.example.manakos.databinding.ActivityAddPaymentsBinding
import com.example.manakos.models.Counter
import com.example.manakos.models.Flat
import com.example.manakos.models.Indication
import com.example.manakos.models.Payment
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.math.RoundingMode
import java.time.LocalDate


class AddPaymentsActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddPaymentsBinding

    private lateinit var payment : Payment
    private lateinit var databaseRequests: DatabaseRequests

    private var periods = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPaymentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        databaseRequests =  DatabaseRequests(this@AddPaymentsActivity)
        payment = Payment()
        binding.buttonBack.setOnClickListener(View.OnClickListener {
            val i = Intent(this, PaymentsActivity::class.java)
            startActivity(i)
        })
        binding.buttonWork.setOnClickListener(View.OnClickListener {
            if(intent.getIntExtra("report", 0) == 1)
                createReport()
            else addPayments()
        })
        fillPeriod()
    }

    private fun createReport() {
        val workbook = HSSFWorkbook()
        val sheet = workbook.createSheet("Pendapatan")
        val payments = databaseRequests.selectPaymentsFromPeriod(payment.period)

        var rowNum = 0

        val row: Row = sheet.createRow(rowNum)
        row.createCell(0).setCellValue("Periode")
        row.createCell(1).setCellValue("Fasilitas")
        row.createCell(2).setCellValue("Nomor Kos")
        row.createCell(3).setCellValue("Total")
        row.createCell(4).setCellValue("Status Pembayaran")

        // Mengisi lembar dengan data
        for (payment_item in payments) {
            createSheetHeader(sheet, ++rowNum, payment_item)
        }

        // Menulis dokumen Excel yang telah dibuat di memori ke file
        try {
            FileOutputStream(File(getExternalPath().path)).use { out -> workbook.write(out) }
            Toast.makeText(this, "Laporan berhasil disimpan di folder Unduhan", Toast.LENGTH_SHORT).show()
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "File tidak ditemukan", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(this, "Tidak diizinkan untuk mengakses penyimpanan", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, "Gagal membuat laporan", Toast.LENGTH_SHORT).show()
        }


        val i = Intent(this, PaymentsActivity::class.java)
        startActivity(i)
    }


    private fun getExternalPath(): File {
        return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path, "Laporan Tagihan yang belum dibayar.xls")
    }

    private fun createSheetHeader(sheet: HSSFSheet, rowNum: Int, payment: Payment) {
        val row = sheet.createRow(rowNum)

        row.createCell(0).setCellValue(payment.period)
        row.createCell(1).setCellValue(databaseRequests.selectNameRateFromId(payment.id_rate))
        row.createCell(2).setCellValue(databaseRequests.selectFlatNumberFromId(payment.id_flat))
        row.createCell(3).setCellValue(payment.amount.toString().toDouble())
        if(payment.status == true) row.createCell(4).setCellValue("Dibayar")
        else row.createCell(4).setCellValue("Не Dibayar")
    }

    private fun fillPeriod() {
        val date = LocalDate.now()
        val monthNames = arrayOf("Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember")
        var period = ""
        period = if (date.monthValue - 1 == 0)
            (monthNames[11] + " " + (date.year - 1))
        else
            (monthNames[date.monthValue - 2] + " " + (date.year))
        payment.period = period
        periods = databaseRequests.selectPeriodsFromPayments()
        if (databaseRequests.selectCountPaymentsWherePeriod(period) == 0)
            periods.add(period)
        binding.editPeriod.adapter = ArrayAdapter(this, R.layout.simple_list_item_1, periods)
        binding.editPeriod.setSelection(periods.indexOf(period))
        binding.editPeriod.isEnabled = false

        if (intent.getIntExtra("report", 0) == 1) {
            binding.buttonWork.text = "Buat Laporan"
            binding.textViewP.text = "Membuat Laporan"
            binding.textView.text = "tentang Pembayaran"
            binding.editPeriod.isEnabled = true
        }
    }


    private fun addPayments() {
        val count = databaseRequests.selectCountPaymentsWherePeriod(payment.period)
        if (count != 0) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setNegativeButton(
                "Ok",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                }
            )
            builder.setMessage("Tidak dapat menambahkan pembayaran baru karena pembayaran untuk periode ini sudah dilakukan!")
            builder.show()
        }
        else{
//////////////////////////------------------------------------------------------------------------------------------------------------

            val flats = databaseRequests.selectFlats()
            val rates = databaseRequests.selectRates()
            val normatives = databaseRequests.selectNormatives()

            if (flats.size != 0) {
                for (flat: Flat in flats) {
                    val counters = databaseRequests.selectCountersWhereUsedAndIdFlat(flat.id)

                    val indications = ArrayList<Indication>()

                    for(counter: Counter in counters) {
                        indications.add(databaseRequests.selectIndicationFromCounterWherePeriod(counter.id, payment.period))
                    }

                    val payment_cw = Payment()
                    val payment_hw = Payment()
                    val payment_ee = Payment()
                    val payment_g = Payment()
                    val payment_te = Payment()
                    var count_cw = 0
                    var count_hw = 0
                    var count_ee = 0
                    var count_g = 0
                    var count_te = 0
                    var sum_ind_cw = 0f
                    var sum_noind_cw = 0f
                    var sum_ind_hw = 0f
                    var sum_noind_hw = 0f
                    var sum_ind_ee = 0f
                    var sum_noind_ee = 0f
                    var sum_ind_g = 0f
                    var sum_noind_g = 0f
                    var sum_ind_te = 0f
                    val sum_noind_te = 0f
                    for (counter: Counter in counters) {
                        when (counter.type) {
                            "Sistem Pengukuran Air Dingin" -> {
                                val rate = rates.first { it.name == "Air Dingin" }
                                val normative = normatives.first { it.name == "Air Dingin" }

                                val indication = indications.firstOrNull { it.id_counter == counter.id }
                                payment_cw.period = payment.period
                                payment_cw.status = false
                                payment_cw.cheque = null
                                payment_cw.id_flat = flat.id
                                payment_cw.id_rate = rate.id
                                payment_cw.id_normative = normative.id
                                if (indication==(null)) {
                                    if (flat.number_of_registered_residents != 0) sum_noind_cw = rate.value * normative.value * flat.number_of_registered_residents
                                    else sum_noind_cw = rate.value * normative.value * flat.number_of_owners
                                    count_cw++
                                } else {
                                    sum_ind_cw += indication.value * rate.value
                                }
                            }
                            "Sistem Pengukuran Air Panas" -> {
                                val rate = rates.first { it.name == "Air Panas" }
                                val normative = normatives.first { it.name == "Air Panas" }

                                val indication = indications.firstOrNull { it.id_counter == counter.id }
                                payment_hw.period = payment.period
                                payment_hw.status = false
                                payment_hw.cheque = null
                                payment_hw.id_flat = flat.id
                                payment_hw.id_rate = rate.id
                                payment_hw.id_normative = normative.id

                                if (indication == null) {
                                        if (flat.number_of_registered_residents != 0) sum_noind_hw = rate.value * normative.value * flat.number_of_registered_residents
                                        else sum_noind_hw = rate.value * normative.value * flat.number_of_owners
                                        count_hw++
                                    } else {
                                        sum_ind_hw += indication.value * rate.value
                                    }
                            }
                            "Sistem Pengukuran Listrik" -> {
                                val rate = rates.first { it.name == "Listrik" }
                                val normative = normatives.first { it.name == "Listrik" }

                                val indication = indications.firstOrNull { it.id_counter == counter.id }
                                payment_ee.period = payment.period
                                payment_ee.status = false
                                payment_ee.cheque = null
                                payment_ee.id_flat = flat.id
                                payment_ee.id_rate = rate.id
                                payment_ee.id_normative = normative.id

                                if (indication==(null)) {
                                    if (flat.number_of_registered_residents != 0) sum_noind_ee = rate.value * normative.value * flat.number_of_registered_residents
                                    else sum_noind_ee = rate.value * normative.value * flat.number_of_owners
                                    count_ee++
                                } else {
                                    sum_ind_ee += indication.value * rate.value
                                }
                            }
                            "Sistem Pengukuran Gas" -> {
                                val rate = rates.first { it.name == "Gas" }
                                val normative = normatives.first { it.name == "Gas" }

                                val indication =
                                    indications.firstOrNull { it.id_counter == counter.id }
                                payment_g.period = payment.period
                                payment_g.status = false
                                payment_g.cheque = null
                                payment_g.id_flat = flat.id
                                payment_g.id_rate = rate.id
                                payment_g.id_normative = normative.id

                                if (indication==(null)) {
                                    if (flat.number_of_registered_residents != 0) sum_noind_g = rate.value * normative.value * flat.number_of_registered_residents
                                    else sum_noind_g = rate.value * normative.value * flat.number_of_owners
                                    count_g++
                                } else {
                                    sum_ind_g += indication.value * rate.value
                                }
                            }
                            "Sistem Pengukuran Pemanasan" -> {
                                val rate = rates.first { it.name == "Energi Panas" }
                                val normative = normatives.first { it.name == "Energi Panas" }

                                val indication =
                                    indications.firstOrNull { it.id_counter == counter.id }
                                payment_te.period = payment.period
                                payment_te.status = false
                                payment_te.cheque = null
                                payment_te.id_flat = flat.id
                                payment_te.id_rate = rate.id
                                payment_te.id_normative = normative.id

                                if (indication==(null)) {
                                    sum_ind_te = normative.value * flat.usable_area
                                    count_te++
                                } else {
                                    sum_ind_te += indication.value * rate.value
                                }
                            }
                        }
                    }
                    var found = 0
                    found = Math.toIntExact(counters.stream().filter { x -> x.type.equals("Meteran air dingin") }.count())
                    if (found == 0) {
                        val rate = rates.first { it.name == "Air dingin" }
                        val normative = normatives.first { it.name == "Air dingin" }

                        var amount: Float? = null
                        if (flat.number_of_registered_residents != 0)
                            amount = rate.value * normative.value * flat.number_of_registered_residents
                        else amount = rate.value * normative.value * flat.number_of_owners
                        amount = amount.toBigDecimal().setScale(2, RoundingMode.UP).toFloat()

                        payment.period = payment.period
                        payment.status = false
                        payment.cheque = null
                        payment.id_flat = flat.id
                        payment.id_rate = rate.id
                        payment.id_normative = normative.id
                        payment.amount = amount
                        databaseRequests.createPayment(payment)
                    } else {
                        if (count_cw != 0) payment_cw.amount = (sum_ind_cw + (sum_noind_cw / count_cw)).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                        else payment_cw.amount = (sum_ind_cw).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                        databaseRequests.createPayment(payment_cw)
                    }
                    found = 0
                    found = Math.toIntExact(counters.stream().filter { x -> x.type.equals("Meteran air panas")
                    }.count())
                    if (found == 0) {
                        val rate = rates.filter { it.name == "Air panas" }.first()
                        val normative = normatives.filter { it.name == "Air panas" }.first()

                        var amount: Float? = null
                        if (flat.number_of_registered_residents != 0)
                            amount = rate.value * normative.value * flat.number_of_registered_residents
                        else amount = rate.value * normative.value * flat.number_of_owners
                        amount = amount.toBigDecimal().setScale(2, RoundingMode.UP).toFloat()

                        payment.period = payment.period
                        payment.status = false
                        payment.cheque = null
                        payment.id_flat = flat.id
                        payment.id_rate = rate.id
                        payment.id_normative = normative.id
                        payment.amount = amount
                        databaseRequests.createPayment(payment)
                    } else {
                        if (count_hw != 0) payment_hw.amount = (sum_ind_hw + (sum_noind_hw / count_hw)).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                        else payment_hw.amount = (sum_ind_hw).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                        databaseRequests.createPayment(payment_hw)
                    }
                    found = 0
                    found = Math.toIntExact(counters.stream().filter { x -> x.type.equals("Meteran listrik") }.count())
                    if (found == 0) {
                        val rate = rates.filter { it.name == "Listrik" }.first()
                        val normative = normatives.filter { it.name == "Listrik" }.first()

                        var amount: Float? = null
                        if (flat.number_of_registered_residents != 0)
                            amount = rate.value * normative.value * flat.number_of_registered_residents
                        else amount = rate.value * normative.value * flat.number_of_owners
                        amount = amount.toBigDecimal().setScale(2, RoundingMode.UP).toFloat()

                        payment.period = payment.period
                        payment.status = false
                        payment.cheque = null
                        payment.id_flat = flat.id
                        payment.id_rate = rate.id
                        payment.id_normative = normative.id
                        payment.amount = amount
                        databaseRequests.createPayment(payment)
                    } else {
                        if (count_ee != 0) payment_ee.amount = (sum_ind_ee + (sum_noind_ee / count_ee)).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                        else payment_ee.amount = (sum_ind_ee).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                        databaseRequests.createPayment(payment_ee)
                    }
                    found = 0
                    found = Math.toIntExact(counters.stream().filter { x -> x.type.equals("Meteran gas") }.count())
                    if (found == 0) {
                        val rate = rates.filter { it.name == "Gas" }.first()
                        val normative = normatives.filter { it.name == "Gas" }.first()

                        var amount: Float? = null
                        if (flat.number_of_registered_residents != 0)
                            amount = rate.value * normative.value * flat.number_of_registered_residents
                        else amount = rate.value * normative.value * flat.number_of_owners
                        amount = amount.toBigDecimal().setScale(2, RoundingMode.UP).toFloat()

                        payment.period = payment.period
                        payment.status = false
                        payment.cheque = null
                        payment.id_flat = flat.id
                        payment.id_rate = rate.id
                        payment.id_normative = normative.id
                        payment.amount = amount
                        databaseRequests.createPayment(payment)
                    } else {
                        if (count_g != 0) payment_g.amount = (sum_ind_g + (sum_noind_g / count_g)).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                        else payment_g.amount = (sum_ind_g).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                        databaseRequests.createPayment(payment_g)
                    }
                    found = 0
                    found = Math.toIntExact(counters.stream().filter { x -> x.type.equals("Meteran pemanas") }.count())
                    if (found == 0) {
                        val rate = rates.filter { it.name == "Energi Panas" }.first()
                        val normative = normatives.filter { it.name == "Energi Panas" }.first()

                        var amount: Float = normative.value * flat.usable_area
                        amount = amount.toBigDecimal().setScale(2, RoundingMode.UP).toFloat()

                        payment.period = payment.period
                        payment.status = false
                        payment.cheque = null
                        payment.id_flat = flat.id
                        payment.id_rate = rate.id
                        payment.id_normative = normative.id
                        payment.amount = amount
                        databaseRequests.createPayment(payment)
                    } else {
                        if (count_te != 0) payment_te.amount = (sum_ind_te + (sum_noind_te / count_te)).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                        else payment_te.amount = (sum_ind_te).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
                        databaseRequests.createPayment(payment_te)
                    }
                }
            }
            Toast.makeText(this, "Perhitungan dilakukan", Toast.LENGTH_SHORT).show()
            val i = Intent(this, PaymentsActivity::class.java)
            startActivity(i)
//////////////////////////----------------------------------------------------------------------------------------------------------------
        }
    }
}
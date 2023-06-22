package com.example.manakos.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.manakos.models.*
import java.io.IOException
import java.sql.SQLException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DatabaseRequests(private var context: Context) {
    private var mDBHelper: DatabaseHelper
    private var mDb: SQLiteDatabase

    init {
        mDBHelper = DatabaseHelper(context)
        try {
            mDBHelper.updateDataBase()
        } catch (mIOException: IOException) {
            throw Error("Tidak dapat memperbarui database")
        }
        mDb = try {
            mDBHelper.getWritableDatabase()
        } catch (mSQLException: SQLException) {
            throw mSQLException
        }

    }

    fun selectTenants(): ArrayList<Tenant> {
        val tenants = ArrayList<Tenant>();
        val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val cursor = mDb.rawQuery("SELECT * FROM tenant", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            val tenant = Tenant(cursor.getInt(0), cursor.getString(1), LocalDate.parse(cursor.getString(2), dtf), cursor.getString(3), cursor.getString(6), cursor.getString(4), cursor.getString(5))
            tenants.add(tenant)
            cursor.moveToNext()
        }
        return tenants
    }

    fun selectTenantsFromId(id:Int): Tenant {
        var tenant = Tenant()
        val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM tenant WHERE id = $id", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            tenant = Tenant(cursor.getInt(0), cursor.getString(1), LocalDate.parse(cursor.getString(2), dtf), cursor.getString(3), cursor.getString(6), cursor.getString(4), cursor.getString(5))
            cursor.moveToNext()
        }
        return tenant
    }

    fun deleteTenant(id: Int): Int {
        val cursor = mDb.delete("tenant", "id=?",arrayOf<String>(id.toString()))
        return cursor
    }

    fun selectCountTenantsFromFlats(id: Int): Int {
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM flat WHERE id_tenant = " + id, null)
        return cursor.count
    }

    fun selectTenantsWherePhoneNumber(phoneNumber: String): Int {
        val cursor = mDb.rawQuery("SELECT * FROM tenant WHERE phone_number = '$phoneNumber'", null)
        return cursor.count
    }

    fun selectTenantsWherePhoneNumberAndEmail(phoneNumber: String, email: String): Int {
        val cursor = mDb.rawQuery("SELECT * FROM tenant WHERE phone_number = '" +  phoneNumber + "'" +
                " or email = '" + email + "'"  , null)
        return cursor.count
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createTenant(tenant: Tenant){
        val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val cv: ContentValues = ContentValues()
        cv.put("full_name", tenant.full_name)
        cv.put("date_of_registration", tenant.date_of_registration.format(dtf))
        cv.put("number_of_family_members", tenant.number_of_family_members)
        cv.put("phone_number", tenant.phone_number)
        cv.put("email", tenant.email)

        val cursor: Long = mDb.insert("tenant", null, cv)

        val l: Long = -1
        if(cursor == l){
            Toast.makeText(context, "Gagal menambahkan ke database!", Toast.LENGTH_SHORT).
            show()
        }
        else {
            Toast.makeText(context, "Penyewa ditambahkan", Toast.LENGTH_SHORT).show()
        }
    }

    fun selectTenantsWherePhoneNumberNotId(id:Int, phoneNumber: String): Int {

        val cursor = mDb.rawQuery("SELECT * FROM tenant WHERE phone_number = '$phoneNumber' and id != $id", null)
        return cursor.count
    }

    fun selectTenantsWherePhoneNumberAndEmailNotId(id:Int, phoneNumber: String, email: String): Int {
        val cursor = mDb.rawQuery("SELECT * FROM tenant WHERE (phone_number = '" +  phoneNumber + "'" +
                " or email = '" + email + "') and id != " + id   , null)
        return cursor.count
    }


    fun updateTenant(tenant: Tenant){
        val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val cv: ContentValues = ContentValues()
        cv.put("full_name", tenant.full_name)
        cv.put("date_of_registration", tenant.date_of_registration.format(dtf))
        cv.put("number_of_family_members", tenant.number_of_family_members)
        cv.put("phone_number", tenant.phone_number)
        cv.put("email", tenant.email)

        val cursor: Int = mDb.update("tenant", cv, "id=?",arrayOf<String>(tenant.id_tenant.toString()))
        if(cursor == -1){
            Toast.makeText(context, "Gagal mengubah ke database!", Toast.LENGTH_SHORT).
            show()
        }
        else {
            Toast.makeText(context, "Penyewa diperbarui", Toast.LENGTH_SHORT).show()
        }
    }

    fun selectFlats(): ArrayList<Flat> {
        val flats = ArrayList<Flat>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM flat", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val flat = Flat(cursor.getInt(0), cursor.getString(2),cursor.getString(1),cursor.getFloat(3),cursor.getFloat(4),cursor.getString(5),cursor.getString(6),cursor.getInt(7),
                cursor.getInt(8), cursor.getInt(9))
            flats.add(flat)
            cursor.moveToNext()
        }
        return flats
    }

    fun selectFlatFromId(id:Int): Flat {
        var flat = Flat()
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM flat WHERE id = $id", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            flat = Flat(cursor.getInt(0), cursor.getString(2),cursor.getString(1),cursor.getFloat(3),cursor.getFloat(4),cursor.getString(5),cursor.getString(6),cursor.getInt(7), cursor.getInt(8), cursor.getInt(9))
            cursor.moveToNext()
        }
        return flat
    }

    fun deleteFlat(id: Int): Int {
        val cursor = mDb.delete("flat", "id=?",arrayOf<String>(id.toString()))
        return cursor
    }

    fun selectCountFlatsFromCountersAndPayments(id: Int): Int {
        var count = 0
        var cursor: Cursor = mDb.rawQuery("SELECT * FROM counter WHERE id_flat = $id", null)
        count += cursor.count
        cursor = mDb.rawQuery("SELECT * FROM payment WHERE id_flat = $id", null)
        return (count + cursor.count)
    }

    fun selectFlatsWhereNumberFlatAndPersonalAccount(flatNumber: String, personalAccount: String): Int {
        val cursor = mDb.rawQuery("SELECT * FROM flat WHERE (flat_number = '" +  flatNumber + "'" +
                " or personal_account = '" + personalAccount + "') "   , null)
        return cursor.count
    }

    fun selectFlatsWhereNumberFlatAndPersonalAccountNotId(id: Int, flatNumber: String, personalAccount: String): Int {
        val cursor = mDb.rawQuery("SELECT * FROM flat WHERE (flat_number = '" +  flatNumber + "'" +
                " or personal_account = '" + personalAccount + "') and id != " + id   , null)
        return cursor.count
    }

    fun createFlat(flat: Flat){
        val cv: ContentValues = ContentValues()
        cv.put("flat_number", flat.flat_number)
        cv.put("personal_account", flat.personal_account)
        cv.put("total_area", flat.total_area)
        cv.put("usable_area", flat.usable_area)
        cv.put("entrance_number", flat.entrance_number)
        cv.put("number_of_rooms", flat.number_of_rooms)
        cv.put("number_of_registered_residents", flat.number_of_registered_residents)
        cv.put("number_of_owners", flat.number_of_owners)
        cv.put("id_tenant", flat.id_tenant)


        val cursor: Long = mDb.insert("flat", null, cv)

        val l: Long = -1
        if(cursor == l){
            Toast.makeText(context, "Gagal menambahkan ke database!", Toast.LENGTH_SHORT).
            show()
        }
        else {
            Toast.makeText(context, "Kamar ditambahkan", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateFlat(flat: Flat){
        val cv: ContentValues = ContentValues()
        cv.put("flat_number", flat.flat_number)
        cv.put("personal_account", flat.personal_account)
        cv.put("total_area", flat.total_area)
        cv.put("usable_area", flat.usable_area)
        cv.put("entrance_number", flat.entrance_number)
        cv.put("number_of_rooms", flat.number_of_rooms)
        cv.put("number_of_registered_residents", flat.number_of_registered_residents)
        cv.put("number_of_owners", flat.number_of_owners)
        cv.put("id_tenant", flat.id_tenant)

        val cursor: Int = mDb.update("flat", cv, "id=?",arrayOf<String>(flat.id.toString()))

        if(cursor == -1){
            Toast.makeText(context, "Gagal mengubah ke database!", Toast.LENGTH_SHORT).
            show()
        }
        else {
            Toast.makeText(context, "Kamar diperbarui", Toast.LENGTH_SHORT).show()
        }
    }

    fun selectFlatNumberFromId(id: Int): String {
        val cursor = mDb.rawQuery("SELECT * FROM flat WHERE id = $id", null)
        var number = ""
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            number = cursor.getString(1)
            cursor.moveToNext()
        }
        return number
    }

    fun selectCounters(): ArrayList<Counter> {
        val counters = ArrayList<Counter>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM counter", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            val counter = Counter(cursor.getInt(0), cursor.getString(1),cursor.getString(2),cursor.getString(3).toBoolean(), cursor.getInt(4))
            counters.add(counter)
            cursor.moveToNext()
        }
        return counters
    }

    fun selectFlatsFromIdTenant(id: Int): ArrayList<Flat> {
        val flats = ArrayList<Flat>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM flat WHERE id_tenant = $id", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            flats.add(Flat(cursor.getInt(0), cursor.getString(2),cursor.getString(1),cursor.getFloat(3),cursor.getFloat(4),cursor.getString(5),cursor.getString(6),cursor.getInt(7), cursor.getInt(8), cursor.getInt(9)))
            cursor.moveToNext()
        }
        return flats
    }

    fun selectCounterFromId(id: Int): Counter {
        var counter = Counter()
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM counter WHERE id = $id", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            counter = Counter(cursor.getInt(0), cursor.getString(1),cursor.getString(2),cursor.getString(3).toBoolean(), cursor.getInt(4))
            cursor.moveToNext()
        }
        return counter
    }

    fun selectCountersFromIndications(id: Int): Int {
        val cursor = mDb.rawQuery("SELECT * FROM indication WHERE id_counter = $id", null)
        return cursor.count
    }

    fun deleteCounter(id: Int): Int {
        val cursor = mDb.delete("counter", "id=?",arrayOf<String>(id.toString()))
        return cursor
    }

    fun selectCountCountersWhereNumberAndType( counterNumber: String, type: String): Int {
        val cursor = mDb.rawQuery("SELECT * FROM counter WHERE (number = '" +  counterNumber + "'" +
                " and type = '" + type + "')" , null)
        return cursor.count
    }

    fun selectCountCountersWhereNumberAndTypeNotId(id: Int, counterNumber: String, type: String): Int {
        val cursor = mDb.rawQuery("SELECT * FROM counter WHERE (number = '" +  counterNumber + "'" +
                " and type = '" + type + "') and id != " + id , null)
        return cursor.count
    }

    fun createCounter(counter: Counter){
        val cv: ContentValues = ContentValues()
        cv.put("number", counter.number)
        cv.put("type", counter.type)
        cv.put("used", counter.used.toString())
        cv.put("id_flat", counter.id_flat)

        val cursor: Long = mDb.insert("counter", null, cv)

        val l: Long = -1
        if(cursor == l){
            Toast.makeText(context, "Gagal menambahkan ke database!", Toast.LENGTH_SHORT).
            show()
        }
        else {
            Toast.makeText(context, "Counter ditambahkan", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateCounter(counter: Counter){
        val cv: ContentValues = ContentValues()
        cv.put("number", counter.number)
        cv.put("type", counter.type)
        cv.put("used", counter.used.toString())
        cv.put("id_flat", counter.id_flat)

        val cursor: Int = mDb.update("counter", cv, "id=?",arrayOf<String>(counter.id.toString()))

        if(cursor == -1){
            Toast.makeText(context, "Gagal mengubah ke database!", Toast.LENGTH_SHORT).
            show()
        }
        else {
            Toast.makeText(context, "Counter diperbarui", Toast.LENGTH_SHORT).show()
        }
    }

    fun selectIdFlatFromCounter(id: Int): Int {
        val cursor = mDb.rawQuery("SELECT id_flat FROM counter WHERE id = $id", null)
        var id_flat = 0
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            id_flat = cursor.getInt(0)
            cursor.moveToNext()
        }
        return id_flat
    }

    fun selectIndications(): ArrayList<Indication> {
        val indications = ArrayList<Indication>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM indication", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val indication = Indication(cursor.getInt(0), cursor.getString(1),cursor.getInt(2), cursor.getInt(3))
            indications.add(indication)
            cursor.moveToNext()
        }
        return indications
    }

    fun selectTypeFromCounter(id: Int): String {
        val cursor = mDb.rawQuery("SELECT type FROM counter WHERE id = $id", null)
        var type = ""
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            type = cursor.getString(0)
            cursor.moveToNext()
        }
        return type
    }

    fun selectIndicationFromId(id: Int): Indication {
        var indication = Indication()
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM indication WHERE id = $id", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            indication = Indication(cursor.getInt(0), cursor.getString(1),cursor.getInt(2), cursor.getInt(3))
            cursor.moveToNext()
        }
        return indication
    }

    fun selectCountersWhereUsed(): ArrayList<Counter> {
        val counters = ArrayList<Counter>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM counter WHERE used = 'true'", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            val counter = Counter(cursor.getInt(0), cursor.getString(1),cursor.getString(2),cursor.getString(3).toBoolean(), cursor.getInt(4))
            counters.add(counter)
            cursor.moveToNext()
        }
        return counters
    }

    fun selectCountFromPayment(period: String): Int {
        val cursor = mDb.rawQuery("SELECT * FROM payment WHERE period = '$period'", null)
        return cursor.count
    }

    fun selectCountIndicationsFromIndicationWherePeriod(period: String, id_counter: Int): Int {
        val cursor = mDb.rawQuery("SELECT * FROM indication WHERE period = '$period' and id_counter = $id_counter", null)
        return cursor.count
    }

    fun deleteIndication(id: Int): Int {
        val cursor = mDb.delete("indication", "id=?",arrayOf<String>(id.toString()))
        return cursor
    }

    fun createIndication(indication: Indication){
        val cv: ContentValues = ContentValues()
        cv.put("period", indication.period)
        cv.put("value", indication.value)
        cv.put("id_counter", indication.id_counter)

        val cursor: Long = mDb.insert("indication", null, cv)

        val l: Long = -1
        if(cursor == l){
            Toast.makeText(context, "Gagal menambahkan ke database!", Toast.LENGTH_SHORT).
            show()
        }
        else {
            Toast.makeText(context, "Indikasi ditambahkan", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateIndication(indication: Indication){
        val cv: ContentValues = ContentValues()
        cv.put("period", indication.period)
        cv.put("value", indication.value)
        cv.put("id_counter", indication.id_counter)

        val cursor: Int = mDb.update("indication", cv, "id=?",arrayOf<String>(indication.id.toString()))

        if(cursor == -1){
            Toast.makeText(context, "Gagal mengubah ke database!", Toast.LENGTH_SHORT).
            show()
        }
        else {
            Toast.makeText(context, "Indikasi diperbarui", Toast.LENGTH_SHORT).show()
        }
    }

    fun selectRates(): ArrayList<Rate> {
        val rates = ArrayList<Rate>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM rate", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            val rate = Rate(cursor.getInt(0), cursor.getString(1),cursor.getFloat(2))
            rates.add(rate)
            cursor.moveToNext()
        }
        return rates
    }

    fun selectNormatives(): ArrayList<Normative> {
        val normatives = ArrayList<Normative>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM normative", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            val normative = Normative(cursor.getInt(0), cursor.getString(1),cursor.getFloat(2))
            normatives.add(normative)
            cursor.moveToNext()
        }
        return normatives
    }

    fun selectRateFromId(id: Int): Rate {
        var rate = Rate()
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM rate WHERE id = $id", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            rate = Rate(cursor.getInt(0), cursor.getString(1),cursor.getFloat(2))
            cursor.moveToNext()
        }
        return rate
    }

    fun selectNormativeFromId(id: Int): Normative {
        var normative = Normative()
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM normative WHERE id = $id", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            normative = Normative(cursor.getInt(0), cursor.getString(1),cursor.getFloat(2))
            cursor.moveToNext()
        }
        return normative
    }

    fun updateRate(rate: Rate){
        val cv: ContentValues = ContentValues()
        cv.put("name", rate.name)
        cv.put("value", rate.value)

        val cursor: Int = mDb.update("rate", cv, "id=?",arrayOf<String>(rate.id.toString()))

        if(cursor == -1){
            Toast.makeText(context, "Gagal update ke database!", Toast.LENGTH_SHORT).
            show()
        }
        else {
            Toast.makeText(context, "Tarif diubah", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateNormative(normative: Normative){
        val cv: ContentValues = ContentValues()
        cv.put("name", normative.name)
        cv.put("value", normative.value)

        val cursor: Int = mDb.update("normative", cv, "id=?",arrayOf<String>(normative.id.toString()))

        if(cursor == -1){
            Toast.makeText(context, "Gagal update ke database!", Toast.LENGTH_SHORT).
            show()
        }
        else {
            Toast.makeText(context, "Normatif diubah", Toast.LENGTH_SHORT).show()
        }
    }

    fun selectNameRateFromId(id: Int): String {
        val cursor = mDb.rawQuery("SELECT name FROM rate WHERE id = $id", null)
        var name = ""
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            name = cursor.getString(0)
            cursor.moveToNext()
        }
        return name
    }

    fun selectPayments(): ArrayList<Payment> {
        val payments = ArrayList<Payment>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM payment", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val payment = Payment(cursor.getInt(0), cursor.getString(1),cursor.getFloat(2), cursor.getString(3), cursor.getString(4).toBoolean(), cursor.getInt(5), cursor.getInt(7), cursor.getInt(6))
            payments.add(payment)
            cursor.moveToNext()
        }
        return payments
    }

    fun selectCountPaymentsWherePeriod(period: String): Int {
        val cursor = mDb.rawQuery("SELECT * FROM payment WHERE period = '$period' ", null)
        return cursor.count
    }

    fun selectCountersWhereUsedAndIdFlat(id_flat: Int): ArrayList<Counter> {
        val counters = ArrayList<Counter>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM counter WHERE used = 'true' and id_flat = $id_flat", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val counter = Counter(cursor.getInt(0), cursor.getString(1),cursor.getString(2),cursor.getString(3).toBoolean(), cursor.getInt(4))
            counters.add(counter)
            cursor.moveToNext()
        }
        return counters
    }

    fun selectIndicationFromCounterWherePeriod(id_counter: Int, period: String): Indication {
        var indication = Indication()
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM indication WHERE id_counter = $id_counter and period = '$period'", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            indication = Indication(cursor.getInt(0), cursor.getString(1),cursor.getInt(2), cursor.getInt(3))
            cursor.moveToNext()
        }
        return indication
    }

    fun createPayment(payment: Payment){
        val cv = ContentValues()
        cv.put("period", payment.period)
        cv.put("status", payment.status.toString())
        cv.put("amount", payment.amount)
        cv.put("cheque", payment.cheque)
        cv.put("id_flat", payment.id_flat)
        cv.put("id_rate", payment.id_rate)
        cv.put("id_normative", payment.id_normative)

        val cursor: Long = mDb.insert("payment", null, cv)

        val l: Long = 0
        if(cursor == l){
            Toast.makeText(context, "Gagal menambahkan pembayaran ke dalam database!", Toast.LENGTH_SHORT).
            show()
        }
    }

    fun selectPaymentFromId(id: Int): Payment {
        var payment = Payment()
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM payment WHERE id = $id", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            payment = Payment(cursor.getInt(0), cursor.getString(1),cursor.getFloat(2), cursor.getString(3), cursor.getString(4).toBoolean(), cursor.getInt(5), cursor.getInt(7), cursor.getInt(6))
            cursor.moveToNext()
        }
        return payment
    }

    fun updatePayment(payment: Payment){
        val cv = ContentValues()
        cv.put("period", payment.period)
        cv.put("status", payment.status.toString())
        cv.put("amount", payment.amount)
        cv.put("cheque", payment.cheque)
        cv.put("id_flat", payment.id_flat)
        cv.put("id_rate", payment.id_rate)
        cv.put("id_normative", payment.id_normative)

        val cursor: Int = mDb.update("payment", cv, "id=?",arrayOf<String>(payment.id.toString()))

        if(cursor == -1){
            Toast.makeText(context, "Gagal menambahkan pembayaran ke dalam database!", Toast.LENGTH_SHORT).
            show()
        }
    }

    fun selectNumberFlats(): ArrayList<String> {
        val flats = ArrayList<String>();
        val cursor: Cursor = mDb.rawQuery("SELECT flat_number FROM flat", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            flats.add(cursor.getString(0))
            cursor.moveToNext()
        }
        return flats
    }

    fun selectIdFlatFromNumber(number: String): Int {
        var flat_number = 0
        val cursor: Cursor = mDb.rawQuery("SELECT id FROM flat WHERE flat_number = '$number' ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            flat_number = cursor.getInt(0)
            cursor.moveToNext()
        }
        return flat_number
    }

    fun selectPeriodsFromIndication(): ArrayList<String> {
        val periods = ArrayList<String>()
        val cursor: Cursor = mDb.rawQuery("SELECT DISTINCT period FROM indication", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            periods.add(cursor.getString(0))
            cursor.moveToNext()
        }
        return periods
    }

    fun selectCountersFromType(type: String): ArrayList<Counter> {
        val counters = ArrayList<Counter>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM counter WHERE type = '$type'", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            val counter = Counter(cursor.getInt(0), cursor.getString(1),cursor.getString(2),cursor.getString(3).toBoolean(), cursor.getInt(4))
            counters.add(counter)
            cursor.moveToNext()
        }
        return counters
    }

    fun selectCountersFromIdFlat(id: Int): ArrayList<Counter> {
        val counters = ArrayList<Counter>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM counter WHERE id_flat = '$id'", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            val counter = Counter(cursor.getInt(0), cursor.getString(1),cursor.getString(2),cursor.getString(3).toBoolean(), cursor.getInt(4))
            counters.add(counter)
            cursor.moveToNext()
        }
        return counters
    }

    fun selectPeriodsFromPayments(): ArrayList<String> {
        val periods = ArrayList<String>()
        val cursor: Cursor = mDb.rawQuery("SELECT DISTINCT period FROM payment", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            periods.add(cursor.getString(0))
            cursor.moveToNext()
        }
        return periods
    }

    fun selectIdRateFromName(name: String): Int {
        var id_rate = 0
        val cursor: Cursor = mDb.rawQuery("SELECT id FROM rate WHERE name = '$name' ", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            id_rate = cursor.getInt(0)
            cursor.moveToNext()
        }
        return id_rate
    }

    fun selectPaymentsFromPeriod(period: String): ArrayList<Payment> {
        val payments = ArrayList<Payment>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM payment where period = '$period' and status = 'true'", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            val payment = Payment(cursor.getInt(0), cursor.getString(1),cursor.getFloat(2), cursor.getString(3), cursor.getString(4).toBoolean(), cursor.getInt(5), cursor.getInt(7), cursor.getInt(6))
            payments.add(payment)
            cursor.moveToNext()
        }
        return payments
    }

    fun selectUserFromTenant(login: String, password: String): Tenant {
        val dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val cursor = mDb.rawQuery("SELECT * FROM tenant WHERE email = '$login' and password = '$password' ", null)
        var tenant: Tenant = Tenant()
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            tenant = Tenant(cursor.getInt(0), cursor.getString(1), LocalDate.parse(cursor.getString(2), dtf), cursor.getString(3), cursor.getString(6), cursor.getString(4), cursor.getString(5))
            cursor.moveToNext()
        }
        return tenant
    }

    fun selectUserFromAdmin(login: String, password: String): Admin {
        val cursor = mDb.rawQuery("SELECT * FROM Administrator WHERE email = '$login' and password = '$password' ", null)
        var admin = Admin()
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            admin = Admin(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
            cursor.moveToNext()
        }
        return admin
    }

    fun selectAdminFromId(id: Int): Admin {
        val cursor = mDb.rawQuery("SELECT * FROM Administrator WHERE id = $id ", null)
        var admin = Admin()
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            admin = Admin(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
            cursor.moveToNext()
        }
        return admin
    }

    fun selectCountTenantsWhereEmail(email: String): Int {
        val cursor = mDb.rawQuery("SELECT * FROM tenant WHERE email = '$email'", null)
        return cursor.count
    }

    fun selectCountAdminWhereEmail(email: String): Int {
        val cursor = mDb.rawQuery("SELECT * FROM Administrator WHERE email = '$email'", null)
        return cursor.count
    }

    fun updateAdmin(admin: Admin) {
        val cv = ContentValues()
        cv.put("email", admin.email)
        cv.put("password", admin.password)

        val cursor: Int =
            mDb.update("Administrator", cv, "id=?", arrayOf<String>(admin.id.toString()))

        if (cursor == -1) {
            Toast.makeText(
                context,
                "Gagal mengubah admin dalam database!",
                Toast.LENGTH_SHORT
            ).show()
        }
        else {
            Toast.makeText(context, "Admin berhasil diubah!", Toast.LENGTH_SHORT).show()
        }
    }

    fun selectIndicationsFromIdCounter(id: Int): ArrayList<Indication> {
        var indications = ArrayList<Indication>()
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM indication WHERE id_counter = $id", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast()) {
            indications.add(Indication(cursor.getInt(0), cursor.getString(1),cursor.getInt(2), cursor.getInt(3)))
            cursor.moveToNext()
        }
        return indications
    }

    fun selectPaymentsFromIdFlat(id: Int): ArrayList<Payment> {
        val payments = ArrayList<Payment>();
        val cursor: Cursor = mDb.rawQuery("SELECT * FROM payment WHERE id_flat = $id", null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val payment = Payment(cursor.getInt(0), cursor.getString(1),cursor.getFloat(2), cursor.getString(3), cursor.getString(4).toBoolean(), cursor.getInt(5), cursor.getInt(7), cursor.getInt(6))
            payments.add(payment)
            cursor.moveToNext()
        }
        return payments
    }

    fun selectCountTenantsWhereEmailNotId(id: Int, email: String): Int {
        val cursor = mDb.rawQuery("SELECT * FROM tenant WHERE email = '$email' and id != $id", null)
        return cursor.count
    }

    fun updateTenantUser(tenant: Tenant){
        val cv: ContentValues = ContentValues()
        cv.put("email", tenant.email)
        cv.put("password", tenant.password)

        val cursor: Int = mDb.update("tenant", cv, "id=?",arrayOf<String>(tenant.id_tenant.toString()))
        if(cursor == -1){
            Toast.makeText(context, "Gagal mengubah tenant dalam database!", Toast.LENGTH_SHORT).
            show()
        }
        else {
            Toast.makeText(context, "Tenant berhasil diubah", Toast.LENGTH_SHORT).show()
        }
    }

    fun selectAdmin(): Admin {
        val cursor = mDb.rawQuery("SELECT * FROM Administrator", null)
        var admin = Admin()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            admin = Admin(cursor.getInt(0), cursor.getString(1), cursor.getString(2))
            cursor.moveToNext()
        }
        return admin
    }

    fun selectCountPaymentsWhereFlatNotStatus(id_flat: Int): Int {
        val cursor = mDb.rawQuery("SELECT * FROM payment WHERE id_flat = $id_flat and status = 'false' ", null)
        return cursor.count
    }

}

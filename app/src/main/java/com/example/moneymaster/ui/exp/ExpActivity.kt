package com.example.moneymaster.ui.exp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.moneymaster.R
import com.example.moneymaster.components.DbDriver
import com.example.moneymaster.databinding.ActivityExpBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class ExpActivity : AppCompatActivity() {

    private val dbHelper=DbDriver.DbHelper(this)
    private lateinit var binding: ActivityExpBinding
    private val dateFormat =SimpleDateFormat("dd MMM. yyyy", Locale.US)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inViewModel =
            ViewModelProvider(this).get(ExpViewModel::class.java)
        binding = ActivityExpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var items: List<String>
        val categoryLayout =  binding.ExpcategoryLayout
        val category : AutoCompleteTextView = binding.Expcategory
        val sumLayout= binding.ExpsumLayout
        val dateLayout=binding.ExpdateLayout
        val dateEditText= dateLayout.editText
        val descriptionLayout = binding.ExpdescriptionLayout
        val buttton=binding.ExpbuttonOperation
        val dateOnPickListener = {
                it:View->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.set(2019,0,1)
            val jan2020 = calendar.timeInMillis
            val calendarConstraint=CalendarConstraints.Builder().setValidator(DateValidatorPointForward.from(jan2020)).build()
            val datePicker= MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(calendarConstraint)
                .build()
            datePicker.addOnPositiveButtonClickListener {selection->
                dateEditText!!.setText(dateFormat.format(selection))
            }
            datePicker.show(supportFragmentManager,"datePicker")
        }
        val btnOnClickListener={
                it:View->
            val sumValidation:Boolean
            val dateValidation:Boolean
            val categoryValidation:Boolean

            val sumText = sumLayout.editText!!.text.toString()
            if(validateSum(sumText)){
                sumLayout.error=null
                sumLayout.isErrorEnabled=false
                Toast.makeText(this,"Sum coorect",Toast.LENGTH_SHORT).show()
                sumValidation=true
            }
            else {
                sumLayout.isErrorEnabled=true
                sumLayout.error="Sum must not be empty, contain no more than 10 numbers and only 2 numbers after dot"
                sumValidation=false
            }
            if(dateEditText!!.text.isEmpty())
            {
                dateLayout.isErrorEnabled=true
                dateLayout.error="Date cannot be empty"
                dateValidation=false
            }
            else {
                dateLayout.isErrorEnabled=false
                dateLayout.error=null
                dateValidation=true
            }
            if(category.text.isEmpty()){
                categoryLayout.isErrorEnabled=true
                categoryLayout.error="Please select category"
                categoryValidation=false
            }
            else {
                categoryLayout.isErrorEnabled = false
                categoryLayout.error = null
                categoryValidation = true
            }
            if(dateValidation&&sumValidation&&categoryValidation) {

                val sum = NumberFormat.getInstance().parse(sumText)!!.toFloat()
                val category = category.text.toString()
                val description = descriptionLayout.editText?.text.toString()
                val date = Calendar.getInstance()
                date.time = dateFormat.parse(dateEditText!!.text.toString())
                dbHelper.insertOperation(0,category,sum,false,description,date.timeInMillis)
                dbHelper.updateAccount(0)
                val intent = Intent()
                intent.putExtra("Result", true)
                setResult(200, intent)
                finish()
            }
        }




        dateLayout.setOnClickListener(dateOnPickListener)
        dateEditText!!.setOnClickListener(dateOnPickListener)

        inViewModel.categorys.observe(this){
            items=it
            val adapter = ArrayAdapter(this, R.layout.category_list_item,items)
            category.setAdapter(adapter)
            category.onItemClickListener= AdapterView.OnItemClickListener {
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                val item = parent?.getItemAtPosition(position)
                Toast.makeText(this,"Item: $item",Toast.LENGTH_SHORT).show()
            }
        }
        inViewModel.getCategorys(dbHelper)
        buttton.setOnClickListener(btnOnClickListener)



        onBackPressedInActivity()
    }

    private fun onBackPressedInActivity() {

        this.onBackPressedDispatcher.addCallback{
            //Toast.makeText(this@InActivity,"Canceled",Toast.LENGTH_SHORT).show()
            Log.d("ActivityIN","BAck Pressedt")
            val intent = Intent()
            intent.putExtra("Result",false)
            setResult(500,intent)
            finish()
        }

    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if(event!!.action==MotionEvent.ACTION_CANCEL) {
            this.onBackPressedDispatcher.onBackPressed()
            true
        } else super.onTouchEvent(event)

    }

    private fun validateSum(token:String):Boolean {
        if (token.isEmpty())
            return false
        else
            if(NumberFormat.getInstance().parse(token)!!.toFloat()== 0.0.toFloat() || NumberFormat.getInstance().parse(token)!!.toInt()== 0)
                return false
            else
                if (token.contains(".")) {
                    val FractionNo: String = token.split(".")[1];
                    if (FractionNo.length > 2)
                        return false;
                    else
                        return true;
                }
                else if (token.length<=10|| token.isNotEmpty())
                    return true
        return false
    }
}
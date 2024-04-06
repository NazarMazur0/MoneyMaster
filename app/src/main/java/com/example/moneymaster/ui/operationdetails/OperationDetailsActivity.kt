package com.example.moneymaster.ui.operationdetails

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.moneymaster.R
import com.example.moneymaster.components.DbDriver
import com.example.moneymaster.components.Formats

import com.example.moneymaster.databinding.ActivityOperationDetailsBinding


class OperationDetailsActivity : AppCompatActivity() {

    private val dbHelper=DbDriver.DbHelper(this)
    private lateinit var binding: ActivityOperationDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val odViewModel =
            ViewModelProvider(this).get(OperationDetailsViewModel::class.java)
        binding = ActivityOperationDetailsBinding.inflate(layoutInflater)
        val operationID = getIntent().getIntExtra("operationID",-1)
        Log.d("operationID","operationID=${operationID}")
        setContentView(binding.root)
        val categoryName = binding.categoryName
        val iconView = binding.operationIcon
        val operationTime = binding.operatioTime
        val operationDescription = binding.operationDescription
        val sum = binding.sum
        odViewModel.operation.observe(this){
            var icon=0
            if (it!=null){
                categoryName.setText(it.categoryName)
                when(it!!.icon){
                    "food" -> icon = R.drawable.food_restaurant_svgrepo_com
                    "transport" -> icon = R.drawable.public_transport_bus_svgrepo_com
                    "rent"-> icon = R.drawable.house_rent_svgrepo_com
                    "health"-> icon = R.drawable.health_svgrepo_com
                    "entertainment"-> icon = R.drawable.entertainment_svgrepo_com
                    "salary"->icon = R.drawable.salary_svgrepo_com
                    else->0
                }
                Glide.with(this).load("").apply( RequestOptions().placeholder(icon)).override(200,200).centerCrop().into(iconView)
                operationTime.setText(Formats.DATE_FORMAT.format(it.date))
                operationDescription.setText(it.description)
                sum.setText( if(it.type) "${it.sum}" else "-${it.sum}")
            }
        }
        odViewModel.getOperation(dbHelper,operationID)


    }
}
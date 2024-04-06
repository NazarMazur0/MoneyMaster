package com.example.moneymaster.ui.main.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.moneymaster.R
import com.example.moneymaster.components.DbDriver
import com.example.moneymaster.components.Formats.DATE_FORMAT
import com.example.moneymaster.models.OperationModel
import com.example.moneymaster.ui.operationdetails.OperationDetailsActivity


class OperationListAdapter(val context: Context,val operations:List<OperationModel>):RecyclerView.Adapter<OperationListAdapter.OperationViewHolder>() {
    private val  dbHelper =DbDriver.DbHelper(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OperationViewHolder =
        OperationViewHolder(LayoutInflater.from(context).inflate(R.layout.operation_list_item,parent,false))
    override fun getItemCount(): Int =
        operations.size
    override fun onBindViewHolder(holder: OperationViewHolder, position: Int) =
        holder.bind(operations[position])


    inner class OperationViewHolder( operationView:View):RecyclerView.ViewHolder(operationView){
       private val idView:TextView
       private val iconView: ImageView
       private val categoryView: TextView
       private val dateView: TextView
       private val sumView: TextView
        init {
            idView = operationView.findViewById(R.id.ID)
            iconView = operationView.findViewById(R.id.icon)
            categoryView = operationView.findViewById(R.id.categoryName)
            dateView = operationView.findViewById(R.id.date)
            sumView = operationView.findViewById(R.id.sum)
            operationView.setOnClickListener {
                val  intent = Intent(context, OperationDetailsActivity::class.java)
                intent.putExtra("operationID",((it.findViewById(R.id.ID) as TextView).text.toString()).toInt())
                context.startActivity(intent)
            }

        }

        fun bind(operation: OperationModel?){
            var icon=0
            when(operation!!.icon){
                "food" -> icon = R.drawable.food_restaurant_svgrepo_com
                "transport" -> icon = R.drawable.public_transport_bus_svgrepo_com
                "rent"-> icon = R.drawable.house_rent_svgrepo_com
                "health"-> icon = R.drawable.health_svgrepo_com
                "entertainment"-> icon = R.drawable.entertainment_svgrepo_com
                "salary"->icon = R.drawable.salary_svgrepo_com
                else->0
            }

            if (operation != null) {
               Glide
                    .with(context)
                    .load("")
                    .apply( RequestOptions().placeholder(icon))
                    .override(200,200)
                    .centerCrop()
                    .into(iconView)
                idView.setText(operation.ID.toString())
                categoryView.setText(operation.categoryName)
                dateView.setText(DATE_FORMAT.format(operation.date ?: 0))
                if (operation.type){
                    sumView.setTextColor(Color.parseColor("#008080"))
                    sumView.setText("${operation.sum}")
                }
                else{
                    sumView.setTextColor(Color.parseColor("#801d21"))
                    sumView.setText("-${operation.sum}")

                }
            }
        }

    }

}
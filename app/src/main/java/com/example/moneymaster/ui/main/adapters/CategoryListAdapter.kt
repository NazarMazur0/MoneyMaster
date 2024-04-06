package com.example.moneymaster.ui.main.adapters

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.moneymaster.R
import com.example.moneymaster.models.CategoryModel



class CategoryListAdapter(val context: Context, val categorys: List<CategoryModel>):RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder =
        CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.category_detailed_list_item,parent,false))
    override fun getItemCount(): Int =
        categorys.size
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) =
        holder.bind(categorys[position])


    inner class CategoryViewHolder(private val CategoryView:View):RecyclerView.ViewHolder(CategoryView){
        val iconView: ImageView
        val categoryNameView: TextView
        val incomeView: TextView
        val expneseView: TextView
        val descriptionView: TextView
        init {
            iconView = CategoryView.findViewById(R.id.icon)
            categoryNameView = CategoryView.findViewById(R.id.categoryName)
            incomeView = CategoryView.findViewById(R.id.income)
            expneseView = CategoryView.findViewById(R.id.expense)
            descriptionView = CategoryView.findViewById(R.id.description)

        }

        fun bind(category: CategoryModel?){
            Log.d("category",category.toString())
            var icon=0
            when(category!!.CategoryIcon){
                "food" -> icon = R.drawable.food_restaurant_svgrepo_com
                "transport" -> icon = R.drawable.public_transport_bus_svgrepo_com
                "rent"-> icon = R.drawable.house_rent_svgrepo_com
                "health"-> icon = R.drawable.health_svgrepo_com
                "entertainment"-> icon = R.drawable.entertainment_svgrepo_com
                "salary"->icon = R.drawable.salary_svgrepo_com
                else->0
            }

            if (category != null) {
                Glide
                    .with(context)
                    .load("")
                    .apply( RequestOptions().placeholder(icon))
                    .override(200,200)
                    .centerCrop()
                    .into(iconView)
                if (category.CategoryIncome==0F){
                    expneseView.setText("-${category.CategoryExpenses}")
                    incomeView.visibility=View.INVISIBLE
                    (CategoryView as ViewManager).removeView(incomeView)
                } else if (category.CategoryExpenses==0F)
                {
                    incomeView.setText("${category.CategoryIncome}")
                    expneseView.visibility=View.INVISIBLE
                    (CategoryView as ViewManager).removeView(expneseView)
                } else
                incomeView.setText("${category.CategoryIncome}")
                expneseView.setText("-${category.CategoryExpenses}")

                categoryNameView.setText(category?.CategoryName)
                incomeView.setTextColor(Color.parseColor("#008080"))
                expneseView.setTextColor(Color.parseColor("#801d21"))
                descriptionView.setText(category.CategoryDescription)

                }
            }
        }
    }


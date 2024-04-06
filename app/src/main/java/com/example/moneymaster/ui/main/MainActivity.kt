package com.example.moneymaster.ui.main

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.moneymaster.R
import com.example.moneymaster.components.DbDriver
import com.example.moneymaster.databinding.ActivityMainBinding
import java.lang.RuntimeException
import java.sql.SQLException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
  //  private lateinit var dbHelper :DbDriver.DbHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     val dbHelper = DbDriver.DbHelper(this)
      dbHelper.writableDatabase
      try {
          dbHelper.getAccountSaldoById(0)
      }
      catch (e:SQLException)
     {
         dbHelper.insertDefaultAccount()
     }
      catch (e:RuntimeException){
          dbHelper.insertDefaultAccount()
      }
      dbHelper.close()
     //   requestWindowFeature(Window.FEATURE_NO_TITLE)
        WindowCompat.setDecorFitsSystemWindows(window,false)
        //this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
       // supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)



    }

}
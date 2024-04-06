package com.example.moneymaster.ui.main.fragments.money

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymaster.ui.exp.ExpActivity
import com.example.moneymaster.components.DbDriver
import com.example.moneymaster.databinding.FragmentMoneyBinding
import com.example.moneymaster.ui.`in`.InActivity
import com.example.moneymaster.ui.main.adapters.OperationListAdapter

class MoneyFragment : Fragment() {

    private var _binding: FragmentMoneyBinding? = null
    private  lateinit var moneyViewModel: MoneyViewModel
    private val binding get() = _binding!!
    private lateinit var dbHelper :DbDriver.DbHelper
    private val inActivityLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == 200) {
            Log.d("ACT","200")
            moneyViewModel.getSaldo(dbHelper)
            moneyViewModel.getOperations(dbHelper)

        }
    }
    private val expActivityLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == 200) {
            Log.d("ACT","200")
            moneyViewModel.getSaldo(dbHelper)
            moneyViewModel.getOperations(dbHelper)

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         dbHelper= DbDriver.DbHelper(requireContext())
         moneyViewModel =
            ViewModelProvider(this).get(MoneyViewModel::class.java)

        _binding = FragmentMoneyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textSaldo: TextView = binding.moneyHome
        moneyViewModel.saldo.observe(viewLifecycleOwner) {
            textSaldo.text = it
        }
        val operationRecycler = binding.operationsRecycler
        moneyViewModel.operations.observe(viewLifecycleOwner){
            val adapter =OperationListAdapter(requireContext(),it)
            val layout = LinearLayoutManager(requireContext())
            layout.orientation=LinearLayoutManager.VERTICAL
            operationRecycler.layoutManager=layout
            operationRecycler.adapter=adapter

        }
        val textSymbol:TextView=binding.symbol
        moneyViewModel.symbol.observe(viewLifecycleOwner){
            textSymbol.text=it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        moneyViewModel.getSaldo(dbHelper)
        moneyViewModel.getSymbol(dbHelper)
        moneyViewModel.getOperations(dbHelper)
        val btnIN = binding.buttonIn
        btnIN.setOnClickListener {
            val  intent =Intent(activity, InActivity::class.java)
            inActivityLauncher.launch(intent)


        }
        val btnEXP = binding.buttonOut
        btnEXP.setOnClickListener {
            val  intent =Intent(activity, ExpActivity::class.java)
            expActivityLauncher.launch(intent)


        }

        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDestroyView() {
        dbHelper.close()
        super.onDestroyView()
        _binding = null
    }
}
package com.example.moneymaster.ui.main.fragments.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymaster.components.DbDriver
import com.example.moneymaster.databinding.FragmentCategoryBinding
import com.example.moneymaster.ui.main.adapters.CategoryListAdapter

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dbHelper : DbDriver.DbHelper

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val categoryViewModel =
                ViewModelProvider(this).get(CategoryViewModel::class.java)
        dbHelper= DbDriver.DbHelper(requireContext())
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val categoryRecycler = binding.categoryRecycler
        categoryViewModel.categorys.observe(viewLifecycleOwner){
            val adapter = CategoryListAdapter(requireContext(),it)
            val layout = LinearLayoutManager(requireContext())
            layout.orientation= LinearLayoutManager.VERTICAL
            categoryRecycler.layoutManager=layout
            categoryRecycler.adapter=adapter

        }
        categoryViewModel.getCategorys(dbHelper)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
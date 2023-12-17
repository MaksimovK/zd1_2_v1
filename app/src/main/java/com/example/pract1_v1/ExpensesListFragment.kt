package com.example.pract1_v1

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ExpensesListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expenses_list, container, false)

        val addButton: Button = view.findViewById(R.id.addExpenseButton)
        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_expensesListFragment_to_addExpenseFragment)
        }
        recyclerView = view.findViewById(R.id.recyclerViewExpenses)
        recyclerView.layoutManager = LinearLayoutManager(context)
        loadExpensesFromSharedPreferences()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadExpensesFromSharedPreferences()
    }

    private fun loadExpensesFromSharedPreferences() {
        val sharedPreferences =
            requireActivity().getSharedPreferences("ExpensesPref", Context.MODE_PRIVATE)
        val expensesJson = sharedPreferences.getString("expenses", "[]")
        val expensesType = object : TypeToken<MutableList<Expense>>() {}.type
        val expenses = Gson().fromJson<MutableList<Expense>>(expensesJson, expensesType)

        recyclerView.adapter = ExpenseAdapter(expenses)
    }
}


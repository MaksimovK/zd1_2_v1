package com.example.pract1_v1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ExpensesListFragment : Fragment(), ExpenseAdapter.ExpenseItemListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expenses_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewExpenses)
        addButton = view.findViewById(R.id.addExpenseButton)

        recyclerView.layoutManager = LinearLayoutManager(context)
        loadExpensesFromSharedPreferences()

        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_expensesListFragment_to_addExpenseFragment)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadExpensesFromSharedPreferences()
    }

    private fun loadExpensesFromSharedPreferences() {
        val sharedPreferences = requireActivity().getSharedPreferences("ExpensesPref", Context.MODE_PRIVATE)
        val expensesJson = sharedPreferences.getString("expenses", "[]")
        val expensesType = object : TypeToken<MutableList<Expense>>() {}.type
        val expenses = Gson().fromJson<MutableList<Expense>>(expensesJson, expensesType)

        recyclerView.adapter = ExpenseAdapter(expenses, this)
    }

    override fun onEditClicked(expense: Expense) {
        val bundle = Bundle().apply {
            putString("expenseId", expense.id)
        }
        findNavController().navigate(R.id.action_expensesListFragment_to_editExpenseFragment, bundle)
    }

    override fun onDeleteClicked(expenseId: String) {
        showDeleteConfirmationDialog(expenseId)
    }

    private fun deleteExpense(expenseId: String) {
        val sharedPreferences = requireActivity().getSharedPreferences("ExpensesPref", Context.MODE_PRIVATE)
        val expensesJson = sharedPreferences.getString("expenses", "[]")
        val expensesType = object : TypeToken<MutableList<Expense>>() {}.type
        val expenses = Gson().fromJson<MutableList<Expense>>(expensesJson, expensesType)

        val updatedExpenses = expenses.filter { it.id != expenseId }.toMutableList()

        val editor = sharedPreferences.edit()
        editor.putString("expenses", Gson().toJson(updatedExpenses))
        editor.apply()

        loadExpensesFromSharedPreferences()
    }

    private fun showDeleteConfirmationDialog(expenseId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Подтверждение удаления")
            .setMessage("Вы точно хотите удалить этот расход?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteExpense(expenseId)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}

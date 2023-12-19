package com.example.pract1_v1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class EditExpenseFragment : Fragment() {
    private lateinit var editTextDate: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextAmount: EditText
    private lateinit var buttonSave: Button
    private var expenseId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_expense, container, false)

        editTextDate = view.findViewById(R.id.editTextDate)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        editTextAmount = view.findViewById(R.id.editTextAmount)
        buttonSave = view.findViewById(R.id.buttonSave)

        expenseId = arguments?.getString("expenseId")
        loadExpenseData(expenseId)

        buttonSave.setOnClickListener {
            updateExpense()
        }

        return view
    }

    private fun loadExpenseData(expenseId: String?) {
        val sharedPreferences = requireActivity().getSharedPreferences("ExpensesPref", Context.MODE_PRIVATE)
        val expensesJson = sharedPreferences.getString("expenses", "[]")
        val expensesType = object : TypeToken<MutableList<Expense>>() {}.type
        val expenses = Gson().fromJson<MutableList<Expense>>(expensesJson, expensesType)

        val expense = expenses.find { it.id == expenseId }
        expense?.let {
            editTextDate.setText(it.date)
            editTextDescription.setText(it.description)
            editTextAmount.setText(it.amount.toString())
        }
    }

    private fun updateExpense() {
        val sharedPreferences = requireActivity().getSharedPreferences("ExpensesPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val expensesJson = sharedPreferences.getString("expenses", "[]")
        val expensesType = object : TypeToken<MutableList<Expense>>() {}.type
        val expenses = Gson().fromJson<MutableList<Expense>>(expensesJson, expensesType)

        val date = editTextDate.text.toString()
        val description = editTextDescription.text.toString()
        val amountString = editTextAmount.text.toString()

        if (!validateInputs(date, description, amountString)) {
            return
        }

        val amount = amountString.toDoubleOrNull() ?: 0.0
        val updatedExpense = Expense(expenseId!!, date, description, amount)
        val updatedExpenses = expenses.map { if (it.id == expenseId) updatedExpense else it }.toMutableList()

        editor.putString("expenses", Gson().toJson(updatedExpenses))
        editor.apply()

        findNavController().navigateUp()
    }

    private fun validateInputs(date: String, description: String, amountString: String): Boolean {
        if (date.isBlank() || description.isBlank() || amountString.isBlank()) {
            showToast("Пожалуйста, заполните все поля")
            return false
        }

        if (!isValidDate(date)) {
            showToast("Формат даты должен быть DD.MM.YYYY")
            return false
        }

        if (amountString.toDoubleOrNull() == null) {
            showToast("Введите корректную сумму")
            return false
        }

        return true
    }

    private fun isValidDate(date: String): Boolean {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        dateFormat.isLenient = false
        return try {
            dateFormat.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

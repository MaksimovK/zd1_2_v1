package com.example.pract1_v1

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseFragment : Fragment() {
    private lateinit var editTextDate: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextAmount: EditText
    private lateinit var buttonSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_expense, container, false)

        editTextDate = view.findViewById(R.id.editTextDate)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        editTextAmount = view.findViewById(R.id.editTextAmount)
        buttonSave = view.findViewById(R.id.buttonSave)

        buttonSave.setOnClickListener {
            saveExpense()
        }

        return view
    }

    private fun saveExpenseInSharedPreferences(expense: Expense) {
        val sharedPreferences = requireActivity().getSharedPreferences("ExpensesPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Преобразуем список расходов в JSON
        val expensesJson = sharedPreferences.getString("expenses", "[]")
        val expensesType = object : TypeToken<MutableList<Expense>>() {}.type
        val expenses = Gson().fromJson<MutableList<Expense>>(expensesJson, expensesType)
        expenses.add(expense)

        // Сохраняем обновленный список обратно в SharedPreferences
        val newExpensesJson = Gson().toJson(expenses)
        editor.putString("expenses", newExpensesJson)
        editor.apply()
    }

    private fun saveExpense() {
        val date = editTextDate.text.toString()
        val description = editTextDescription.text.toString()
        val amountString = editTextAmount.text.toString()

        if (!validateInputs(date, description, amountString)) {
            return
        }

        val amount = amountString.toDouble()
        val expense = Expense(date, description, amount)
        saveExpenseInSharedPreferences(expense)

        findNavController().navigateUp()
    }


    private fun validateInputs(date: String, description: String, amount: String): Boolean {
        if (date.isBlank() || description.isBlank() || amount.isBlank()) {
            showToast("Пожалуйста, заполните все поля")
            return false
        }

        if (!isValidDate(date)) {
            showToast("Формат даты должен быть DD.MM.YYYY")
            return false
        }

        if (amount.toDoubleOrNull() == null) {
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

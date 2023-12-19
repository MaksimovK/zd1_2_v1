package com.example.pract1_v1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private var expenses: MutableList<Expense>,
    private val listener: ExpenseItemListener
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    interface ExpenseItemListener {
        fun onEditClicked(expense: Expense)
        fun onDeleteClicked(expenseId: String)
    }

    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateView: TextView = view.findViewById(R.id.dateTextView)
        val descriptionView: TextView = view.findViewById(R.id.descriptionTextView)
        val amountView: TextView = view.findViewById(R.id.amountTextView)
        val editButton: ImageButton = view.findViewById(R.id.editButton)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.dateView.text = expense.date
        holder.descriptionView.text = expense.description
        holder.amountView.text = String.format("%.2f", expense.amount)

        holder.editButton.setOnClickListener {
            listener.onEditClicked(expense)
        }

        holder.deleteButton.setOnClickListener {
            listener.onDeleteClicked(expense.id)
        }
    }

    override fun getItemCount() = expenses.size
}

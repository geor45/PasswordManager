package com.example.passwordmanager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class PasswordAdapter(
    private var passwordList: List<PasswordEntity>,
    private val onDeleteClick: (PasswordEntity) -> Unit
) : RecyclerView.Adapter<PasswordAdapter.PasswordViewHolder>() {

    class PasswordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvPassword: TextView = itemView.findViewById(R.id.tvPassword)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val btnCopy: ImageButton = itemView.findViewById(R.id.btnCopy) // Το νέο κουμπί
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PasswordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_password, parent, false)
        return PasswordViewHolder(view)
    }

    override fun onBindViewHolder(holder: PasswordViewHolder, position: Int) {
        val password = passwordList[position]
        holder.tvTitle.text = password.title
        holder.tvPassword.text = "••••••••" // Κρύβουμε τον κωδικό για ασφάλεια στη λίστα

        // Λειτουργία Διαγραφής
        holder.btnDelete.setOnClickListener {
            onDeleteClick(password)
        }

        // Λειτουργία Αντιγραφής (Copy to Clipboard)
        holder.btnCopy.setOnClickListener {
            val context = holder.itemView.context
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Password", password.password)
            clipboard.setPrimaryClip(clip)

            // Ενημέρωση του χρήστη με ένα Toast
            Toast.makeText(context, "${password.title}: Αντιγράφηκε!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = passwordList.size

    fun updateData(newList: List<PasswordEntity>) {
        passwordList = newList
        notifyDataSetChanged()
    }
}
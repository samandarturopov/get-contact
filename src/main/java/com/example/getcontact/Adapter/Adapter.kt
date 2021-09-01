package com.example.getcontact.Adapter

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.getcontact.Model.Contact
import com.example.getcontact.databinding.ItemContactBinding

class Adapter(
    context: Context,
    var list: List<Contact>,
    var onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<Adapter.VH>() {

    var viewBinderHelper = ViewBinderHelper()

    inner class VH(var itemContactBinding: ItemContactBinding) :
        RecyclerView.ViewHolder(itemContactBinding.root) {
        init {
            viewBinderHelper.setOpenOnlyOne(true)
        }

        fun onBind(contact: Contact, position: Int) {
            viewBinderHelper.bind(itemContactBinding.swipeReveal, position.toString())
            itemContactBinding.name.text = contact.name
            itemContactBinding.number.text = contact.number
            itemContactBinding.menyu.setOnClickListener {
                onItemClickListener.onItemClickMenu(contact, position)
            }
            itemContactBinding.telImage.setOnClickListener {
                onItemClickListener.onItemClickTel(contact, position)
            }
            itemContactBinding.telephone.setOnClickListener {
                onItemClickListener.itemClickTelephone(contact, position)
            }
            itemContactBinding.message.setOnClickListener { v ->
                onItemClickListener.itemClickMessage(contact, position)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.VH {
        return VH(ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Adapter.VH, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface OnItemClickListener {
        fun itemClickTelephone(contact: Contact, position: Int)
        fun itemClickMessage(contact: Contact, position: Int)
        fun onItemClickMenu(contact: Contact, position: Int)
        fun onItemClickTel(contact: Contact, position: Int)
    }

}
package com.example.getcontact.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.Navigation
import com.example.getcontact.Model.Contact
import com.example.getcontact.R
import com.example.getcontact.databinding.FragmentMessageBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MessageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            val bundle = this.arguments

            if (bundle != null) {
                val myInt = bundle.getInt("position", 0)
                position = myInt
            }
        }
    }

    lateinit var binding: FragmentMessageBinding
    private val contactList = ArrayList<Contact>()
    var nameM = ""
    var numberM = ""
    var messageSMS = ""

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessageBinding.inflate(inflater, container, false)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                Array(1) { android.Manifest.permission.READ_CONTACTS },
                111
            )
        } else {
            val contacts = requireActivity().contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            while (contacts!!.moveToNext()) {
                val name =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val contact = Contact(number, name)
                contactList.add(contact)
            }

            contacts.close()
            binding.name.text = contactList[position].name
            binding.phoneNumber.text = contactList[position].number
            nameM = contactList[position].name
            numberM = contactList[position].number
        }

        binding.exit.setOnClickListener { v ->
            Navigation.findNavController(v).popBackStack()
        }

        binding.sendMessage.setOnClickListener { v ->
                if (binding.messageSmsText.text.toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Message text is empty", Toast.LENGTH_SHORT)
                        .show()
            } else {
                val smsManager = SmsManager.getDefault()
                smsManager.sendTextMessage(
                    numberM,
                    null,
                    binding.messageSmsText.text.toString(),
                    null,
                    null
                )
                Toast.makeText(requireContext(), "Successful", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(v).popBackStack()
            }
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MessageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MessageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
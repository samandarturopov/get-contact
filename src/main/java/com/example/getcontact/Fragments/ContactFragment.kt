package com.example.getcontact.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.getcontact.Adapter.Adapter
import com.example.getcontact.Model.Contact
import com.example.getcontact.R
import com.example.getcontact.databinding.FragmentContactBinding
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
 * Use the [ContactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    private lateinit var binding: FragmentContactBinding
    lateinit var rvAdapter: Adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactBinding.inflate(inflater, container, false)
        val list: MutableList<Contact> = mutableListOf()
        rvAdapter = Adapter(requireContext(), list, object : Adapter.OnItemClickListener {
            override fun itemClickTelephone(contact: Contact, position: Int) {
                Dexter.withContext(requireContext())
                    .withPermission(Manifest.permission.CALL_PHONE)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                            val call = Intent(Intent.ACTION_CALL)
                            call.data = Uri.parse("tel: ${contact.number}")
                            startActivity(call)
                        }

                        override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                            TODO("Not yet implemented")
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: PermissionRequest?,
                            p1: PermissionToken?
                        ) {
                            p1?.continuePermissionRequest()
                        }

                    }).check()
            }

            override fun itemClickMessage(contact: Contact, position: Int) {
                Dexter.withContext(requireContext())
                    .withPermission(Manifest.permission.SEND_SMS)
                    .withListener(object : PermissionListener {
                        override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                            val bundle = Bundle()
                            bundle.putInt("position", position)
                            Navigation.findNavController(requireView())
                                .navigate(R.id.messageFragment, bundle)
                        }

                        override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                            TODO("Not yet implemented")
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: PermissionRequest?,
                            p1: PermissionToken?
                        ) {
                            p1?.continuePermissionRequest()
                        }

                    }).check()
            }

            override fun onItemClickMenu(contact: Contact, position: Int) {
                println(position)
            }

            override fun onItemClickTel(contact: Contact, position: Int) {

            }
        })

        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_CONTACTS)
            .withListener(object : PermissionListener {
                @SuppressLint("Range", "NotifyDataSetChanged")
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
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
                        val contact = Contact(name, number)
                        list.add(contact)
                    }
                    rvAdapter.notifyDataSetChanged();
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    binding.recyclerView.adapter = rvAdapter
                }

                @SuppressLint("Range")
                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setMessage("Dasturga ruxsat bering")
                    alertDialog.setPositiveButton("Ruxsat sorash") { dialog, which ->

                        checkPerm(list)

                    }

                    alertDialog.setNegativeButton("Ruxsat soramaslik") { dialog, which ->
                        dialog?.dismiss()
                        val intent = Intent(Intent.ACTION_MAIN)
                        intent.addCategory(Intent.CATEGORY_HOME)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    alertDialog.show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }

            }).check()


        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun checkPerm(list: MutableList<Contact>) {
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_CONTACTS)
            .withListener(object : PermissionListener {
                @SuppressLint("Range")
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val contacts = requireActivity().contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                    )
                    while (contacts!!.moveToNext()) {
                        val name =
                            contacts.getString(
                                contacts.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                                )
                            )
                        val number =
                            contacts.getString(
                                contacts.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                            )
                        val contact = Contact(name, number)
                        list.add(contact)
                    }
                    rvAdapter.notifyDataSetChanged();
                    binding.recyclerView.layoutManager =
                        LinearLayoutManager(requireContext())
                    binding.recyclerView.adapter = rvAdapter
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    TODO("Not yet implemented")
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1!!.continuePermissionRequest()
                }

            }).check()
    }
}

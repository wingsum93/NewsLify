package com.crushtech.newslify.ui.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.crushtech.newslify.R
import com.crushtech.newslify.ui.NewsActivity
import com.crushtech.newslify.ui.util.Constants
import com.muddzdev.styleabletoastlibrary.StyleableToast
import kotlinx.android.synthetic.main.send_feeback_dialog.*

class SendFeebackFragment : Fragment(R.layout.send_feeback_dialog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendEmailFeedback.setOnClickListener {
            val subject = emailSubject.text.toString()
            val message = emailBody.text.toString()

            if (TextUtils.isEmpty(subject) || TextUtils.isEmpty(message)) {
                StyleableToast.makeText(
                    requireContext(),
                    "please input all required fields",
                    R.style.customToast
                ).show()
                return@setOnClickListener
            }
            hideKeyboard()
            StyleableToast.makeText(
                requireContext(),
                "thanks for your valuable feedback",
                R.style.customToast1
            ).show()
            sendEmail(subject, message)
        }
        cancelFeedback.setOnClickListener {
            findNavController().navigate(R.id.action_sendFeebackFragment_to_settingsFragment)
        }

    }

    private fun sendEmail(subject: String, message: String) {
        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.MY_EMAIL))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        mIntent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(Intent.createChooser(mIntent, "Send Using:"))
        } catch (e: Exception) {
            StyleableToast.makeText(requireContext(), e.message, R.style.customToast)
        }
    }

    override fun onResume() {
        (activity as NewsActivity).hideBottomNavigation()
        super.onResume()
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
package com.halil.chatapp.ui.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.halil.chatapp.R
import com.halil.chatapp.ui.activity.MainActivity

class WebViewFragment : Fragment() {

    private lateinit var webView: WebView
    private val args: WebViewFragmentArgs by navArgs()
    private lateinit var bottomNavigationView: BottomNavigationView

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as MainActivity).hideBottomNavigationView()

        val supportActionBar: androidx.appcompat.app.ActionBar? =
            (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.hide()
        supportActionBar?.setShowHideAnimationEnabled(false)
        val view = inflater.inflate(R.layout.fragment_web_view, container, false)
        webView = view.findViewById(R.id.webView)
        return view

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.settings.apply {
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
        }

        val url = args.url
        if (url.endsWith(".pdf", ignoreCase = true)) {
            val driveViewerUrl = "https://drive.google.com/viewerng/viewer?embedded=true&url=$url"
            webView.loadUrl(driveViewerUrl)
        } else {
            webView.loadUrl(url)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        val supportActionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.show()
        (requireActivity() as MainActivity).showBottomNavigationView()
    }
}





package com.dawidk.common.errorHandling

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.dawidk.common.R
import com.dawidk.common.binding.viewBinding
import com.dawidk.common.databinding.FragmentErrorDialogBinding
import com.dawidk.common.errorHandling.utils.BlurredBitmap
import com.dawidk.common.utils.callback
import com.dawidk.common.utils.checkCallbackImplemented
import kotlinx.parcelize.Parcelize

class ErrorDialogFragment : DialogFragment(R.layout.fragment_error_dialog) {

    private val binding by viewBinding(FragmentErrorDialogBinding::bind)
    private lateinit var config: Config

    override fun onAttach(context: Context) {
        super.onAttach(context)
        checkCallbackImplemented<Callback>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        config = arguments?.get(DIALOG_CONFIG) as Config

        binding.errorDialogMessage.text = config.error.message
        setBlur()
        onListener(config.error)
    }

    private fun onListener(error: Throwable) {
        binding.errorDialogRetry.setOnClickListener {
            callback<Callback>().onPositiveButtonClicked(error)
            this@ErrorDialogFragment.dismiss()
        }

        binding.errorDialogClose.setOnClickListener {
            callback<Callback>().onNegativeButtonClicked()
            this@ErrorDialogFragment.dismiss()
        }
    }

    private fun setBlur() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                BlurredBitmap.takeScreenShot(requireActivity()) { bitmap ->
                    val rootView: View = binding.root
                    val blurBitmap = BlurredBitmap.getBlur(context, bitmap)
                    val crossFadeTransition = createTransitionDrawable(blurBitmap)

                    rootView.background = crossFadeTransition
                    crossFadeTransition?.startTransition(CROSS_FADE_DURATION)

                }
            } else {
                BlurredBitmap.takeScreenShot(requireActivity())?.let { bitmap ->
                    val rootView: View = binding.root
                    val blurBitmap = BlurredBitmap.getBlur(context, bitmap)
                    val crossFadeTransition = createTransitionDrawable(blurBitmap)

                    rootView.background = crossFadeTransition
                    crossFadeTransition?.startTransition(CROSS_FADE_DURATION)
                }
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    private fun createTransitionDrawable(blurBitmap: Bitmap?): TransitionDrawable? {
        val backgrounds = arrayOfNulls<Drawable>(2)
        backgrounds[0] =
            ResourcesCompat.getDrawable(resources, R.drawable.transparent_drawable, null)
        backgrounds[1] = BitmapDrawable(context?.resources, blurBitmap)
        val crossFadeTransition = TransitionDrawable(backgrounds)
        crossFadeTransition.isCrossFadeEnabled = true
        return crossFadeTransition
    }

    companion object {

        private const val CROSS_FADE_DURATION = 200
        private const val DIALOG_CONFIG = "dialog_config"

        private fun newInstance(config: Config): ErrorDialogFragment {
            return ErrorDialogFragment().apply {
                this.isCancelable = false
                this.arguments = bundleOf(DIALOG_CONFIG to config)
            }
        }

        @Parcelize
        private data class Config(val error: Throwable) : Parcelable

        fun show(childFragmentManager: FragmentManager, error: Throwable) {
            childFragmentManager.beginTransaction().run {
                val config = Config(error)
                newInstance(config).show(this, null)
            }
        }
    }

    interface Callback {

        fun onPositiveButtonClicked(error: Throwable)
        fun onNegativeButtonClicked()
    }
}
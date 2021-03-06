package com.codinginflow.imagesearchapp.ui.details

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.codinginflow.imagesearchapp.MainActivity
import com.codinginflow.imagesearchapp.R
import com.codinginflow.imagesearchapp.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args by navArgs<DetailsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDetailsBinding.bind(view)

        changeActionBarVisibility(false)

        binding.apply {
            val photo = args.photo

            Glide.with(this@DetailsFragment)
                .load(photo.urls.full)
                .fitCenter()
                .error(R.drawable.ic_error)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = true
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isGone = true
                        textViewCreator.isVisible = true
                        textViewDescription.isVisible = photo.description != null
                        return false
                    }
                })
                .into(imageView)

            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, photo.urls.regular)
                type = "text/plain"
            }

            imageView.setOnLongClickListener {
                context?.startActivity(Intent.createChooser(shareIntent, null))
                true
            }

            textViewDescription.text = photo.description

            val uri = Uri.parse(photo.user.attributionUrl)

            val intent = Intent(Intent.ACTION_VIEW, uri)

            textViewCreator.apply {
                val creatorBeginning = context.getString(R.string.creator_beginning)
                val name = photo.user.name
                val creatorTail = context.getString(R.string.creator_tail)
                val creatorAndLink = creatorBeginning + name + creatorTail
                text = creatorAndLink
                setOnClickListener {
                    context.startActivity(intent)
                }
                paint.isUnderlineText = true
            }
        }
    }

    private fun changeActionBarVisibility(visibile: Boolean) {
        with(requireActivity()) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                (this as MainActivity).supportActionBar?.apply {
                    if (visibile) show()
                    else hide()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        changeActionBarVisibility(true)
    }
}
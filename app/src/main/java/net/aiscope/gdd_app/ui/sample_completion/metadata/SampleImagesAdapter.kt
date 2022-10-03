package net.aiscope.gdd_app.ui.sample_completion.metadata

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.ItemMetadataSampleImageBinding
import net.aiscope.gdd_app.model.CompletedCapture
import net.aiscope.gdd_app.ui.util.BitmapReader

class SampleImagesAdapter(
    private val uiScope: CoroutineScope,
    private val onAddImageClicked: () -> Unit,
    private val onImageClicked: (CompletedCapture) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val captures: MutableList<CompletedCapture> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            R.layout.item_metadata_add_image ->
                AddImageViewHolder(
                    layoutInflater.inflate(viewType, parent, false),
                    onAddImageClicked
                )
            R.layout.item_metadata_sample_image -> {
                val binding = ItemMetadataSampleImageBinding.inflate(layoutInflater, parent, false)
                CaptureViewHolder(
                    view = binding.root,
                    binding = binding,
                    uiScope = uiScope,
                    onImageClicked = onImageClicked
                )

            }
            else -> throw IllegalArgumentException("View type $viewType not known")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddImageViewHolder -> {}
            is CaptureViewHolder ->
                holder.bind(captures[position - 1])
            else -> throw IllegalArgumentException("View holder ${holder.javaClass} not known")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> R.layout.item_metadata_add_image
            else -> R.layout.item_metadata_sample_image
        }
    }

    override fun getItemCount(): Int {
        return captures.size + 1
    }

    fun setCaptures(captures: List<CompletedCapture>) {
        this.captures.clear()
        this.captures.addAll(captures.reversed())
        this.notifyDataSetChanged()
    }

}

private class AddImageViewHolder(view: View, private val onAddImageClicked: () -> Unit) :
    RecyclerView.ViewHolder(view) {

    init {
        itemView.setOnClickListener { onAddImageClicked() }
    }
}

private class CaptureViewHolder(
    view: View,
    private val binding: ItemMetadataSampleImageBinding,
    private val uiScope: CoroutineScope,
    onImageClicked: (CompletedCapture) -> Unit
) : RecyclerView.ViewHolder(view) {
    private lateinit var capture: CompletedCapture

    init {
        itemView.setOnClickListener { onImageClicked(capture) }
    }

    fun bind(capture: CompletedCapture) {
        this.capture = capture

        (itemView.tag as? Job)?.cancel()
        itemView.tag = uiScope.launch {
            with(binding) {
                sampleImage.setImageBitmap(null)
                val imageBmp = BitmapReader.decodeSampledBitmapAndCache(
                    capture.image,
                    itemView.context.resources.getDimensionPixelSize(R.dimen.sample_image_thumbnail_width),
                    itemView.context.resources.getDimensionPixelSize(R.dimen.sample_image_thumbnail_height),
                    itemView.context.cacheDir
                )
                sampleImage.setImageBitmap(imageBmp)

                sampleMask.setImageBitmap(null)
                if (!capture.maskIsEmpty) {
                    val maskBmp = BitmapReader.decodeSampledBitmapAndCache(
                        capture.mask,
                        itemView.context.resources.getDimensionPixelSize(R.dimen.sample_image_thumbnail_width),
                        itemView.context.resources.getDimensionPixelSize(R.dimen.sample_image_thumbnail_height),
                        itemView.context.cacheDir
                    )
                    sampleMask.setImageBitmap(maskBmp)
                }

                maskDot.isVisible = !capture.maskIsEmpty
            }
        }
    }
}

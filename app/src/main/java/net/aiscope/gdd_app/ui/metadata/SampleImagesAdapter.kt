package net.aiscope.gdd_app.ui.metadata

import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.util.BitmapReader
import java.io.File

class SampleImagesAdapter(
    private val uiScope: CoroutineScope,
    private val onAddImageClicked: () -> Unit,
    private val onImageClicked: (File, File) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val images: MutableList<File> = mutableListOf()
    private val masks: MutableList<File> = mutableListOf()
    private val hasMask: MutableList<Boolean> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_metadata_add_image -> AddImageViewHolder(view, onAddImageClicked)
            R.layout.item_metadata_sample_image -> ImageViewHolder(
                view,
                uiScope,
                onImageClicked
            )
            else -> throw IllegalArgumentException("View type $viewType not known")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddImageViewHolder -> {}
            is ImageViewHolder ->
                holder.bind(images[position - 1], masks[position - 1], hasMask[position - 1])
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
        return images.size + 1
    }

    fun setImages(images: List<File>) {
        this.images.clear()
        this.images.addAll(images.reversed())
        this.notifyDataSetChanged()
    }

    fun setMasks(masks: List<File>) {
        this.masks.clear()
        this.masks.addAll(masks.reversed())
        this.notifyDataSetChanged()
    }

    fun setHasMask(hasMask: List<Boolean>) {
        this.hasMask.clear()
        this.hasMask.addAll(hasMask.reversed())
        this.notifyDataSetChanged()
    }

}

private class AddImageViewHolder(view: View, private val onAddImageClicked: () -> Unit) :
    RecyclerView.ViewHolder(view) {

    init {
        itemView.setOnClickListener { onAddImageClicked() }
    }
}

private class ImageViewHolder(
    view: View, private val uiScope: CoroutineScope, onImageClicked: (File, File) -> Unit
) : RecyclerView.ViewHolder(view) {
    private lateinit var imageFile: File
    private lateinit var maskFile: File

    init {
        itemView.setOnClickListener { onImageClicked(imageFile, maskFile) }
    }

    fun bind(image: File, mask: File, hasMask: Boolean) {
        imageFile = image
        maskFile = mask

        val sampleImage: ImageView = itemView.findViewById(R.id.sample_image)
        val maskDotImage: ImageView = itemView.findViewById(R.id.mask_dot)

        (itemView.tag as? Job)?.cancel()
        itemView.tag = uiScope.launch {
            sampleImage.setImageBitmap(null)
            val bitmap = BitmapReader.decodeSampledBitmapAndCache(
                image,
                sampleImage.context.resources.getDimensionPixelSize(R.dimen.sample_image_thumbnail_width),
                sampleImage.context.resources.getDimensionPixelSize(R.dimen.sample_image_thumbnail_height),
                sampleImage.context.cacheDir
            )
            sampleImage.setImageBitmap(bitmap)

            if (hasMask) maskDotImage.visibility = VISIBLE
        }
    }
}

package net.aiscope.gdd_app.ui.metadata

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.extensions.writeToFile
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_metadata_add_image -> AddImageViewHolder(view, onAddImageClicked)
            R.layout.item_metadata_sample_image -> ImageViewHolder(view as ImageView, uiScope, onImageClicked)
            else -> throw IllegalArgumentException("View type $viewType not known")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddImageViewHolder -> {}
            is ImageViewHolder ->
                holder.bind(images[position - 1], masks[position - 1], holder.itemView.context.cacheDir)
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

}

private class AddImageViewHolder(view: View, private val onAddImageClicked: () -> Unit) :
    RecyclerView.ViewHolder(view) {

    init {
        itemView.setOnClickListener { onAddImageClicked() }
    }
}

private class ImageViewHolder(
    view: ImageView, private val uiScope: CoroutineScope, onImageClicked: (File, File) -> Unit
) : RecyclerView.ViewHolder(view) {
    private lateinit var imageFile: File
    private lateinit var maskFile: File

    init {
        itemView.setOnClickListener { onImageClicked(imageFile, maskFile) }
    }

    fun bind(image: File, mask: File, cacheDir: File) {
        imageFile = image
        maskFile = mask

        (itemView.tag as? Job)?.cancel()
        itemView.tag = uiScope.launch {
            (itemView as ImageView).setImageBitmap(null)
            val bitmap = decodeSampledBitmapAndCache(
                image,
                itemView.context.resources.getDimensionPixelSize(R.dimen.sample_image_thumbnail_width),
                itemView.context.resources.getDimensionPixelSize(R.dimen.sample_image_thumbnail_height),
                itemView.context.cacheDir
            )
            itemView.setImageBitmap(bitmap)
        }
    }
}

suspend fun decodeSampledBitmapAndCache(
    image: File,
    reqWidth: Int,
    reqHeight: Int,
    cacheDir: File
): Bitmap = withContext(Dispatchers.IO) {
    val cachedImage = File(
        cacheDir,
        "${image.nameWithoutExtension}_${reqWidth}x${reqHeight}.${image.extension}"
    )
    if (cachedImage.exists()) {
        return@withContext BitmapFactory.decodeFile(cachedImage.absolutePath)
    }
    val bitmap = BitmapReader.decodeSampledBitmapFromResource(image, reqWidth, reqHeight, false)

    //Write to cache for future access
    bitmap.writeToFile(cachedImage, Bitmap.CompressFormat.JPEG)

    bitmap
}



package net.aiscope.gdd_app.ui.metadata

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import net.aiscope.gdd_app.R
import java.io.File

class SampleImagesAdapter(private val onAddImageClicked: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val images: MutableList<File> = mutableListOf()

    private val uiScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_metadata_add_image -> AddImageViewHolder(view, onAddImageClicked)
            R.layout.item_metadata_sample_image -> ImageViewHolder(view as ImageView)
            else -> throw IllegalArgumentException("View type $viewType not known")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AddImageViewHolder -> holder.bind()
            is ImageViewHolder -> {
                (holder.itemView.tag as? Job)?.cancel()
                holder.itemView.tag = uiScope.launch(Dispatchers.IO) {
                    holder.bind(images[position - 1])
                }
            }
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
        this.images.addAll(images)
        this.notifyDataSetChanged()
    }
}

private class AddImageViewHolder(view: View, val onAddImageClicked: () -> Unit) :
    RecyclerView.ViewHolder(view) {
    fun bind() {
        itemView.setOnClickListener { onAddImageClicked() }
    }
}

private class ImageViewHolder(view: ImageView) : RecyclerView.ViewHolder(view) {
    suspend fun bind(image: File) {
        withContext(Dispatchers.Main) {
            (itemView as ImageView).setImageBitmap(null)
        }
        val bitmap = decodeSampledBitmapFromResource(
            image,
            itemView.context.resources.getDimensionPixelSize(R.dimen.sample_image_thumbnail_width),
            itemView.context.resources.getDimensionPixelSize(R.dimen.sample_image_thumbnail_height)
        )
        withContext(Dispatchers.Main) {
            (itemView as ImageView).setImageBitmap(bitmap)
        }
    }
}

suspend fun decodeSampledBitmapFromResource(
    image: File,
    reqWidth: Int,
    reqHeight: Int
): Bitmap = withContext(Dispatchers.IO) {
    // First decode with inJustDecodeBounds=true to check dimensions
    BitmapFactory.Options().run {
        inJustDecodeBounds = true
        BitmapFactory.decodeFile(image.absolutePath, this)

        // Calculate inSampleSize
        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        inJustDecodeBounds = false

        BitmapFactory.decodeFile(image.absolutePath, this)
    }
}

suspend fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int = withContext(Dispatchers.IO) {
    // Raw height and width of image
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return@withContext inSampleSize
}

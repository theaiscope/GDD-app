package net.aiscope.gdd_app.ui.metadata

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import net.aiscope.gdd_app.R
import java.io.File

class SampleImagesAdapter(private val onAddImageClicked: () -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val images: MutableList<File> = mutableListOf()

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
            is ImageViewHolder -> holder.bind(images[position - 1])
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
    fun bind(image: File) {
        (itemView as ImageView).setImageURI(
            Uri.fromFile(image)
        )

    }
}


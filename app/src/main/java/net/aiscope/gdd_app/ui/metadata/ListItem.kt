package net.aiscope.gdd_app.ui.metadata

data class ListItem(val id: Long? = null, val name: String? = null) {

    override fun toString(): String {
        return name.orEmpty()
    }
}
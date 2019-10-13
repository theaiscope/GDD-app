package net.aiscope.gdd_app.ui.metadata

data class ListItem(val id: Long, val name: String) {

    override fun toString(): String {
        return name.orEmpty()
    }
}

package org.mozilla.reference.browser.library

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.TextView
import org.mozilla.reference.browser.R
import org.mozilla.reference.browser.ext.components
import org.mozilla.reference.browser.ext.loadIntoView

/**
 * Borrowed from the Fenix project
 */

interface MultiSelectionInteractor<T> {
    fun open(item: T)

    fun select(item: T)

    fun deselect(item: T)

    fun onDeleteSome(items: Set<T>)

    fun onBackPressed(): Boolean
}

interface SelectionHolder<T> {
    val selectedItems: Set<T>
}

class LibraryItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val titleView: TextView
    val urlView: TextView
    val metaButton: ImageButton

    private val iconView: ImageSwitcher
    private val favicon: ImageView

    init {
        LayoutInflater.from(context).inflate(LAYOUT_ID, this, true)
        titleView = findViewById(R.id.title_view)
        urlView = findViewById(R.id.url_view)
        iconView = findViewById(R.id.icon_view)
        favicon = findViewById(R.id.favicon)
        metaButton = findViewById(R.id.meta_btn)
        clipToPadding = false
    }

    fun loadFavicon(url: String) {
        context.components.core.icons.loadIntoView(favicon, url)
    }

    fun toggleMetaButton(showMetaButton: Boolean) {
        metaButton.visibility = if (showMetaButton) View.VISIBLE else View.INVISIBLE
    }

    fun toggleIconView(showCheckMark: Boolean) {
        val changedIconType = if (showCheckMark) ICON_TYPE_CHECK_MARK else ICON_TYPE_FAVICON
        if (iconView.displayedChild != changedIconType) {
            iconView.displayedChild = changedIconType
        }
    }

    fun <T> setSelectionInteractor(
        item: T,
        holder: SelectionHolder<T>,
        interactor: MultiSelectionInteractor<T>
    ) {
        setOnClickListener {
            val selected = holder.selectedItems
            when {
                selected.isEmpty() -> interactor.open(item)
                item in selected -> interactor.deselect(item)
                else -> interactor.select(item)
            }
        }

        setOnLongClickListener {
            if (item !in holder.selectedItems) {
                interactor.select(item)
            } else {
                interactor.deselect(item)
            }
            true
        }
    }

    companion object {
        const val LAYOUT_ID = R.layout.two_line_list_item_layout

        private const val ICON_TYPE_FAVICON = 0
        private const val ICON_TYPE_CHECK_MARK = 1
    }
}

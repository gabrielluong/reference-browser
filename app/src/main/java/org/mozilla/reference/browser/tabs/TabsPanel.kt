/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.reference.browser.tabs

import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import mozilla.components.feature.tabs.tabstray.TabsFeature
import org.mozilla.reference.browser.R

class TabsPanel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TabLayout(context, attrs), TabLayout.OnTabSelectedListener {
    private var normalTab: Tab
    private var privateTab: Tab
    private var workContainer: Tab
    private var tabsFeature: TabsFeature? = null
    private var updateTabsToolbar: ((isPrivate: Boolean, contextId: String) -> Unit)? = null

    init {
        normalTab = newTab().apply {
            contentDescription = "Tabs"
            icon = resources.getThemedDrawable(R.drawable.mozac_ic_tab)
        }

        privateTab = newTab().apply {
            contentDescription = "Private tabs"
            icon = resources.getThemedDrawable(R.drawable.mozac_ic_private_browsing)
        }

        workContainer = newTab().apply {
            contentDescription = "Work"
            icon = resources.getThemedDrawable(R.drawable.mozac_ic_briefcase)
            tag = "Work"
        }

        addOnTabSelectedListener(this)

        addTab(normalTab)
        addTab(privateTab)
        addTab(workContainer)
    }

    fun initialize(
        tabsFeature: TabsFeature?,
        updateTabsToolbar: (isPrivate: Boolean, contextId: String) -> Unit
    ) {
        this.tabsFeature = tabsFeature
        this.updateTabsToolbar = updateTabsToolbar
    }

    override fun onTabSelected(tab: Tab?) {
        // Tint the selected tab's icon.
        tab?.icon?.colorTint(R.color.photonPurple50)

        tabsFeature?.filterTabs { tabSessionState ->
            when (tab) {
                normalTab -> !tabSessionState.content.private
                privateTab -> tabSessionState.content.private
                else -> tabSessionState.contextId == tab?.tag
            }
        }

        val contextId = ""
        updateTabsToolbar?.invoke(tab == privateTab, contextId)
    }

    override fun onTabReselected(tab: Tab?) {
        // no-op
    }

    override fun onTabUnselected(tab: Tab?) {
        // Clear the tint for the unselected tab's icon.
        tab?.icon?.colorFilter = null
    }

    private fun Resources.getThemedDrawable(@DrawableRes resId: Int) =
        getDrawable(resId, context.theme)

    private fun Drawable.colorTint(@ColorRes color: Int) = apply {
        mutate()
        setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_IN)
    }
}

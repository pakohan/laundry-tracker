package com.pakohan.laundrytracker.ui.nav.preferences

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NamedNavArgument
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import com.pakohan.laundrytracker.R
import com.pakohan.laundrytracker.data.entity.UserPreferences
import com.pakohan.laundrytracker.ui.PreferencesViewModelFactory
import com.pakohan.laundrytracker.ui.nav.TabNavigationDestination
import com.pakohan.laundrytracker.ui.partials.Link
import com.pakohan.laundrytracker.ui.partials.LinkData
import com.pakohan.laundrytracker.ui.partials.LogoAttribution

object PreferencesDestination : TabNavigationDestination {
    override val route = "preferences"
    override val icon: ImageVector = Icons.Filled.Settings
    override val titleRes = R.string.preferences
    override val arguments: List<NamedNavArgument> = emptyList()
}

@Composable
fun Preferences(
    modifier: Modifier = Modifier,
    viewModel: PreferencesViewModel = viewModel(factory = PreferencesViewModelFactory()),
) {
    val settings by viewModel.settings.collectAsState()
    PreferencesPane(
        modifier,
        settings,
        viewModel::setEnableTimer,
        viewModel::setTimerDurationSeconds,
    )
}

@Composable
private fun PreferencesPane(
    modifier: Modifier = Modifier,
    settings: UserPreferences,
    onCheckedChange: (Boolean) -> Unit,
    onTimerDurationChange: (Int) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SettingsSwitch(
            state = settings.enableTimer,
            title = { Text(stringResource(R.string.enable_timer_setting)) },
            enabled = true,
            onCheckedChange = onCheckedChange,
        )
        SettingsSlider(
            value = (settings.timerDurationSeconds / 60).toFloat(),
            valueRange = 30f..90f,
            steps = 5,
            title = { Text("Timer duration") },
            subtitle = { Text("${settings.timerDurationSeconds / 60} minutes") },
            enabled = settings.enableTimer,
            onValueChange = { onTimerDurationChange(it.toInt() * 60) },
        )
        Spacer(modifier = Modifier.weight(1f))
        LogoAttribution(
            modifier = Modifier.padding(16.dp),
            style = TextStyle.Default.copy(textAlign = TextAlign.Center),
            linkData = LinkData(
                fullText = "Launcher icon by vectordoodle under CC-BY license",
                linksList = listOf(
                    Link(
                        linkText = "vectordoodle",
                        linkInfo = "https://www.svgrepo.com/svg/453127/laundry",
                        onClick = {
                            val url = "https://www.svgrepo.com/svg/453127/laundry"
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setData(Uri.parse(url))
                            startActivity(
                                context,
                                intent,
                                Bundle(),
                            )
                        },
                    ),
                ),
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreferencesPanePreview() {
    var settings by remember {
        mutableStateOf(UserPreferences())
    }
    PreferencesPane(
        settings = settings,
        onCheckedChange = { settings = settings.copy(enableTimer = it) },
        onTimerDurationChange = { settings = settings.copy(timerDurationSeconds = it) },
    )
}

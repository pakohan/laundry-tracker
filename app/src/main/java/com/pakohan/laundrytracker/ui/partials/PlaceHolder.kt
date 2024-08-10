package com.pakohan.laundrytracker.ui.partials

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PlaceHolder(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) = Column(
    modifier = modifier
        .fillMaxSize()
        .padding(40.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
) {
    Spacer(modifier = Modifier.weight(1f))
    val mergedStyle = LocalTextStyle.current.merge(MaterialTheme.typography.titleLarge)
        .copy(textAlign = TextAlign.Center)
    CompositionLocalProvider(
        LocalTextStyle provides mergedStyle,
        content = content,
    )
    Spacer(modifier = Modifier.weight(1f))
}

@Preview(showBackground = true)
@Composable
fun PreviewPlaceHolder() = PlaceHolder {
    Text(
        text = """Start by adding your first laundry item.
Press + below.""".trimMargin(),
    )
}

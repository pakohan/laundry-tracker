package com.pakohan.laundrytracker.ui.partials

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview

@Immutable
data class LinkData(
    val fullText: String,
    val linksList: List<Link>,
)

@Immutable
data class Link(
    val linkText: String,
    val linkInfo: String,
    val onClick: (String) -> Unit,
)

@Composable
fun ClickableText(
    modifier: Modifier = Modifier,
    linkData: LinkData,
) {
    val annotatedString = buildAnnotatedString {
        append(linkData.fullText)
        linkData.linksList.forEach { link ->
            var startIndex = linkData.fullText.indexOf(link.linkText)
            while (startIndex >= 0) {
                val endIndex = startIndex + link.linkText.length
                addStyle(
                    style = SpanStyle(color = MaterialTheme.colorScheme.secondary),
                    start = startIndex,
                    end = endIndex,
                )
                addStringAnnotation(
                    tag = link.linkText,
                    annotation = link.linkInfo,
                    start = startIndex,
                    end = endIndex,
                )
                startIndex = linkData.fullText.indexOf(
                    link.linkText,
                    endIndex,
                )
            }
        }
    }

    ClickableText(
        modifier = modifier.semantics {
            this.contentDescription = "save laundry item"
        },
        style = LocalTextStyle.current.copy(color = LocalContentColor.current),
        text = annotatedString,
        onClick = { position ->
            linkData.linksList.forEach { link ->
                annotatedString.getStringAnnotations(
                    link.linkText,
                    position,
                    position,
                )
                    .firstOrNull()
                    ?.let {
                        link.onClick.invoke(it.item)
                        return@forEach
                    }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun LogoAttributionPreview() {
    ClickableText(
        linkData = LinkData(
            fullText = "Some sample text with a link. Click here to visit profile screen.",
            linksList = listOf(
                Link(
                    linkText = "link",
                    linkInfo = "https://example.com/",
                    onClick = { },
                ),
                Link(
                    linkText = "Click here",
                    linkInfo = "",
                    onClick = { },
                ),
            ),
        ),
    )
}

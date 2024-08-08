package com.pakohan.laundrytracker.ui.partials

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Preview
@Composable
fun TextInputDialog(
    show: Boolean = true,
    title: String = "Input text",
    onVisibilityChange: (Boolean) -> Unit = {},
    onConfirm: (String) -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var input by remember { mutableStateOf("") }
    if (!show) {
        input = ""
    } else {
        val focusRequester = remember { FocusRequester() }

        val submit = {
            keyboardController?.hide()
            onConfirm(input)
            onVisibilityChange(false)
        }

        Dialog(onDismissRequest = { onVisibilityChange(false) }) {
            Card {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    TextField(
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .padding(top = 16.dp),
                        singleLine = true,
                        value = input,
                        onValueChange = { input = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { submit() },
                        ),
                    )
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                    TextButton(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 24.dp),
                        onClick = submit,
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

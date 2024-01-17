package com.parisjohn.pricemonitoring.base.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.parisjohn.pricemonitoring.ui.theme.Purple40


@Composable
fun DialogSelection(
    title: String,
    optionsList: List<String>,
    onSubmitButtonClick: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var newListText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismissRequest.invoke() }) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        ) {

            Column(modifier = Modifier.padding(10.dp)) {

                androidx.compose.material3.Text(
                    text = title,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                CustomInputView(text = newListText,
                    onValueChange = {
                        newListText = it
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))

                Button(onClick = {
                    onSubmitButtonClick.invoke(newListText)
                    onDismissRequest.invoke()
                },   colors = ButtonDefaults.buttonColors(backgroundColor = Purple40),
                    modifier = Modifier.fillMaxWidth()) {

                    Icon(Icons.Filled.Add, contentDescription = "Add")

                }

                Text(
                    text = "----- or -----",
                    fontSize = 18.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal
                )


                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn() {
                    items(optionsList) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                onSubmitButtonClick.invoke(it)
                                onDismissRequest.invoke()
                            }) {
                            RadioButton(
                                selected = false,
                                onClick = {
                                    onSubmitButtonClick.invoke(it)
                                    onDismissRequest.invoke()
                                }
                            )
                            Text(
                                text = it,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

        }
    }
}


@Composable
fun CustomInputView(
    text: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) = Box(
    modifier = modifier
        .clip(CircleShape)
        .background(Color(0XFF101921))

) {
    TextField(value = text,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color(0xFFEEEEEE),
            placeholderColor = Color(0xFF606366),
            textColor = Color.Black,
            focusedIndicatorColor = Color.Transparent, cursorColor = Color(0XFF070E14)
        ),
        placeholder = { Text(text = "Create New Monitor List") }
    )
}
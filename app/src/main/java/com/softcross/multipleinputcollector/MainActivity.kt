package com.softcross.multipleinputcollector

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.softcross.multipleinputcollector.ui.theme.MultipleInputCollectorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultipleInputCollectorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AutoComplete(
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoComplete(modifier: Modifier) {

    val animals = listOf(
        "Lion",
        "Tiger",
        "Leopard",
        "Cheetah",
        "Panda",
        "Gorilla",
    )
    var word by remember { mutableStateOf("") }
    var taggedWords by remember { mutableStateOf(emptyList<String>()) }
    var isExpand by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var textFieldPositionX by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 16.dp),
    ) {
        SelectionLayout(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
                .background(CardDefaults.cardColors().containerColor, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .padding(vertical = 4.dp)

        ) {
            taggedWords.forEach { word ->
                TaggedWordItem(word = word) {
                    taggedWords = taggedWords - word
                }
            }
            BasicTextField(
                modifier = Modifier
                    .then(
                        if (taggedWords.isNotEmpty()) {
                            Modifier.width(100.dp)
                        } else {
                            Modifier.fillMaxWidth()
                        }
                    )
                    .onGloballyPositioned { coordinates ->
                        textFieldPositionX = coordinates.positionInWindow().x / 2
                    },
                value = word,
                onValueChange = {
                    word = it
                    isExpand = it.isNotEmpty()
                },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (word.isNotEmpty()) {
                            taggedWords = taggedWords + word
                            word = ""
                        }
                    }
                ),
                singleLine = true,
                decorationBox = @Composable { innerTextField ->
                    TextFieldDefaults.DecorationBox(
                        value = word,
                        innerTextField = innerTextField,
                        placeholder = {
                            Text(
                                "Enter a key",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        enabled = true,
                        singleLine = true,
                        contentPadding = PaddingValues(8.dp),
                        interactionSource = interactionSource,
                        visualTransformation = VisualTransformation.None,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        )
                    )
                })
        }



        AnimatedVisibility(
            visible = isExpand,
            modifier = Modifier.graphicsLayer { translationX = textFieldPositionX }) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .graphicsLayer {
                        translationY = 50f
                    },
                shape = SelectionShape(12f),
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 150.dp),
                ) {
                    items(
                        animals.filter {
                            it.lowercase()
                                .contains(word.lowercase()) || it.lowercase()
                                .contains("others")
                        }.sortedByDescending {
                            word.lowercase().firstOrNull() == it.lowercase().first()
                        }
                    ) {
                        ItemsCategory(title = it) { title ->
                            isExpand = false
                            taggedWords = taggedWords + title
                            word = ""
                        }
                    }
                    item {
                        ItemsCategory(title = "Add") { title ->
                            isExpand = false
                            taggedWords = taggedWords + word
                            word = ""
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemsCategory(
    title: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable {
                onSelect(title)
            }
            .fillMaxWidth(0.4f)
            .padding(10.dp)
    ) {
        Text(text = title, fontSize = 16.sp)
    }
}

@Composable
fun TaggedWordItem(
    word: String,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = word,
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                start = 4.dp,
                top = 4.dp,
                bottom = 4.dp,
                end = 1.dp
            )
        )
        Icon(
            Icons.Default.Clear,
            contentDescription = "",
            modifier = Modifier
                .padding(
                    start = 1.dp,
                    end = 4.dp,
                    top = 4.dp,
                    bottom = 4.dp
                )
                .clickable {
                    onRemove()
                }
        )
    }
}
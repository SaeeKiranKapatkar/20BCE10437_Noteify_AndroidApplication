package com.example.vit20bps1033ass3


import android.content.Intent
import android.icu.text.Normalizer.NO
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.semantics.SemanticsActions.OnClick
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.vit20bps1033ass3.Database.NoteDatabase
import com.example.vit20bps1033ass3.model.NoteViewModel
import com.example.vit20bps1033ass3.model.NoteViewModelFactory
import com.example.vit20bps1033ass3.model.Notes
import com.example.vit20bps1033ass3.repository.NoteRepository
import com.google.android.material.color.utilities.MaterialDynamicColors.background


class MainActivity : ComponentActivity() {
    private lateinit var viewModel : NoteViewModel
    private lateinit var db : NoteDatabase
    private lateinit var repo : NoteRepository
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // FONT
        val lato = FontFamily(
            Font(R.font.latobold, FontWeight.Bold),
            Font(R.font.latoregular, FontWeight.Normal)
        )
        setContent {
            db = NoteDatabase.getDatabase(this)
            repo = NoteRepository(db.notesDao())
            viewModel = ViewModelProvider(this, NoteViewModelFactory(repo))[NoteViewModel::class.java]

            val notes by viewModel.getNotes.observeAsState()
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF9F9F9))
                    .padding(top = 30.dp, start = 30.dp, end = 30.dp, bottom = 30.dp)
            ) {
                Text(modifier = Modifier.padding(bottom = 18.dp), text = "Noteify",
                    fontWeight = FontWeight.Bold,fontSize = 32.sp, textAlign = TextAlign.Left, fontFamily = lato,
                color = Color(0xff383838)
                )
                LazyColumn(){
                    notes?.let{items ->
                        items(items){
                            Card(
                                modifier = Modifier
                                    .padding(bottom = 23.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        val intent =
                                            Intent(this@MainActivity, GetNotesActivity::class.java)
                                        intent.putExtra("noteType", "Edit")
                                        intent.putExtra("noteTitle", it.title)
                                        intent.putExtra("noteDescription", it.description)
                                        intent.putExtra("noteId", it.id)
                                        startActivity(intent)
                                        finish()
                                    },
                                elevation = 4.dp, backgroundColor = Color(0xFFE4DCCF),
                                shape = RoundedCornerShape(corner = CornerSize(12.dp))
                            ) {
                                Row(modifier = Modifier.align(Alignment.Start)) {
                                    Column() {
                                        Text(
                                            text = it.title,
                                            style = TextStyle(
                                                color = Color(0xff383838),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(top = 15.dp, start = 10.dp),
                                            fontFamily = lato
                                        )
                                        Text(
                                            text = it.timeStamp,
                                            style = TextStyle(
                                                color = Color(0xff6F6F6F),
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                        modifier = Modifier.padding(top = 25.dp, start = 10.dp, bottom = 15.dp), fontFamily = lato
                                    )

                                }
                            }
                                Row(modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 35.dp, bottom = 30.dp, start = 262.dp)){
                                    Image(painter = painterResource(id = R.drawable.share), contentDescription = null,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable {

                                                val intent = Intent()
                                                intent.action = Intent.ACTION_SEND_MULTIPLE
                                                intent.putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "Title : " + it.title + "\n" + " Description: " + it.description
                                                )
                                                intent.type = "text/plain"
                                                startActivity(
                                                    Intent.createChooser(
                                                        intent,
                                                        "Send to : "
                                                    )
                                                )
                                            })
                                }

                                Row(modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(top = 35.dp, bottom = 30.dp, start = 310.dp)){
                                    Image(painter = painterResource(id = R.drawable.delete), contentDescription = null,
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clickable {
                                                // DELETE NOTE
                                                viewModel.deleteNote(it)
                                            })
                                }
                            }
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {

                    AddNew()
                }
            }
        }
    }


@Composable
fun AddNew() {
        Box(modifier = Modifier.fillMaxSize()) {
            val openDialog = remember {
                mutableStateOf(false)
            }

            FloatingActionButton(
                modifier = Modifier.align(alignment = Alignment.BottomEnd),
                onClick = {
                    openDialog.value = true
                },
                backgroundColor = Color.White,
                contentColor = Color(0xFF7D9D9C),
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, "", Modifier.clickable {
                    // CREATE NEW NOTE
                    val intent = Intent(
                    this@MainActivity,
                    GetNotesActivity::class.java
                )
                    startActivity(intent)
                    finish() })
            }
        }
    }
}

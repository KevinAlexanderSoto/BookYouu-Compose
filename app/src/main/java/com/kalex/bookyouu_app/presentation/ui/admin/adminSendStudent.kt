package com.kalex.bookyouu_app.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.kalex.bookyouu_app.R
import com.kalex.bookyouu_app.camara.CameraCapture
import com.kalex.bookyouu_app.presentation.composables.ButtonText
import com.kalex.bookyouu_app.presentation.composables.Drawer
import com.kalex.bookyouu_app.presentation.composables.Icono
import com.kalex.bookyouu_app.presentation.composables.Imagen
import com.kalex.bookyouu_app.presentation.theme.blanco
import com.kalex.bookyouu_app.presentation.theme.bookYouuPrimary
import com.kalex.bookyouu_app.presentation.validations.getFileSizeFloat
import com.kalex.bookyouu_app.presentation.validations.validarString
import com.kalex.bookyouu_app.presentation.viewModels.OficesViewModel
import com.kalex.bookyouu_app.presentation.viewModels.PostDocumentViewModel
import com.kalex.usodecamara.galeria.GallerySelect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.kalex.bookyouu_app.common.getCapturedImage
import java.io.ByteArrayOutputStream
import android.util.Base64
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import com.kalex.bookyouu_app.common.getGaleryImage
import java.io.IOException
import java.io.InputStream
import kotlin.collections.ArrayList


@ExperimentalPermissionsApi
@Composable
fun AdminCreateStudent(
    navController: NavHostController,
    oficesViewModel: OficesViewModel = hiltViewModel(),
    postDocumentViewModel : PostDocumentViewModel = hiltViewModel()
) {

    val resp = oficesViewModel.state.value
    val correo = oficesViewModel.correo
    //para barra lateral
    val scaffoldState = rememberScaffoldState(
        drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    )
    val scope = rememberCoroutineScope()

    //barra de cargando
    if (resp.isLoading){
        Box(modifier = Modifier.fillMaxSize()
            , contentAlignment = Alignment.Center
        ){
            CircularProgressIndicator(modifier = Modifier
                .fillMaxSize(0.1f)

            )
        }
    }
    //Logica para obtener ciudades de la API
    if (!resp.isLoading) {
        ToolBar(
            navController,
            scope,
            scaffoldState,
            correo,
            postDocumentViewModel
        )

    }
}
@ExperimentalPermissionsApi
@Composable
fun ToolBar(
    navController :NavHostController,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    correo: String,
    postDocumentViewModel : PostDocumentViewModel
){
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
        TopAppBar(
            title = { Text(text = "Agregar Estudiante") },
            navigationIcon = {
                IconButton(onClick = {
                    navController.popBackStack()
                }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "go back"
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    scope.launch {
                    scaffoldState.drawerState.open()
                } }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "menu hamburgesa"
                    )
                }
            },
        )
    },
        drawerContent = { Drawer(scope, scaffoldState, navController) },
        drawerGesturesEnabled = true

        ) {
        FormularioDoc(correo,postDocumentViewModel)
    }
}

@ExperimentalPermissionsApi
@Composable
fun FormularioDoc(
    correo: String,
    postDocumentViewModel: PostDocumentViewModel

) {
    Column (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),// para hacer scroll
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
            ){

        //------------------------------------Formulario---------------------------------
        val listaDocumento = listOf("CC", "TI", "CE", "PA")
        val listaCarreras = listOf("Ing.Sitemas","Sicologia","Salud ocupacional")
        // manejar focus de los texto,
        val localFocusManager = LocalFocusManager.current
        Spacer(Modifier.size(4.dp))

        val menu1= dropDownMenu(listaDocumento, nombreInput = "Tipo de Documento")
        val text1 :String = InputText(label = "Numero de documento",onAction = {
            // bajar al siguiente field
            localFocusManager.moveFocus(FocusDirection.Down)
        })
        val text2 = InputText(label = "Nombre",onAction = {
            // bajar al siguiente field
            localFocusManager.moveFocus(FocusDirection.Down)
        })
        val text3 = InputText(label = "Apellido",onAction = {
            // bajar al siguiente field
            localFocusManager.moveFocus(FocusDirection.Down)
        })
        val text4 = InputText(label = "Correo",correo,onAction = {
            // bajar al siguiente field
            localFocusManager.moveFocus(FocusDirection.Down)
        })
        val menu2=dropDownMenu(listaCarreras, nombreInput = "Carrera")





//-------------------validaciones para habilitar enviar data---------------------------
           val validacion = validarString(text1)&&validarString(text2)&&validarString(text3) &&validarString(text4)&&validarString(menu1)&&validarString(menu2)

//-------------------Armado del body para Post Document---------------------------
        //Crear Body para mandar
        var requestBody : RequestBody? =null

        if(validacion){
//-------------------Convertir Base 64---------------------------

            // Create JSON using JSONObject
            val jsonObject = JSONObject()
            jsonObject.put("TipoId", menu1)
            jsonObject.put("Identificacion", text1)
            jsonObject.put("Nombre", text2)
            jsonObject.put("Apellido", text3)
            jsonObject.put("Ciudad", menu2)
            jsonObject.put("Correo", text4)

            var jsonObjectString = jsonObject.toString()

            requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())
        }

        BtnEnviarImg(validacion,postDocumentViewModel,requestBody)

    }

}


@Composable
fun InputText(
label : String,
initialValue :String="",
onAction: () -> Unit
):String{
    var text by remember { mutableStateOf(initialValue ) }
    TextField(
        value = text,
        singleLine = true,
        onValueChange = { text = it },
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .background(Color.White),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions (onDone = {
            onAction()
        }),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
        )
    )
        return text
}

@Composable
fun dropDownMenu(
    listaDocumento: List<String>,
    nombreInput:String):String
{

    var expanded by remember { mutableStateOf(false) }
    val suggestions = listaDocumento
    var selectedText by remember { mutableStateOf("") }

    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column {
    TextField(
        value = selectedText,
        onValueChange = { selectedText = it },
        readOnly = true,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .onGloballyPositioned { coordinates ->
                //This value is used to assign to the DropDown the same width
                textfieldSize = coordinates.size.toSize()
            }
            .background(Color.White),
        label = { Text(nombreInput) },
        trailingIcon = {
            Icon(icon, "contentDescription",
                Modifier.clickable { expanded = !expanded })
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
        )
    )
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(with(LocalDensity.current) { textfieldSize.width.toDp() })
    ) {
        suggestions.forEach { label ->
            DropdownMenuItem(onClick = {
                selectedText = label
                expanded = false
            }) {
                Text(text = label)
            }
        }
    }
}
        return selectedText
}

@Composable
fun BtnEnviarImg(
    validacion: Boolean,
    postDocumentViewModel: PostDocumentViewModel,
    requestBody: RequestBody?
) {
    val context = LocalContext.current
    Button(
        onClick = {
            requireNotNull(requestBody)
            postDocumentViewModel.postDocument(requestBody)

            runBlocking {
                delay(100)
            }


        },
        modifier = Modifier
            .padding(top = 25.dp, bottom = 12.dp)
            .fillMaxWidth(0.8f)
        ,
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(23.dp),
        contentPadding = PaddingValues(9.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = bookYouuPrimary,
            contentColor = blanco
        ),
        enabled = validacion
    ) {
        var resp = postDocumentViewModel.state
        resp.value.respuesta
        println(resp.value)
        if(resp.value.respuesta != null){
            Toast.makeText(context," Enviado Exitosamente",Toast.LENGTH_LONG).show()
            println(resp.value)
        }

        Icono(url = R.drawable.send_24, valor = 25)
        Spacer(Modifier.size(4.dp))
        ButtonText("Enviar",20)

    }
}

@ExperimentalPermissionsApi
@Composable
fun BtncargarImg(
    texto: String,
    icono :Any
):Boolean{

    var click by remember { mutableStateOf(false) }
    Button(
        onClick = {
            click = true
        },
        modifier = Modifier
            .padding(top = 23.dp)
            ,
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(9.dp),
        contentPadding = PaddingValues(8.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = bookYouuPrimary,
            contentColor = blanco
        ),
        enabled = true
    ) {

        Icono(icono,20)
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        ButtonText(texto,15)

    }

    return click
}

val EMPTY_IMAGE_URI: Uri = Uri.parse("file://dev/null")

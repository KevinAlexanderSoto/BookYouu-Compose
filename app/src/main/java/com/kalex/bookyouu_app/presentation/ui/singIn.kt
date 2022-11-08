package com.kalex.bookyouu_app.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kalex.bookyouu_app.R
import com.kalex.bookyouu_app.presentation.composables.ButtonText
import com.kalex.bookyouu_app.presentation.composables.Icono
import com.kalex.bookyouu_app.presentation.composables.Imagen
import com.kalex.bookyouu_app.presentation.theme.blanco
import com.kalex.bookyouu_app.presentation.theme.bookYouuPrimary
import com.kalex.bookyouu_app.presentation.validations.Emailvalidation
import com.kalex.bookyouu_app.presentation.viewModels.SingInViewModel
import com.kalex.bookyouu_app.presentation.viewModels.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

@Composable fun SingIn(
    navController: NavController,
    signInviewModel : SingInViewModel = hiltViewModel(),
){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),// para hacer scroll
        verticalArrangement = Arrangement.spacedBy(11.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val resources = LocalContext.current.resources
        Text(text = resources.getString(R.string.login_title_text),
            style = MaterialTheme.typography.h6,
            fontSize = 40.sp,
            color = colors.secondary,
            modifier = Modifier
                .padding(30.dp)

            )

        Textfield(resources.getString(R.string.login_subtitle_text))
        Spacer(modifier = Modifier
            .padding(30.dp))
        // manejar focus de los texto,
        val localFocusManager = LocalFocusManager.current

        //state hoisting Email
        val text = remember { Emailvalidation() }

        EmailField(
            text.correo,
            text.error,
            onAction = {
                // bajar al siguiente field
                localFocusManager.moveFocus(FocusDirection.Down)
            }
        ){

            text.correo = it
            text.validate()
        }

        //state hoisting Password
        var password = remember { mutableStateOf("") }
        PasswordFiels(
            password.value,
            onAction = { localFocusManager.clearFocus() }
        )
        { password.value = it
        }

        //viewModel.getUser(text.correo,password.value)
        //var resp = viewModel.state.value
        Buttonin(habilitado = text.valid(),signInviewModel,navController,text.correo,password.value )


    }


}

@Composable
fun Buttonin(
    habilitado: Boolean,
    viewModel: SingInViewModel,
    navController: NavController,
    correo: String,
    contraseña :String
) {
    val context = LocalContext.current
    Button(
        onClick = {
            runBlocking {
                launch {
                    delay(100L)
                }
            }

            //println("Respuesta de server: $resp")

                //TODO: IMPLEMENT THIS WHIT FIRE BASE
                // val acceso = user.acceso
                val acceso = true
                println("acceso $acceso")
                // println("respuesta${resp.user}")
                if (acceso == true){

                    Toast.makeText(context,"Acceso concedido",Toast.LENGTH_LONG).show()
                    //TODO: CHECK IF WE NEED THIS
                    // viewModel.saveAll(nombre = user.nombre,correo = correo, contraseña = contraseña)
                    // navController.navigate("home/${resp.user?.nombre}")
                    navController.navigate("adminhome/${"prueba"}") //TODO :  HERE WE CAN SEND THE CONTEXT OR USER TYPE

                }else if(acceso == false){
                    Toast.makeText(context,"El Correo o la Contraseña son incorrectos",Toast.LENGTH_LONG).show()

                }



        },
        modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxWidth(0.8f)
        ,
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(23.dp),
        contentPadding = PaddingValues(12.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = bookYouuPrimary,
            contentColor = blanco
        ),
        enabled = habilitado
    ) {

        Icono(R.drawable.outline_login_24,30)
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        ButtonText("Ingresar",22)

    }
    //if(resp.isLoading) CircularProgressIndicator()
}

@Composable
fun Textfield(texto: String ) {
    Text(text = texto,
        style = MaterialTheme.typography.h6,
        color = colors.secondary,
        modifier = Modifier
            .padding(4.dp)
    )
}

@Composable
fun EmailField(
    text: String,
    error: String?,
    onAction: () -> Unit,
    onEmailChanged: (String) -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


    TextField(
        value = text,
        singleLine = true,
        onValueChange = { onEmailChanged(it)},
        label = { Text(text = "Email") },
        placeholder = { Text("example@gmail.com") },
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(0.9f),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            textColor = colors.secondary
        ),
        shape = RoundedCornerShape(9.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
            ) ,
        keyboardActions = KeyboardActions (onNext = {
            onAction()
        }),
        isError = error != null
    )
        error?.let{ FielError(it) }
    }
}

@Composable
fun FielError(it: String) {
    Text(
        text = it,
        style = TextStyle(color =colors.error)
    )
}

@Composable
fun PasswordFiels(
    pass: String,
    onAction: () -> Unit,
    onPasswordChange: (String) -> Unit
) {
    TextField(
        value = pass,
        onValueChange = { onPasswordChange(it) },
        label = { Text("Contraseña" ) },
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(0.9f),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            textColor = colors.secondary
        ),
        shape = RoundedCornerShape(9.dp),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ) ,
        keyboardActions = KeyboardActions (onDone = {
            onAction()
        }),

    )
}



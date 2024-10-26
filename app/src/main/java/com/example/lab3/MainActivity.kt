package com.example.lab3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab3.ui.theme.Lab3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    InputScreen(innerPadding)
                }
            }
        }
    }
}

@Composable
fun InputScreen(innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        var pc by remember { mutableStateOf("") }
        var sigma1 by remember { mutableStateOf("") }
        var sigma2 by remember { mutableStateOf("") }
        var v by remember { mutableStateOf("") }
        var results by remember { mutableStateOf<ProfitLossResults?>(null) }

        OutlinedTextField(
            value = pc,
            onValueChange = { pc = it },
            label = { Text("Середньодобова потужність (Pc), МВт") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = sigma1,
            onValueChange = { sigma1 = it },
            label = { Text("Початкове середньоквадратичне відхилення (σ1)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = sigma2,
            onValueChange = { sigma2 = it },
            label = { Text("Покращене середньоквадратичне відхилення (σ2)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = v,
            onValueChange = { v = it },
            label = { Text("Вартість електроенергії (V), грн/кВт*год") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val pcValue = pc.toDoubleOrNull()
            val sigma1Value = sigma1.toDoubleOrNull()
            val sigma2Value = sigma2.toDoubleOrNull()
            val vValue = v.toDoubleOrNull()

            if (pcValue != null && sigma1Value != null && sigma2Value != null && vValue != null) {
                results = calculateProfitAndLoss(
                    pc = pcValue,
                    sigma1 = sigma1Value,
                    sigma2 = sigma2Value,
                    v = vValue
                )
            }
        }) {
            Text("Розрахувати")
        }

        Spacer(modifier = Modifier.height(16.dp))

        results?.let {
            DisplayResults(it)
        }
    }
}

@Composable
fun DisplayResults(results: ProfitLossResults) {
    Column {
        Text(text = "Прибуток до покращення: ${String.format("%.2f", results.profitBeforeImprovement)} тис. грн")
//Text(text = "Втрати до покращення: ${String.format("%.2f", results.lossBeforeImprovement)} тис. грн")
        Text(text = "Прибуток після покращення: ${String.format("%.2f", results.profitAfterImprovement)} тис. грн")
      //  Text(text = "Втрати після покращення: ${String.format("%.2f", results.lossAfterImprovement)} тис. грн")
    }
}

data class ProfitLossResults(val profitBeforeImprovement: Double, val lossBeforeImprovement: Double, val profitAfterImprovement: Double, val lossAfterImprovement: Double)

fun calculateProfitAndLoss(pc: Double, sigma1: Double, sigma2: Double, v: Double): ProfitLossResults {
    // Частки без небалансів
    val b1 = 0.20 // 20% (перед вдосконаленням)
    val b2 = 0.68 // 68% (після вдосконалення)

    // Обчислення енергії
    val W1 = pc * 24 * b1 // Енергія, що генерується без небалансів до вдосконалення
    val W2 = pc * 24 * (1 - b1) // Енергія, що генерується з небалансами до вдосконалення
    val W3 = pc * 24 * b2 // Енергія, що генерується без небалансів після вдосконалення
    val W4 = pc * 24 * (1 - b2) // Енергія, що генерується з небалансами після вдосконалення

    // Прибуток і штрафи
    val I1 = W1 * v // Прибуток до вдосконалення
    val S1 = W2 * v // Штраф до вдосконалення
    val I2 = W3 * v // Прибуток після вдосконалення
    val S2 = W4 * v // Штраф після вдосконалення

    // Чистий прибуток
    val netProfitBeforeImprovement = I1 - S1 // Чистий прибуток до вдосконалення
    val netProfitAfterImprovement = I2 - S2 // Чистий прибуток після вдосконалення

    return ProfitLossResults(netProfitBeforeImprovement, S1, netProfitAfterImprovement, S2)
}

fun main() {
    // Введення даних
    val pc = 5.0 // Потужність в МВт
    val v = 7.0 // Вартість за МВт-год в тис. грн

    // Розрахунок прибутку і втрат
    val results = calculateProfitAndLoss(pc, 0.0, 0.0, v)

    // Виведення результатів
    println("Прибуток до вдосконалення: ${String.format("%.2f", results.profitBeforeImprovement)} тис. грн")
        // println("Втрати до вдосконалення: ${String.format("%.2f", results.lossBeforeImprovement)} тис. грн")
    println("Прибуток після вдосконалення: ${String.format("%.2f", results.profitAfterImprovement)} тис. грн")
   // println("Втрати після вдосконалення: ${String.format("%.2f", results.lossAfterImprovement)} тис. грн")
}

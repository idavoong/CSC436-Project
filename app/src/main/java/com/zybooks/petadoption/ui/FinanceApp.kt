package com.zybooks.petadoption.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.zybooks.petadoption.data.Finance
import com.zybooks.petadoption.data.FinanceDataSource
import com.zybooks.petadoption.data.FinanceType
import com.zybooks.petadoption.ui.theme.FinanceTheme
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale


sealed class Routes {
    @Serializable
    data object Home

    @Serializable
    data class Edit(
        val financeId: Int
    )
}

@Composable
fun FinanceApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Home
    ) {
        composable<Routes.Home> {
            HomeScreen(
                onEditClick = { finance ->
                    navController.navigate(
                        Routes.Edit(finance.id)
                    )
                }
            )
        }
        composable<Routes.Edit> { backstackEntry ->
            val edit: Routes.Edit = backstackEntry.toRoute()

            EditScreen(
                id = edit.financeId, // Ensure `Routes.Edit` has an `id`
                onSubmit = { updatedFinance ->
                    // Handle finance update logic (e.g., update database or state)
                },
                onDelete = { financeToDelete ->
                    // Handle finance deletion logic
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceAppBar(
    title: String,
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean = false,
    onUpClick: () -> Unit = { },
) {
    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onUpClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                }
            }
        }
    )
}

@Composable
fun HomeScreen(
    onEditClick: (Finance) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            FinanceAppBar(title = "Recent Expenses")
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            modifier = modifier.padding(innerPadding)
        ) {
            items(viewModel.financeList) { finance ->
                ExpenseItem(
                    expense = finance,
                    onEditClick = onEditClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ExpenseItem(
    expense: Finance,
    onEditClick: (Finance) -> Unit,  // Keep only the edit action
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        // Card itself
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
            modifier = Modifier.fillMaxWidth()  // Removed clickable modifier
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val formattedDate =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(expense.date)

                Text(
                    text = "Date: $formattedDate | ${expense.name}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Amount: $${"%.2f".format(expense.amount)}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = expense.category.name, // Convert enum to string
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Edit Icon in the top-right corner
        IconButton(
            onClick = { onEditClick(expense) },
            modifier = Modifier
                .align(Alignment.TopEnd) // Position it at the top-right corner
                .padding(8.dp)  // Add some padding from the edges
        ) {
            Icon(
                imageVector = Icons.Default.Edit,  // Use the default Edit icon
                contentDescription = "Edit"
            )
        }
    }
}

@Composable
fun EditScreen(
    id: Int,
    viewModel: EditViewModel = viewModel(),
    onSubmit: (Finance) -> Unit,
    onDelete: (Finance) -> Unit,
    modifier: Modifier = Modifier
) {
    val finance by viewModel.finance.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadFinance(id) // Load finance when screen starts
    }

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var name by remember { mutableStateOf(finance.name) }
    var date by remember { mutableStateOf(finance.date.toString()) }
    var category by remember { mutableStateOf(finance.category.name) }
    var amount by remember { mutableStateOf(finance.amount.toString()) }

    Scaffold(
        topBar = { FinanceAppBar(title = "Edit Expense") }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { onDelete(finance) }) {
                    Text("Delete")
                }
                Button(onClick = {
                    val updatedFinance = finance.copy(
                        name = name,
                        date = dateFormatter.parse(date) ?: finance.date, // Convert String -> Date
                        category = try { enumValueOf<FinanceType>(category) } catch (e: IllegalArgumentException) { finance.category }, // Convert String -> FinanceType
                        amount = amount.toDoubleOrNull() ?: finance.amount
                    )
                    onSubmit(updatedFinance)
                }) {
                    Text("Submit")
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    FinanceTheme {
        HomeScreen(
            onEditClick = {}
        )
    }
}

@Preview
@Composable
fun PreviewEditScreen() {
    val finance = FinanceDataSource().getFinance(1) ?: Finance() // Get a sample finance object
    val dummyViewModel = EditViewModel() // Create a mock ViewModel

    FinanceTheme {
        EditScreen(
            id = finance.id,
            viewModel = dummyViewModel,
            onSubmit = { updatedFinance ->
                // Mock submit action for preview
                println("Updated finance: $updatedFinance")
            },
            onDelete = { financeToDelete ->
                // Mock delete action for preview
                println("Deleted finance: $financeToDelete")
            }
        )
    }
}

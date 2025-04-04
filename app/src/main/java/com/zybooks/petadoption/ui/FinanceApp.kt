package com.zybooks.petadoption.ui

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.zybooks.petadoption.data.Finance
import com.zybooks.petadoption.data.FinanceType
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


sealed class Routes {
    @Serializable
    data object Home

    @Serializable
    data class Edit(val financeId: Int)

    @Serializable
    data object Input
}

@Composable
fun FinanceApp() {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.Home
    ) {
        composable<Routes.Home> {
            HomeScreen(
                onEditClick = { finance -> navController.navigate(Routes.Edit(finance.id)) },
                onAddNewClick = { navController.navigate(Routes.Input) },
                viewModel = homeViewModel
            )
        }
        composable<Routes.Edit> { backStackEntry ->
            val editRoute: Routes.Edit = backStackEntry.toRoute()
            EditScreen(
                id = editRoute.financeId,
                homeViewModel = homeViewModel,
                onSubmit = {},
                onDelete = { financeToDelete ->
                    homeViewModel.deleteFinance(financeToDelete)
                },
                navController = navController
            )
        }
        composable<Routes.Input> {
            InputScreen(
                homeViewModel = homeViewModel,
                navController = navController,
                onSubmit = {}
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
    onUpClick: () -> Unit = { }
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
                    Icon(Icons.Filled.ArrowBack, "Back")
                }
            }
        }
    )
}

@Composable
fun HomeScreen(
    onEditClick: (Finance) -> Unit,
    onAddNewClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val financeList by viewModel.financeList.collectAsState()

    Scaffold(
        topBar = { FinanceAppBar(title = "Recent Expenses") },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddNewClick) {
                Icon(Icons.Default.Add, contentDescription = "Add new expense")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            modifier = modifier.padding(innerPadding)
        ) {
            items(financeList) { finance ->
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
    onEditClick: (Finance) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(expense.date.time)
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
                        text = expense.category.name,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        IconButton(
            onClick = { onEditClick(expense) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit"
            )
        }
    }
}

@Composable
fun EditScreen(
    id: Int,
    homeViewModel: HomeViewModel,
    viewModel: EditViewModel = viewModel(),
    onSubmit: (Finance) -> Unit,
    onDelete: (Finance) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val finance by viewModel.finance.collectAsState()

    LaunchedEffect(id) {
        viewModel.loadFinance(id)
    }

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var name by remember { mutableStateOf(finance.name) }
    var dateString by remember { mutableStateOf(dateFormatter.format(finance.date.time)) }
    var category by remember { mutableStateOf(finance.category.name) }
    var amount by remember { mutableStateOf(finance.amount.toString()) }

    LaunchedEffect(finance) {
        name = finance.name
        dateString = dateFormatter.format(finance.date.time)
        category = finance.category.name
        amount = finance.amount.toString()
    }

    Scaffold(
        topBar = {
            FinanceAppBar(
                title = "Edit Expense",
                canNavigateBack = true,
                onUpClick = { navController.popBackStack() }
            )
        }
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
                value = dateString,
                onValueChange = { dateString = it },
                label = { Text("Date (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (INCOME or EXPENSE)") },
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
                Button(onClick = {
                    onDelete(finance)
                    navController.popBackStack()
                }) {
                    Text("Delete")
                }
                Button(onClick = {
                    val updatedFinance = finance.copy(
                        name = name,
                        date = try {
                            Calendar.getInstance().apply {
                                time = dateFormatter.parse(dateString)
                            }
                        } catch (e: Exception) {
                            finance.date
                        },
                        category = try {
                            enumValueOf<FinanceType>(category.uppercase(Locale.getDefault()))
                        } catch (e: IllegalArgumentException) {
                            finance.category
                        },
                        amount = amount.toDoubleOrNull() ?: finance.amount
                    )

                    homeViewModel.updateFinance(updatedFinance)
                    onSubmit(updatedFinance)
                    navController.popBackStack()
                }) {
                    Text("Submit")
                }
            }
        }
    }
}

@Composable
fun InputScreen(
    homeViewModel: HomeViewModel,
    navController: NavController,
    onSubmit: (Finance) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    var name by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            FinanceAppBar(
                title = "Add New Expense",
                canNavigateBack = true,
                onUpClick = { navController.popBackStack() }
            )
        }
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
                value = dateString,
                onValueChange = { dateString = it },
                label = { Text("Date (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (INCOME or EXPENSE)") },
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
            Button(onClick = {
                val newFinance = Finance(
                    name = name,
                    date = try {
                        Calendar.getInstance().apply {
                            time = dateFormatter.parse(dateString)
                        }
                    } catch (e: Exception) {
                        Calendar.getInstance()
                    },
                    category = try {
                        enumValueOf<FinanceType>(category.uppercase(Locale.getDefault()))
                    } catch (e: IllegalArgumentException) {
                        FinanceType.EXPENSE
                    },
                    amount = amount.toDoubleOrNull() ?: 0.0
                )

                homeViewModel.addFinance(newFinance)
                onSubmit(newFinance)
                navController.popBackStack()
            }) {
                Text("Submit")
            }
        }
    }
}

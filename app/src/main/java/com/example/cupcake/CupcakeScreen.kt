package com.example.cupcake

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cupcake.data.DataSource
import com.example.cupcake.ui.OrderSummaryScreen
import com.example.cupcake.ui.OrderViewModel
import com.example.cupcake.ui.SelectOptionScreen
import com.example.cupcake.ui.StartOrderScreen


enum class CupcakeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name), Flavor(title = R.string.choose_flavor), Pickup(title = R.string.choose_pickup_date), Summary(
        title = R.string.order_summary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CupcakeAppBar(
    modifier: Modifier = Modifier,
    currentScreen: CupcakeScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
) {
    TopAppBar(title = { Text(text = stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.back_button)
                    )
                }
            }
        })
}

@Composable
fun CupcakeApp(
    viewModel: OrderViewModel = OrderViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val  currentScreen = CupcakeScreen.valueOf(
        backStackEntry?.destination?.route ?: CupcakeScreen.Start.name
    )
    Scaffold(topBar = {
        CupcakeAppBar(
            currentScreen = currentScreen,
            canNavigateBack = navController.previousBackStackEntry !=null,
            navigateUp = { navController.navigateUp() }
        )
    }) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = CupcakeScreen.Start.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            composable(route = CupcakeScreen.Start.name) {
                StartOrderScreen(quantityOption = DataSource.quantityOptions,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(dimensionResource(id = R.dimen.padding_medium)),
                    onNextButtonClicked = { qty ->
                        viewModel.setQuantity(qty)
                        navController.navigate(CupcakeScreen.Flavor.name)
                    })
            }

            composable(route = CupcakeScreen.Flavor.name) {
                val context = LocalContext.current
                SelectOptionScreen(modifier = Modifier.fillMaxHeight(),
                    subtotal = uiState.price,
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Pickup.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel, navController
                        )
                    },
                    options = DataSource.flavors.map { id -> context.resources.getString(id) },
                    onSelectionChange = { flavor -> viewModel.setFlavor(flavor) })
            }

            composable(route = CupcakeScreen.Pickup.name) {
                val context = LocalContext.current
                SelectOptionScreen(modifier = Modifier.fillMaxHeight(),
                    subtotal = uiState.price,
                    options = uiState.pickupOptions,
                    onSelectionChange = { pickupDate -> viewModel.setDate(pickupDate) },
                    onNextButtonClicked = { navController.navigate(CupcakeScreen.Summary.name) },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel, navController
                        )
                    })
            }

            composable(route = CupcakeScreen.Summary.name) {
                OrderSummaryScreen(orderUiState = uiState,
                    onSendButtonClicked = { subject, summery ->

                    },
                    onCancelButtonClicked = {
                        cancelOrderAndNavigateToStart(
                            viewModel, navController
                        )
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }

        }
    }
}

private fun cancelOrderAndNavigateToStart(
    viewModel: OrderViewModel, navController: NavHostController
) {
    viewModel.resetOrder()
    navController.popBackStack(CupcakeScreen.Start.name, inclusive = false)
}

/**
 * Creates an intent to share order details
 */
private fun shareOrder(context: Context, subject: String, summary: String) {
    // Create an ACTION_SEND implicit intent with order details in the intent extras
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(
        Intent.createChooser(
            intent, context.getString(R.string.new_cupcake_order)
        )
    )
}
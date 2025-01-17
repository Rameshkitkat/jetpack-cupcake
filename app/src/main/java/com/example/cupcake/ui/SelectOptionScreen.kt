package com.example.cupcake.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.cupcake.R
import com.example.cupcake.ui.components.FormattedPriceLabel
import com.example.cupcake.ui.theme.CupcakeTheme

@Composable
fun SelectOptionScreen(
    modifier: Modifier = Modifier,
    subtotal: String,
    options: List<String>,
    onSelectionChange: (String) -> Unit = {},
    onNextButtonClicked: () -> Unit = {},
    onCancelButtonClicked: () -> Unit = {}
) {

    var selectedValue by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {

            options.forEach { item ->
                Row(
                    modifier = Modifier.selectable(
                        selected = selectedValue == item,
                        onClick = {
                            selectedValue = item
                            onSelectionChange(item)
                        }
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedValue == item,
                        onClick = {
                            selectedValue = item
                            onSelectionChange(item)
                        }
                    )
                    Text(text = item)
                }
            }

            HorizontalDivider(
                thickness = dimensionResource(id = R.dimen.thickness_divider),
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_medium))
            )

            FormattedPriceLabel(
                subtotal = subtotal,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(
                        top = dimensionResource(id = R.dimen.padding_medium),
                        bottom = dimensionResource(id = R.dimen.padding_medium)
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(id = R.dimen.padding_medium)),
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
                verticalAlignment = Alignment.Bottom
            ) {

                OutlinedButton(
                    onClick = onCancelButtonClicked,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(id = R.string.cancel))
                }

                Button(
                    modifier = Modifier.weight(1f),
                    enabled = selectedValue.isNotEmpty(),
                    onClick = onNextButtonClicked
                ) {
                    Text(stringResource(R.string.next))
                }
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun SelectOptionPreview() {
    CupcakeTheme {
        SelectOptionScreen(
            subtotal = "299.99",
            options = listOf("Option 1", "Option 2", "Option 3", "Option 4"),
            modifier = Modifier.fillMaxHeight()
        )
    }
}
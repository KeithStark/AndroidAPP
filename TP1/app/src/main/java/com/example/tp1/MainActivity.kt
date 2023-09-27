package com.example.tp1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme{
                // Define a mutable list to store trip entries
                val trips by remember { mutableStateOf(mutableListOf<Trip>()) }

                // Add a state variable to track the currently selected trip for modification
                val selectedTrip = remember { mutableStateOf<Trip?>(null) }

                // Center the content vertically and horizontally
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Column {
                        TripListView(
                            trips = trips,
                            onModifyClick = { trip ->
                                // Set the selected trip for modification
                                selectedTrip.value = trip
                            },
                            onDeleteClick = { tripToDelete ->
                                // Remove the trip from the list
                                trips.remove(tripToDelete)
                            }
                        )
                        AddTripView(trips, selectedTrip)
                    }
                }
            }
        }
    }

    data class Trip(var destination: String, var date: String, var description: String)

    @Composable
    fun TripView(
        trip: Trip,
        onModifyClick: () -> Unit,
        onDeleteClick: (Trip) -> Unit
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Destination: ${trip.destination}")
            Text("Date: ${trip.date}")
            Text("Description: ${trip.description}")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onModifyClick() },
                    modifier = Modifier
                        .size(100.dp, 36.dp)
                ) {
                    Text(text = "Modify")
                }
                Button(
                    onClick = { onDeleteClick(trip) },
                    modifier = Modifier
                        .size(100.dp, 36.dp)
                ) {
                    Text(text = "Delete")
                }
            }
        }
    }


    @Composable
    fun TripListView(
        trips: List<Trip>,
        onModifyClick: (Trip) -> Unit,
        onDeleteClick: (Trip) -> Unit
    ) {
        Column {
            trips.forEach { trip ->
                TripView(
                    trip = trip,
                    onModifyClick = { onModifyClick(trip) },
                    onDeleteClick = { onDeleteClick(trip) }
                )
            }
            LaunchedEffect(trips) {
            }
        }
    }






    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun AddTripView(trips: MutableList<Trip>, selectedTrip: MutableState<Trip?>) {
        var destination by rememberSaveable { mutableStateOf("") }
        var date by rememberSaveable { mutableStateOf("") }
        var description by rememberSaveable { mutableStateOf("") }

        val keyboardController = LocalSoftwareKeyboardController.current

        // State to manage error messages
        var destinationError by rememberSaveable { mutableStateOf("") }
        var dateError by rememberSaveable { mutableStateOf("") }
        var descriptionError by rememberSaveable { mutableStateOf("") }

        // State to keep track of the button text
        var buttonText by remember { mutableStateOf("Add") }

        // Update fields with selected trip data when it changes
        LaunchedEffect(selectedTrip.value) {
            selectedTrip.value?.let { trip ->
                destination = trip.destination
                date = trip.date
                description = trip.description
                buttonText = "Update" // Set button text to "Update" when modifying
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Input fields for destination, date, and description
            TextField(
                value = destination,
                onValueChange = {
                    destination = it
                    destinationError = ""
                },
                placeholder = { Text("Destination") },
                modifier = Modifier.padding(bottom = 8.dp),
                enabled = selectedTrip.value == null || selectedTrip.value != null
            )

            TextField(
                value = date,
                onValueChange = {
                    date = it
                    dateError = ""
                },
                placeholder = { Text("Date (JJ/MM/AAAA)") },
                modifier = Modifier.padding(bottom = 8.dp),
                enabled = selectedTrip.value == null || selectedTrip.value != null
            )

            TextField(
                value = description,
                onValueChange = {
                    description = it
                    descriptionError = ""
                },
                placeholder = { Text("Description") },
                modifier = Modifier.padding(bottom = 8.dp),
                enabled = selectedTrip.value == null || selectedTrip.value != null
            )

            // Display error messages if validation fails
            if (destinationError.isNotEmpty()) {
                Text(
                    text = destinationError,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (dateError.isNotEmpty()) {
                Text(
                    text = dateError,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (descriptionError.isNotEmpty()) {
                Text(
                    text = descriptionError,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Add button to submit/update the trip
            Button(
                onClick = {
                    // Check if a trip is selected for modification
                    if (selectedTrip.value != null) {
                        // Modify the existing trip with the modified details
                        val modifiedTrip = selectedTrip.value!!
                        modifiedTrip.destination = destination
                        modifiedTrip.date = date
                        modifiedTrip.description = description

                        // Clear the selected trip
                        selectedTrip.value = null

                        // Clear the input fields
                        destination = ""
                        date = ""
                        description = ""

                        // Change the button text back to "Add"
                        buttonText = "Add"
                    } else {
                        // Check for empty destination, date, and description
                        if (destination.isBlank() || date.isBlank() || description.isBlank()) {
                            // Set error messages for empty fields
                            if (destination.isBlank()) {
                                destinationError = "Destination is required"
                            }
                            if (date.isBlank()) {
                                dateError = "Date is required"
                            }
                            if (description.isBlank()) {
                                descriptionError = "Description is required"
                            }
                        } else {
                            // Check date format
                            if (!date.matches(Regex("\\d{2}/\\d{2}/\\d{4}"))) {
                                dateError = "Invalid date format (JJ/MM/AAAA)"
                            } else {
                                // Create a new trip and add it to the list
                                val newTrip = Trip(destination, date, description)
                                trips.add(newTrip)

                                // Clear input fields
                                destination = ""
                                date = ""
                                description = ""
                                keyboardController?.hide()

                                // Clear any previous error messages
                                destinationError = ""
                                dateError = ""
                                descriptionError = ""
                            }
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = buttonText) // Use the buttonText variable for dynamic text
            }

            // Display the list of trips below the button
            trips.forEach { trip ->
                TripView(
                    trip = trip,
                    onModifyClick = { selectedTrip.value = trip },
                    onDeleteClick = { trips.remove(trip) }
                )
            }
        }
    }


}
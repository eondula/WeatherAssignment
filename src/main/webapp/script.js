document.getElementById('weatherForm').addEventListener('submit', function(e) {
    e.preventDefault();
    const lat = document.getElementById('latitude').value;
    const lon = document.getElementById('longitude').value;
    
    console.log(`Fetching weather for lat: ${lat}, lon: ${lon}`);
    
    // Check if the user is logged in
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
    const userId = localStorage.getItem('userId');

    // If logged in, use POST request to store search data
    if (isLoggedIn && userId) {
        fetch('WeatherServlet', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                lat: lat,
                lon: lon,
                isLoggedIn: true,
                userId: parseInt(userId)
            })
        })
        .then(response => {
            console.log('Response status:', response.status);
            return response.json(); // Parse JSON response
        })
        .then(data => {
            displayWeatherData(data, lat, lon);
        })
        .catch(error => {
            console.error('Fetch error:', error);
            document.getElementById('result').innerHTML = `Error: ${error.message}`;
        });
    } else {
        // If not logged in, use GET request
        fetch(`WeatherServlet?lat=${encodeURIComponent(lat)}&lon=${encodeURIComponent(lon)}`)
            .then(response => {
                console.log('Response status:', response.status);
                return response.json(); // Parse JSON response
            })
            .then(data => {
                displayWeatherData(data, lat, lon);
            })
            .catch(error => {
                console.error('Fetch error:', error);
                document.getElementById('result').innerHTML = `Error: ${error.message}`;
            });
    }
});

// Function to display the weather data
function displayWeatherData(data, lat, lon) {
    console.log('Full data received:', data);

    // Check for valid weather data and display it
    if (data.main && data.weather && data.weather[0]) {
        const tempCelsius = (data.main.temp - 273.15).toFixed(2);
        const description = data.weather[0].description;

        document.getElementById('result').innerHTML = `
            <h3>Weather at (${lat}, ${lon})</h3>
            <p>Temperature: ${tempCelsius}°C</p>
            <p>Description: ${description}</p>
        `;
    } else {
        document.getElementById('result').innerHTML = "Error: Unable to fetch weather details.";
    }
}

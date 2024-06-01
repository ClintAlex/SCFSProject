let stompClient = null;
let city = 'Odense'; // Default city

document.addEventListener("DOMContentLoaded", function() {
    const urlParams = new URLSearchParams(window.location.search);
    city = urlParams.get('city') || city;
    document.getElementById("cityName").textContent = city;
    document.getElementById('cityInput').value = city;
    connect();
    fetchWeatherData(city).then(data => renderChart(data));
});

function connect() {
    const socket = new SockJS('/weather-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/weather/' + city, function(weatherUpdate) {
            const data = JSON.parse(weatherUpdate.body);
            updateChart(data);
        });
    });
}

function changeCity() {
    const newCity = document.getElementById('cityInput').value;
    if (newCity && newCity !== city) {
        city = newCity;
        document.getElementById("cityName").textContent = city;
        if (stompClient) {
            stompClient.disconnect(() => {
                connect();
                fetchWeatherData(city).then(data => renderChart(data));
            });
        }
        const newUrl = `${window.location.origin}${window.location.pathname}?city=${city}`;
        window.history.pushState({ path: newUrl }, '', newUrl);
    }
}

async function fetchWeatherData(city) {
    const response = await fetch('/api/weather?city=' + city);
    const data = await response.json();
    return data;
}

let weatherChart;
function renderChart(data) {
    const ctx = document.getElementById('weatherChart').getContext('2d');
    const times = data.map(d => new Date(d.time * 1000).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false }));
    const temperatures = data.map(d => d.temperature);

    if (weatherChart) {
        weatherChart.destroy();
    }

    weatherChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: times,
            datasets: [{
                label: 'Temperature (°C)',
                data: temperatures,
                borderColor: 'rgba(255, 99, 132, 1)',
                backgroundColor: 'rgba(255, 99, 132, 0.2)',
                borderWidth: 1
            }]
        },
        options: {
            plugins: {
                legend: {
                    labels: {
                        color: '#FFFFFF',
                        font: {
                            size: 18
                        }
                    }
                }
            },
            scales: {
                x: {
                    type: 'category',
                    title: {
                        display: true,
                        text: 'Time',
                        color: '#FFFFFF',
                        font: {
                            size: 18
                        }
                    },
                    ticks: {
                        color: '#FFFFFF',
                        font: {
                            size: 14
                        }
                    },
                    grid: {
                        color: 'rgba(255, 255, 255, 0.2)'
                    }
                },
                y: {
                    beginAtZero: false,
                    title: {
                        display: true,
                        text: 'Temperature (°C)',
                        color: '#FFFFFF',
                        font: {
                            size: 18
                        }
                    },
                    ticks: {
                        color: '#FFFFFF',
                        font: {
                            size: 14
                        }
                    },
                    grid: {
                        color: 'rgba(255, 255, 255, 0.2)'
                    }
                }
            }
        }
    });

    updateWeatherDetails(data);
}

function updateChart(data) {
    const times = data.map(d => new Date(d.time * 1000).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false }));
    const temperatures = data.map(d => d.temperature);

    weatherChart.data.labels = times;
    weatherChart.data.datasets[0].data = temperatures;
    weatherChart.update();

    updateWeatherDetails(data);
}

function updateWeatherDetails(data) {
    if (data.length > 0) {
        const latestData = data[data.length - 1];
        document.getElementById('humidity').textContent = latestData.humidity;
        document.getElementById('windSpeed').textContent = latestData.windSpeed;
        document.getElementById('windDirection').textContent = getWindDirection(latestData.windDirection);
        document.getElementById('weatherIcon').src = `http://openweathermap.org/img/w/${latestData.icon}.png`;
    }
}

function getWindDirection(degree) {
    const directions = ['NORTH', 'NORTH-EAST', 'EAST', 'SOUTH-EAST', 'SOUTH', 'SOUTH-WEST', 'WEST', 'NORTH-WEST'];
    const index = Math.round((degree % 360) / 45) % 8;
    return directions[index];
}

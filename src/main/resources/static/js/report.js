// Fungsi redirect untuk admin panel
function redirectToAdminDashboard() {
    window.location.href = '/admin/dashboard';
}

function redirectToManageCustomers() {
    window.location.href = '/admin/manage-customers';
}

function redirectToReports() {
    window.location.href = '/admin/reports';
}

function redirectToMonthlyReports() {
    window.location.href = '/admin/kelolaLaporan';
}

function redirectToManageGenres() {
    window.location.href = '/admin/genres/manage';
}

function redirectToManageActors() {
    window.location.href = '/admin/actors/manage';
}

function handleLogout() {
    window.location.href = '/logout';
}

// Mobile menu handling
const mobileMenuTrigger = document.querySelector('.mobile-menu-trigger');
const sidebar = document.querySelector('.sidebar');

mobileMenuTrigger.addEventListener('click', () => {
    sidebar.classList.toggle('active');
});

// Close mobile menu when clicking outside
document.addEventListener('click', (e) => {
    if (!sidebar.contains(e.target) && !mobileMenuTrigger.contains(e.target)) {
        sidebar.classList.remove('active');
    }
});

// Chart functions
let rentalChart;

function calculateAverageRentals() {
    return rentalStats.map(stat => {
        if (stat.rentalCount > 0 && stat.targetCount > 0) {
            return (stat.rentalCount / stat.targetCount);
        } else {
            return 0;
        }
    });
}

// Function to initialize the chart
function initializeChart() {
    const ctx = document.getElementById('rentalChart').getContext('2d');
    const averageRentals = calculateAverageRentals();

    rentalChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: rentalStats.map(stat => stat.movieTitle),
            datasets: [
                {
                    label: 'Jumlah Penyewaan',
                    data: rentalStats.map(stat => stat.rentalCount),
                    borderColor: 'rgba(99, 102, 241, 1)',
                    backgroundColor: 'rgba(99, 102, 241, 0.2)',
                    borderWidth: 2,
                    tension: 0.3,
                    fill: true,
                    pointBackgroundColor: 'rgba(99, 102, 241, 1)',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
                {
                    label: 'Target Penyewaan',
                    data: rentalStats.map(stat => stat.targetCount || stat.rentalCount * 1.2),
                    borderColor: 'rgba(52, 211, 153, 1)',
                    backgroundColor: 'rgba(52, 211, 153, 0.2)',
                    borderWidth: 2,
                    tension: 0.3,
                    fill: true,
                    pointBackgroundColor: 'rgba(52, 211, 153, 1)',
                    pointBorderColor: '#fff',
                    pointBorderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                intersect: false,
                mode: 'index'
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        color: 'rgba(241, 245, 249, 0.8)'
                    },
                    grid: {
                        color: 'rgba(241, 245, 249, 0.1)'
                    }
                },
                x: {
                    ticks: {
                        color: 'rgba(241, 245, 249, 0.8)',
                        maxRotation: 45,
                        minRotation: 45
                    },
                    grid: {
                        color: 'rgba(241, 245, 249, 0.1)'
                    }
                }
            },
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        color: 'rgba(241, 245, 249, 0.8)',
                        padding: 20,
                        usePointStyle: true,
                        pointStyle: 'circle'
                    }
                },
                title: {
                    display: true,
                    text: 'Perbandingan Penyewaan Film',
                    font: {
                        size: 16,
                        weight: 'bold'
                    },
                    padding: {
                        top: 10,
                        bottom: 30
                    },
                    color: 'rgba(241, 245, 249, 0.8)'
                },
                tooltip: {
                    enabled: true,
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleFont: {
                        size: 13
                    },
                    bodyFont: {
                        size: 12
                    },
                    padding: 15,
                    displayColors: true,
                    callbacks: {
                        label: function (context) {
                            let label = context.dataset.label || '';
                            if (label) {
                                label += ': ';
                            }
                            if (context.parsed.y !== null) {
                                label += parseFloat(context.parsed.y).toFixed(1);
                            }
                            return label;
                        }
                    }
                }
            }
        }
    });
}

// Function to update chart data
function updateChartData() {
    if (rentalChart) {
        const averageRentals = calculateAverageRentals();
        rentalChart.data.datasets[1].data = rentalStats.map(stat => stat.targetCount || stat.rentalCount * 1.2);
        rentalChart.update();
    }
}

// Add event listeners to update target buttons
function setupUpdateTargetButtons() {
    document.querySelectorAll('.update-target-btn').forEach(btn => {
        btn.addEventListener('click', async function () {
            const filmId = this.getAttribute('data-film-id');
            const targetInput = document.querySelector(`.target-input[data-film-id="${filmId}"]`);
            const targetCount = parseInt(targetInput.value);

            if (isNaN(targetCount) || targetCount < 0) {
                alert('Please enter a valid target number');
                return;
            }

            try {
                const response = await fetch(`/admin/rental/update-target/${filmId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ targetCount })
                });

                if (response.ok) {
                    // Update local data
                    const filmIndex = rentalStats.findIndex(stat => stat.filmId === parseInt(filmId));
                    if (filmIndex !== -1) {
                        rentalStats[filmIndex].targetCount = targetCount;
                        updateChartData();
                    }
                    alert('Target updated successfully');
                } else {
                    throw new Error('Failed to update target');
                }
            } catch (error) {
                console.error('Error updating target:', error);
                alert('Failed to update target: ' + error.message);
            }
        });
    });
}

// Initialize everything when the page loads
document.addEventListener('DOMContentLoaded', () => {
    initializeChart();
    setupUpdateTargetButtons();
});
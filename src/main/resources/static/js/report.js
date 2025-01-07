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

let rentalChart;

// Inisialisasi chart rental film
function initializeChart() {
    const ctx = document.getElementById('rentalChart').getContext('2d');

    // Membuat chart baru dengan konfigurasi
    rentalChart = new Chart(ctx, {
        type: 'line',
        data: {
            // Data untuk sumbu X (judul film)
            labels: rentalStats.map(stat => stat.movieTitle),
            datasets: [
                {
                    // Dataset untuk jumlah penyewaan aktual
                    label: 'Jumlah Penyewaan',
                    data: rentalStats.map(stat => stat.rentalCount),
                    borderColor: 'rgba(99, 102, 241, 1)',
                    backgroundColor: 'rgba(99, 102, 241, 0.2)',
                    // Konfigurasi tampilan garis dan titik
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
                    // Dataset untuk target penyewaan
                    label: 'Target Penyewaan',
                    data: rentalStats.map(stat => stat.targetCount || stat.rentalCount * 1.2),
                    borderColor: 'rgba(52, 211, 153, 1)',
                    backgroundColor: 'rgba(52, 211, 153, 0.2)',
                    // Konfigurasi tampilan garis dan titik
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
            // Konfigurasi responsif chart
            responsive: true,
            maintainAspectRatio: false,
            // Konfigurasi interaksi dengan chart
            interaction: {
                intersect: false,
                mode: 'index'
            },
            // Konfigurasi skala sumbu X dan Y
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
            // Konfigurasi plugin chart (legend, title, tooltip)
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

// Fungsi untuk memperbarui data chart
function updateChartData() {
    if (rentalChart) {
        // Memperbarui data target penyewaan
        rentalChart.data.datasets[1].data = rentalStats.map(stat => stat.targetCount || stat.rentalCount * 1.2);
        rentalChart.update();
    }
}

// Fungsi untuk mengatur event listener tombol update target
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

document.addEventListener('DOMContentLoaded', () => {
    initializeChart();
    setupUpdateTargetButtons();
});
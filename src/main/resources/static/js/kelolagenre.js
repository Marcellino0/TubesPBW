
        // Fungsi redirect untuk admin panel
        function redirectToAdminDashboard() {
            window.location.href = '/admin/dashboard';
        }

        function redirectToManageCustomers() {
            window.location.href = '/admin/manage-customers';
        }

        function redirectToManageGenres() {
            window.location.href = '/admin/genres/manage';
        }

        function redirectToManageActors() {
            window.location.href = '/admin/actors/manage';
        }

        function redirectToReports() {
            window.location.href = '/admin/reports';
        }

        function redirectToMonthlyReports() {
            window.location.href = '/admin/kelolaLaporan';
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

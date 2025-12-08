<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>API Metrics Dashboard - Real Time</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Courier New', monospace;
            background: #0d1117;
            color: #c9d1d9;
            padding: 20px;
        }

        .container {
            max-width: 1400px;
            margin: 0 auto;
        }

        .header {
            background: #161b22;
            border: 1px solid #30363d;
            padding: 30px;
            border-radius: 6px;
            margin-bottom: 20px;
        }

        .header h1 {
            color: #58a6ff;
            font-size: 2em;
            margin-bottom: 10px;
        }

        .header .status {
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 0.9em;
        }

        .status-indicator {
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: #3fb950;
            animation: pulse 2s infinite;
        }

        .status-indicator.offline {
            background: #f85149;
        }

        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.5; }
        }

        .metrics-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }

        .metric-card {
            background: #161b22;
            border: 1px solid #30363d;
            padding: 20px;
            border-radius: 6px;
            transition: transform 0.2s;
        }

        .metric-card:hover {
            transform: translateY(-2px);
            border-color: #58a6ff;
        }

        .metric-card h3 {
            color: #8b949e;
            font-size: 0.85em;
            margin-bottom: 10px;
            text-transform: uppercase;
        }

        .metric-value {
            font-size: 2.5em;
            color: #58a6ff;
            font-weight: bold;
        }

        .metric-label {
            color: #8b949e;
            font-size: 0.9em;
            margin-top: 5px;
        }

        .endpoints-section {
            background: #161b22;
            border: 1px solid #30363d;
            padding: 30px;
            border-radius: 6px;
            margin-bottom: 20px;
        }

        .endpoints-section h2 {
            color: #58a6ff;
            margin-bottom: 20px;
            font-size: 1.5em;
        }

        .endpoint {
            background: #0d1117;
            border: 1px solid #30363d;
            padding: 20px;
            border-radius: 6px;
            margin-bottom: 15px;
            transition: border-color 0.3s;
        }

        .endpoint:hover {
            border-color: #58a6ff;
        }

        .endpoint-header {
            display: flex;
            align-items: center;
            gap: 15px;
            margin-bottom: 15px;
            flex-wrap: wrap;
        }

        .method {
            padding: 5px 10px;
            border-radius: 3px;
            font-weight: bold;
            font-size: 0.85em;
        }

        .method-get { background: #238636; color: white; }
        .method-post { background: #1f6feb; color: white; }
        .method-put { background: #d29922; color: white; }
        .method-delete { background: #da3633; color: white; }

        .endpoint-path {
            color: #c9d1d9;
            font-size: 1.1em;
            flex: 1;
        }

        .endpoint-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
            gap: 15px;
        }

        .stat {
            background: #161b22;
            padding: 10px;
            border-radius: 4px;
        }

        .stat-label {
            color: #8b949e;
            font-size: 0.75em;
            margin-bottom: 5px;
        }

        .stat-value {
            color: #58a6ff;
            font-size: 1.3em;
            font-weight: bold;
        }

        .response-time {
            color: #3fb950;
        }

        .error-rate {
            color: #f85149;
        }

        .success-rate {
            color: #3fb950;
        }

        .controls {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 10px 20px;
            background: #21262d;
            color: #c9d1d9;
            border: 1px solid #30363d;
            border-radius: 6px;
            cursor: pointer;
            font-family: 'Courier New', monospace;
            transition: all 0.3s;
        }

        .btn:hover {
            background: #30363d;
            border-color: #58a6ff;
        }

        .btn-primary {
            background: #1f6feb;
            border-color: #1f6feb;
        }

        .btn-primary:hover {
            background: #388bfd;
        }

        .btn-danger {
            background: #da3633;
            border-color: #da3633;
        }

        .btn-danger:hover {
            background: #f85149;
        }

        .chart-container {
            background: #161b22;
            border: 1px solid #30363d;
            padding: 20px;
            border-radius: 6px;
            margin-bottom: 20px;
            min-height: 250px;
        }

        .chart-bars {
            display: flex;
            align-items: flex-end;
            justify-content: space-around;
            height: 180px;
            gap: 8px;
            margin-top: 20px;
        }

        .bar-container {
            flex: 1;
            display: flex;
            flex-direction: column;
            align-items: center;
            min-width: 60px;
        }

        .bar {
            width: 100%;
            background: linear-gradient(to top, #238636, #3fb950);
            border-radius: 4px 4px 0 0;
            position: relative;
            transition: all 0.3s;
            min-height: 5px;
        }

        .bar:hover {
            opacity: 0.8;
        }

        .bar-value {
            color: #58a6ff;
            font-size: 0.8em;
            font-weight: bold;
            margin-bottom: 5px;
        }

        .bar-label {
            font-size: 0.65em;
            color: #8b949e;
            text-align: center;
            margin-top: 8px;
            line-height: 1.2;
        }

        .alert {
            padding: 15px;
            border-radius: 6px;
            margin-bottom: 20px;
            border: 1px solid;
        }

        .alert-error {
            background: #1a0f0f;
            border-color: #da3633;
            color: #f85149;
        }

        .alert-info {
            background: #0f1419;
            border-color: #1f6feb;
            color: #58a6ff;
        }

        .loading {
            text-align: center;
            padding: 40px;
            color: #8b949e;
        }

        .auto-refresh {
            display: flex;
            align-items: center;
            gap: 10px;
            color: #8b949e;
        }

        .auto-refresh input[type="checkbox"] {
            width: 18px;
            height: 18px;
            cursor: pointer;
        }

        .last-update {
            color: #8b949e;
            font-size: 0.85em;
            margin-left: auto;
        }
    </style>
</head>
<body>
<div class="container">
    <!-- Header -->
    <div class="header">
        <h1>📊 API Metrics Dashboard - Real Time</h1>
        <div class="status">
            <span class="status-indicator" id="statusIndicator"></span>
            <span id="statusText">Connecting...</span>
            <span style="margin-left: 20px;">Base URL: <code style="color: #58a6ff;" id="apiUrl"></code></span>
        </div>
    </div>

    <!-- Controls -->
    <div class="controls">
        <button class="btn btn-primary" onclick="loadMetrics()">🔄 Refresh Now</button>
        <button class="btn btn-danger" onclick="resetMetrics()">🗑️ Reset All Metrics</button>
        <div class="auto-refresh">
            <input type="checkbox" id="autoRefresh" checked>
            <label for="autoRefresh">Auto-refresh (5s)</label>
        </div>
        <span class="last-update" id="lastUpdate">Never updated</span>
    </div>

    <!-- Alert Container -->
    <div id="alertContainer"></div>

    <!-- Global Metrics -->
    <div class="metrics-grid">
        <div class="metric-card">
            <h3>Total Requests</h3>
            <div class="metric-value" id="totalRequests">0</div>
            <div class="metric-label">All time</div>
        </div>
        <div class="metric-card">
            <h3>Success Rate</h3>
            <div class="metric-value success-rate" id="successRate">100%</div>
            <div class="metric-label">2xx responses</div>
        </div>
        <div class="metric-card">
            <h3>Avg Response Time</h3>
            <div class="metric-value response-time" id="avgResponseTime">0ms</div>
            <div class="metric-label">Average latency</div>
        </div>
        <div class="metric-card">
            <h3>Error Count</h3>
            <div class="metric-value error-rate" id="errorCount">0</div>
            <div class="metric-label">4xx + 5xx errors</div>
        </div>
    </div>

    <!-- Request Chart -->
    <div class="chart-container">
        <h3 style="color: #58a6ff;">Request Distribution by Endpoint</h3>
        <div class="chart-bars" id="requestChart">
            <div class="loading">Loading chart...</div>
        </div>
    </div>

    <!-- Endpoints -->
    <div class="endpoints-section">
        <h2>📡 API Endpoints - Live Statistics</h2>
        <div id="endpointsContainer">
            <div class="loading">Loading endpoints data...</div>
        </div>
    </div>
</div>

<script>
    // Configuración
    const BASE_URL = window.location.origin + '/estudiantes-api/api';
    const METRICS_URL = BASE_URL + '/metrics';
    let autoRefreshInterval = null;

    // Inicializar
    document.addEventListener('DOMContentLoaded', () => {
        document.getElementById('apiUrl').textContent = BASE_URL;
        loadMetrics();
        setupAutoRefresh();
    });

    // Configurar auto-refresh
    function setupAutoRefresh() {
        const checkbox = document.getElementById('autoRefresh');

        checkbox.addEventListener('change', (e) => {
            if (e.target.checked) {
                startAutoRefresh();
            } else {
                stopAutoRefresh();
            }
        });

        // Iniciar auto-refresh si está marcado
        if (checkbox.checked) {
            startAutoRefresh();
        }
    }

    function startAutoRefresh() {
        if (autoRefreshInterval) return;
        autoRefreshInterval = setInterval(loadMetrics, 5000);
    }

    function stopAutoRefresh() {
        if (autoRefreshInterval) {
            clearInterval(autoRefreshInterval);
            autoRefreshInterval = null;
        }
    }

    // Cargar métricas desde el servidor
    async function loadMetrics() {
        try {
            const response = await fetch(METRICS_URL);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const data = await response.json();

            // Actualizar UI
            updateGlobalMetrics(data);
            updateEndpoints(data.endpoints || []);
            updateChart(data.endpoints || []);
            updateStatus(true);
            updateLastUpdate();
            clearAlert();

        } catch (error) {
            console.error('Error loading metrics:', error);
            updateStatus(false);
            showAlert('Error loading metrics: ' + error.message, 'error');
        }
    }

    // Actualizar métricas globales
    function updateGlobalMetrics(data) {
        document.getElementById('totalRequests').textContent = data.totalRequests || 0;
        document.getElementById('successRate').textContent =
            (data.successRate || 100).toFixed(1) + '%';
        document.getElementById('avgResponseTime').textContent =
            (data.averageResponseTime || 0) + 'ms';
        document.getElementById('errorCount').textContent = data.errorRequests || 0;
    }

    // Actualizar endpoints
    function updateEndpoints(endpoints) {
        const container = document.getElementById('endpointsContainer');

        if (endpoints.length === 0) {
            container.innerHTML = '<div class="loading">No requests recorded yet. Start using the API!</div>';
            return;
        }

        container.innerHTML = endpoints.map(endpoint => {
            const methodClass = 'method-' + endpoint.method.toLowerCase();
            const statusColor = getStatusColor(endpoint.lastStatus);

            return `
                    <div class="endpoint">
                        <div class="endpoint-header">
                            <span class="method ${methodClass}">${endpoint.method}</span>
                            <span class="endpoint-path">${endpoint.path}</span>
                        </div>
                        <div class="endpoint-stats">
                            <div class="stat">
                                <div class="stat-label">Requests</div>
                                <div class="stat-value">${endpoint.count}</div>
                            </div>
                            <div class="stat">
                                <div class="stat-label">Avg Time</div>
                                <div class="stat-value response-time">${endpoint.averageTime}ms</div>
                            </div>
                            <div class="stat">
                                <div class="stat-label">Min Time</div>
                                <div class="stat-value">${endpoint.minTime}ms</div>
                            </div>
                            <div class="stat">
                                <div class="stat-label">Max Time</div>
                                <div class="stat-value">${endpoint.maxTime}ms</div>
                            </div>
                            <div class="stat">
                                <div class="stat-label">Last Status</div>
                                <div class="stat-value" style="color: ${statusColor}">${endpoint.lastStatus}</div>
                            </div>
                            <div class="stat">
                                <div class="stat-label">Success Rate</div>
                                <div class="stat-value success-rate">${endpoint.successRate.toFixed(1)}%</div>
                            </div>
                        </div>
                    </div>
                `;
        }).join('');
    }

    // Actualizar gráfico
    function updateChart(endpoints) {
        const chartContainer = document.getElementById('requestChart');

        if (endpoints.length === 0) {
            chartContainer.innerHTML = '<div class="loading">No data to display</div>';
            return;
        }

        const maxCount = Math.max(...endpoints.map(e => e.count), 1);

        chartContainer.innerHTML = endpoints.slice(0, 10).map(endpoint => {
            const height = (endpoint.count / maxCount) * 100;
            const label = `${endpoint.method} ${endpoint.path}`.substring(0, 30);

            return `
                    <div class="bar-container">
                        <div class="bar-value">${endpoint.count}</div>
                        <div class="bar" style="height: ${height}%"></div>
                        <div class="bar-label">${label}</div>
                    </div>
                `;
        }).join('');
    }

    // Resetear métricas
    async function resetMetrics() {
        if (!confirm('¿Estás seguro de resetear todas las métricas? Esta acción no se puede deshacer.')) {
            return;
        }

        try {
            const response = await fetch(METRICS_URL, { method: 'DELETE' });

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}`);
            }

            showAlert('Metrics reset successfully!', 'info');
            loadMetrics();
        } catch (error) {
            console.error('Error resetting metrics:', error);
            showAlert('Error resetting metrics: ' + error.message, 'error');
        }
    }

    // Actualizar estado
    function updateStatus(online) {
        const indicator = document.getElementById('statusIndicator');
        const statusText = document.getElementById('statusText');

        if (online) {
            indicator.classList.remove('offline');
            statusText.innerHTML = 'API Status: <strong style="color: #3fb950;">ONLINE</strong>';
        } else {
            indicator.classList.add('offline');
            statusText.innerHTML = 'API Status: <strong style="color: #f85149;">OFFLINE</strong>';
        }
    }

    // Actualizar última actualización
    function updateLastUpdate() {
        const now = new Date();
        const timeString = now.toLocaleTimeString();
        document.getElementById('lastUpdate').textContent = `Last update: ${timeString}`;
    }

    // Mostrar alerta
    function showAlert(message, type) {
        const container = document.getElementById('alertContainer');
        const alertClass = type === 'error' ? 'alert-error' : 'alert-info';

        container.innerHTML = `
                <div class="alert ${alertClass}">
                    ${message}
                </div>
            `;

        setTimeout(clearAlert, 5000);
    }

    function clearAlert() {
        document.getElementById('alertContainer').innerHTML = '';
    }

    // Obtener color según status code
    function getStatusColor(status) {
        if (status >= 200 && status < 300) return '#3fb950';
        if (status >= 300 && status < 400) return '#d29922';
        if (status >= 400 && status < 500) return '#f85149';
        if (status >= 500) return '#da3633';
        return '#8b949e';
    }
</script>
</body>
</html>
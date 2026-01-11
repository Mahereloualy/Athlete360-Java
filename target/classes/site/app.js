// ===== GLOBAL VARIABLES (must be at top) =====
var API = "http://localhost:8000/api";
var allAthletes = [];
var currentPage = 1;
var itemsPerPage = 10;
var avatarColors = ['#6366f1', '#8b5cf6', '#ec4899', '#14b8a6', '#f59e0b', '#ef4444', '#22c55e', '#3b82f6'];

// Chart defaults
if (typeof Chart !== 'undefined') {
    Chart.defaults.color = '#94a3b8';
    Chart.defaults.borderColor = 'rgba(255, 255, 255, 0.1)';
}

// ===== HELPER FUNCTIONS =====
function getInitials(name) {
    if (!name) return '?';
    return name.split(' ').map(function (n) { return n[0]; }).join('').toUpperCase().substring(0, 2);
}

function getAvatarColor(name) {
    if (!name) return avatarColors[0];
    var hash = 0;
    for (var i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    return avatarColors[Math.abs(hash) % avatarColors.length];
}

// ===== ATHLETES FUNCTIONS =====
async function loadAthletes() {
    try {
        var res = await fetch(API + "/athletes");
        var data = await res.json();
        allAthletes = data;
        applyFilters();
    } catch (e) {
        console.error("loadAthletes error:", e);
    }
}

function applyFilters() {
    var searchEl = document.getElementById("searchBox");
    var sportEl = document.getElementById("sportFilter");
    var limitEl = document.getElementById("limitFilter");

    var search = searchEl ? searchEl.value.toLowerCase() : "";
    var sportFilter = sportEl ? sportEl.value : "";
    var limit = limitEl ? parseInt(limitEl.value) : 10;

    var filtered = allAthletes.filter(function (a) {
        var matchSearch = a.name.toLowerCase().indexOf(search) >= 0 || a.sport.toLowerCase().indexOf(search) >= 0;
        var matchSport = !sportFilter || a.sport === sportFilter;
        return matchSearch && matchSport;
    });

    itemsPerPage = limit || filtered.length;
    currentPage = 1;
    renderAthletes(filtered);
}

function renderAthletes(athletes) {
    var tbody = document.querySelector("#athletesTable");
    if (!tbody) return;

    var start = (currentPage - 1) * itemsPerPage;
    var end = itemsPerPage > 0 ? start + itemsPerPage : athletes.length;
    var pageAthletes = athletes.slice(start, end);

    tbody.innerHTML = "";
    pageAthletes.forEach(function (a) {
        var initials = getInitials(a.name);
        var color = getAvatarColor(a.name);
        var tr = document.createElement("tr");
        tr.innerHTML =
            '<td>' +
            '<div class="athlete-cell">' +
            '<div class="avatar" style="background: ' + color + '">' + initials + '</div>' +
            '<span>' + a.name + '</span>' +
            '</div>' +
            '</td>' +
            '<td>' + a.age + '</td>' +
            '<td>' + a.height.toFixed(2) + 'm</td>' +
            '<td>' + a.weight.toFixed(1) + 'kg</td>' +
            '<td><span class="badge">' + a.sport + '</span></td>' +
            '<td>' + a.imc.toFixed(1) + '</td>' +
            '<td><button class="btn-delete" onclick="deleteAthlete(' + a.id + ')">Delete</button></td>';
        tbody.appendChild(tr);
    });

    renderPagination(athletes.length);
}

function renderPagination(totalItems) {
    var paginationEl = document.getElementById("pagination");
    if (!paginationEl) return;
    if (itemsPerPage >= totalItems || itemsPerPage === 0) {
        paginationEl.innerHTML = "";
        return;
    }

    var totalPages = Math.ceil(totalItems / itemsPerPage);
    var html = '<span>Page ' + currentPage + ' of ' + totalPages + '</span>';
    html += '<button onclick="changePage(-1)"' + (currentPage === 1 ? ' disabled' : '') + '>Prev</button>';
    html += '<button onclick="changePage(1)"' + (currentPage === totalPages ? ' disabled' : '') + '>Next</button>';
    paginationEl.innerHTML = html;
}

function changePage(delta) {
    currentPage += delta;
    var search = document.getElementById("searchBox");
    var sportEl = document.getElementById("sportFilter");
    var searchVal = search ? search.value.toLowerCase() : "";
    var sportFilter = sportEl ? sportEl.value : "";

    var filtered = allAthletes.filter(function (a) {
        var matchSearch = a.name.toLowerCase().indexOf(searchVal) >= 0;
        var matchSport = !sportFilter || a.sport === sportFilter;
        return matchSearch && matchSport;
    });
    renderAthletes(filtered);
}

async function deleteAthlete(id) {
    if (!confirm("Delete this athlete?")) return;
    await fetch(API + "/athletes?id=" + id, { method: "DELETE" });
    loadAthletes();
}

async function addAthlete() {
    var data = {
        name: document.getElementById("name").value,
        age: +document.getElementById("age").value,
        height: +document.getElementById("height").value,
        weight: +document.getElementById("weight").value,
        sport: document.getElementById("sport").value
    };
    await fetch(API + "/athletes", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data)
    });
    document.getElementById("name").value = "";
    document.getElementById("age").value = "";
    document.getElementById("height").value = "";
    document.getElementById("weight").value = "";
    loadAthletes();
}

// ===== DASHBOARD FUNCTIONS =====
async function loadDashboard() {
    try {
        var res = await fetch(API + "/athletes");
        var athletes = await res.json();

        var total = athletes.length;
        var avgWeight = 0, avgImc = 0, avgAge = 0;
        if (total > 0) {
            avgWeight = athletes.reduce(function (s, a) { return s + a.weight; }, 0) / total;
            avgImc = athletes.reduce(function (s, a) { return s + a.imc; }, 0) / total;
            avgAge = athletes.reduce(function (s, a) { return s + a.age; }, 0) / total;
        }

        var totalEl = document.getElementById("totalAthletes");
        var imcEl = document.getElementById("avgImc");
        var weightEl = document.getElementById("avgWeight");
        var ageEl = document.getElementById("avgAge");

        if (totalEl) totalEl.innerText = total;
        if (imcEl) imcEl.innerText = avgImc.toFixed(2);
        if (weightEl) weightEl.innerText = avgWeight.toFixed(1) + " kg";
        if (ageEl) ageEl.innerText = avgAge.toFixed(0) + " yrs";

        loadAgeChart(athletes);
        loadBmiChart(athletes);
    } catch (e) {
        console.error("loadDashboard error:", e);
    }
}

function loadAgeChart(athletes) {
    var ctx = document.getElementById("ageChart");
    if (!ctx || typeof Chart === 'undefined') return;

    var ranges = { '18-22': 0, '23-27': 0, '28-32': 0, '33-37': 0, '38+': 0 };
    athletes.forEach(function (a) {
        if (a.age <= 22) ranges['18-22']++;
        else if (a.age <= 27) ranges['23-27']++;
        else if (a.age <= 32) ranges['28-32']++;
        else if (a.age <= 37) ranges['33-37']++;
        else ranges['38+']++;
    });

    new Chart(ctx, {
        type: "bar",
        data: {
            labels: Object.keys(ranges),
            datasets: [{
                label: "Athletes",
                data: Object.values(ranges),
                backgroundColor: ['#6366f1', '#8b5cf6', '#ec4899', '#14b8a6', '#f59e0b'],
                borderRadius: 8
            }]
        },
        options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } }
    });
}

function loadBmiChart(athletes) {
    var ctx = document.getElementById("bmiChart");
    if (!ctx || typeof Chart === 'undefined') return;

    var categories = { 'Underweight': 0, 'Normal': 0, 'Overweight': 0, 'Obese': 0 };
    athletes.forEach(function (a) {
        if (a.imc < 18.5) categories['Underweight']++;
        else if (a.imc < 25) categories['Normal']++;
        else if (a.imc < 30) categories['Overweight']++;
        else categories['Obese']++;
    });

    new Chart(ctx, {
        type: "doughnut",
        data: {
            labels: Object.keys(categories),
            datasets: [{
                data: Object.values(categories),
                backgroundColor: ['#3b82f6', '#22c55e', '#f59e0b', '#ef4444'],
                borderWidth: 0
            }]
        },
        options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'right' } } }
    });
}

async function loadWeightChart() {
    try {
        var res = await fetch(API + "/weights");
        var data = await res.json();
        var ctx = document.getElementById("weightChart");
        if (!ctx || typeof Chart === 'undefined') return;
        new Chart(ctx, {
            type: "line",
            data: {
                labels: data.map(function (d) { return d.date; }),
                datasets: [{
                    label: "Average weight",
                    data: data.map(function (d) { return d.weight; }),
                    borderColor: '#6366f1',
                    backgroundColor: 'rgba(99, 102, 241, 0.2)',
                    fill: true,
                    tension: 0.4
                }]
            },
            options: { responsive: true, maintainAspectRatio: false }
        });
    } catch (e) {
        console.error("loadWeightChart error:", e);
    }
}

async function loadSportChart() {
    try {
        var res = await fetch(API + "/sports");
        var data = await res.json();
        var ctx = document.getElementById("sportChart");
        if (!ctx || typeof Chart === 'undefined') return;
        new Chart(ctx, {
            type: "doughnut",
            data: {
                labels: Object.keys(data),
                datasets: [{
                    data: Object.values(data),
                    backgroundColor: ["#6366f1", "#8b5cf6", "#ec4899", "#14b8a6", "#f59e0b"],
                    borderWidth: 0
                }]
            },
            options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'right' } } }
        });
    } catch (e) {
        console.error("loadSportChart error:", e);
    }
}

async function loadRecommendations() {
    try {
        var res = await fetch(API + "/recommendations");
        var recs = await res.json();
        var container = document.getElementById("recs");
        if (!container) return;
        container.innerHTML = "";
        recs.forEach(function (r) {
            var div = document.createElement("div");
            div.className = "card recommendation-card";
            div.innerHTML = '<span class="rec-icon">💡</span><span>' + r + '</span>';
            container.appendChild(div);
        });
    } catch (e) {
        console.error("loadRecommendations error:", e);
    }
}

// ===== INITIALIZATION =====
document.addEventListener("DOMContentLoaded", function () {
    loadAthletes();
    loadDashboard();
    loadWeightChart();
    loadSportChart();
    loadRecommendations();
});

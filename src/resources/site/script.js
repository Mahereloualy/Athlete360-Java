/* ===== ATHLETES ===== */
async function loadAthletes() {
  const body = document.getElementById("athletesBody");
  if (!body) return;

  const res = await fetch("/api/athletes", { cache: "no-store" });
  const data = await res.json();

  body.innerHTML = "";
  data.athletes.forEach(a => {
    body.innerHTML += `
      <tr>
        <td>${a.name}</td>
        <td>${a.age}</td>
        <td>${a.height}</td>
        <td>${a.weight}</td>
        <td>${a.imc}</td>
      </tr>`;
  });
}

async function addAthlete(event) {
  event.preventDefault();
  const form = event.target;

  const body = new URLSearchParams(new FormData(form));

  const res = await fetch("/api/athletes", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body
  });

  if (!res.ok) {
    alert("Add athlete failed");
    return;
  }

  form.reset();
  await loadAthletes();
  await loadAdvancedDashboard();
  await loadRecommendations();
}

/* ===== DASHBOARD + CHARTS ===== */
async function loadAdvancedDashboard() {
  const totalEl = document.getElementById("totalAthletes");
  const imcEl = document.getElementById("avgIMC");
  const wEl = document.getElementById("avgWeight");

  const chart1 = document.getElementById("imcChart");
  const chart2 = document.getElementById("weightChart");

  if (!totalEl && !chart1 && !chart2) return;

  const res = await fetch("/api/dashboard/extended", { cache: "no-store" });
  const d = await res.json();

  if (totalEl) totalEl.textContent = d.total;
  if (imcEl) imcEl.textContent = d.avgIMC;
  if (wEl) wEl.textContent = d.avgWeight + " kg";

  if (chart1 && window.Chart) {
    if (window.imcChartInstance) window.imcChartInstance.destroy();
    window.imcChartInstance = new Chart(chart1, {
      type: "bar",
      data: {
        labels: d.names,
        datasets: [{ label: "IMC", data: d.imcValues }]
      },
      options: { responsive: true }
    });
  }

  if (chart2 && window.Chart) {
    if (window.weightChartInstance) window.weightChartInstance.destroy();
    window.weightChartInstance = new Chart(chart2, {
      type: "line",
      data: {
        labels: d.names,
        datasets: [{ label: "Weight (kg)", data: d.weightValues }]
      },
      options: { responsive: true }
    });
  }
}

/* ===== RECOMMENDATIONS ===== */
async function loadRecommendations() {
  const box = document.getElementById("recoBox");
  if (!box) return;

  const res = await fetch("/api/recommendations/global", { cache: "no-store" });
  const d = await res.json();

  box.innerHTML = `
    <h3>Global Recommendation</h3>
    <p><b>Average IMC:</b> ${d.avgIMC}</p>
    <p>${d.plan}</p>
  `;
}

/* ===== INIT ===== */
document.addEventListener("DOMContentLoaded", () => {
  loadAthletes();
  loadAdvancedDashboard();
  loadRecommendations();
});

import json
import numpy as np
import matplotlib.pyplot as plt
import os

# Constants
alpha = 1700e-9
beta = 1600e9      # 800 Gb/s

TRIVANCE_SIZE = 9
OTHER_SIZE = 8

# Message sizes: 32B * 2^i, i=0..25
message_sizes = [32 * (2 ** i) for i in range(25)]

# Load data
with open("../../results/results.json", "r") as f:
    data = json.load(f)

# Define algorithm families
algorithm_families = {
    "Trivance": ["TRIVANCE_BANDWIDTH", "TRIVANCE_LATENCY"],
    "Swing": ["SWING_BANDWIDTH", "SWING_LATENCY"],
    "Swing MP": ["SWING_BANDWIDTH_TWO_PORT", "SWING_LATENCY"],
    "Rec.Doub. MP": ["RECURSIVE_DOUBLING_BANDWIDTH_TWO_PORT", "RECURSIVE_DOUBLING_LATENCY"],
    "Ring MP": ["RING_TWO_PORT", "RING_TWO_PORT"],
    "In One": ["IN_ONE", "IN_ONE"],
}

# Select base and comparisons
base_family = "Trivance"
comparison_families = ["Swing", "Swing MP", "Rec.Doub. MP", "Ring MP", "In One"]

family_colors = {
    "Swing MP": "#d62728",
    "Rec.Doub. MP": "#2ca02c",
    "Ring MP": "#ff7f0e",
    "Swing": "#8c564b",
    "In one": "black",
}

# Map name to selected latency/bandwidth pair
def extract_cost_for_size(entry, target_size):
    if target_size not in entry["network_sizes"]:
        raise ValueError(f"Size {target_size} not found in entry: {entry['name']}")
    idx = entry["network_sizes"].index(target_size)
    return entry["cost_latency"][idx], entry["cost_bandwidth"][idx]

# Compute cost curve
def compute_cost(latency, bandwidth):
    return [alpha * latency + (m * bandwidth / beta) for m in message_sizes]

# Map name to cost
name_to_cost = {}
for entries in algorithm_families.values():
    for name in entries:
        entry = next((e for e in data if e["name"] == name), None)
        if entry is None:
            raise ValueError(f"Algorithm '{name}' not found.")

        target_size = TRIVANCE_SIZE if "TRIVANCE" in name else OTHER_SIZE
        latency, bandwidth = extract_cost_for_size(entry, target_size)
        name_to_cost[name] = compute_cost(latency, bandwidth)

# Compute min cost for a family and track source
def min_cost_and_origin(family_names):
    all_costs = [name_to_cost[n] for n in family_names]
    stacked = np.vstack(all_costs)
    min_indices = np.argmin(stacked, axis=0)
    min_values = np.min(stacked, axis=0)
    origin_names = [family_names[i] for i in min_indices]
    return min_values, origin_names

# Base cost and origin
base_cost, base_origins = min_cost_and_origin(algorithm_families[base_family])

# Plot setup
os.makedirs("../../plots/new", exist_ok=True)
plt.figure(figsize=(10, 6))

font_scale = 2.3
base_fontsize = plt.rcParams['font.size'] if 'font.size' in plt.rcParams else 10
fs = base_fontsize * font_scale

# Plot each comparison family
for comp_family in comparison_families:
    comp_cost, comp_origins = min_cost_and_origin(algorithm_families[comp_family])
    diff_percent = [((c2 - c1) / c1) * 100 for c1, c2 in zip(base_cost, comp_cost)]

    plt.plot(
        message_sizes,
        diff_percent,
        linestyle='-',
        color=family_colors.get(comp_family, "black"),
        marker='o',
        markersize=5,
        label=f"{comp_family}"
    )

# Add bigger dots at switch points
all_families = [base_family] + comparison_families
for family in all_families:
    if family == "Ring MP":
        continue

    min_vals, origins = min_cost_and_origin(algorithm_families[family])
    try:
        switch_idx = next(i for i, name in enumerate(origins) if "BANDWIDTH" in name)
    except StopIteration:
        continue

    switch_x = message_sizes[switch_idx]
    switch_y = (
        ((min_vals[switch_idx] - base_cost[switch_idx]) / base_cost[switch_idx]) * 100
        if family != base_family else 0
    )

    if family == base_family:
        ymin, ymax = plt.ylim()
        y_vals = np.linspace(ymin, ymax, 500)
        wiggle_amplitude = (np.log2(message_sizes[-1]) - np.log2(message_sizes[0])) * 0.005
        wiggle = wiggle_amplitude * np.sin(30 * np.pi * (y_vals - ymin) / (ymax - ymin))
        x_vals = switch_x + wiggle
        plt.plot(x_vals, y_vals, color='gray', linewidth=2, linestyle=':', zorder=6)
    else:
        plt.plot(
            switch_x,
            switch_y,
            'o',
            color=family_colors.get(family, "green"),
            markersize=12,
            zorder=7
        )

# Formatting
plt.xscale('log', base=2)
xtick_labels = [
    f"{m}B" if m < 1024 else (f"{m // 1024}KB" if m < 1024**2 else f"{m // (1024**2)}MB")
    for m in message_sizes
]
plt.xticks(
    message_sizes[::2],
    xtick_labels[::2],
    rotation=45,
    fontsize=fs
)
plt.yticks(fontsize=fs)
plt.xlabel("Message Size", fontsize=fs)
plt.ylabel("Relative Cost vs. Trivance (%)", fontsize=fs)
plt.title(f"Latency: {alpha * 1e9:.0f} ns, Bandwidth: 1.6 Tb/s", fontsize=fs)
plt.grid(True, which='both', linestyle='--', linewidth=0.5)
plt.axhline(0, color='gray', linewidth=0.8, linestyle='--')
plt.ylim(-60, 70)
plt.legend(fontsize=fs * 0.85)
plt.tight_layout()

# Save plot
filename = base_family.lower().replace(" ", "_") + f"_unified_comparison_{OTHER_SIZE}-{TRIVANCE_SIZE}_{int(alpha*1e9)}_{int(beta/1e9)}.pdf"
plt.savefig(f"../../plots/new/{filename}")
plt.close()
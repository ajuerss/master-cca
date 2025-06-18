import json
import numpy as np
import matplotlib.pyplot as plt
import os

# Constants
alpha = 700e-9  # 700 ns
beta = 4e11     # 400 Gb/s

# Message sizes: 32B * 4^i, i=0..12
message_sizes = [32 * (4 ** i) for i in range(13)]

# Load data
with open("../../results/results_comparison_relative.json", "r") as f:
    data = json.load(f)

# Define algorithm families
algorithm_families = {
    "Trivance": ["TRIVANCE_BANDWIDTH", "TRIVANCE_LATENCY"],
    "Swing": ["SWING_BANDWIDTH", "SWING_LATENCY"],
    "Swing Two Port": ["SWING_BANDWIDTH_TWO_PORT", "SWING_LATENCY_TWO_PORT"],
    "Recursive Doubling": ["RECURSIVE_DOUBLING_BANDWIDTH", "RECURSIVE_DOUBLING_LATENCY"]
}

# Select base and comparisons
base_family = "Trivance"
comparison_families = ["Swing", "Swing Two Port", "Recursive Doubling"]

# Colors for families' lines
family_colors = {
    "Swing": "blue",
    "Swing Two Port": "orange",
    "Recursive Doubling": "red",
}

# Compute cost curve
def compute_cost(entry):
    latency = entry["cost_latency"][0]
    bandwidth = entry["cost_bandwidth"][0]
    return [alpha * latency + (m * bandwidth / beta) for m in message_sizes]

# Map name to cost
name_to_cost = {}
for entries in algorithm_families.values():
    for name in entries:
        entry = next((e for e in data if e["name"] == name), None)
        if entry is None:
            raise ValueError(f"Algorithm '{name}' not found.")
        name_to_cost[name] = compute_cost(entry)

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
        label=f"{comp_family} vs {base_family}"
    )

# Add vertical switch markers for all families
all_families = [base_family] + comparison_families
used_positions = set()

for family in all_families:
    _, origins = min_cost_and_origin(algorithm_families[family])
    switch_idx = next(i for i, name in enumerate(origins) if "BANDWIDTH" in name)
    switch_x = message_sizes[switch_idx]
    if switch_x in used_positions:
        continue  # avoid duplicate lines
    used_positions.add(switch_x)
    plt.axvline(x=switch_x, color='gray', linestyle='--', linewidth=1)
    plt.text(
        switch_x * 1.05,
        plt.ylim()[1] * 0.9,
        f"{switch_x // 1024}KB",
        rotation=90,
        verticalalignment='center',
        color='gray'
    )

# Formatting
plt.xscale('log', base=2)
plt.xticks(
    message_sizes,
    [f"{m}B" if m < 1024 else (f"{m // 1024}KB" if m < 1024**2 else f"{m // (1024**2)}MB") for m in message_sizes],
    rotation=45
)
plt.xlabel("Message Size")
plt.ylabel("Cost Difference (%) (0 = equal cost)")
plt.grid(True, which='both', linestyle='--', linewidth=0.5)
plt.axhline(0, color='gray', linewidth=0.8, linestyle='--')
plt.legend()
plt.tight_layout()

# Save plot
filename = base_family.lower().replace(" ", "_") + "_unified_comparisons_all_switches.png"
plt.savefig(f"../../plots/new/{filename}")
plt.close()

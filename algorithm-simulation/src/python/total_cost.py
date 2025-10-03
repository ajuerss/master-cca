import json
import numpy as np
import matplotlib.pyplot as plt
import os

# Constants
alpha = 10000e-9
beta = 800e9  # 800 Gb/s

TRIVANCE_SIZE = 9
OTHER_SIZE = 8

# Message sizes: 32B * 2^i, i = 0..24
message_sizes = [32 * (2 ** i) for i in range(25)]

# Load data
with open("../../results/results.json", "r") as f:
    data = json.load(f)

# Define algorithm families
algorithm_families = {
    "Swing": ["SWING_BANDWIDTH", "SWING_LATENCY"],
    "Swing MP": ["SWING_BANDWIDTH_TWO_PORT", "SWING_LATENCY"],
    "Recursive Doubling MP": ["RECURSIVE_DOUBLING_BANDWIDTH_TWO_PORT", "RECURSIVE_DOUBLING_LATENCY"],
    "Ring MP": ["RING_TWO_PORT", "RING_TWO_PORT"],
    "Trivance": ["TRIVANCE_BANDWIDTH", "TRIVANCE_LATENCY"],
}

family_colors = {
    "Swing": "#8c564b",
    "Swing MP": "#d62728",
    "Recursive Doubling MP": "#2ca02c",
    "Ring MP": "#ff7f0e",
    "Trivance": "#1f77b4",
}

# Extract latency and bandwidth for a given size
def extract_cost_for_size(entry, target_size):
    if target_size not in entry["network_sizes"]:
        raise ValueError(f"Size {target_size} not found in entry: {entry['name']}")
    idx = entry["network_sizes"].index(target_size)
    return entry["cost_latency"][idx], entry["cost_bandwidth"][idx]

# Compute cost curve from latency and bandwidth
def compute_cost(latency, bandwidth):
    return [alpha * latency + (m * bandwidth / beta) for m in message_sizes]

# Map each variant name to its cost curve
name_to_cost = {}
for entries in algorithm_families.values():
    for name in entries:
        entry = next((e for e in data if e["name"] == name), None)
        if entry is None:
            raise ValueError(f"Algorithm '{name}' not found.")

        target_size = TRIVANCE_SIZE if "TRIVANCE" in name else OTHER_SIZE
        latency, bandwidth = extract_cost_for_size(entry, target_size)
        name_to_cost[name] = [c * 1000 for c in compute_cost(latency, bandwidth)]

# Get minimum cost across variants in a family and track origin
def min_cost_and_origin(family_names):
    all_costs = [name_to_cost[n] for n in family_names]
    stacked = np.vstack(all_costs)
    min_indices = np.argmin(stacked, axis=0)
    min_values = np.min(stacked, axis=0)
    origin_names = [family_names[i] for i in min_indices]
    return min_values, origin_names

# Output directory
os.makedirs("../../plots/new", exist_ok=True)

# Plot
plt.figure(figsize=(10, 6))

font_scale = 2.5
base_fontsize = plt.rcParams.get('font.size', 10)
fs = base_fontsize * font_scale

# Plot each family as its own curve
for family, variants in algorithm_families.items():
    cost_curve, origins = min_cost_and_origin(variants)
    plt.plot(
        message_sizes,
        cost_curve,
        linestyle='-',
        color=family_colors.get(family, "black"),
        marker='o',
        markersize=5,
        label=family
    )

    # Mark switch point
    try:
        switch_idx = next(i for i, name in enumerate(origins) if "BANDWIDTH" in name)
    except StopIteration:
        continue

    switch_x = message_sizes[switch_idx]
    switch_y = cost_curve[switch_idx]

    plt.plot(
        switch_x,
        switch_y,
        'o',
        color=family_colors.get(family, "black"),
        markersize=12,
        zorder=7
    )

# Axis formatting
plt.xscale('log', base=2)
xtick_labels = [
    f"{m}B" if m < 1024 else (f"{m // 1024}KB" if m < 1024**2 else f"{m // (1024**2)}MB")
    for m in message_sizes
]
plt.yscale('log')
plt.xticks(message_sizes[::2], xtick_labels[::2], rotation=45, fontsize=fs)
plt.yticks(fontsize=fs)
plt.xlabel("Message Size", fontsize=fs)
plt.ylabel("Completion Time (ms)", fontsize=fs)
plt.title(f"Latency: {alpha * 1e9:.0f} ns, Bandwidth: {beta / 1e9:.0f} Gb/s", fontsize=fs)
plt.grid(True, which='both', linestyle='--', linewidth=0.5)
plt.legend(fontsize=fs * 0.9)
plt.tight_layout()

# Save
filename = f"unified_absolute_{OTHER_SIZE}_{int(alpha*1e9)}_{int(beta/1e9)}.pdf"
plt.savefig(f"../../plots/new/{filename}")
plt.close()

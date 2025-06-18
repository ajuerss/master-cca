import json
import numpy as np
import matplotlib.pyplot as plt
import os

# Constants
alpha = 100e-9  # 700 ns
beta = 1e11     # 400 Gb/s

# Message sizes: 32B * 4^i, i=0..19 (~34 GB)
message_sizes = [32 * (4 ** i) for i in range(13)]

# Load data
with open("../../results/results_comparison_relative.json", "r") as f:
    data = json.load(f)

# Specify algorithms
base_algorithm_name = "TRIVANCE_BANDWIDTH"
comparison_algorithm_names = [
    "SWING_BANDWIDTH",
    "SWING_BANDWIDTH_TWO_PORT",
    "RECURSIVE_DOUBLING_BANDWIDTH",
    "RING_TWO_PORT",
]

# Format name for labels
def format_name(name):
    return name.replace("_", " ").title()

# Compute cost
def compute_cost(entry):
    latency = entry["cost_latency"][0]
    bandwidth = entry["cost_bandwidth"][0]
    return [alpha * latency + (m * bandwidth / beta) for m in message_sizes]

# Locate base algorithm
base_entry = next((e for e in data if e["name"] == base_algorithm_name), None)
if base_entry is None:
    raise ValueError(f"Base algorithm '{base_algorithm_name}' not found.")
base_cost = compute_cost(base_entry)
base_label = format_name(base_algorithm_name)

# Create output dir if needed
os.makedirs("../../plots/new", exist_ok=True)

# Plot setup
plt.figure(figsize=(10, 6))

# Compare each algorithm
for comp_name in comparison_algorithm_names:
    comp_entry = next((e for e in data if e["name"] == comp_name), None)
    if comp_entry is None:
        raise ValueError(f"Comparison algorithm '{comp_name}' not found.")

    comp_cost = compute_cost(comp_entry)
    comp_label = format_name(comp_name)

    diff_percent = [((c2 - c1) / c1) * 100 for c1, c2 in zip(base_cost, comp_cost)]

    plt.plot(message_sizes, diff_percent, marker='o', label=f"{comp_label} vs {base_label}")

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

# Save to file
filename = f"{base_algorithm_name.lower()}_comparisons_relative_cost.png"
plt.savefig(f"../../plots/new/{filename}")
plt.close()

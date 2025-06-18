import json
import matplotlib.pyplot as plt
import numpy as np

# Load data
with open("../../results/results.json", "r") as f:
    data = json.load(f)

# Algorithms to include
included_algorithms = [
    "TRIVANCE_LATENCY",
    "RECURSIVE_DOUBLING_LATENCY",
    "SWING_LATENCY"
]

plt.figure(figsize=(10, 6))

# Plot selected algorithms with formatted labels
for entry in data:
    if entry["name"] in included_algorithms:
        name = entry["name"]
        label = name.replace("_", " ").title()
        x = np.array(entry["network_sizes"])
        y = np.array(entry["cost_bandwidth"])
        plt.plot(x, y, marker='o', label=label)

plt.xlabel("Ring Size")
plt.ylabel("Congestion-Aware Bandwidth Cost")
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("../../plots/new/bandwidth_plot.png")

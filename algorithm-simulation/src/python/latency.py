import json
import matplotlib.pyplot as plt
import numpy as np
from collections import defaultdict

# Load data
with open("../../results/results.json", "r") as f:
    data = json.load(f)

# Algorithms to include
included_algorithms = [
    "TRIVANCE_LATENCY",
    "RECURSIVE_DOUBLING_LATENCY",
    "SWING_LATENCY",
    "SWING_LATENCY_TWO_PORT"
]

plt.figure(figsize=(10, 6))

# Track identical curves using stringified y-values
y_curve_counts = defaultdict(int)

for entry in data:
    if entry["name"] in included_algorithms:
        name = entry["name"]
        label = name.replace("_", " ").title()
        x = np.array(entry["network_sizes"])
        y = np.array(entry["cost_latency"])

        y_key = tuple(y)
        offset = y_curve_counts[y_key] * 0.05
        y_shifted = y + offset
        y_curve_counts[y_key] += 1

        plt.plot(x, y_shifted, marker='o', label=label)

plt.xlabel("Ring Size")
plt.ylabel("Latency Cost as Number of Steps")
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("../../plots/new/latency_plot.png")

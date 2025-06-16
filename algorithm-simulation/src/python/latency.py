import json
import matplotlib.pyplot as plt
import numpy as np
from collections import defaultdict

with open("../../results/latency.json", "r") as f:
    data = json.load(f)

plt.figure(figsize=(10, 6))

# Track identical curves using stringified y-values
y_curve_counts = defaultdict(int)

for entry in data:
    name = entry["name"]
    x = np.array(entry["network_sizes"])
    y = np.array(entry["cost_latency"])

    y_key = tuple(y)
    offset = y_curve_counts[y_key] * 0.05
    y_shifted = y + offset
    y_curve_counts[y_key] += 1

    plt.plot(x, y_shifted, marker='o', label=name)

plt.xlabel("Ring Size")
plt.ylabel("Latency Cost in Steps")
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("../../plots/latency_plot.png")

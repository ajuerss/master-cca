import json
import matplotlib.pyplot as plt
import numpy as np

with open("../../results/results.json", "r") as f:
    data = json.load(f)

plt.figure(figsize=(10, 6))

for entry in data:
    name = entry["name"]
    x = np.array(entry["network_sizes"])
    y = np.array(entry["cost_bandwidth"])
    plt.plot(x, y, marker='o', label=name)

plt.xlabel("Ring Size")
plt.ylabel("Bandwidth Cost as factor of message size")
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("../../plots/new/bandwidth_plot.png")

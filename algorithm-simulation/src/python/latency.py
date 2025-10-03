import json
import matplotlib.pyplot as plt
import numpy as np
from collections import defaultdict

# Load data
with open("../../results/results.json", "r") as f:
    data = json.load(f)

# Algorithms to include with translated names
label_map = {
    "TRIVANCE_LATENCY": "Trivance (L)",
    "RECURSIVE_DOUBLING_LATENCY": "Recursive Doubling (L)",
    "SWING_LATENCY": "Swing (L)",
    "RING_TWO_PORT": "Ring",
}

color_map = {
    "TRIVANCE_LATENCY": "#1f77b4",                    # blue
    "TRIVANCE_BANDWIDTH": "#1f77b4",                    # blue
    "SWING_BANDWIDTH_TWO_PORT": "#d62728",              # red
    "SWING_LATENCY": "#d62728",              # red
    "RECURSIVE_DOUBLING_LATENCY": "#2ca02c", # green
    "RECURSIVE_DOUBLING_BANDWIDTH_TWO_PORT": "#2ca02c", # green
    "RING_TWO_PORT": "#ff7f0e",                         # orange
    "RECURSIVE_DOUBLING_BANDWIDTH": "#9467bd",          # purple
    "SWING_BANDWIDTH": "#8c564b",                       # brown
}

included_algorithms = list(label_map.keys())

plt.figure(figsize=(12, 10))

# Track identical curves using stringified y-values
y_curve_counts = defaultdict(int)

for entry in data:
    if entry["name"] in included_algorithms:
        name = entry["name"]
        label = label_map[name]
        x = np.array(entry["network_sizes"])
        y = np.array(entry["cost_latency"])

        y_key = tuple(y)
        offset = y_curve_counts[y_key] * 0.05
        y_shifted = y + offset
        y_curve_counts[y_key] += 1

        plt.plot(x, y_shifted, marker='o', markersize=15, linewidth=3.0, label=label, color=color_map[name])

font_scale = 4.5  # 50% increase
base_fontsize = plt.rcParams['font.size'] if 'font.size' in plt.rcParams else 10
fs = base_fontsize * font_scale

plt.xlabel("Ring Size", fontsize=fs)
plt.ylabel("Number of Steps", fontsize=fs)
plt.xticks(fontsize=fs)

plt.ylim(0, 9)
new_yticks = np.arange(0, 10, 3)
plt.yticks(new_yticks, fontsize=fs)

# Enforce legend order based on label_map
handles, labels = plt.gca().get_legend_handles_labels()
ordered_labels = list(label_map.values())
ordered_handles = [handles[labels.index(lab)] for lab in ordered_labels if lab in labels]
plt.suptitle("Latency-Optimal Algorithms", fontsize=fs * 1.1)

plt.grid(True)
plt.legend(ordered_handles, ordered_labels, loc="lower right", fontsize=fs*0.7)
plt.tight_layout()
plt.savefig("../../plots/new/latency_plot.pdf")

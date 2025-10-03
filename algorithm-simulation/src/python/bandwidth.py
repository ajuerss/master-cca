import json
import matplotlib.pyplot as plt
import numpy as np

# Load data
with open("../../results/results.json", "r") as f:
    data = json.load(f)

# Label map with desired order
label_map = {
    "TRIVANCE_BANDWIDTH": "Trivance (B)",
    "RECURSIVE_DOUBLING_BANDWIDTH_TWO_PORT": "Recursive Doubling (B)",
    "SWING_BANDWIDTH_TWO_PORT": "Swing (B)",
    "RING_TWO_PORT": "Ring"
}

# Color map for all algorithms using distinct default colors
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
handles = []
labels = []

plt.figure(figsize=(12, 10))

# Plot selected algorithms with translated labels
for entry in data:
    name = entry["name"]
    if name in included_algorithms:
        label = label_map[name]
        x = np.array(entry["network_sizes"])
        y = np.maximum(np.array(entry["cost_bandwidth"]), 1)
        color = color_map.get(name, None)
        (line,) = plt.plot(x, y, marker='o', label=label, color=color,  markersize=18, linewidth=5.0)
        handles.append((name, line))

# Sort legend handles by label_map order
handles, labels = plt.gca().get_legend_handles_labels()
ordered_labels = list(label_map.values())
ordered_handles = [handles[labels.index(lab)] for lab in ordered_labels if lab in labels]

font_scale = 4.5  # 50% increase
base_fontsize = plt.rcParams['font.size'] if 'font.size' in plt.rcParams else 10
fs = base_fontsize * font_scale

plt.xlabel("Ring Size", fontsize=fs)
plt.ylabel("Transmission Delay Opt.", fontsize=fs, labelpad=15)
plt.xticks(fontsize=fs)
plt.yticks(np.arange(1, 6, 1), fontsize=fs)
plt.ylim(0.5, 5)
plt.suptitle("Bandwidth-Optimal Algorithms", fontsize=fs * 1.1)

plt.grid(True)
plt.legend(ordered_handles, ordered_labels, loc="upper left", fontsize=fs*0.7)

plt.tight_layout()
plt.savefig("../../plots/new/bandwidth_plot.pdf")
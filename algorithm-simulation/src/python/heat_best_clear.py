import numpy as np
import matplotlib.pyplot as plt
import json
from collections import defaultdict
from matplotlib.patches import Patch
from matplotlib.ticker import FuncFormatter

# Font scaling factor
FONT_SCALE = 2.3

# Message size: 2 MB
message_size = 512
message_size_bytes = message_size * 1024

# Network sizes
TRIVANCE_SIZE = 9
OTHER_SIZE = 8

# Load data
with open("../../results/results.json", "r") as f:
    raw_data = json.load(f)

# Group by algorithm name and select the appropriate network size
grouped_params = defaultdict(list)
for entry in raw_data:
    name = entry["name"]
    sizes = entry["network_sizes"]
    latencies = entry["cost_latency"]
    bandwidths = entry["cost_bandwidth"]

    target_size = TRIVANCE_SIZE if "TRIVANCE" in name else OTHER_SIZE

    if target_size in sizes:
        idx = sizes.index(target_size)
        grouped_params[name].append({
            "latency": latencies[idx],
            "bandwidth": bandwidths[idx]
        })

# Alpha and Beta grid
alpha_vals = np.linspace(100e-9, 10000e-9, 300)
beta_vals = np.linspace(100e9, 3200e9, 300)
A, B = np.meshgrid(alpha_vals, beta_vals)

# Compute all cost surfaces
all_names = list(grouped_params.keys())
cost_surfaces = []
name_lookup = []

for name in all_names:
    for variant in grouped_params[name]:
        cost = A * variant["latency"] + (message_size_bytes * variant["bandwidth"] / B)
        cost_surfaces.append(cost)
        name_lookup.append(name)

cost_surfaces = np.array(cost_surfaces)

# Identify best
best_indices = np.argmin(cost_surfaces, axis=0)

# Sorted names by appearance
sorted_names = sorted(set(name_lookup))
name_to_int = {name: i for i, name in enumerate(sorted_names)}
int_to_name = {i: name for name, i in name_to_int.items()}

# Define colors
algorithm_colors = {
    "TRIVANCE_BANDWIDTH": [0.1216, 0.4667, 0.7059, 1.0],
    "TRIVANCE_LATENCY": [0.4627, 0.7412, 0.9137, 1.0],
    "SWING_BANDWIDTH_TWO_PORT": [0.8392, 0.1529, 0.1569, 1.0],
    "SWING_LATENCY": [0.949, 0.557, 0.557, 1.0],
    "RING_TWO_PORT": [1.0, 0.4980, 0.0549, 1.0],
}

name_map = {
    "TRIVANCE_BANDWIDTH": "Trivance (B)",
    "TRIVANCE_LATENCY": "Trivance (L)",
    "SWING_BANDWIDTH_TWO_PORT": "Swing (B)",
    "SWING_LATENCY": "Swing (L)",
    "RING_TWO_PORT": "Ring"
}

fallback_colors = plt.cm.tab20(np.linspace(0, 1, len(sorted_names)))

# Winner ID map
winner_ids = np.array([name_to_int[name_lookup[i]] for i in best_indices.flat]).reshape(best_indices.shape)

# RGBA image
rgba_image = np.zeros(winner_ids.shape + (4,))
for i, name in int_to_name.items():
    color = algorithm_colors.get(name, fallback_colors[i % len(fallback_colors)])
    rgba_image[winner_ids == i] = color

# Compute area percentages
total_points = winner_ids.size
area_counts = [(winner_ids == i).sum() for i in range(len(sorted_names))]
area_percents = [100 * c / total_points for c in area_counts]

# Formatter for thousands separator
thousands_fmt = FuncFormatter(lambda x, pos: f"{int(x):,}")

# Plot
plt.figure(figsize=(10, 6))
plt.imshow(
    rgba_image,
    extent=[
        alpha_vals[0] * 1e9,
        alpha_vals[-1] * 1e9,
        beta_vals[0] / 1e9,
        beta_vals[-1] / 1e9
    ],
    aspect='auto',
    origin='lower'
)

legend_elements = [
    Patch(
        facecolor=algorithm_colors.get(name, fallback_colors[i % len(fallback_colors)]),
        label=f"{name_map.get(name, name)}: {area_percents[i]:.1f}%"
    )
    for i, name in int_to_name.items()
    if area_percents[i] >= 0.1
]
plt.legend(handles=legend_elements, title="Best Algorithm", loc="upper right",
           fontsize=plt.rcParams['font.size'] * FONT_SCALE,
           title_fontsize=plt.rcParams['font.size'] * FONT_SCALE)

plt.xlabel("Latency α (ns)", fontsize=plt.rcParams['font.size'] * FONT_SCALE)
plt.ylabel("Network Bandwidth 1/β (Gb/s)", fontsize=plt.rcParams['font.size'] * FONT_SCALE)

# Apply thousands separator formatting
plt.gca().xaxis.set_major_formatter(thousands_fmt)
plt.gca().yaxis.set_major_formatter(thousands_fmt)

plt.xticks(fontsize=plt.rcParams['font.size'] * FONT_SCALE)
plt.yticks(fontsize=plt.rcParams['font.size'] * FONT_SCALE)
plt.title(f"Message: 512 KB | Network sizes: {OTHER_SIZE:,} , {TRIVANCE_SIZE:,} ",
          fontsize=plt.rcParams['font.size'] * FONT_SCALE)

plt.tight_layout()
plt.savefig(f"../../plots/new/heat-512KB-clear-n{OTHER_SIZE}-{TRIVANCE_SIZE}.pdf")

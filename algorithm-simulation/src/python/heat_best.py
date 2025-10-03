import numpy as np
import matplotlib.pyplot as plt
import json
from collections import defaultdict
from matplotlib.patches import Patch
from matplotlib.colors import ListedColormap
import matplotlib as mpl

# Font scaling factor
FONT_SCALE = 2

# Message size: 2 MB
message_size = 4096 * 2
message_size_bytes = message_size * 1024

# Network sizes
TRIVANCE_SIZE = 243
OTHER_SIZE = 256

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

# Categorize each algorithm
def get_category(name):
    if "TRIVANCE" in name:
        return "TRIVANCE"
    elif "SWING" in name:
        return "SWING"
    elif "RING" in name:
        return "RING"
    else:
        return "OTHER"

name_categories = [get_category(name) for name in name_lookup]

# Find best algorithm at each point
winner_indices = np.argmin(cost_surfaces, axis=0)
winner_costs = np.min(cost_surfaces, axis=0)
winner_names = np.array(name_lookup)[winner_indices.flat]
winner_cats = np.array(name_categories)[winner_indices.flat]

# Compute improvement intensity over best other-category algorithm
intensities = np.ones(winner_costs.shape)

for idx, (win_idx, win_cat) in enumerate(zip(winner_indices.flat, winner_cats)):
    row = idx // winner_costs.shape[1]
    col = idx % winner_costs.shape[1]

    competing_costs = []
    for i, cost_surface in enumerate(cost_surfaces):
        if name_categories[i] != win_cat:
            competing_costs.append(cost_surface[row, col])
    if competing_costs:
        best_other_cost = min(competing_costs)
        winner_cost = winner_costs[row, col]

        # Ratio: winner / best_other
        if best_other_cost > 0:
            ratio = winner_cost / best_other_cost
            if ratio >= 1.0:
                intensity = 0.0  # equal or worse
            elif ratio <= 0.75:
                intensity = 1.0  # strong improvement
            else:
                intensity = (1.0 - ratio) / 0.25  # linear between 0.75 and 1.0
        else:
            intensity = 1.0
        intensities[row, col] = intensity

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

fallback_colors = plt.cm.tab20(np.linspace(0, 1, len(sorted_names)))

# Winner ID map
winner_ids = np.array([name_to_int[name_lookup[i]] for i in winner_indices.flat]).reshape(winner_indices.shape)

# RGBA image with intensity-scaled alpha
rgba_image = np.zeros(winner_ids.shape + (4,))
for i, name in int_to_name.items():
    base_color = algorithm_colors.get(name, fallback_colors[i % len(fallback_colors)])
    mask = (winner_ids == i)
    rgba_image[mask] = base_color
    rgba_image[mask, 3] *= intensities[mask]  # scale alpha by intensity

# Compute area percentages
total_points = winner_ids.size
area_counts = [(winner_ids == i).sum() for i in range(len(sorted_names))]
area_percents = [100 * c / total_points for c in area_counts]

# Plot
fig, ax = plt.subplots(figsize=(10, 6))
im = ax.imshow(
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

# Legend for algorithms
legend_elements = [
    Patch(
        facecolor=algorithm_colors.get(name, fallback_colors[i % len(fallback_colors)]),
        label=f"{name}: {area_percents[i]:.1f}%"
    )
    for i, name in int_to_name.items()
    if area_percents[i] >= 0.1
]
ax.legend(handles=legend_elements, title="Best Algorithm", loc="upper right",
          fontsize=plt.rcParams['font.size'] * FONT_SCALE * 0.8,
          title_fontsize=plt.rcParams['font.size'] * FONT_SCALE * 0.8)

# Add custom scalar mappable for intensity bar
cmap = ListedColormap([[1,1,1,a] for a in np.linspace(0,1,256)])
norm = mpl.colors.Normalize(vmin=0.75, vmax=1.0)
cbar = fig.colorbar(mpl.cm.ScalarMappable(norm=norm, cmap=cmap),
                    ax=ax, orientation='vertical', pad=0.02)
cbar.set_label("Cost ratio to next best from other category", fontsize=plt.rcParams['font.size'] * FONT_SCALE * 0.8)
cbar.ax.set_yticklabels(["≤0.75", "0.80", "0.85", "0.90", "0.95", "1.00"])

# Labels
ax.set_xlabel("Latency α (ns)", fontsize=plt.rcParams['font.size'] * FONT_SCALE)
ax.set_ylabel("Network Bandwidth 1/β (Gb/s)", fontsize=plt.rcParams['font.size'] * FONT_SCALE)
ax.tick_params(axis='both', labelsize=plt.rcParams['font.size'] * FONT_SCALE)

plt.tight_layout()
plt.savefig(f"../../plots/new/heat-{message_size}KB-clear-n{OTHER_SIZE}-{TRIVANCE_SIZE}.pdf")

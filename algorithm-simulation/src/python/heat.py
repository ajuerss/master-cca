import numpy as np
import matplotlib.pyplot as plt
import json

# Message size: 512 KB
message_size_bytes = 2048 * 1024

# Load data
with open("../../results/results-heat-bucket-8-9.json", "r") as f:
    raw_data = json.load(f)

# Ensure exactly two algorithms are present
if len(raw_data) != 2:
    raise ValueError("Input must contain exactly two algorithm entries.")

# Extract algorithm names and parameters
names = [entry["name"] for entry in raw_data]
params = {
    names[0]: {
        "latency": raw_data[0]["cost_latency"][0],
        "bandwidth": raw_data[0]["cost_bandwidth"][0]
    },
    names[1]: {
        "latency": raw_data[1]["cost_latency"][0],
        "bandwidth": raw_data[1]["cost_bandwidth"][0]
    }
}

# Alpha: 100ns to 10000ns
alpha_vals = np.linspace(100e-9, 10000e-9, 300)

# Beta: 100 Gb/s to 3200 Gb/s
beta_vals = np.linspace(100e9, 3200e9, 300)

# Create meshgrid
A, B = np.meshgrid(alpha_vals, beta_vals)

# Compute cost matrices
def compute_cost_matrix(latency, bandwidth):
    return A * latency + (message_size_bytes * bandwidth / B)

cost_a = compute_cost_matrix(**params[names[0]])
cost_b = compute_cost_matrix(**params[names[1]])

# Percent difference relative to second algorithm
cost_diff_percent = 100 * (cost_a - cost_b) / cost_b

# Plotting
plt.figure(figsize=(10, 6))
cmap = plt.cm.seismic
im = plt.imshow(
    cost_diff_percent,
    cmap=cmap,
    extent=[
        alpha_vals[0] * 1e9,
        alpha_vals[-1] * 1e9,
        beta_vals[0] / 1e9,
        beta_vals[-1] / 1e9
    ],
    aspect='auto',
    origin='lower',
    vmin=-np.max(np.abs(cost_diff_percent)),
    vmax=np.max(np.abs(cost_diff_percent))
)

plt.colorbar(im, label="Cost Difference (%)")
plt.xlabel("Alpha (ns)")
plt.ylabel("Beta (Gb/s)")
plt.tight_layout()
plt.savefig("../../plots/new/heat-bucket-2MB-l.pdf")

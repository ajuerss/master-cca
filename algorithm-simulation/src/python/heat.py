import numpy as np
import matplotlib.pyplot as plt

# Message size: 2 MB
message_size_bytes = 512 * 1024  # 2 MB in bytes

# Algorithm parameters
algorithms = {
    "TRIVANCE_BANDWIDTH": {"latency": 10.0, "bandwidth": 3.33},
    "SWING_BANDWIDTH_TWO_PORT": {"latency": 16.0, "bandwidth": 2.78}
}

# Alpha: latency penalty from 100ns to 1000ns
alpha_vals = np.linspace(100e-9, 1000e-9, 300)

# Beta: bandwidth from 50 GB/s to 500 GB/s
beta_vals = np.linspace(50e9, 500e9, 300)

# Create meshgrid
A, B = np.meshgrid(alpha_vals, beta_vals)

# Compute cost matrices
def compute_cost_matrix(latency, bandwidth):
    return A * latency + (message_size_bytes * bandwidth / B)

cost_trivance = compute_cost_matrix(**algorithms["TRIVANCE_BANDWIDTH"])
cost_swing = compute_cost_matrix(**algorithms["SWING_BANDWIDTH_TWO_PORT"])

# Compute percent difference relative to Swing cost
cost_diff_percent = 100 * (cost_trivance - cost_swing) / cost_swing

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
    vmin=-np.max(np.abs(cost_diff_percent)),  # Symmetric color scale
    vmax=np.max(np.abs(cost_diff_percent))
)

plt.colorbar(im, label="Cost Difference (%)")
plt.xlabel("Alpha Cost in (ns)")
plt.ylabel("Beta Cost in (GB/s)")
plt.tight_layout()
plt.savefig("../../plots/new/heat.png")

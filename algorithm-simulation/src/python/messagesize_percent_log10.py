import json
import numpy as np
import matplotlib.pyplot as plt

# Constants
alpha = 1e-7  # 100 ns
beta = 1e11   # 100 GB/s

# Message sizes in bytes: start from 32B, each tick times 4, 15 ticks (~536MB)
message_sizes = [32 * (4 ** i) for i in range(15)]

# Read JSON
with open("../../results/2048_2187_messagesize_bandwidth.json", "r") as f:
    data = json.load(f)

if len(data) != 2:
    raise ValueError("Input JSON must contain exactly two algorithms.")

# Extract the two algorithms dynamically
alg1 = data[0]
alg2 = data[1]

def compute_cost(entry):
    latency = entry["cost_latency"][0]
    bandwidth = entry["cost_bandwidth"][0]
    return [alpha * latency + (m * bandwidth / beta) for m in message_sizes]

cost1 = compute_cost(alg1)
cost2 = compute_cost(alg2)

# Compute log10 ratio scaled by 100 for better symmetry and interpretability
import math
log_ratio_percent = [100 * np.log10(c2 / c1) for c1, c2 in zip(cost1, cost2)]

# Plot
plt.figure(figsize=(10, 6))
plt.plot(message_sizes, log_ratio_percent, marker='o', color='blue',
         label=f"{alg2['name']} vs {alg1['name']} (log10 ratio × 100)")

# Formatting
plt.xscale('log', base=2)
plt.xticks(
    message_sizes,
    [f"{m}B" if m < 1024 else (f"{m // 1024}KB" if m < 1024 ** 2 else f"{m // (1024 ** 2)}MB") for m in message_sizes],
    rotation=45
)
plt.xlabel("Message Size")
plt.ylabel("Log10 Ratio × 100\n(0 = equal, +100 = 10× cost, -100 = 1/10× cost)")
plt.title(f"Relative Cost Difference of {alg2['name']} vs {alg1['name']}")
plt.grid(True, which='both', linestyle='--', linewidth=0.5)
plt.axhline(0, color='gray', linewidth=0.8, linestyle='--')  # baseline at 0%
plt.legend()
plt.tight_layout()
plt.savefig("../../plots/new/trivance_swing_bandwidth_RC_messagesize_2048_2187.png")

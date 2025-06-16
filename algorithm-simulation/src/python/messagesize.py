import json
import numpy as np
import matplotlib.pyplot as plt

# Constants
alpha = 1e-7  # 100 ns
beta = 1e11   # 100 GB/s

# Message sizes in bytes: start from 32B, each tick times 4, 15 ticks (~536MB)
message_sizes = [32 * (4 ** i) for i in range(15)]

# Read JSON
with open("../../results/messagesize_bandwidth.json", "r") as f:
    data = json.load(f)

# Extract algorithms
alg_dict = {entry["name"]: entry for entry in data}
swing = alg_dict["SWING_BANDWIDTH_TWO_PORT"]
trivance = alg_dict["TRIVANCE_BANDWIDTH"]

# Compute C(A) for both
def compute_cost(entry):
    latency = entry["cost_latency"][0]
    bandwidth = entry["cost_bandwidth"][0]
    return [alpha * latency + (m * bandwidth / beta) for m in message_sizes]

cost_swing = compute_cost(swing)
cost_trivance = compute_cost(trivance)

# Compute percentage difference relative to swing, centered at zero
diff_percent = [((t - s) / s) * 100 for t, s in zip(cost_trivance, cost_swing)]

# Plot
plt.figure(figsize=(10, 6))
plt.plot(message_sizes, diff_percent, marker='o', color='blue', label="Trivance vs Swing (%)")

# Formatting
plt.xscale('log', base=2)
plt.xticks(
    message_sizes,
    [f"{m}B" if m < 1024 else (f"{m // 1024}KB" if m < 1024 ** 2 else f"{m // (1024 ** 2)}MB") for m in message_sizes],
    rotation=45
)
plt.xlabel("Message Size")
plt.ylabel("Cost Difference (%) (0 = equal cost)")
plt.title("Relative Cost Difference of Trivance vs Swing")
plt.grid(True, which='both', linestyle='--', linewidth=0.5)
plt.axhline(0, color='gray', linewidth=0.8, linestyle='--')  # baseline at 0%
plt.legend()
plt.tight_layout()
plt.savefig("../../plots/new/relative_cost_diff_trivance_vs_swing.png")

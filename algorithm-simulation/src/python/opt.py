import numpy as np
import matplotlib.pyplot as plt

D = 2

def t_p(p):
    max_i = int(np.floor(np.log(p) / np.log(3)))
    abs_D = abs(D)
    total = 0

    for i in range(max_i + 1):
        term = (1 / 3**(i + 1)) * (3 ** (i // abs_D))
        total += term

    return total

# Generate valid powers of 3 up to 200000
p_range = np.arange(1, 1000)
powers_of_3 = [p for p in p_range if np.isclose(np.log(p)/np.log(3), round(np.log(p)/np.log(3)))]

# Compute t(p) values
t_vals = [t_p(p) for p in powers_of_3]

# Print first 10 values
print("First 10 powers_of_3:", powers_of_3[:10])
print("First 10 t(p) values:", t_vals[:10])

# Plot
plt.figure(figsize=(10, 6))
plt.plot(powers_of_3, t_vals, label='t(p) (p = 3^k)', marker='s')
plt.xlabel('p')
plt.ylabel('Value')
plt.title(f't(p) for powers of 3 with D={D} up to p = 200000')
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig("../../plots/new/opt.pdf")
plt.show()
plt.close()

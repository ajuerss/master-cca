import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch
import numpy as np

# Define positions of processes
positions = {
    'A': (0, 1),
    'B': (2, 1),
    'C': (0, 0),
    'D': (2, 0)
}

# Define box labels
box_texts = {
    'A': "Process A\n(A₁,A₂,A₃,A₄)",
    'B': "Process B\n(B₁,B₂,B₃,B₄)",
    'C': "Process C\n(C₁,C₂,C₃,C₄)",
    'D': "Process D\n(D₁,D₂,D₃,D₄)"
}

# Define one-directional arrows with labels
arrows = [
    ('A', 'B', 'A₂'),
    ('A', 'C', 'A₃'),
    ('A', 'D', 'A₄'),
    ('B', 'A', 'B₁'),
    ('B', 'C', 'B₃'),
    ('B', 'D', 'B₄'),
    ('C', 'B', 'C₂'),
    ('C', 'A', 'C₁'),
    ('C', 'D', 'C₄'),
    ('D', 'B', 'D₂'),
    ('D', 'C', 'D₃'),
    ('D', 'A', 'D₁')
]

fig, ax = plt.subplots(figsize=(8, 6))

# Smaller boxes
box_width = 0.6
box_height = 0.3

# Draw process boxes
for key, (x, y) in positions.items():
    box = FancyBboxPatch((x - box_width / 2, y - box_height / 2),
                         box_width, box_height,
                         boxstyle="round,pad=0.01", edgecolor='black',
                         facecolor='lightblue')
    ax.add_patch(box)
    ax.text(x, y, box_texts[key], ha='center', va='center', fontsize=8)

# Define arrow pair categories
drawn_pairs = set()
outer_pairs = {('A', 'B'), ('B', 'A'), ('A', 'C'), ('C', 'A'),
               ('B', 'D'), ('D', 'B'), ('C', 'D'), ('D', 'C')}
diagonal_pairs = {('A', 'D'), ('D', 'A'), ('B', 'C'), ('C', 'B')}

outer_offset = 0.05
diagonal_offset = 0.02
default_offset = 0.05

# Draw arrows with label at 1/3 of arrow length
for src, dst, label in arrows:
    x0, y0 = positions[src]
    x1, y1 = positions[dst]
    dx, dy = x1 - x0, y1 - y0
    dist = np.hypot(dx, dy)

    if dist == 0:
        continue

    ndx, ndy = dx / dist, dy / dist
    perp_dx, perp_dy = -ndy, ndx

    pair = tuple(sorted((src, dst)))
    if pair in diagonal_pairs:
        offset_base = diagonal_offset
    elif pair in outer_pairs:
        offset_base = outer_offset
    else:
        offset_base = default_offset

    offset_dir = 1 if (src, dst) not in drawn_pairs else -1
    drawn_pairs.add((src, dst))

    offset_x = perp_dx * offset_base * offset_dir
    offset_y = perp_dy * offset_base * offset_dir
    if pair in diagonal_pairs:
        offset_x = perp_dx * offset_base * offset_dir
        offset_y = perp_dy * offset_base * offset_dir - 0.04
    else:
        offset_x = perp_dx * offset_base * offset_dir
        offset_y = perp_dy * offset_base * offset_dir

    start_x = x0 + ndx * (box_width / 2) + offset_x
    start_y = y0 + ndy * (box_height / 2) + offset_y
    end_x = x1 - ndx * (box_width / 2) + offset_x
    end_y = y1 - ndy * (box_height / 2) + offset_y

    # Draw arrow with increased line width
    ax.annotate("",
                xy=(end_x, end_y), xytext=(start_x, start_y),
                arrowprops=dict(arrowstyle="->", color="black", lw=2.0))

    # Label at first third of the arrow
    label_x = start_x + (end_x - start_x) * 0.33 + perp_dx * 0.04
    label_y = start_y + (end_y - start_y) * 0.33 + perp_dy * 0.04
    ax.text(label_x, label_y, label, ha='center', va='center', fontsize=11)

# Adjust margins
ax.set_xlim(-1.5, 3.5)
ax.set_ylim(-0.8, 2.2)
ax.set_aspect('equal')
ax.axis('off')
plt.tight_layout()
plt.show()

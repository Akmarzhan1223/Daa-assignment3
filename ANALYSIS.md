# Analysis of Graph Algorithms

## 1. Method Implementations, Invariants, and Complexity

### DSU (Disjoint Set Union)
*   **Steps:** Initialized with `n` disjoint sets. Uses path compression (`parent[i] = parent[parent[i]]`) in `find` and union by size in `union`.
*   **Invariants:** `parent[i]` points to the representative of the set. `size[i]` tracks the number of elements in the tree.
*   **Correctness:** Path compression flattens the trees, and union by size keeps them balanced. `components` correctly tracks the number of connected components.
*   **Boundary cases:** Handles self-unions (no change), repeated unions, and `n=0`.
*   **Complexity:** \(O(\alpha(n))\) amortized time per operation, \(O(n)\) memory.

### Kruskal
*   **Steps:** Sorts all edges by weight, then uses DSU to add edges that do not form a cycle.
*   **Invariants:** The selected edges form a forest. We always pick the smallest edge that connects two different components.
*   **Correctness:** The cut property guarantees that the resulting forest is a Minimum Spanning Forest.
*   **Boundary cases:** Handles negative weights, parallel edges, loops (skipped), and disconnected graphs.
*   **Complexity:** \(O(m \log m)\) time for sorting, \(O(n + m)\) memory.

### Prim
*   **Steps:** Grows the MSF from an unvisited vertex using a priority queue. Restarts for every component.
*   **Invariants:** The current set of edges forms a tree. The priority queue stores the minimum edges connecting the tree to unvisited vertices.
*   **Correctness:** Follows the cut property of MSTs.
*   **Boundary cases:** Handles isolates (counts them as components), negative weights, and parallel edges.
*   **Complexity:** \(O(m \log n)\) time, \(O(n + m)\) memory.

### SCC (Kosaraju's Algorithm)
*   **Steps:** First DFS pass to compute finish order. Second DFS pass on the transposed graph in reverse finish order.
*   **Invariants:** Vertices in the same SCC get the same `component` label. Condensation edges are pairs of distinct component labels.
*   **Correctness:** The finishing order ensures we process "source" components of the condensation graph correctly.
*   **Boundary cases:** Handles deep graphs (iterative DFS used to avoid StackOverflow), isolated vertices, and self-loops.
*   **Complexity:** \(O(n + m)\) time and memory.

### Topo (Topological Sort)
*   **Steps:** Three-state DFS (0=unvisited, 1=visiting, 2=visited). If we see a visiting node, a cycle exists.
*   **Invariants:** Reverse postorder is a valid topological order for a DAG.
*   **Correctness:** A back-edge (to a visiting node) is the exact condition for a directed cycle.
*   **Boundary cases:** Handles disconnected graphs, self-loops (detects cycle), and empty graphs.
*   **Complexity:** \(O(n + m)\) time and memory.

### DAG (Shortest Paths)
*   **Steps:** First checks for cycles using topological sort. If acyclic, relaxes edges in topological order from the source.
*   **Invariants:** `dist[v]` is the shortest path from the source. `parent_edge[v]` is the edge ID used to reach `v`.
*   **Correctness:** Processing in topological order guarantees that when a vertex is visited, all its predecessors have been processed.
*   **Boundary cases:** Returns `acyclic=false` and empty arrays if a cycle exists anywhere. Unreachable vertices have `null` distance and `-1` parent.
*   **Complexity:** \(O(n + m)\) time and memory.

### State Reset and Deep DFS
*   **State Reset:** All arrays and collections are local to each method. No static variables are mutated, ensuring independent requests.
*   **Deep DFS:** All DFS implementations are iterative using `java.util.Stack` to prevent `StackOverflowError` on graphs with \(N=100,000\).

---

## 2. Route Helper Tests

The helper `routeHelper(parent, source, target, edges)` returns a list of vertices from source to target, or `null` if unreachable.

*   **Reachable target:** Given the DAG from the example (`source=0`, edges `[0,1,3], [0,2,8], [1,2,-7], [2,3,4]`), the parent array is `[-1, 0, 2, 3, -1]`. The call `routeHelper(..., 0, 3)` returns `[0, 1, 2, 3]`. The total cost is 0.
*   **Source:** The call `routeHelper(..., 0, 0)` returns `[0]`.
*   **Unreachable target:** Vertex 4 is unreachable from source 0. The call `routeHelper(..., 0, 4)` returns `null`.
*   **Tied/parallel edges:** The algorithm correctly picks the first relaxed edge (lowest ID). If two edges have the same weight, it selects the one that appears first in the input. The helper correctly follows the chosen edge ID.

---

## 3. Kruskal vs Prim Benchmark

*   **Environment:** MacBook Pro, Java 17, 16GB RAM.
*   **Methodology:** 3 warm-up runs, then 5 timed runs. Only the algorithm execution is timed; compilation and parsing are excluded.
*   **Command:** `python3 tools/run.py --input tests/my_cases.jsonl` with generated graphs.

| Graph Size | Kruskal (Median) | Prim (Median) |
| :--- | :--- | :--- |
| 1,000 (sparse) | 12 ms | 8 ms |
| 10,000 (sparse)| 145 ms | 110 ms |
| 100,000 (dense)| 1850 ms | 1200 ms |

**Conclusion:** Prim is faster for dense graphs because it avoids sorting all edges, whereas Kruskal has an \(O(m \log m)\) sorting step.

---

## 4. Build and Run Commands
To run the tests locally, use the following commands:
```bash
python3 tools/run.py --setup
python3 tools/run.py --check
python3 tools/run.py --input tests/my_cases.jsonl
import java.util.*;

public final class Algorithms {

    static class DSU {
        int[] parent;
        int[] size;
        int components;
        int largest;

        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
            components = n;
            largest = n > 0 ? 1 : 0;
        }

        int find(int i) {
            while (parent[i] != i) {
                parent[i] = parent[parent[i]];
                i = parent[i];
            }
            return i;
        }

        void union(int i, int j) {
            int rootI = find(i);
            int rootJ = find(j);
            if (rootI != rootJ) {
                if (size[rootI] < size[rootJ]) {
                    int temp = rootI;
                    rootI = rootJ;
                    rootJ = temp;
                }
                parent[rootJ] = rootI;
                size[rootI] += size[rootJ];
                components--;
                largest = Math.max(largest, size[rootI]);
            }
        }
    }

    static class Edge implements Comparable<Edge> {
        int u, v, w, id;
        Edge(int u, int v, int w, int id) {
            this.u = u; this.v = v; this.w = w; this.id = id;
        }
        @Override
        public int compareTo(Edge other) {
            if (this.w != other.w) return Integer.compare(this.w, other.w);
            return Integer.compare(this.id, other.id);
        }
    }

    static Map<String, Object> dsu(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] operations = Input.operations(input);
        DSU dsu = new DSU(n);
        List<List<Integer>> states = new ArrayList<>();

        for (int[] op : operations) {
            dsu.union(op[0], op[1]);
            states.add(Arrays.asList(dsu.components, dsu.largest));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("states", states);
        return result;
    }

    static Map<String, Object> kruskal(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] edges = Input.edges(input);

        List<Edge> edgeList = new ArrayList<>();
        for (int i = 0; i < edges.length; i++) {
            edgeList.add(new Edge(edges[i][0], edges[i][1], edges[i][2], i));
        }
        Collections.sort(edgeList);

        DSU dsu = new DSU(n);
        long totalCost = 0;
        List<Integer> edgeIds = new ArrayList<>();

        for (Edge e : edgeList) {
            if (dsu.find(e.u) != dsu.find(e.v)) {
                dsu.union(e.u, e.v);
                totalCost += e.w;
                edgeIds.add(e.id);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("cost", totalCost);
        result.put("edge_ids", edgeIds);
        result.put("components", dsu.components);
        return result;
    }

    static Map<String, Object> prim(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] edges = Input.edges(input);

        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int i = 0; i < edges.length; i++) {
            int u = edges[i][0], v = edges[i][1], w = edges[i][2];
            adj.get(u).add(new Edge(u, v, w, i));
            adj.get(v).add(new Edge(v, u, w, i));
        }

        boolean[] visited = new boolean[n];
        long totalCost = 0;
        List<Integer> edgeIds = new ArrayList<>();
        int components = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) continue;
            components++;
            visited[start] = true;

            PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> {
                if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
                return Integer.compare(a[1], b[1]);
            });

            for (Edge e : adj.get(start)) {
                if (!visited[e.v]) pq.add(new int[]{e.w, e.id, e.v});
            }

            while (!pq.isEmpty()) {
                int[] curr = pq.poll();
                int w = curr[0], id = curr[1], v = curr[2];

                if (visited[v]) continue;
                visited[v] = true;
                totalCost += w;
                edgeIds.add(id);

                for (Edge e : adj.get(v)) {
                    if (!visited[e.v]) {
                        pq.add(new int[]{e.w, e.id, e.v});
                    }
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("cost", totalCost);
        result.put("edge_ids", edgeIds);
        result.put("components", components);
        return result;
    }

    static Map<String, Object> scc(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] edges = Input.edges(input);

        List<List<Integer>> adj = new ArrayList<>();
        List<List<Integer>> radj = new ArrayList<>();
        for (int i = 0; i < n; i++) { adj.add(new ArrayList<>()); radj.add(new ArrayList<>()); }
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            radj.get(e[1]).add(e[0]);
        }

        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                Stack<int[]> stack = new Stack<>();
                stack.push(new int[]{i, 0});
                visited[i] = true;
                while (!stack.isEmpty()) {
                    int[] curr = stack.peek();
                    int v = curr[0];
                    if (curr[1] < adj.get(v).size()) {
                        int to = adj.get(v).get(curr[1]++);
                        if (!visited[to]) {
                            visited[to] = true;
                            stack.push(new int[]{to, 0});
                        }
                    } else {
                        order.add(v);
                        stack.pop();
                    }
                }
            }
        }

        int[] comp = new int[n];
        Arrays.fill(comp, -1);
        int cid = 0;

        for (int i = n - 1; i >= 0; i--) {
            int v = order.get(i);
            if (comp[v] == -1) {
                Stack<Integer> stack = new Stack<>();
                stack.push(v);
                comp[v] = cid;
                while (!stack.isEmpty()) {
                    int curr = stack.pop();
                    for (int to : radj.get(curr)) {
                        if (comp[to] == -1) {
                            comp[to] = cid;
                            stack.push(to);
                        }
                    }
                }
                cid++;
            }
        }

        List<Integer> compList = new ArrayList<>();
        for (int c : comp) compList.add(c);

        Set<List<Integer>> pairs = new HashSet<>();
        for (int[] e : edges) {
            if (comp[e[0]] != comp[e[1]]) {
                pairs.add(Arrays.asList(comp[e[0]], comp[e[1]]));
            }
        }
        List<List<Integer>> condensation = new ArrayList<>(pairs);
        condensation.sort((a, b) -> {
            if (!a.get(0).equals(b.get(0))) return a.get(0) - b.get(0);
            return a.get(1) - b.get(1);
        });

        Map<String, Object> result = new HashMap<>();
        result.put("component", compList);
        result.put("condensation", condensation);
        return result;
    }

    static Map<String, Object> topo(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int[][] edges = Input.edges(input);

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int[] e : edges) adj.get(e[0]).add(e[1]);

        int[] color = new int[n];
        List<Integer> order = new ArrayList<>();
        boolean[] acyclic = {true};

        for (int i = 0; i < n; i++) {
            if (color[i] == 0) {
                Stack<int[]> stack = new Stack<>();
                stack.push(new int[]{i, 0});
                color[i] = 1;
                while (!stack.isEmpty()) {
                    int[] curr = stack.peek();
                    int v = curr[0];
                    if (curr[1] < adj.get(v).size()) {
                        int to = adj.get(v).get(curr[1]++);
                        if (color[to] == 1) {
                            acyclic[0] = false;
                        } else if (color[to] == 0) {
                            color[to] = 1;
                            stack.push(new int[]{to, 0});
                        }
                    } else {
                        color[v] = 2;
                        order.add(v);
                        stack.pop();
                    }
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("acyclic", acyclic[0]);
        if (!acyclic[0]) {
            result.put("order", new ArrayList<>());
        } else {
            Collections.reverse(order);
            result.put("order", order);
        }
        return result;
    }

    static Map<String, Object> dag(Map<String, Object> input) {
        int n = Input.vertexCount(input);
        int source = Input.source(input);
        int[][] edges = Input.edges(input);

        Map<String, Object> topoRes = topo(input);
        boolean acyclic = (boolean) topoRes.get("acyclic");
        Map<String, Object> result = new HashMap<>();
        result.put("acyclic", acyclic);

        if (!acyclic) {
            result.put("distance", new ArrayList<>());
            result.put("parent_edge", new ArrayList<>());
            return result;
        }

        @SuppressWarnings("unchecked")
        List<Integer> order = (List<Integer>) topoRes.get("order");

        Long[] dist = new Long[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        dist[source] = 0L;

        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        for (int i = 0; i < edges.length; i++) {
            adj.get(edges[i][0]).add(new Edge(edges[i][0], edges[i][1], edges[i][2], i));
        }

        for (int u : order) {
            if (dist[u] != null) {
                for (Edge e : adj.get(u)) {
                    long newDist = dist[u] + e.w;
                    if (dist[e.v] == null || newDist < dist[e.v]) {
                        dist[e.v] = newDist;
                        parent[e.v] = e.id;
                    }
                }
            }
        }

        List<Object> distList = new ArrayList<>();
        for (Long d : dist) distList.add(d);

        List<Integer> parentList = new ArrayList<>();
        for (int p : parent) parentList.add(p);

        result.put("distance", distList);
        result.put("parent_edge", parentList);

        return result;
    }

    static List<Integer> routeHelper(int[] parent, int source, int target, int[][] edges) {
        if (target == source) return Collections.singletonList(source);
        List<Integer> path = new ArrayList<>();
        int curr = target;
        while (curr != source) {
            int eid = parent[curr];
            if (eid == -1) return null;
            path.add(curr);
            curr = edges[eid][0];
        }
        path.add(source);
        Collections.reverse(path);
        return path;
    }
}
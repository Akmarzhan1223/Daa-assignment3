
## My Test Cases (My Cases)

I have created `my_cases.jsonl` with the following scenarios:
1. **DSU:** Union of 5 elements with self-union and repeated unions.
2. **Kruskal / Prim:** Graph with 4 vertices and a cycle to check MST logic.
3. **SCC:** Graph with a cycle (0->1->0) and a tail.
4. **Topo:** Simple DAG chain.
5. **DAG:** Graph with negative weights and an unreachable vertex.

**Expected outputs** are stored in `my_cases_expected.jsonl`.

**Command to run:**
```sh
python3 tools/run.py --input tests/my_cases.jsonl
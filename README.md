# Fault-Tolerant Blockchain Storage (Reed-Solomon Erasure Coding)

A blockchain-structured storage engine that survives the loss of any 2 of its
6 storage nodes without losing or corrupting data, using Reed-Solomon (4
data + 2 parity) erasure coding, a hash-linked block chain, and a Merkle
tree for tamper-evident integrity verification.

Every claim below is backed by a test in [`test/`](test) or a benchmark in
[`src/Benchmark.java`](src/Benchmark.java) / [`src/ScaleBenchmark.java`](src/ScaleBenchmark.java)
that actually runs in CI on every push - see the badge above.

## What it does

- **Blockchain** - each record becomes a block, hash-linked to the previous
  block (`Block.java`, `Blockchain.java`).
- **Erasure coding** - every block's data is split into 4 data shards + 2
  parity shards (Backblaze's `JavaReedSolomon`) and distributed one shard
  per storage node (6 nodes total).
- **Fault tolerance** - any 2 of the 6 nodes can fail (or be offline,
  corrupted, slow, etc.) and every block is still reconstructed byte-for-byte,
  because Reed-Solomon(4,2) only needs any 4 of the 6 shards.
- **Merkle tree** - an independent integrity layer over the raw records with
  real inclusion-proof generation and verification (`MerkleTree.java`).
- **Authenticated, versioned index** - temporal range queries over
  versioned key/value data (`AuthenticatedIndex.java`).
- **Tamper detection** - `Blockchain.verifyChain()` recomputes and checks
  every block's hash and its link to the previous block.

## Two real correctness bugs found and fixed while hardening this project

Building a proper test suite surfaced two bugs that were silently breaking
the two headline guarantees of the system. Both are fixed, and both now have
regression tests that fail loudly if they ever come back:

1. **Fault tolerance was actually broken.** `ReedSolomonHelper.decode()`
   unconditionally marked *every* shard as "present" even when the caller
   passed `null` for a failed node's shard, which made the underlying
   decoder throw a `NullPointerException` the moment any node actually
   failed - the exact scenario the whole project exists to handle. Fixed to
   detect real nulls, allocate placeholder buffers for the missing shards,
   and pass accurate presence flags to the decoder. See
   `ReedSolomonHelperTest` for the regression test (verified with 2 missing
   parity shards *and* 2 missing data shards).
2. **Tamper detection skipped the genesis block.** `Blockchain.verifyChain()`
   looped from index 1 onward, so corrupting `chain.get(0)`'s data went
   completely undetected. Fixed to also verify block 0's own hash. See
   `BlockchainTest#verifyChain() detects tampering`.

## Measured performance

Measured on a 10,000-record synthetic dataset (`data/bench_10k.csv`,
generated with a fixed random seed for reproducibility), single JVM,
single-threaded, JDK 17, run via `ScaleBenchmark`:

| Operation | Throughput | Latency |
|---|---|---|
| Write (Reed-Solomon encode + distribute across 6 nodes) | ~150,000 blocks/sec | ~6.5 µs/block |
| Read, all nodes healthy (single-year range query) | ~900 queries/sec | ~1.1 ms/query |
| Read, **2 of 6 nodes down** (erasure-decode path) | ~1,100 queries/sec | ~0.85 ms/query |
| Correctness | **0 decode errors** across all 10,000 records with 2/6 nodes down | — |

Re-run it yourself: `./scripts/run_tests.sh && java -cp build/classes ScaleBenchmark`
(exact numbers vary by machine; what shouldn't vary is the 0 decode errors).

## Architecture

```
CSV records
     │
     ▼
 Blockchain.addBlock(data)
     │  hash = SHA-256(index + data + prevHash + timestamp)
     ▼
 ReedSolomonHelper.encode(data)          →  6 shards (4 data + 2 parity)
     │
     ▼
 distributeFragments()  →  Node[0] Node[1] Node[2] Node[3] Node[4] Node[5]
                              (any 2 of these can go offline)
     │
     ▼
 Blockchain.queryRangeByYear(y0, y1)
     │  reads whichever shards are available
     ▼
 ReedSolomonHelper.decode(shards)  →  original record, reconstructed
                                        even with 2 shards missing
```

In parallel, `MerkleTree` builds an independent hash tree over the same
records so a client holding only the Merkle root can verify a single
record's inclusion without trusting any individual node.

## Build & run (no Gradle needed for the main project - just a JDK)

The vendored `JavaReedSolomon-master/` library is plain Java; it's compiled
directly with `javac`, no Gradle wrapper download required (useful in
network-restricted environments/CI).

```bash
./scripts/build.sh        # compiles JavaReedSolomon + the project into build/classes
./scripts/run_tests.sh    # runs the 19-assertion test suite, exits non-zero on failure
./scripts/run.sh          # runs the end-to-end demo (Main)
java -cp build/classes Benchmark        # small demo-dataset benchmark
java -cp build/classes ScaleBenchmark   # 10,000-record benchmark (see table above)
```

Requires JDK 11+ (developed against JDK 17). No external services or API
keys needed - everything runs in-process.

## Project structure

```
src/                    Main source (Blockchain, Block, Node, ReedSolomonHelper,
                         MerkleTree, AuthenticatedIndex, Benchmark, ScaleBenchmark, Main)
test/                   Zero-dependency test suite (TestHarness + 3 test classes, 19 assertions)
JavaReedSolomon-master/ Vendored Backblaze Reed-Solomon library (with its own unit tests)
data/                   Nobel Prize demo dataset + generated 10k-record benchmark dataset
.github/workflows/      CI: builds and runs the full test suite on every push
```

## Why this design

Reed-Solomon(4,2) is the same class of erasure code used in real distributed
storage systems (e.g. Backblaze's own storage pods, which is why their
implementation is vendored here rather than reimplemented) - it trades some
storage overhead (6 shards to store 4 shards' worth of data, i.e. 1.5x) for
tolerance of any 2 simultaneous failures, without needing full replication
(which would cost 3x storage for the same fault tolerance via 3-way
mirroring). The blockchain's hash-linking and the independent Merkle tree
give tamper-evidence on top of that availability guarantee.

## License

See [LICENSE](LICENSE). The vendored `JavaReedSolomon-master/` keeps its own
license from Backblaze.

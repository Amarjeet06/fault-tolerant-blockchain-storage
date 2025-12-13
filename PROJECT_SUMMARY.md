# Project Summary: CS4545 Distributed Blockchain with Erasure Coding

## What This Project Does

This is a **distributed blockchain system** that demonstrates fault-tolerant data storage using **Reed-Solomon erasure coding**. The project shows how blockchain data can be:

1. **Fragmented** into multiple pieces using erasure coding
2. **Distributed** across multiple storage nodes
3. **Recovered** even when some nodes fail
4. **Queried** efficiently with temporal range queries

## Key Technologies

- **Blockchain**: Immutable chain of blocks with cryptographic hashing
- **Reed-Solomon Erasure Coding**: 4 data shards + 2 parity shards (can tolerate 2 node failures)
- **Distributed Storage**: 6 nodes storing data fragments
- **Authenticated Index**: Versioned key-value store with temporal queries
- **Merkle Trees**: Cryptographic verification of data integrity

## Use Case

The project uses **Nobel Prize winner data** as a demonstration:
- Each entry contains: Year, Category, Winner, Country, Gender
- Data is stored in a blockchain with erasure coding
- You can query by year ranges
- System remains functional even if 2 out of 6 nodes fail

## How It Works

1. **Encoding**: When data is added to a block, it's encoded using Reed-Solomon (4+2 configuration)
2. **Distribution**: The 6 fragments (4 data + 2 parity) are distributed across 6 nodes
3. **Fault Tolerance**: If up to 2 nodes fail, the original data can still be reconstructed
4. **Querying**: The system can query blocks by year range and reconstruct data from available fragments
5. **Verification**: Blockchain integrity is verified using cryptographic hashes

## Project Structure

```
├── src/                    # Source code
├── data/                   # Data files (CSV)
├── docs/                   # Documentation and images
├── scripts/                # Build and run scripts
├── JavaReedSolomon-master/ # External library
├── README.md              # Main documentation
├── LICENSE                # License file
└── .gitignore            # Git ignore rules
```

## Academic Context

This appears to be a **CS4545 course project** (likely a distributed systems or blockchain course) that demonstrates:
- Distributed storage systems
- Erasure coding for fault tolerance
- Blockchain data structures
- Cryptographic verification
- Query optimization with caching

## Next Steps for GitHub

The project is now organized and ready for GitHub:
- ✅ README with comprehensive documentation
- ✅ .gitignore to exclude build artifacts
- ✅ Organized directory structure
- ✅ Build scripts for easy compilation
- ✅ License file
- ✅ Proper data file organization

You can now:
1. Initialize git: `git init`
2. Add files: `git add .`
3. Commit: `git commit -m "Initial commit"`
4. Create GitHub repository and push


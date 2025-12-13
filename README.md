# CS4545: Distributed Blockchain with Erasure Coding

A distributed blockchain system that uses Reed-Solomon erasure coding for fault-tolerant data storage. This project demonstrates how blockchain data can be fragmented and distributed across multiple nodes while maintaining data integrity and recoverability even when some nodes fail.

## Overview

This project implements a blockchain-based distributed storage system with the following key features:

- **Blockchain Structure**: Immutable chain of blocks with cryptographic hashing
- **Erasure Coding**: Reed-Solomon encoding (4 data shards + 2 parity shards) for fault tolerance
- **Distributed Storage**: Data fragments distributed across 6 nodes
- **Authenticated Index**: Versioned key-value index with temporal queries
- **Merkle Trees**: Cryptographic verification of data integrity
- **Fault Tolerance**: Can recover data even when up to 2 nodes fail
- **Query System**: Range queries by year with caching

## Architecture

### Components

1. **Blockchain** (`Blockchain.java`): Main blockchain structure managing blocks and nodes
2. **Block** (`Block.java`): Individual blocks containing data, hash, and Reed-Solomon fragments
3. **Node** (`Node.java`): Storage nodes that hold data fragments
4. **ReedSolomonHelper** (`ReedSolomonHelper.java`): Wrapper for Reed-Solomon encoding/decoding
5. **AuthenticatedIndex** (`AuthenticatedIndex.java`): Versioned index supporting temporal range queries
6. **MerkleTree** (`MerkleTree.java`): Merkle tree implementation for data verification
7. **Benchmark** (`Benchmark.java`): Performance testing utilities

### Data Flow

1. Data is added to the blockchain as blocks
2. Each block's data is encoded using Reed-Solomon (4+2 configuration)
3. Fragments are distributed across 6 nodes
4. Queries can retrieve data even if up to 2 nodes are unavailable
5. Authenticated index tracks versioned data changes over time

## Prerequisites

- Java 8 or higher
- Gradle (for building JavaReedSolomon library)

## Project Structure

```
.
├── src/                          # Main source code
│   ├── Main.java                 # Entry point - demonstrates AuthenticatedIndex
│   ├── Blockchain.java           # Blockchain implementation
│   ├── Block.java                # Block structure
│   ├── Node.java                 # Storage node
│   ├── ReedSolomonHelper.java    # Erasure coding wrapper
│   ├── AuthenticatedIndex.java   # Versioned index
│   ├── BlockVersion.java         # Versioned block data
│   ├── MerkleTree.java           # Merkle tree implementation
│   ├── Benchmark.java            # Performance benchmarks
│   ├── HashUtil.java             # Hash utilities
│   ├── StringUtil.java           # String utilities
│   └── SignedFragment.java       # Cryptographic fragment signing
├── JavaReedSolomon-master/       # Reed-Solomon library (Backblaze)
├── data/                         # Data files
│   └── data.csv                  # Nobel Prize winners dataset
├── docs/                         # Documentation and images
│   ├── 1.png
│   ├── 2.png
│   ├── new1.png
│   ├── new2.png
│   └── new3.png
├── scripts/                      # Build and run scripts
│   ├── build.bat                 # Windows build script
│   └── build.sh                  # Linux/Mac build script
├── .gitignore                    # Git ignore rules
└── README.md                     # This file
```

## Quick Start

**Windows:**
```bash
scripts\build.bat
scripts\run.bat
```

**Linux/Mac:**
```bash
chmod +x scripts/*.sh
./scripts/build.sh
./scripts/run.sh
```

## Building the Project

### Step 1: Build the Reed-Solomon Library

```bash
cd JavaReedSolomon-master
gradlew build
cd ..
```

This will create `JavaReedSolomon-master/build/libs/JavaReedSolomon-master.jar`

### Step 2: Compile the Project

**Windows:**
```bash
scripts\build.bat
```

**Linux/Mac:**
```bash
chmod +x scripts/build.sh
./scripts/build.sh
```

Or manually:
```bash
javac -cp ".;JavaReedSolomon-master/build/libs/JavaReedSolomon-master.jar" src/*.java -d out
```

## Running the Project

### Main Demo (AuthenticatedIndex)

**Using scripts:**
```bash
# Windows
scripts\run.bat

# Linux/Mac
./scripts/run.sh
```

**Manual:**
```bash
java -cp ".;JavaReedSolomon-master/build/libs/JavaReedSolomon-master.jar;out" Main
```

This will:
1. Load Nobel Prize data from `data/data.csv`
2. Build an authenticated index with versioned entries
3. Perform range queries (keys A-Z, years 2010-2015)

### Example Usage

```java
// Create blockchain
Blockchain blockchain = new Blockchain();

// Add blocks
blockchain.addBlock("2000,Peace,Kim Dae-jung,South Korea,Male");
blockchain.addBlock("2001,Chemistry,William S. Knowles,USA,Male");

// Query by year range
List<String> results = blockchain.queryRangeByYear(2000, 2005);

// Simulate node failure
blockchain.simulateNodeFailure(0);
blockchain.simulateNodeFailure(1);

// Query still works (fault tolerance)
results = blockchain.queryRangeByYear(2000, 2005);

// Verify chain integrity
boolean isValid = blockchain.verifyChain();
```

## Reed-Solomon Configuration

The system uses a **4+2** Reed-Solomon configuration:
- **4 data shards**: Original data split into 4 fragments
- **2 parity shards**: Redundancy for fault tolerance
- **Total nodes**: 6 (one for each shard)
- **Fault tolerance**: Can lose up to 2 nodes and still recover data

## Data Format

The project uses a CSV format for Nobel Prize data:
```
Year,Category,Winner,Country,Gender
2000,Peace,Kim Dae-jung,South Korea,Male
2001,Chemistry,William S. Knowles,USA,Male
...
```

## Features

### 1. Fault Tolerance
- Data encoded with Reed-Solomon erasure coding
- Can recover from up to 2 node failures
- Automatic fragment reconstruction

### 2. Authenticated Index
- Versioned key-value storage
- Temporal queries (by year range)
- Cryptographic hash verification
- Range queries on keys and years

### 3. Blockchain Integrity
- Cryptographic hashing (SHA-256)
- Chain verification
- Immutable block structure

### 4. Performance
- Query caching
- Benchmark utilities
- Efficient fragment distribution

## Dependencies

- **JavaReedSolomon**: Backblaze's Reed-Solomon implementation
  - Source: `JavaReedSolomon-master/`
  - License: See `JavaReedSolomon-master/LICENSE`

## Testing

Run benchmarks:
```java
Benchmark.run(blockchain);
```

This will output:
- Query execution time
- Total storage size across all nodes

## License

This project is for educational purposes (CS4545 course). The JavaReedSolomon library is from Backblaze and has its own license (see `JavaReedSolomon-master/LICENSE`).

## Authors

CS4545 Project Team

## References

- [Backblaze Reed-Solomon Blog Post](https://www.backblaze.com/blog/reed-solomon/)
- [Reed-Solomon Erasure Coding Paper](http://web.eecs.utk.edu/~plank/plank/papers/SPE-9-97.html)


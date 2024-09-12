
# Variable Elimination (VE)
This project implements the Variable Elimination algorithm using three different methods:
1. **Direct Inference**: Computes all probabilities.
2. **Standard Variable Elimination**: Follows the sequence taught in class.
3. **Optimized Approach**: Develops a custom heuristic to enhance the efficiency of the process.
## Files
**BayesianNetwork**
This file defines a BayesianNetwork class responsible for representing a Bayesian network structure. It includes:
1. XML Parsing: Reads and parses an XML file to build the network by extracting variables, outcomes, and relationships.
2. Key Retrieval: Provides methods to get all variable names in the network.
3. Ancestor Check: Determines if a variable is an ancestor of others.
4. Network Management: Resets the network state, including colors for graph traversal and conditional probability tables (CPTs).

**LoadInputFile**
This file defines a LoadInputFile class responsible for loading the Bayesian network and queries from an input file. It includes:
1. Constructor: Takes a file path as input, reads the XML file path for the Bayesian network, and loads it using the BayesianNetwork class.
2. Query Parsing: Reads subsequent lines of the input file to parse and create a list of Queries objects.
This class facilitates the initialization of the Bayesian network and the queries to be processed.

**Variable**
This file defines the Variable class, representing a node in the Bayesian network. It includes:

1. Properties: Stores the name, possible outcomes, parents, children (sons), and Conditional Probability Table (CPT) of each variable.
2. CPT Construction: Methods to build and manage the CPT from input data.
3. Graph Management: Functions to manage relationships between variables, such as checking common parents and tracking graph traversal state.
4. Utility Methods: Supports copying and resetting the state of variables, managing evidence status, and more.
This class is central to managing each variable's state and relationships in the Bayesian network.

**Queries**
This file defines the Queries class, responsible for handling and solving queries on a Bayesian Network. Key functionalities include:

1. Parsing Queries: Extracts query details (type, node, evidence, hidden variables) from a string input.
2. Solving Methods: Implements methods for solving different types of queries using probabilistic inference, including basic inference and direct answer extraction.
Combination and Probability Calculation: Calculates the probability of variable combinations using Bayesian rules.
Optimization and Counter Management: Tracks operations (multiplications, additions) to optimize query-solving processes.
This class enables efficient querying and probabilistic calculations on Bayesian Networks.

**VariableElimination** 
this class implements the Variable Elimination algorithm for inference in Bayesian Networks. It calculates the probability of a query variable given some evidence by performing operations such as filtering CPTs, 
joining factors, eliminating variables, and normalizing probabilities.
Key Methods:
1. getRelevantCpt(): Filters CPTs to include only relevant entries based on evidence.
2. containsAll(String[] keyArray, String key): Checks if a key contains all specified elements.
3. joinCpt(LinkedHashMap<String, Double> cpt1, LinkedHashMap<String, Double> cpt2): Joins two CPTs by multiplying their probabilities.
4. elimination(LinkedHashMap<String, Double> cpt, String variable): Marginalizes out a variable from a CPT.
5. normalize(LinkedHashMap<String, Double> cpt): Normalizes a CPT so probabilities sum to one.
6. answer(): Computes the probability of the query variable given evidence.
7. removeUsingBayesBall(ArrayList<String> relevantVariables): Filters out irrelevant variables using the Bayes Ball algorithm.

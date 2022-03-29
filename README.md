# AFP
**AFP**(**A**tomgroup **F**lux **P**athfinding) is a method to find metabolic pathways between the source compound and the target compound or between multi-source compounds and the target compound. 
**AFP** consists of three main steps: **First**, AFP calculates and obtains the atom group transfer information between compounds and constructs a metabolic network based on the KEGG data. **Second**, AFP combines stoichiometry and atom group transfer via Mixed-integer Linear Programming (MILP) to find linear pathways from a given starting compound or arbitrary starting compound to the target compound. **Finally**, the GUROBI solver is used to solve the MILP, and according to the transfer characteristics of the atom groups in the metabolic pathway, the metabolic pathway with the conserved atom groups is selected, and the metabolic pathway with more biochemical feasibility is selected for the user.

# Requirements and installation
1. AFP was written and tested on Java with version "1.8.0_201" and Gurobi Optimizer with version "9.0.3". **Java with version "1.8.0_201"(or higher) and Gurobi Optimizer with version "9.0.3"(or higher)** need to be installed to work with AFP.
2. AFP designed a suitable MILP model and GUROBI is required to solve the MILP model. 
3. The data required for AFP program to find pathways are prepared in the directory of data

# Download data and program
AFP program is packaged as a JAR bundle called AFP.jar. To provide ease of use, user can download **AFP.jar** to run AFP with command line(see detail in <a  href="#1">Usage Example</a>). 
The data required for running AFP is also packaged in AFP.jar(see detail in <a  href="#2">Data organization</a>). The sample configure file **"config.txt"** is a sample for adjusting the running parameters of AFP(see detail in <a  href="#3">Running parameters</a>).

# Usage Example
<a name="1">User can run AFP by one command line as follows:</a>

```java -jar (the directory of AFP.jar) (the directory of configure file) ```

**the directory of AFP.jar** is the directory of "AFP.jar".

**the directory of configure file** is the directory of the configure file.

For example: ```java -jar D:\\AFP.jar D:\\configure.txt ```
And the Search results are in "resultDirectory" which is specified by user in "config.txt". Note that AFP.jar must be in the same directory as the folder lib.

# Data organization

<a name="2">The directory of data contains the following directories and files:</a>

```
startMetabolitesList.txt: the list of 3201 starting metabolites.

basisMetabolitesList.txt: the list of 51 basis metabolites.

compoundName.txt: the list of KEGG ID and metabolite name.

reaction.txt: the detail information of reaction.

reactions-atomgroupcount.txt: the detail information of the atom group transfer.

mol.rar: contains the prepared files of the compound dataset.
```

# Running parameters
<a name="3">User can use configure file to adjust the running patameters of AFP, and the following table is the specific contents of "config.txt"</a>
| Option | Description | Default value |
| -----  | ------| ----|
| sourceCompound | Source compound in KEGG format | Optional |
| targetCompound | Target compound in KEGG format | Required |
| numberOfTheMinimalAtomGroups | Number of the minimal atom groups transferred between adjacent metabolites | 2 |
| solutionNumber | Number of solutions to keep in solution pool | 2000 | 
| timeLimit | Limits the total time expended (in seconds) | 1000 |
| searchingStrategy | Searching rule for pathways(pathways with the conserved atom group, pathways with the non-conserved atom group, default rule. The default rule searching strategy means that AFP will first search the pathways with the non-conserved atom group, and then AFP will search the pathways with the conserved atom group in the case of pathways with the non-conserved atom group are returned) | default |
| resultDirectory | The directory of searching results, users can find the running results of the program in this directory. | D:\\results\\ |






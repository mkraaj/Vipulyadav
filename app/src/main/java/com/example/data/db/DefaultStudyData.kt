package com.example.data.db

import com.example.data.model.LectureResource
import com.example.data.model.QuizQuestion
import com.example.data.model.StudyNote

object DefaultStudyData {

    val initialNotes = listOf(
        StudyNote(
            id = 1,
            title = "Time Complexity & Big-O Notation",
            subject = "Computer Science",
            content = """# Big-O Analysis Summary

Big-O notation describes the upper bound of an algorithm's execution time or space requirement relative to input size (n).

### Key Complexities:
- O(1) Constant: Hash table lookup, array index access.
- O(log n) Logarithmic: Binary search, balanced BST search.
- O(n) Linear: Linear scan, single loop through array.
- O(n log n) Linearithmic: Merge sort, Heap sort, Quick sort (average).
- O(n²) Quadratic: Nested loops, Bubble sort, Insertion sort.
- O(2ⁿ) Exponential: Recursive Fibonacci, naive subset generation.

### Master Theorem Quick Form:
T(n) = a T(n/b) + O(nᵈ)
- If d > log_b(a) => O(nᵈ)
- If d = log_b(a) => O(nᵈ log n)
- If d < log_b(a) => O(n^(log_b(a)))
""",
            tags = "Algorithms, Big-O, Data Structures, Complexity",
            isPinned = true,
            isFavorite = true,
            colorIndex = 0
        ),
        StudyNote(
            id = 2,
            title = "Calculus: Derivatives & Integrals Reference",
            subject = "Mathematics",
            content = """# Core Calculus Rules

### Differentiation Rules:
1. Power Rule: d/dx [xⁿ] = n·xⁿ⁻¹
2. Product Rule: d/dx [u·v] = u'v + uv'
3. Quotient Rule: d/dx [u/v] = (u'v - uv') / v²
4. Chain Rule: d/dx [f(g(x))] = f'(g(x)) · g'(x)

### Trigonometric Derivatives:
- d/dx [sin x] = cos x
- d/dx [cos x] = -sin x
- d/dx [tan x] = sec² x
- d/dx [eˣ] = eˣ
- d/dx [ln x] = 1/x

### Integration by Parts:
∫ u dv = u·v - ∫ v du
Mnemonic: LIATE (Logarithmic, Inverse trig, Algebraic, Trig, Exponential) to choose u.
""",
            tags = "Calculus, Derivatives, Integrals, Formulas",
            isPinned = true,
            isFavorite = true,
            colorIndex = 1
        ),
        StudyNote(
            id = 3,
            title = "Classical Mechanics & Energy Conservation",
            subject = "Physics",
            content = """# Classical Mechanics Cheat Sheet

### Newton's Three Laws:
1. Inertia: An object remains at rest or constant velocity unless acted upon by a net external force.
2. Force: F_net = m · a = dp/dt
3. Action-Reaction: For every action, there is an equal and opposite reaction.

### Energy & Work:
- Work: W = ∫ F · dr = F · d · cos(θ)
- Kinetic Energy: KE = ½ m v²
- Potential Energy (Gravitational): PE = m · g · h
- Conservation of Mechanical Energy: E_initial = E_final (in closed system with conservative forces).

### Circular Motion:
- Centripetal Acceleration: a_c = v² / r = ω² · r
- Centripetal Force: F_c = (m · v²) / r
""",
            tags = "Physics, Mechanics, Newton, Energy",
            isPinned = false,
            isFavorite = false,
            colorIndex = 2
        ),
        StudyNote(
            id = 4,
            title = "Chemical Bonding & Molecular Geometry (VSEPR)",
            subject = "Chemistry",
            content = """# VSEPR Theory & Bonding

The Valence Shell Electron Pair Repulsion (VSEPR) model predicts the 3D geometry of molecules based on electron domain minimization.

### Geometries based on Steric Number:
- Steric 2 (0 lone pairs): Linear (180°) e.g., CO₂, BeCl₂
- Steric 3 (0 lone pairs): Trigonal Planar (120°) e.g., BF₃
- Steric 3 (1 lone pair): Bent (<120°) e.g., SO₂, NO₂⁻
- Steric 4 (0 lone pairs): Tetrahedral (109.5°) e.g., CH₄
- Steric 4 (1 lone pair): Trigonal Pyramidal (107°) e.g., NH₃
- Steric 4 (2 lone pairs): Bent (104.5°) e.g., H₂O

### Intermolecular Forces (strongest to weakest):
1. Ion-Dipole
2. Hydrogen Bonding (H bonded to N, O, or F)
3. Dipole-Dipole
4. London Dispersion Forces (present in all molecules)
""",
            tags = "Chemistry, VSEPR, Bonding, Molecular Geometry",
            isPinned = false,
            isFavorite = true,
            colorIndex = 3
        ),
        StudyNote(
            id = 5,
            title = "Cellular Respiration & ATP Synthesis",
            subject = "Biology",
            content = """# Stages of Cellular Respiration

C₆H₁₂O₆ + 6O₂ → 6CO₂ + 6H₂O + ~30-32 ATP

### 1. Glycolysis (Cytosol)
- Anaerobic process.
- 1 Glucose (6C) → 2 Pyruvate (3C).
- Net yield: 2 ATP + 2 NADH.

### 2. Pyruvate Oxidation (Mitochondrial Matrix)
- 2 Pyruvate → 2 Acetyl-CoA + 2 CO₂ + 2 NADH.

### 3. Citric Acid Cycle / Krebs Cycle (Mitochondrial Matrix)
- 2 turns per glucose.
- Yields: 2 ATP (or GTP), 6 NADH, 2 FADH₂, 4 CO₂.

### 4. Oxidative Phosphorylation & ETC (Inner Mitochondrial Membrane)
- Electron carriers donate electrons to Complexes I-IV.
- Proton gradient generated across inner membrane.
- ATP Synthase drives chemiosmosis: produces ~26-28 ATP.
""",
            tags = "Biology, ATP, Cellular Respiration, Biochemistry",
            isPinned = false,
            isFavorite = false,
            colorIndex = 4
        ),
        StudyNote(
            id = 6,
            title = "Effective Active Recall & Spaced Repetition",
            subject = "Study Skills",
            content = """# Evidence-Based Study Framework

### 1. The Testing Effect (Active Recall)
- Testing yourself forces the brain to retrieve information, strengthening neural pathways far better than passive re-reading or highlighting.

### 2. Spaced Repetition Intervals
- Day 0: Learn new concept and write key questions.
- Day 1: First retrieval practice.
- Day 3: Second retrieval practice.
- Day 7: Third retrieval practice.
- Day 14: Fourth retrieval practice.
- Day 30: Long-term review.

### 3. The Feynman Technique
1. Choose a concept.
2. Teach it to a 6th grader (avoid jargon).
3. Identify gaps in understanding when stuck.
4. Review source material and simplify explanations.
""",
            tags = "Productivity, Active Recall, Study Habits, Metacognition",
            isPinned = false,
            isFavorite = true,
            colorIndex = 5
        )
    )

    val initialLectures = listOf(
        LectureResource(
            id = 1,
            title = "Module 1: Asymptotic Analysis & Recurrences",
            subject = "Computer Science",
            courseCode = "CS 201",
            instructor = "Prof. Vipul Yadav",
            durationMinutes = 50,
            summary = "Rigorous introduction to growth of functions, big-O, omega, theta notation, recursion trees, and Master Theorem.",
            keyTakeaways = "• Master Theorem cases and limitations\n• Recursion tree method for divide-and-conquer\n• Lower bounds on comparison-based sorting",
            resourceLink = "https://ocw.mit.edu/courses/electrical-engineering-and-computer-science",
            isCompleted = true,
            isBookmarked = true,
            orderIndex = 1
        ),
        LectureResource(
            id = 2,
            title = "Module 2: Graph Algorithms (BFS, DFS & Dijkstra)",
            subject = "Computer Science",
            courseCode = "CS 201",
            instructor = "Prof. Vipul Yadav",
            durationMinutes = 60,
            summary = "Graph representations, topological sort, bipartite graph verification, and single-source shortest paths using priority queues.",
            keyTakeaways = "• BFS gives unweighted shortest paths in O(V + E)\n• DFS computes connected components and cycles\n• Dijkstra's runs in O((V + E) log V) with min-heap",
            resourceLink = "https://visualgo.net/en/graphds",
            isCompleted = false,
            isBookmarked = true,
            orderIndex = 2
        ),
        LectureResource(
            id = 3,
            title = "Calculus III: Partial Derivatives & Gradient Vectors",
            subject = "Mathematics",
            courseCode = "MATH 202",
            instructor = "Prof. Vipul Yadav",
            durationMinutes = 45,
            summary = "Multivariable functions, contour plots, directional derivatives, tangent planes, and the geometric interpretation of the gradient vector ∇f.",
            keyTakeaways = "• The gradient ∇f points in the direction of steepest ascent\n• Magnitude ||∇f|| is the maximum rate of increase\n• ∇f is perpendicular to level curves",
            resourceLink = "https://tutorial.math.lamar.edu/Classes/CalcIII/CalcIII.aspx",
            isCompleted = false,
            isBookmarked = false,
            orderIndex = 3
        ),
        LectureResource(
            id = 4,
            title = "Electromagnetism: Gauss's Law & Electric Flux",
            subject = "Physics",
            courseCode = "PHYS 102",
            instructor = "Prof. Vipul Yadav",
            durationMinutes = 55,
            summary = "Electric field lines, definition of surface flux Φ_E = ∮ E · dA, and calculating fields for spherical, cylindrical, and planar symmetries.",
            keyTakeaways = "• Net flux through closed surface equals Q_enclosed / ε₀\n• Field inside a conductor in electrostatic equilibrium is zero\n• Symmetry determines suitable Gaussian surface",
            resourceLink = "https://physics.info/flux/",
            isCompleted = false,
            isBookmarked = true,
            orderIndex = 4
        ),
        LectureResource(
            id = 5,
            title = "Organic Reactions: SN1 vs SN2 Mechanisms",
            subject = "Chemistry",
            courseCode = "CHEM 210",
            instructor = "Prof. Vipul Yadav",
            durationMinutes = 50,
            summary = "Nucleophilic substitution mechanisms, substrate sterics, solvent effects (polar protic vs polar aprotic), and stereochemical outcomes.",
            keyTakeaways = "• SN2: Bimolecular, one-step backside attack with Walden inversion (favored by primary alkyl halides)\n• SN1: Unimolecular carbocation intermediate, racemization (favored by tertiary alkyl halides)",
            resourceLink = "https://chemguide.co.uk/mechanisms/nucrep/whatis.html",
            isCompleted = false,
            isBookmarked = false,
            orderIndex = 5
        ),
        LectureResource(
            id = 6,
            title = "Molecular Biology: Transcription & Translation",
            subject = "Biology",
            courseCode = "BIO 110",
            instructor = "Prof. Vipul Yadav",
            durationMinutes = 45,
            summary = "The Central Dogma of Molecular Biology: RNA polymerase mechanism, mRNA processing (capping, polyadenylation, splicing), and ribosome translation.",
            keyTakeaways = "• Transcription occurs in nucleus in eukaryotes\n• Introns are excised and exons are spliced\n• Ribosome translates codons from 5' to 3' via tRNA anticodons",
            resourceLink = "https://www.nature.com/scitable/topicpage/translation-dna-to-mrna-to-protein-393/",
            isCompleted = false,
            isBookmarked = false,
            orderIndex = 6
        )
    )

    val initialQuestions = listOf(
        QuizQuestion(
            id = 1,
            subject = "Computer Science",
            topic = "Algorithms",
            difficulty = "Easy",
            question = "What is the worst-case time complexity of searching for an element in an unsorted array of size n?",
            optionA = "O(1)",
            optionB = "O(log n)",
            optionC = "O(n)",
            optionD = "O(n²)",
            correctOptionIndex = 2,
            explanation = "In an unsorted array, the target element may be at the very end or absent entirely, requiring a linear scan through all n elements. Thus, the worst-case complexity is O(n).",
            hint = "You must inspect elements one by one without prior ordering."
        ),
        QuizQuestion(
            id = 2,
            subject = "Computer Science",
            topic = "Data Structures",
            difficulty = "Medium",
            question = "Which data structure is primarily used to implement Breadth-First Search (BFS) in a graph?",
            optionA = "Stack",
            optionB = "Queue",
            optionC = "Priority Queue",
            optionD = "Binary Search Tree",
            correctOptionIndex = 1,
            explanation = "BFS explores neighbors level-by-level in First-In-First-Out (FIFO) order, which is directly implemented using a Queue. A Stack is used for Depth-First Search (DFS).",
            hint = "Think FIFO (First In, First Out) ordering."
        ),
        QuizQuestion(
            id = 3,
            subject = "Computer Science",
            topic = "Data Structures",
            difficulty = "Hard",
            question = "What is the worst-case time complexity of QuickSort when using a naive first-element pivot on an already sorted array?",
            optionA = "O(n log n)",
            optionB = "O(n)",
            optionC = "O(n²)",
            optionD = "O(2ⁿ)",
            correctOptionIndex = 2,
            explanation = "When an array is already sorted and the first element is chosen as pivot, the partition produces unbalanced subproblems of sizes 0 and n - 1 at each level, causing recursion depth n and total time O(n²).",
            hint = "Unbalanced partitions degenerate into repeated (n-1) splits."
        ),
        QuizQuestion(
            id = 4,
            subject = "Mathematics",
            topic = "Calculus",
            difficulty = "Easy",
            question = "What is the derivative of f(x) = 3x⁴ - 5x + 7 with respect to x?",
            optionA = "12x³ - 5",
            optionB = "12x³ - 5x",
            optionC = "3x³ - 5",
            optionD = "7x³ - 5",
            correctOptionIndex = 0,
            explanation = "Applying the power rule: d/dx(3x⁴) = 3 * 4x³ = 12x³, d/dx(-5x) = -5, and the derivative of constant 7 is 0. So f'(x) = 12x³ - 5.",
            hint = "Apply the power rule: d/dx(xⁿ) = n·xⁿ⁻¹."
        ),
        QuizQuestion(
            id = 5,
            subject = "Mathematics",
            topic = "Linear Algebra",
            difficulty = "Medium",
            question = "If the determinant of a square matrix A is zero (det(A) = 0), what does this imply about the matrix?",
            optionA = "The matrix is invertible",
            optionB = "The matrix has full rank",
            optionC = "The matrix is singular and has no inverse",
            optionD = "All eigenvalues of A are positive",
            correctOptionIndex = 2,
            explanation = "A matrix with det(A) = 0 is defined as singular. It cannot be inverted because 1/det(A) would involve division by zero.",
            hint = "Recall the formula for matrix inversion A⁻¹ = (1/det(A)) * adj(A)."
        ),
        QuizQuestion(
            id = 6,
            subject = "Mathematics",
            topic = "Calculus",
            difficulty = "Hard",
            question = "What is the value of ∫₀^∞ x·e⁻ˣ dx?",
            optionA = "0",
            optionB = "1",
            optionC = "e",
            optionD = "∞ (diverges)",
            correctOptionIndex = 1,
            explanation = "Using integration by parts: let u = x (du = dx) and dv = e⁻ˣ dx (v = -e⁻ˣ). ∫ x·e⁻ˣ dx = [-x·e⁻ˣ]₀^∞ + ∫₀^∞ e⁻ˣ dx = 0 + [-e⁻ˣ]₀^∞ = 0 - (-1) = 1. This is also Γ(2) = 1! = 1.",
            hint = "Use integration by parts ∫ u dv = u·v - ∫ v du."
        ),
        QuizQuestion(
            id = 7,
            subject = "Physics",
            topic = "Classical Mechanics",
            difficulty = "Easy",
            question = "If net external force acting on an object is zero, what can be concluded about its motion?",
            optionA = "It must be completely stationary",
            optionB = "Its acceleration is zero (constant velocity)",
            optionC = "It is decelerating",
            optionD = "Its kinetic energy is increasing",
            correctOptionIndex = 1,
            explanation = "By Newton's Second Law F = m·a, if F = 0, then acceleration a = 0. The object maintains constant velocity (which includes remaining at rest if initially at rest).",
            hint = "Newton's First Law (Law of Inertia)."
        ),
        QuizQuestion(
            id = 8,
            subject = "Physics",
            topic = "Electromagnetism",
            difficulty = "Medium",
            question = "According to Faraday's Law of Induction, what induces an electromotive force (EMF) in a closed circuit loop?",
            optionA = "A constant electrostatic charge",
            optionB = "A changing magnetic flux through the loop",
            optionC = "A steady direct current",
            optionD = "The gravitational field",
            correctOptionIndex = 1,
            explanation = "Faraday's Law states EMF = -dΦ_B/dt, meaning an electromotive force is induced whenever the magnetic flux threading through the surface bounded by the loop changes over time.",
            hint = "EMF is proportional to the time rate of change of magnetic flux."
        ),
        QuizQuestion(
            id = 9,
            subject = "Physics",
            topic = "Thermodynamics",
            difficulty = "Hard",
            question = "Which law of thermodynamics states that the entropy of an isolated system always increases or remains constant in a spontaneous process?",
            optionA = "Zeroth Law",
            optionB = "First Law",
            optionC = "Second Law",
            optionD = "Third Law",
            correctOptionIndex = 2,
            explanation = "The Second Law of Thermodynamics dictates that in any spontaneous natural process, the total entropy of an isolated system never decreases (ΔS ≥ 0).",
            hint = "It establishes the thermodynamic arrow of time."
        ),
        QuizQuestion(
            id = 10,
            subject = "Chemistry",
            topic = "VSEPR & Bonding",
            difficulty = "Easy",
            question = "What is the molecular geometry of methane (CH₄)?",
            optionA = "Square planar",
            optionB = "Tetrahedral",
            optionC = "Trigonal pyramidal",
            optionD = "Linear",
            correctOptionIndex = 1,
            explanation = "Methane has 4 single C-H bonds and zero lone pairs around the central carbon atom (steric number 4). The VSEPR geometry is tetrahedral with bond angles of ~109.5°.",
            hint = "4 bonding pairs of electrons with no lone pairs."
        ),
        QuizQuestion(
            id = 11,
            subject = "Chemistry",
            topic = "Organic Chemistry",
            difficulty = "Medium",
            question = "Which solvent type strongly favors the SN2 reaction mechanism over SN1?",
            optionA = "Polar protic solvents (e.g., Water, Ethanol)",
            optionB = "Polar aprotic solvents (e.g., Acetone, DMSO)",
            optionC = "Non-polar solvents with strong hydrogen bonding",
            optionD = "Aqueous acids",
            correctOptionIndex = 1,
            explanation = "Polar aprotic solvents dissolve cations while leaving nucleophiles unencumbered (unsolvated), maximizing their nucleophilicity and driving bimolecular backside attack (SN2).",
            hint = "Solvents without acidic protons to cage the nucleophile."
        ),
        QuizQuestion(
            id = 12,
            subject = "Biology",
            topic = "Genetics",
            difficulty = "Easy",
            question = "Which nitrogenous base pairs with Adenine (A) in a double-stranded DNA molecule?",
            optionA = "Uracil (U)",
            optionB = "Cytosine (C)",
            optionC = "Thymine (T)",
            optionD = "Guanine (G)",
            correctOptionIndex = 2,
            explanation = "In DNA, Adenine pairs with Thymine via two hydrogen bonds (A=T), while Guanine pairs with Cytosine via three hydrogen bonds (G≡C). (Uracil replaces Thymine in RNA).",
            hint = "Chargaff's rules: A pairs with T via 2 hydrogen bonds."
        ),
        QuizQuestion(
            id = 13,
            subject = "Biology",
            topic = "Biochemistry",
            difficulty = "Medium",
            question = "Where does the Citric Acid Cycle (Krebs cycle) take place in eukaryotic cells?",
            optionA = "Cytosol",
            optionB = "Endoplasmic reticulum",
            optionC = "Mitochondrial matrix",
            optionD = "Thylakoid lumen",
            correctOptionIndex = 2,
            explanation = "In eukaryotes, pyruvate enters the mitochondrion where it is oxidized to Acetyl-CoA, which enters the Krebs cycle occurring within the mitochondrial matrix.",
            hint = "Inside the innermost fluid compartment of the mitochondrion."
        ),
        QuizQuestion(
            id = 14,
            subject = "Study Skills",
            topic = "Metacognition",
            difficulty = "Easy",
            question = "What is the Feynman Technique primarily designed to do?",
            optionA = "Memorize flashcards by rote repetition",
            optionB = "Simplify complex ideas by explaining them in plain language",
            optionC = "Speed-read 500 words per minute",
            optionD = "Cram before exams with all-nighters",
            correctOptionIndex = 1,
            explanation = "The Feynman Technique involves explaining a concept as if teaching a beginner without jargon, which quickly uncovers any conceptual gaps or illusions of competence.",
            hint = "Named after Nobel physicist Richard Feynman."
        )
    )
}

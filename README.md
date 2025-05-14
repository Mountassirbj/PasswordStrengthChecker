
# Password Strength Checker

A simple yet effective tool to assess the strength of passwords based on key factors such as length, complexity, and uniqueness. This application provides detailed feedback to help users create stronger and more secure passwords.

## Features

- **Length Evaluation**: Ensures the password is of adequate length (minimum of 8 characters).
- **Complexity Check**: Verifies the presence of:
  - Lowercase letters
  - Uppercase letters
  - Digits
  - Special characters (`@$!%*?&`)
- **Uniqueness Check**: Assesses how unique the characters in the password are, minimizing repetitive patterns.
- **Feedback and Recommendations**: Provides actionable suggestions to improve password strength.
- **Normalized Score**: Returns a score between 0 to 100 to represent the password strength.
- **Strength Category**: Categorizes passwords as Weak, Moderate, or Strong based on the normalized score.

## Strength Categories

- **Strong**: Score ≥ 80
- **Moderate**: Score ≥ 50 but < 80
- **Weak**: Score < 50

## Scoring System

The scoring is based on the following factors:

1. **Length (30%)**: Passwords longer than 12 characters are rated as excellent, earning maximum points.
2. **Complexity (50%)**: Includes checks for lowercase, uppercase, digits, and special characters.
3. **Uniqueness (20%)**: Evaluates the ratio of unique characters to total characters in the password.

Each factor is weighted, and the total score is calculated to give a normalized score between 0 and 100.

## Usage

### How to Use:
1. **Clone the repository**:
    ```bash
    git clone https://github.com/yourusername/password-strength-checker.git
    cd password-strength-checker
    ```

2. **Run the program**:
    - Compile the Java program:
    ```bash
    javac PasswordStrengthChecker.java
    ```

    - Run the program:
    ```bash
    java PasswordStrengthChecker
    ```

    - Enter a password when prompted to check its strength.

3. **Example**:
    - **Input**:
        ```
        NikhilSingh@21122
        ```
    - **Output**:
        ```
        Password Strength Feedback:
        - Password length is excellent.
        - Password has a good level of uniqueness.
        Normalized Score: 90/100
        Overall password strength: Strong
        ```

## Example Outputs

### Example 1: Strong Password

**Input**:
```
P@ssw0rd123!
```

**Output**:
```
Password Strength Feedback:
- Password length is excellent.
- Password has a good level of uniqueness.
Normalized Score: 100/100
Overall password strength: Strong
```

### Example 2: Moderate Password

**Input**:
```
abcd123
```

**Output**:
```
Password Strength Feedback:
- Password is too short. Minimum length is 8 characters.
- Password should include at least one uppercase letter.
- Password should include at least one special character (@$!%*?&).
- Password could be more unique. Avoid using repetitive characters.
Normalized Score: 50/100
Overall password strength: Moderate
```

## Contributing

We welcome contributions! If you'd like to improve this project, please fork the repository and submit a pull request. Whether it's fixing bugs, improving the scoring system, or adding new features, we appreciate your help in making this tool more robust and secure.

## Contact

For any questions, feel free to reach out to the repository owner or open an issue on GitHub.

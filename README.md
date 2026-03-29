Bank Account Management System (Java)
What This Project Does
This project is a Bank Account Management System developed using Java programming language. This Bank Account Management System allows users to manage their bank accounts by performing various operations such as creating savings or current accounts, depositing money, withdrawing money, checking their account balances, viewing their account transactions, applying interest to their accounts, and deleting their accounts.
This Bank Account Management System also uses file handling operations, and all the data will be stored in a file named accounts.txt. This data will not be lost even if we close the program.
Problem It Solves
Managing bank account records is a tedious and complicated process. This Bank Account Management System helps users manage their bank account records in a simple and efficient manner.
How to Set Up

1. Requirements

Java (JDK 8 or later versions)
Any IDE (VS Code / IntelliJ) OR Terminal

2. Download / Clone the Repository
git clone <your-repo-link>
cd Bank-Management-System-Java

3. Compile the Program
javac Main.java

4. Run the Program
java Main
How to Use
When we run this Bank Account Management System, we will see a menu displayed on the screen:
=================================
  Bank Account Management System
=================================

--- MENU ---
1. Create Savings Account
2. Create Current Account
3. Deposit
4. Withdraw
5. Check Balance
6. View All Accounts
7. Apply Interest (Savings only)
8. View Transaction History
9. Delete Account
0. Exit
Steps:
To perform operations using this Bank Account Management System, we have to enter 1 to create a savings account.
Enter 2 to open a new current account (minimum deposit ₹1000)
Enter 3 to deposit money in your account
Enter 4 to withdraw money from your account
Enter 5 to check your balance and account details
Enter 6 to view all existing accounts
Enter 7 to apply 4% interest on your savings account
Enter 8 to view full transaction history of your account
Enter 9 to delete your account
Enter 0 to exit

Data Storage

Data will be stored in a file named accounts.txt
Data will be saved automatically after each operation
Data will be loaded automatically at the start of the program to avoid data loss

Account Rules

For savings accounts, minimum deposit required is ₹500, and minimum balance should not go below ₹500
For current accounts, minimum deposit required is ₹1000, and there will be an overdraft facility up to a certain limit
For savings accounts, 4% interest will be applied

Technologies Used

Java
Object Oriented Programming (Abstract Class, Inheritance, Polymorphism)
Custom Exception Handling
ArrayList
File Handling (BufferedReader, BufferedWriter)

Project Files
Bank-Management-System-Java/
│── Main.java
│── accounts.txt
What I Learned 
Implemented Object-Oriented Programming concepts like abstract classes and method overriding
Used custom exceptions for better error handling
Used file handling for persistent data storage
Built a real-world Java console application
Improved logical and problem-solving skills

Author
VIDUSHI KANUNGO
24BAI10123
B.Tech(AI ML) | VIT Bhopal University

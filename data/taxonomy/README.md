# Taxonomy

### 1. Extract Class

&emsp; **i. Decomposition**

&emsp;**P2 + Refactoring Subcategory:**

````
Extract class refactorings are frequently employed to decompose complex classes to improve readability.
###
An extract class refactoring pattern is to extract a few attributes and/or methods as a new class to decompose this class. Here is an example of the extract class refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer extracted the following attributes and methods:
```
[extracted code]
```
as new class "className".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential extract class refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The to-be-refactored class.

### 2. Extract Method

&emsp; **i. Decomposition**

&emsp;**P2 + Refactoring Subcategory:**

````
Extract method refactorings are frequently employed to decompose complex methods to improve readability.
###
An extract method refactoring pattern is to extract a piece of code as a new method to decompose this method. Here is an example of the extract method refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer extracted the following statements:
```
[extracted code]
```
as new method "methodName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential extract method refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The to-be-refactored method.

&emsp; **ii. Code Duplication**

&emsp;**P2 + Refactoring Subcategory**

````
Extract method refactorings are frequently employed to remove duplicate code snippets to improve readability and reusability.
###
An extract method refactoring pattern is to extract repeated statements as a new method within the class. Here is an example of the extract method refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer extracted the following statements:
```
[extracted code]
```
as new method "methodName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential extract method refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:**  The methods with repeated statements.

### 3. Extract Variable

&emsp; **i. Complex Expression**

&emsp;**P2 + Refactoring Subcategory:**

````
Extract variable refactorings are frequently employed to extract complex expressions to improve readability.
###
An extract variable refactoring pattern is to extract complex expressions as a new variable within the same method. Here is an example of the extract variable refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer extracted the expression "expression" as new variable "variableName":
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential extract variable refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The enclosing method.

&emsp; **ii. Code Duplication**

&emsp;**P2 + Refactoring Subcategory**

````
Extract variable refactorings are frequently employed to remove repeated expressions to improve readability and reusability.
###
An extract variable refactoring pattern is to extract repeated expressions as a new variable within the same method. Here is an example of the extract variable refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer extracted the expression "expression" as new variable "variableName":
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential extract method refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The enclosing method.

### 4. Inline Method

&emsp; **i. Proxy Method**

&emsp;**P2 + Refactoring Subcategory:**

````
Inline method refactorings are frequently employed to eliminate unnecessary methods to improve readability.
###
An inline method refactoring pattern is to remove methods that are only used as proxies in other methods. Here is an example of the inline method refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer removed the unnecessary method "methodName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential inline method refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The to-be-refactored method and the methods that call it.

&emsp; **ii. Single Line Statement**

&emsp;**P2 + Refactoring Subcategory**

````
Inline method refactorings are frequently employed to eliminate unnecessary methods to improve readability.
###
An inline method refactoring pattern is to remove methods with only a few statements. Here is an example of the inline method refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer removed the unnecessary method "methodName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential inline method refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The to-be-refactored method and the methods that call it.

&emsp; **iii. Use Only Once**

&emsp;**P2 + Refactoring Subcategory**

````
Inline method refactorings are frequently employed to eliminate unnecessary methods to improve readability.
###
An inline method refactoring pattern is to remove private methods that are called once within the class. Here is an example of the inline method refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer removed the unnecessary method "methodName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential inline method refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The to-be-refactored method and the methods that call it.

### 5. Inline Variable

&emsp; **i. Use As Reference**

&emsp;**P2 + Refactoring Subcategory:**

````
Inline variable refactorings are frequently employed to eliminate unnecessary variables to improve readability.
###
An inline variable refactoring pattern is to remove variables that are only used as references to other objects. Here is an example of the inline variable refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer removed the unnecessary variable "variableName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential inline variable refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The enclosing method.

&emsp; **ii. Use Only Once**

&emsp;**P2 + Refactoring Subcategory**

````
Inline method refactorings are frequently employed to eliminate unnecessary methods to improve readability.
###
An inline variable refactoring pattern is to remove variables that are used only once within the method. Here is an example of the inline variable refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer removed the unnecessary variable "variableName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential inline variable refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The enclosing method.

### 6. Rename Attribute

&emsp; **i. Naming Convention**

&emsp;**P2 + Refactoring Subcategory:**

````
Rename attribute refactorings are frequently employed to modify low-quality identifiers to improve readability
###
A rename attribute refactoring pattern is to rename attributes that do not conform to the naming convention. Here is an example of the rename attribute refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer renamed the attribute "oldName" to "newName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential rename attribute refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The to-be-refactored attribute and other attributes within the enclosing class.

&emsp; **ii. Attribute Type**

&emsp;**P2 + Refactoring Subcategory**

````
Rename attribute refactorings are frequently employed to modify low-quality identifiers to improve readability.
###
A rename attribute refactoring pattern is to rename attributes that are inconsistent with their types. Here is an example of the rename attribute refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer renamed the attribute "oldName" to "newName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential rename attribute refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The to-be-refactored attribute and other attributes within the enclosing class.

&emsp; **iii. Inconsistent Context**

&emsp;**P2 + Refactoring Subcategory:**

````
Rename attribute refactorings are frequently employed to modify low-quality identifiers to improve readability
###
A rename attribute refactoring pattern is to rename attributes that are inconsistent with methods within the enclosing class. Here is an example of the rename attribute refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer renamed the attribute "oldName" to "newName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential rename attribute refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The to-be-refactored attribute and other attributes and the methods that use it within the enclosing class.

### 7. Rename Method

&emsp; **i. Inconsistent Context**

&emsp;**P2 + Refactoring Subcategory:**

````
Rename method refactorings are frequently employed to modify low-quality identifiers to improve readability.
###
A rename method refactoring pattern is to rename methods to align with the naming convention in other methods. Here is an example of the rename method refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer renamed the method "oldName" to "newName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential rename method refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The to-be-refactored method and other method signatures within the enclosing class.

&emsp; **ii. Inconsistent Method Name**

&emsp;**P2 + Refactoring Subcategory**

````
Rename method refactorings are frequently employed to modify low-quality identifiers to improve readability.
###
A rename method refactoring pattern is to rename methods whose names are inconsistent with their bodies. Here is an example of the rename method refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer renamed the method "oldName" to "newName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential rename method refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The to-be-refactored method.

### 8. Rename Parameter

&emsp; **i. Ambiguous Semantic**

&emsp;**P2 + Refactoring Subcategory:**

````
Rename parameter refactorings are frequently employed to modify low-quality identifiers to improve readability
###
A rename parameter refactoring pattern is to rename parameters whose semantics are inappropriate in the context. Here is an example of the rename parameter refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer renamed the parameter "oldName" to "newName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential rename parameter refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The enclosing method.

&emsp; **ii. Parameter Type**

&emsp;**P2 + Refactoring Subcategory**

````
Rename parameter refactorings are frequently employed to modify low-quality identifiers to improve readability.
###
A rename parameter refactoring pattern is to rename parameters that are inconsistent with their types. Here is an example of the rename parameter refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer renamed the parameter "oldName" to "newName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential rename parameter refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The enclosing method.

&emsp; **iii. Inconsistent Context**

&emsp;**P2 + Refactoring Subcategory:**

````
Rename parameter refactorings are frequently employed to modify low-quality identifiers to improve readability
###
A rename parameter refactoring pattern is to rename parameters to align with the terminology used in other methods. Here is an example of the rename parameter refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer renamed the parameter "oldName" to "newName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential rename parameter refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The enclosing method and other method signatures within the enclosing class.

### 9. Rename Variable

&emsp; **i. Naming Convention**

&emsp;**P2 + Refactoring Subcategory:**

````
Rename variable refactorings are frequently employed to modify low-quality identifiers to improve readability
###
A rename variable refactoring pattern is to rename variables that do not conform to the naming convention. Here is an example of the rename variable refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer renamed the variable "oldName" to "newName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential rename variable refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The enclosing method.

&emsp; **ii. Initialization**

&emsp;**P2 + Refactoring Subcategory**

````
Rename variable refactorings are frequently employed to modify low-quality identifiers to improve readability.
###
A rename variable refactoring pattern is to rename variables that are inconsistent with method calls in their initialization expressions. Here is an example of the rename variable refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer renamed the variable "oldName" to "newName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential rename variable refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The enclosing method.

&emsp; **iii. Variable Type**

&emsp;**P2 + Refactoring Subcategory:**

````
Rename variable refactorings are frequently employed to modify low-quality identifiers to improve readability
###
A rename variable refactoring pattern is to rename variables that are inconsistent with their types. Here is an example of the rename variable refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer renamed the variable "oldName" to "newName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential rename variable refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The enclosing method.

&emsp; **iv. Ambiguous Semantic**

&emsp;**P2 + Refactoring Subcategory:**

````
Rename variable refactorings are frequently employed to modify low-quality identifiers to improve readability
###
A rename variable refactoring pattern is to rename variables whose semantics are inappropriate in the context. Here is an example of the rename variable refactoring that follows this pattern.
The source code before refactoring is:
```
[example code before refactoring]
```
The source code after refactoring is:
```
[example code after refactoring]
```
In this example, the developer renamed the variable "oldName" to "newName".
####
As a developer, imagine your team leader requests you to review a piece of code to identify potential rename variable refactoring opportunities. The original code snippet is as follows:
```
[original code]
```
If there are any refactoring opportunities, please do it and generate the refactored code.
Otherwise, simply state that no refactoring is necessary.
````

------

&emsp;**Search Space Limitation:** The enclosing method.
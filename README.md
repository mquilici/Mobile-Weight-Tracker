# Mobile-Weight-Tracker

<p align="center">
     <img src="/images/Table_Light.jpeg" alt="alt text" width="250px" hspace="10">
     <img src="/images/Plot_Light.jpeg" alt="alt text" width="250px" hspace="10">
     <img src="/images/BMI_Light.jpeg" alt="alt text" width="250px" hspace="10">
</p>

### Description
This program is weight-tracking app for Android that I developed for CS 360 Mobile Architecture and Development in December 2020. The app allows the user to record their daily weight measurements and displays the results in a table. The user can add weights, edit weights, set a goal weight, plot the weights in a graph, and change various settings. If the user enters their height in the settings screen, a new column will be displayed in the table that shows their body mass index (BMI) for each row.

### Why I selected this item
I selected this program for my ePortfolio is because it is one of the more complex programs that I have written. The program demonstrates my ability to take a project from the requirements phase through to completion. To develop the program, I first conducted research to understand what features users would like to see. Information was gathered by reading studies about fitness apps and looking at reviews for similar apps. Using this data, I sketched out a layout for the app that included all of the required features. I then proceeded to create a basic interface in Android Studio that had minimal functionality but allowed me to test the layout. Following a model-view-controller (MVC) approach, I then implemented the model classes which are responsible for storing the user’s weight data. Model classes include a weight class for storing a weight measurement and a database class with methods for storing all of the weights in an SQLite database. Lastly, I implemented the controller classes that contain the logic for user interaction. Controller classes are responsible for storing user input in the database and displaying data in a table.

To improve this artifact, I added a BMI plot that displays the users latest BMI value along with contour lines that represent standard BMI ranges like underweight, normal, and overweight. A new icon was added to the bottom navigation bar to enable quick navigation to the new plot. Using several for-loops, arrays of points that represent the x and y values for each line were computed and then plotted and filled them with different colors.

In addition to adding a new BMI plot, I also improved the program by fixing a few bugs that were discovered during a code review. For example, in several locations, I replaced so-called “magic numbers” with named variables that more clearly indicate what the numbers represent. I removed error logs that display debugging information. I also removed extraneous code and improved documentation by including comments in several locations. During these enhancements, I discovered additional bugs, including an error parsing user weight input that caused the program to crash. If the user entered a decimal point for the weight, the program would be unable to convert the string input to a float. To resolve the problem, I added a try-catch block when parsing the input as well as a conditional statement that checks if the input is an empty string or decimal point.

### Course Objectives
I exceeded course objectives for this project by adding a significant new feature to the application. I took a concept that weight-conscious individuals find important (BMI measurements), sketched a prototype layout, modified the program layout, and implemented logic needed to draw the new plot. This process is similar to typical software maintenance cycles where developers incrementally add new features and fix errors with each release.





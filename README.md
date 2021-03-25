### Briefly describe the artifact. What is it? When was it created?
This artifact is weight-tracking application that I developed for CS 360 Mobile Architecture and Development in December 2020. The app runs on the Android platform and allows the user to record their weight over time. Weights and the dates on which they were recorded are displayed in a table. The user can add weights, edit weights, set a goal weight, plot the weights in a graph, and change various settings. If the user enters their height in the settings screen, a new column will be displayed in the table that shows a body mass index (BMI) value for each row.

### Justify the inclusion of the artifact in your ePortfolio. Why did you select this item? What specific components of the artifact showcase your skills and abilities in software development? How was the artifact improved?
The reason I selected this artifact for my ePortfolio is because it is one of the more complex programs that I have written. The program demonstrates my ability to take a project from the requirements phase through to completion. To develop the program, I first conducted research to understand what features users would like to see. Information was gathered by reading studies about fitness apps and looking at reviews for similar apps. Using this data, I sketched out a layout for the app that included all of the required features. I then proceeded to create a skeleton interface in Android Studio that had minimal functionality but allowed me to test the layout. Following a model-view-controller paradigm, I then implemented the model classes that are responsible for storing the user’s weight data. Model classes include a weight class for storing a single weight measurement, as well as a database class that provides methods for storing and retrieving weights from an SQLite database. Lastly, I implemented the controller classes that include the logic for user interaction. Controller classes are responsible for storing user input in the database and displaying data in a table.

To improve this artifact, a BMI plot has been added to display the users latest BMI value along with contour lines that represent standard BMI ranges like underweight, normal, and overweight (see images below). A new icon has been added to the bottom navigation bar. This icon navigates to the plot screen, which draws lines using the BMI formula discussed above. Using several for-loops, I computed arrays of values that represent the x and y values for each line. I then plotted the lines on the table and filled them with different colors.

<p align="center">
     <img src="/images/Table_Light.jpeg" alt="alt text" width="250px" hspace="10">
     <img src="/images/Plot_Light.jpeg" alt="alt text" width="250px" hspace="10">
     <img src="/images/BMI_Light.jpeg" alt="alt text" width="250px" hspace="10">
</p>

In addition to adding a new BMI plot, I also improved the artifact by fixing several errors that were discovered during code reviews. For example, in several locations, I replaced so-called “magic numbers” with named variables that more clearly indicate what the numbers represent. I removed error logs that display debugging information. I removed extraneous code. I also improved documentation by commenting uncommented sections of code. During these enhancements, I discovered additional errors, including an error parsing input that caused the program to crash. If the user entered a decimal point for the weight, the program would be unable to convert the input string input to a float. I added a try-catch block when parsing the input as well as a conditional statement that checks if the input is an empty string or decimal point.  


### Did you meet the course objectives you planned to meet with this enhancement in Module One? Do you have any updates to your outcome-coverage plans?
I believe I met course objects with this enhancement as it shows that I can add a significant new feature to a mobile application. I took a feature that fitness app users find important (BMI measurements), sketched a prototype layout, and implemented the feature. I also fixed several errors with the program that I discovered during my code review. This process is similar to typical software maintenance cycles where developers release one version that may not have every desired feature, and incrementally add features and fix errors in future releases.





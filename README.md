# Android-Weight-Tracker

**Briefly summarize the requirements and goals of the app you developed. What user needs was this app designed to address?**

This Android app is targeted towards health-conscious individuals who are interested in tracking their weight over time and will help them achieve their goal weight. Users will log in to the app with a username and password and will be presented with a table displaying a record of their weights and days on which their weights were recorded. After measuring their weight on a scale, users will then be able to add the value to the table for that day. Users will also be able to set a goal weight so they can be notified when they achieve their goal.

**What screens and features were necessary to support user needs and produce a user-centered UI for the app? How did your UI designs keep users in mind? Why were your designs successful?**

The app includes a database for loading and storing logins, passwords, weights, dates, and goal values. When the user starts the app, they can log in or register. When logging in, the user’s credentials are verified against the database. When registering, their credentials are added to the database. Once the user is authenticated, their weight history will be loaded from the database and displayed in a table. The user can add a new weight to the table using a floating action button, or set a goal weight using the overflow menu option. When adding a new weight, the user will be able to enter a weight value and a date value. When adding a goal weight, the user will select if it is a weight loss goal or a weight gain goal and enter a goal weight value. Lastly, a popup notification will alert the user when they achieve their weight goal. 

The UI was designed with the user in mind following Material Design standards for the use of color and placement of elements on the screen. For example, important buttons are given a color fill, whereas less important buttons have no fill. The use of color draws the user's attention towards more important items and makes the interface more appealing. The UI designs are successful as they have been tested on a variety of devices with different screen sizes and orientations to ensure that graphical elements are correctly positions and do not overlap on smaller devices.

**How did you approach the process of coding your app? What techniques or strategies did you use? How could those be applied in the future?**

The process of coding my app involved determining what features were needed based on the program requirements, sketching the user interface, implementing the user interface elements in Android Studio, and then writing the code needed to make the app function. Sketching the interface was an important first step as it helped solidify design elements  and allowed me to explore a variety of options before finding one I felt worked best. After sketching the UI design, it was straight-forward to implement a skeleton UI in Android Studio to test usability. With the UI in place, I then implemented the code needed to work with the SQLite database, get weights and other inputs from the user, and populate the weight table with data from the database. To write the code, it was essential to reference examples from the Zybooks course material to understand how a given feature might be implemented and then adapt the examples to suit the program. 
     
**How did you test to ensure your code was functional? Why is this process important and what did it reveal?**

To ensure the code is functional, both static testing and dynamic testing techniques were used. The code was reviewed multiple times and analyzed with Android Studio's code inspector to look for errors. I also manually tested a variety of boundary-cases with user input to ensure that no input would cause undesirable behavior. The testing process is important as it reduces the likelihood that errors in the code will find their way into the final build. Numerous errors were discovered during this process, such as issues with the database logic and weight table.

**Considering the full app design and development process, from initial planning to finalization, where did you have to innovate to overcome a challenge?**

One particularly tricky challenge to overcome was adding alternating background colors to the weight table rows. Without delineation of the rows, the weight table would be difficult to read. The initial approach was to simply change the background of the itemView for each row depending if the position is even or odd. This appears to work at first until the user deletes a row and removes one of the colors, making two adjacent rows have the same background. To solve this, my first approach was to program the app to refresh using startActivity(getIntent()); finish(); overridePendingTransition(0,0); however this created additional problems. I finally found a solution using decorators with dark and light-colored drawables used for the background. The decorators ensure the row backgrounds alternate correctly when the user deletes or adds an item.

**In what specific component from your mobile app were you particularly successful in demonstrating your knowledge, skills, and experience?**

After completing the basic app requirements, I decided to add additional features such as weight units (kg or lb), a dark mode, and a graph of the weights. I think these elements demonstrate my experience with app development as they show that I can create new features that go beyond the existing coursework. The weight plotting activity required the use of the Hellocharts library, reading the data from the SQLite database, and using the appropriate methods to add the data to the plot.

<img src="https://github.com/mquilici/Mobile-Weight-Tracker/blob/master/images/Table_Light.jpeg" alt="alt text" height="600px" class="center">
<img src="https://github.com/mquilici/Mobile-Weight-Tracker/blob/master/images/Weight_Table.jpeg" alt="alt text" height="600px" class="center">
<img src="https://github.com/mquilici/Mobile-Weight-Tracker/blob/master/images/Weight_Plot.jpeg" alt="alt text" height="600px" class="center">
<img src="https://github.com/mquilici/Mobile-Weight-Tracker/blob/master/images/BMI_Plot.jpeg" alt="alt text" height="600px" class="center">



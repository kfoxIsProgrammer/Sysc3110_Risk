# Sysc3110_Risk
This is used for our Sysc 3110 Project. The game of risk

Team: Java Squad


__Teammates (Add your name and email):__
1. Kevin Fox : kevinfox@cmail.carleton.ca @SolidFox#7122
2. Dimitry Koutchine : DimitryKoutchine@cmail.carleton.ca @Dkout#7347
3. Omar Hashmi : OmarHashmi@cmail.carleton.ca @belt#8711
4. Kshitij Sawhney : kshitijsawhney@cmail.carleton.ca

__Deliverables:__
1. Jar file containing the source files.
2. Design documents (changes and design choices)
3. JUnit tests
4. Uml Diagram 
5. User Manual
6. Javadoc 

__Changes from previous iteration:__

This is the second iteration of the project. The Graphical User Interface was implemented to interact with the user.
We have incorporated a map/data loader in anticipation for milestone 4. 
The model sends tranactional State information to the view using a Context class.

__Known issues:__

Bug 1. When the user is prompted to enter player names, if any of the inputs are left blank you will crash. Sorry :D

Potential Bug 1. Could you make sure that the attack algorithm is correct. I might have misunderstood the rule for risk.

Class Cohesion isn't the best. RiskModel still has the map in it's components, while the map holds the same data.

Make sure that the maps folder is in the same directory as the jar 

When testing you must always press enter when anything pops up

__Roadmap:__

The next goal for our game is to introduce the playable Deploy and Fortify phases.
As well we want to further refine our design based on TA feedback to increase class cohesion.
The final milestone is to add a save feature and a method to import custom maps.



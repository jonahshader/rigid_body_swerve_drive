# rigid_body_swerve_drive
Vehicle physics simulation with experimental path planning algorithms

The primary purpose of this software is to come up with a reasonable approximation of vehicle physics, in particular with regards to handling. I am currently building an electric go kart where all four wheels can be steered independently and continuous/indefinitely, hence the name "swerve drive" in the repo name. The idea is to simulate various cars with different handling and project their motions onto the real go kart. This would enable things like "virtual drifting" where the vehicle goes through the motions of drifting (i.e. the vehicle goes sideways through tight turns) without actually losing grip on any of the wheels. Since the wheels can all be steered independently, the vehicle would just steer the rear wheels appropriately to give the vehicle the motion of drifting. The benefit of this is you can practice drifting and other agressive maneuvers without damaging the weels or other hardware. The projection onto the real vehicle can also be scaled down or slowed down so that one could practice maneuvers slowly at first and then ramp up the speed as the driver becomes more comfortable. 

Since this go kart is electrically controlled, it can also perform driving maneuvers autonomously. I plan to use GPS modules to give the go kart a sense of position to enable things like mapping out virtual race tracks, and auto braking if one drives outside of this track to prevent crashes. Path generation, like the gifs from below, could also be used to generate an optimal path around one of these virutal race tracks. The motions of that path could then be projected onto the real go kart and the driver could experience it.

This is a near optimal path where the vehicle hits all four waypoints while avoiding the wall on the left:
![Alt Text](https://github.com/jonahshader/rigid_body_swerve_drive/blob/master/images/no_force_lines.gif)


Here is what a non-optimal one looks like with force vector rendering enabled. Yellow lines indicate that the wheel is slipping on the ground and green indicates no slip:
![Alt Text](https://github.com/jonahshader/rigid_body_swerve_drive/blob/master/images/undertrained.gif)

As you can see, the forces being applied to the wheels are very noisy. This is because the training algorithm for the path uses mutation and selection which is not the most efficient way of training. 

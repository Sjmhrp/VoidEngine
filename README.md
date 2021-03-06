# VoidEngine [![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)
A Java-based 3D Game Engine

## Features
* 3D Rendering Engine
* Phinn-Blong Shading model
* 3D Rigid body dynamics
* Discrete collision detection
* Broadphase collision detection (using a Dynamic Axis-Aligned Bounding Box Tree)
* Narrowphase collision detection using GJK/EPA
* Multiple Collision Shapes, including Box, Capsule, Cone, Cylinder, Ellipsoid, Sphere, Triangle and Convex Hull
* Concave shape collisions using a static TriMesh or Convex Decomposition
* Collision response (includng friction and restitution) using a Projected Gauss-Seidel Mixed Linear Complementarity Solver
* 3D Voxel-Based Terrain using Dual Contouring
* Joints, including Prismatic, Revolute, Spherical and Weld Joints
* Support for Rigged Animations
* CPU-based Particles
* Springs
* Multi-Threading
* Cross-Platform Support, including Windows, OSX and Linux
 
## Controls
* W/A/S/D/Space/Shift - Move Camera
* Q - Rewind Time
* E - Enter/Exit Edit Mode
  
## Getting Started
* Windows:

  Run build.bat

* OSX/Linux:

  Run in a terminal:

      ./gradlew build
  
The compiled jar is located in build/libs
  
Or download a compiled jar from [here](https://github.com/Sjmhrp/VoidEngine/releases)
  
## Upcoming Features
* Shadows
* Water
* Better Stabilization for joints
* Featherstone solver for joints
* More Post-Processing Effects
* Physically Based Lighting Model
* Angular/Linear Damping
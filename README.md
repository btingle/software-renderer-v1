# software-renderer-v1
A software renderer implemented from scratch in Java. Features programmable shaders with vertex and fragment stages. Also contains implementation of scene graph and object/component system (like unity) for creating interactive games. Features a demo that allows you to fly a plane around procedurally generated terrain.

![Screenshot of renderer demo](https://i.ibb.co/Tghnkx4/planescreenshot.png)

Some notes on the demo:
* Terrain is generated infinitely using perlin noise
* Terrain is rendered using triplanar shading to showcase the programmable shaders
* The game runs at around ~20 fps on my Intel i5-7200U 2.5ghz 2 core cpu

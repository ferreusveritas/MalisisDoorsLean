# MalisisDoorsLean

## Features:

* Adds animations to doors, trap doors and fence gates.
* Adds new animated sliding doors that comes in wood or iron material.
* Adds sensors which detect players passing under and send a redstone signal to the block they are attached to.
* Adds vanishing blocks. Frames can be crafted and placed in the world, when supplied with redstone current, the 
vanish into thin air make all neighboring vanishing blocks vanishing as well. When the redstone current stop, 
the frames go back to being solid blocks. A frame can be activated with normal blocks which are used to "paint"
the frame. Different types of frames implies different vanishing propagation behavior :
  - wood frames propagate to all frames around them
  - iron frames propagate to all frames around painted with the same block
  - gold frames propagate to all frames around painted with the same block and the same metadata (ie red wool would not make a blue wool vanish)
  - diamond frames has their own GUI to configure their behavior. You can choose for each direction if they should propagate and the delay 
  - Frames not painted automatically propagate their state.

Add this to VM Arguments when deving under Eclipse:
   -Dfml.coreMods.load=net.malisis.doors.asm.MalisisCorePlugin
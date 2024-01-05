# ![](./src/main/resources/icon.png "Logo") ImhotepMC

Ever wanted to build large structures, but you lack the creativity and/or determination?
Tired of Schematica's half-functional printer mode?
Well wait no more, becuase ImhotepMC has you covered!
With ImhotepMC, you can now create and build your own schematics, all using
simple machines!

ImhotepMC was made as a result of Enginecrafter77's general dissatisfaction with the state of BuildCraft
for 1.12.2. It is correct to state that ImhotepMC is **heavily** inspired by BuildCraft (it can be thought of as essentially a stripped down version of the latter),
but also by other mods such as RFTools and so on.

ImhotepMC is named after an ancient egyptian builder [Imhotep](https://en.wikipedia.org/wiki/Imhotep) who's thought to have built the pyramid of Djoser.
The reason I chose that name is that this was the only thing circulating in my mind when I thought about the word "build".

## Features
 * Schematic item
 * **Construction tape** - to mark areas!
 * **Architect Table** - create a schematic from an area!
 * **Blueprint Library** - save blueprints to your PC and/or load your blueprints from your PC!
 * **Builder** - builds structures according to blueprints
 * **Terraformer** - builds geometric shapes, use shape cards to configure
 * Wide schematic format support:
   * **Litematica** format support! (native for ImhotepMC)
   * **Schematica** format support!
   * **MCEdit** format support!
   * **Sponge schematic** format support!
 * Blueprint translation tables - use schematics made for later minecraft versions \*1
 * **SMP Compatible!**

*1: Blueprint translation is performed automatically when loading a schematic from later version.
Not all blocks from later versions have equivalents in 1.12.2.
Therefore, blueprint translation is generally a lossy process (i.e. information is lost).
This should be taken into account when using schematic from later versions.

## Installing

Either grab a precompiled binary from curseforge, or compile it yourself.

```shell
./gradlew setupCIWorkspace build
```

Your mod is under `./build/libs/ImhotepMC-*.jar`

## Getting started

First, download your schematic, and place it under `.minecraft/schematics`.
Now, place a block called **Blueprint Library**. Craft one blueprint item by
combining a piece of paper and lapis dye. Open up the blueprint library, and place
the blueprint into the top right slot. Then, select the schematic in the listing, and
click the open icon. The blueprint will now appear written. Now, place a **Builder** in
such a way that you would be standing in the structure's bottom front right corner. Right-click
the builder holding the blueprint. The builder will render the outline of the build area.
Right-click the builder with an empty hand, and the red light should flip to a green light, indicating
that the projection is active. Now you should see the projection of the structure. Right click it with
an empty hand again to turn off the projection. The builder expects the materials in a chest on top of it.
Every time it needs some block, it displays it on the front display and also on a floating label 2 blocks above the builder.
Supply the builder with some power, give it the blocks it needs in the chest atop it, and you should see the blocks appearing!

## Bug reports
If you think you found a bug, don't hesitate to open up an issue on github.

## Special Thanks
SpaceToad - Created BuildCraft (inspiration for ImhotepMC)

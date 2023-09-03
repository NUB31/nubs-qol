<div align="center">

<img src="assets/icon.png" width="200px" alt="NUB's QOL" />

Welcome to my small quality of life mod for minecraft

</div>

## Features

### Easy elytra takeoff

Tired of having to press the space bar twice when you want to take off with an elytra?
Well how about zero! With easy elytra takeoff, just right click with a firework rocket and take off from anywhere!

Note: You cannot be looking at a block when taking off. This mod does not override the default implementation when
right-clicking a block

Another note: This function requires the mod installed both on the client and the server (Does not affect single-player)

### Inventory/container sorting

Use a hotkey when hovering over a container to instantly sort them. The default hotkey is middle mouse button, this can
be changed however

### Hold and drag item transfer

When having an open container, hold the left mouse button and drag to instantly transfer items to or from the container

## Mod progress

### Client side

The following features requires the mod to be installed on the client

* Hit living entities through all non-solid blocks such as sugar cane, tall grass and fern when right-clicking
    * [x] Basic Implementation
    * [ ] Settings section
        * [ ] Main toggle


* Setting menu
    * [ ] Layout
    * [ ] Keybinding
    * [ ] Toggles


* Inventory/container sorting
    * [ ] Implementation
    * [ ] A "sort" button at the top of the container GUI
    * [ ] Settings section
        * [ ] Main toggle
        * [ ] Configurable hotkey for sorting


* Hold and drag to quickly transfer items between containers
    * [ ] Implementation
    * [ ] Settings section
        * [ ] Main toggle

### Server side

The following features requires the mod to be installed on the server

### Client and server side

The following features requires the mod to be installed both on the client and the server

* Client Server communication
    * [x] Make clients aware the mod is also running on the server
    * [x] Activate some client features only if mod is running on server and client

* Take off by right-clicking with a firework rocket
    * [x] Basic Implementation
    * [x] Ignore non-solid blocks such as sugar cane, tall grass and fern when right-clicking
    * [x] Use different implementation when mod is not installed on the server
    * [ ] Settings menu
        * [ ] Main toggle
        * [ ] Ignore non-solid block toggle



# Flashlight Mod for Minecraft 1.20.1 (Fabric)

A client-side Fabric mod that adds a realistic flashlight mechanic and dynamic lighting for SPB-Revamped blocks.

## Features

### 1. Realistic Flashlight
- **Toggle**: Press `R` to turn the flashlight on/off
- **Realistic Beam Physics**:
  - **Close Range (1-3 blocks)**: Narrow, focused beam with maximum brightness (light level 15)
  - **Medium Range (4-8 blocks)**: Beam starts to spread, maintaining good brightness
  - **Long Range (9-16 blocks)**: Wide beam with reduced brightness, illuminating a larger area

The flashlight uses realistic inverse-square-law physics - the closer you are, the more concentrated and brighter the light. As you move further away, the beam spreads out to illuminate more blocks but with less intensity.

### 2. SPB-Revamped Block Lighting
Adds CLIENT-SIDE light emission to the following SPB-Revamped blocks:
- `spb-revamped:tiny_fluorescent_light`
- `spb-revamped:ceiling_light`
- `spb-revamped:thin_fluorescent_light`
- `spb-revamped:fluorescent_light`

All emit light level 15 on the client side.

## Installation

### Prerequisites
- Minecraft 1.20.1
- Fabric Loader 0.14.21 or later
- Fabric API 0.83.0+1.20.1 or later

### Steps
1. Download the mod JAR file
2. Place it in your `.minecraft/mods` folder
3. Launch Minecraft with the Fabric profile

## Building from Source

### Requirements
- JDK 17 or later
- Gradle (included via wrapper)

### Build Steps
```bash
# Navigate to the mod directory
cd flashlight-mod

# Build the mod (Windows)
gradlew.bat build

# Build the mod (Linux/Mac)
./gradlew build
```

The built JAR will be in `build/libs/flashlight-1.0.0.jar`

## Configuration

### Keybinding
You can change the flashlight toggle key in:
- Options → Controls → Key Binds → Flashlight → Toggle Flashlight

Default: `R`

## Technical Details

### How It Works
The mod uses mixins to inject custom lighting calculations into Minecraft's rendering pipeline:

1. **Flashlight System**:
   - Calculates player's look direction
   - Traces rays in the viewing direction
   - Applies light based on distance (inverse square law approximation)
   - Spreads beam based on distance for realism

2. **Block Lighting**:
   - Intercepts light level queries for SPB-Revamped blocks
   - Overrides their light level to 15 client-side
   - Works within a 32-block radius of the player

### Client-Side Only
This mod is entirely client-side. The lighting effects are only visible to you and don't affect:
- Server-side game logic
- Other players (in multiplayer)
- Actual block light levels (mobs can still spawn in "lit" areas)

## Compatibility

- **Fabric API**: Required
- **OptiFine**: Not compatible (use Sodium + Iris instead)
- **Sodium/Iris**: Should be compatible
- **Other Lighting Mods**: May conflict with mods that modify lighting systems

## Known Limitations

1. Lighting is purely visual and doesn't prevent mob spawning
2. The dynamic lighting system has a maximum range of 16 blocks for the flashlight
3. SPB-Revamped blocks must be within 32 blocks to emit light
4. Performance may vary based on system specifications

## Troubleshooting

### Flashlight not working
- Check that the keybind isn't conflicting with another mod
- Verify Fabric API is installed
- Check the console for errors

### SPB-Revamped blocks not lighting up
- Ensure the SPB-Revamped mod is installed
- Block names must match exactly (case-sensitive)
- Stay within 32 blocks of the blocks

### Performance Issues
- Reduce render distance
- Close other applications
- Consider allocating more RAM to Minecraft

## License
MIT License

## Credits
Created for Minecraft 1.20.1 Fabric

# Installation & Build Guide

## Quick Install (Pre-built JAR)

If you have a pre-built JAR file:

1. Make sure you have Minecraft 1.20.1 with Fabric Loader installed
2. Download and install Fabric API 0.83.0+1.20.1 or later from https://modrinth.com/mod/fabric-api
3. Place the `flashlight-1.0.0.jar` in your `.minecraft/mods` folder
4. Launch Minecraft
5. Press `R` to toggle the flashlight!

## Building from Source

### Requirements
- Java Development Kit (JDK) 17 or later
  - Download from: https://adoptium.net/
- Git (optional, for cloning)

### Step-by-Step Build Instructions

#### Windows:
```batch
# Navigate to the mod folder
cd flashlight-mod

# Build the mod
gradlew.bat build

# The JAR will be in build\libs\flashlight-1.0.0.jar
```

#### Linux/Mac:
```bash
# Navigate to the mod folder
cd flashlight-mod

# Make gradlew executable (if not already)
chmod +x gradlew

# Build the mod
./gradlew build

# The JAR will be in build/libs/flashlight-1.0.0.jar
```

### First Time Setup
The first build will download Gradle, dependencies, and Minecraft assets. This can take 5-10 minutes. Subsequent builds will be much faster.

### Troubleshooting Build Issues

**"JAVA_HOME is not set"**
- Install JDK 17 or later
- Set JAVA_HOME environment variable to your JDK installation path

**"Could not resolve dependencies"**
- Check your internet connection
- Try again - sometimes Maven repositories have temporary issues

**Build fails with "OutOfMemoryError"**
- Edit `gradle.properties` and increase `-Xmx2G` to `-Xmx4G`

## Testing in Development

To test the mod without building a JAR:

```bash
# Run Minecraft with the mod loaded
./gradlew runClient

# Or on Windows:
gradlew.bat runClient
```

This will launch Minecraft with your mod loaded for testing.

## Installing the Built Mod

1. Locate the JAR file in `build/libs/flashlight-1.0.0.jar`
2. Copy it to your `.minecraft/mods` folder
3. Make sure Fabric Loader and Fabric API are installed
4. Launch Minecraft 1.20.1 with Fabric profile

## Verifying Installation

1. Launch Minecraft
2. Click "Mods" from the main menu
3. Look for "Flashlight Mod" in the list
4. In-game, press `ESC` → Options → Controls → Key Binds
5. Look for the "Flashlight" category

## Usage

### Flashlight
- **Toggle**: Press `R` (default, rebindable)
- **Close range**: Narrow, bright beam
- **Far range**: Wide, dimmer beam
- Works up to 16 blocks away

### SPB-Revamped Lighting
If you have SPB-Revamped installed, these blocks will automatically emit light (client-side):
- Tiny Fluorescent Light
- Ceiling Light
- Thin Fluorescent Light
- Fluorescent Light

No configuration needed - it just works!

## Performance Tips

- The mod is lightweight but does calculate lighting every frame
- If you experience FPS drops, try reducing your render distance
- Compatible with performance mods like Sodium and Lithium

## Updating the Mod

To update to a new version:
1. Delete the old JAR from your mods folder
2. Build or download the new version
3. Place the new JAR in your mods folder
4. Restart Minecraft

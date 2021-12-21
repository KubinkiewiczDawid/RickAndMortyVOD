## Development

#### Branching

Branch types:

```
- feature/*
- bug/*
- refactor/*
```

Branch naming:
```{[TASK_NAME]}{branch_type}/{work_description}```

#### Development process

Development is done from `develop` branch.

1. Create a branch from **develop** following the branching rules
2. After developed, create a PR to **develop** branch
3. Sqash and merge

## Structure

- MVI
- Clean architecture
- Jetpack navigation, datastore, firebase
- Dependency injection with Koin
- Kotlin Coroutines, Glide

## Pre settings & tools for AS

### Code style

Copy settings from `rickAndMortyFormatStyle.xml` to AS preferences (Preferences -> Editor ->
Code style -> import scheme)

### Auto format

There are two ways to implement auto formatting (although SaveActions also allows to auto-format all
files on BUILD)

#### SaveActions

Install SaveActions plugin and copy to it settings from `saveactions_settings.xml` (or just copy
file into `./idea/`). Then restart AS with "Invalidate Caches / Restart" option

The shortcut for SaveActions: ⌘ + ⇧ + S (command + shift + S)

#### Macro

With 3 simple steps you can reorder and reformat your code automatically with ⌘ + S. That shortcut
you are used to press constantly although you know Android Studio automatically saves all files for
you. Give ⌘ + S a different meaning:

1. Make sure a Java source file has focus (or you can’t record all steps)

2.

- Select Edit > Macros > Start Macro Recording
- Select Code > Optimize Imports
- Select Code > Reformat Code
- Select Code > Rearrange Code
- Select File > Save All
- Select Edit > Macros > Stop Macro Recording and give it a name (mine is
  OptimizeImportsReformatRearrangeSave)

3. Go to Preferences > Keymap

- Find the Macro section
- Add ⌘ + S shortcut for the new macro

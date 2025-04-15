# capacitor-call-rec-plugin

Capacitor plugin for recording phone calls on Android

## Install

```bash
npm install capacitor-call-rec-plugin
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`initialize(...)`](#initialize)
* [`enableCallRecording(...)`](#enablecallrecording)
* [`getRecordings()`](#getrecordings)
* [`deleteRecording(...)`](#deleterecording)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)
### initialize(...)

```typescript
initialize(options: { directory?: string; }) => Promise<{ success: boolean; }>
```

| Param         | Type                                 |
| ------------- | ------------------------------------ |
| **`options`** | <code>{ directory?: string; }</code> |

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------


### enableCallRecording(...)

```typescript
enableCallRecording(options: { enabled: boolean; }) => Promise<{ enabled: boolean; }>
```

| Param         | Type                               |
| ------------- | ---------------------------------- |
| **`options`** | <code>{ enabled: boolean; }</code> |

**Returns:** <code>Promise&lt;{ enabled: boolean; }&gt;</code>

--------------------


### getRecordings()

```typescript
getRecordings() => Promise<{ recordings: Recording[]; }>
```

**Returns:** <code>Promise&lt;{ recordings: Recording[]; }&gt;</code>

--------------------


### deleteRecording(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
// deleteRecording(options: { id: string; }) => Promise<{ success: boolean; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

[//]: # (| **`options`** | <code>{ id: string; }</code> |)

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

[//]: # (**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>)

--------------------

</docgen-api>

##  Capacitor Plugin Project Structure

capacitor-call-recorder-plugin/
├── android/
│   ├── build.gradle                  # Auto-generated
│   ├── gradle/                       # Auto-generated
│   ├── gradle.properties             # Auto-generated
│   ├── gradlew                       # Auto-generated
│   ├── gradlew.bat                   # Auto-generated
│   ├── settings.gradle               # Auto-generated
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml   # MODIFY: Add permissions and services
│           └── java/
│               └── com/
│                   └── yourcompany/  # This folder name might be different in your case
│                       └── plugins/
│                           └── callrecorder/
│                               ├── CallRecorderPlugin.java    # MODIFY: Main plugin class
│                               ├── CallDetectionService.java  # ADD: Service for call detection
│                               └── CallRecorderService.java   # ADD: Service for call recording
├── dist/                      # Generated after build
│   └── esm/                   # Generated TypeScript output
├── node_modules/              # Auto-generated
├── src/
│   ├── definitions.ts         # MODIFY: Define plugin interface
│   ├── index.ts               # Auto-generated, usually doesn't need modification
│   └── web.ts                 # MODIFY: Fix unused parameter warnings
├── package.json               # MODIFY: Plugin metadata and dependencies
├── package-lock.json          # Auto-generated
└── tsconfig.json              # Auto-generated, usually doesn't need modification

Key Files to Modify in Your Capacitor Plugin

- `android/src/main/AndroidManifest.xml`: Add all required permissions and service declarations
- `android/src/main/java/com/yourcompany/plugins/callrecorder/CallRecorderPlugin.java`: Implement the main plugin class
- `android/src/main/java/com/yourcompany/plugins/callrecorder/CallDetectionService.java`: Implement the call detection service
- `android/src/main/java/com/yourcompany/plugins/callrecorder/CallRecorderService.java`: Implement the call recording service
- `src/definitions.ts`: Define the TypeScript interface for your plugin
- `src/web.ts`: Implement the web fallback (with fixed unused parameter warnings)

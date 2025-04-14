# capacitor-call-rec-plugin

Capacitor plugin for recording phone calls on Android

## Install

```bash
npm install capacitor-call-rec-plugin
npx cap sync
```

## API

<docgen-index>

* [`initialize(...)`](#initialize)
* [`enableCallRecording(...)`](#enablecallrecording)
* [`getRecordings()`](#getrecordings)
* [`deleteRecording(...)`](#deleterecording)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

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
deleteRecording(options: { id: string; }) => Promise<{ success: boolean; }>
```

| Param         | Type                         |
| ------------- | ---------------------------- |
| **`options`** | <code>{ id: string; }</code> |

**Returns:** <code>Promise&lt;{ success: boolean; }&gt;</code>

--------------------


### Interfaces


#### Recording

| Prop              | Type                 |
| ----------------- | -------------------- |
| **`id`**          | <code>string</code>  |
| **`filePath`**    | <code>string</code>  |
| **`phoneNumber`** | <code>string</code>  |
| **`duration`**    | <code>number</code>  |
| **`timestamp`**   | <code>number</code>  |
| **`isOutgoing`**  | <code>boolean</code> |

</docgen-api>

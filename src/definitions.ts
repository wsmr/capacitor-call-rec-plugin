export interface CallRecorderPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}

// export interface CallRecorderPluginPlugin {
//   initialize(options: { directory?: string }): Promise<{ success: boolean }>;
//
//   enableCallRecording(options: { enabled: boolean }): Promise<{ enabled: boolean }>;
//
//   getRecordings(): Promise<{ recordings: Recording[] }>;
//
//   deleteRecording(options: { id: string }): Promise<{ success: boolean }>;
// }
//
// export interface Recording {
//   id: string;
//   filePath: string;
//   phoneNumber: string;
//   duration: number;
//   timestamp: number;
//   isOutgoing: boolean;
// }

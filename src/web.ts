import { WebPlugin } from '@capacitor/core';

import type { CallRecorderPluginPlugin, Recording } from './definitions';

export class CallRecorderPluginWeb extends WebPlugin implements CallRecorderPluginPlugin {
  // async echo(options: { value: string }): Promise<{ value: string }> {
  //   console.log('ECHO', options);
  //   return options;
  // }
  // Use underscore prefix to indicate intentionally unused parameters
  async initialize(_options: { directory?: string }): Promise<{ success: boolean }> {
    console.warn('Call recording is not supported in web environment');
    return { success: false };
  }

  async enableCallRecording(_options: { enabled: boolean }): Promise<{ enabled: boolean }> {
    console.warn('Call recording is not supported in web environment');
    return { enabled: false };
  }

  async getRecordings(): Promise<{ recordings: Recording[] }> {
    console.warn('Call recording is not supported in web environment');
    return { recordings: [] };
  }

  async deleteRecording(_options: { id: string }): Promise<{ success: boolean }> {
    console.warn('Call recording is not supported in web environment');
    return { success: false };
  }
}

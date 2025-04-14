import { WebPlugin } from '@capacitor/core';

import type { CallRecorderPluginPlugin } from './definitions';

export class CallRecorderPluginWeb extends WebPlugin implements CallRecorderPluginPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

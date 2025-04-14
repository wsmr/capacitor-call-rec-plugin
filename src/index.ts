import { registerPlugin } from '@capacitor/core';

import type { CallRecorderPluginPlugin } from './definitions';

const CallRecorderPlugin = registerPlugin<CallRecorderPluginPlugin>('CallRecorderPlugin', {
  web: () => import('./web').then((m) => new m.CallRecorderPluginWeb()),
});

export * from './definitions';
export { CallRecorderPlugin };
